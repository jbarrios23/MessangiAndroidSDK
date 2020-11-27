package com.ogangi.Messangi.SDK.Demo;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingCircularRegion;
import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingLocation;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class MessagingNotificationReceiver extends BroadcastReceiver {

    public static String CLASS_TAG=MessagingNotificationReceiver.class.getSimpleName();
    public static String TAG="MESSAGING";
    private String nameMethod;
    private MessagingNotification messagingNotification;
    private double wayLatitude, wayLongitude;
    private ArrayList<MessagingCircularRegion> messagingCircularRegions;
    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "uno";
    private Messaging messaging;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

        messaging=Messaging.getInstance();
        String alertMessage = context.getResources().getString(context.getResources().getIdentifier(intent.getAction(), "string", context.getPackageName()));
        //Toast.makeText(context, alertMessage, Toast.LENGTH_LONG).show();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Action:  "+ alertMessage);
        boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);

        if (!hasError) {
            String action=intent.getAction();
            Serializable data = intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
            //optional code to determinate if app is Background or not
            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": isInBackground:  "+ isInBackground);
             if(intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION)&& data!=null){
                Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": DATA:  "+ data);

                 handleDataNotification(data, intent, context, action,isInBackground);


            }else if(intent.getAction().equals(Messaging.ACTION_FETCH_LOCATION)){
                 wayLatitude = intent.getDoubleExtra(Messaging.INTENT_EXTRA_DATA_lAT,0.00);
                 wayLongitude = intent.getDoubleExtra(Messaging.INTENT_EXTRA_DATA_lONG,0.00);


                 Location location=new Location(LOCATION_SERVICE);
                 location.setLatitude(wayLatitude);
                 location.setLongitude(wayLongitude);
                 MessagingLocation messagingLocation=new MessagingLocation(location);
                 if(isInBackground){
                     Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location Lat:  "
                             + wayLatitude
                             +" Long: "+wayLongitude);
                 }else{
                     sendEventToActivity(Messaging.ACTION_FETCH_LOCATION,messagingLocation,context);
                     Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location Lat:  "
                             + wayLatitude
                             +" Long: "+wayLongitude);
                 }

             }else if(intent.getAction().equals(Messaging.ACTION_GEOFENCE_ENTER)
                     ||intent.getAction().equals(Messaging.ACTION_GEOFENCE_EXIT)){
                  messagingCircularRegions=(ArrayList<MessagingCircularRegion>)data;
                 if(isInBackground){
                 Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Geofence:  "
                             + messagingCircularRegions);
                 }else{
                 sendEventToActivity(Messaging.ACTION_FETCH_GEOFENCE,messagingCircularRegions,context);
                 Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Geofence:  "
                             + messagingCircularRegions);
                 }
                 //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();


             }else{

                Toast.makeText(context,alertMessage,Toast.LENGTH_LONG).show();

            }

        }else{
        Toast.makeText(context,"An error occurred on action "
                    +alertMessage,Toast.LENGTH_LONG).show();
        }

    }

    private void handleDataNotification(Serializable data, Intent intent, Context context, String action, boolean isInBackground) {

        if(isInBackground){
            Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": No action for notification in Background :  "
                    + isInBackground);
            messagingNotification = (MessagingNotification) data;
            String subject="";
            String content = "";
            String Title="";
            String Text = "";
            String Image="";
            boolean showCustomNotification=false;
            for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                if(!entry.getKey().equals("profile")){
                    Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": key: "+entry.getKey() + " value: " + entry.getValue());
                    if(entry.getKey().equals("subject")) {
                        subject= (String) entry.getValue();
                    }else if(entry.getKey().equals("content")){

                        content= (String) entry.getValue();
                    }else if(entry.getKey().equals("Title")){

                        Title= (String) entry.getValue();
                    }else if(entry.getKey().equals("Text")){

                        Text= (String) entry.getValue();
                    }else if(entry.getKey().equals("Image")){

                        Image= (String) entry.getValue();
                        showCustomNotification=true;
                    }

                }

            }
            if(showCustomNotification){
                showCustomNotification(Title,Text,Image,context);

            }

        }else{
            sendEventToActivity(action,data,context);
        }
    }

    private void showCustomNotification(String title, String text, String image, Context context) {
        nameMethod="showCustomNotification";
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(image);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    //Bitmap bmp = Messaging.getBitmapFromURL(image);
                    Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": bitmap "+bmp);
                    Intent notificationIntent=null;
                    try {

                        notificationIntent = new Intent(context,
                                Class.forName(messaging.getNameClass()));
                        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": name class "
                                +messaging.getNameClass());


                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        Log.e(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": error "+e.getMessage());

                    }catch (NullPointerException e){
                        e.printStackTrace();
                        Log.e(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": error "+e.getMessage());
                        notificationIntent = new Intent("android.intent.action.MAIN");
                    }

                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    final PendingIntent pendingIntent = PendingIntent.getActivity(context
                            , 0, notificationIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


                    Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setLargeIcon(bmp)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setNotificationSilent()
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(bmp)
                                    .bigLargeIcon(null))
                            .build();

                notificationManager.notify(1 /* ID of notification */, notification);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": error 1 " + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": error 1 " + e.getMessage());
                }


            }
        }).start();

    }

    /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     * @param something : Object Serializable for send to activity (Ej messagingDevice).
     * @param context : context instance
     */
    private void sendEventToActivity(String action, Serializable something, Context context) {
        this.nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(something!=null) {
            Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Action:  " + action + " data: " + something.toString());
            Intent intent = new Intent(action);
            intent.putExtra(Messaging.INTENT_EXTRA_DATA, something);
            intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR, something == null);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            Log.e(TAG, "ERROR: " + CLASS_TAG + ": " + nameMethod);
        }
    }


}
