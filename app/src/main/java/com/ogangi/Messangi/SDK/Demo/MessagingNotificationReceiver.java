package com.ogangi.Messangi.SDK.Demo;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingCircularRegion;
import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingLocation;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
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
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Has error:  "+ hasError);
        if (!hasError ) {
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

                 Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();

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
                 }



             }else if(intent.getAction().equals(Messaging.ACTION_GEOFENCE_ENTER)
                     ||intent.getAction().equals(Messaging.ACTION_GEOFENCE_EXIT)){
                  messagingCircularRegions=(ArrayList<MessagingCircularRegion>)data;
                 if(isInBackground){
                 Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Geofence:  "
                             + messagingCircularRegions);
                 }else{
                 sendEventToActivity(Messaging.ACTION_FETCH_GEOFENCE,messagingCircularRegions,context);
                 }
                 Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();


             }else{

                Toast.makeText(context,intent.getAction(),Toast.LENGTH_LONG).show();

            }

        }else{
        Toast.makeText(context,"An error occurred on action "
                    +intent.getAction(),Toast.LENGTH_LONG).show();
        }

    }

    private void handleDataNotification(Serializable data, Intent intent, Context context, String action, boolean isInBackground) {

        if(isInBackground){
            intent.putExtra(Messaging.INTENT_EXTRA_DATA,data);
            intent.putExtra("isInBackground",isInBackground);
            intent.setClassName(context.getPackageName(), context.getPackageName()+".MainActivity");
            //intent.setClassName(context.getPackageName(), context.getPackageName()+".LoginActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            sendEventToActivity(action,data,context);
        }
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
