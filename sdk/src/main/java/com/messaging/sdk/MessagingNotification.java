package com.messaging.sdk;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MessagingNotification implements Serializable {

    private Messaging messaging = Messaging.getInstance();
    private Context context;

    //common

    private String notificationId;
    private boolean silent;
    private String title;
    private String body;
    //private String deepLink;
    private String clickAction;
    private Uri deepUriLink;

    private Map<String,String> additionalData;
    private int badge;
    private RemoteMessage rawPayload;

    //exclusive

    private String icon;
    private String imageUrl;
    private boolean sticky;
    private String channelId;
    private String ticker;
    private String sound;

    //For RemoteMessage
    private RemoteMessage.Notification notification;
    public String nameMethod;

    private NotificationManager notificationManager;
    private static final String ADMIN_CHANNEL_ID ="admin_channel";



    public MessagingNotification(RemoteMessage remoteMessage, Context context) {
     this.nameMethod="MessagingNotification";
     this.context=context;
     this.notification=remoteMessage.getNotification();
     //common
     this.notificationId=remoteMessage.getMessageId();
     this.silent=true;
     if(this.notification!=null){
         this.silent=false;
         this.title=remoteMessage.getNotification().getTitle();
         this.body=remoteMessage.getNotification().getBody();
         this.clickAction=remoteMessage.getNotification().getClickAction();
         this.deepUriLink=remoteMessage.getNotification().getLink();
         this.icon=remoteMessage.getNotification().getIcon();
         this.sound=remoteMessage.getNotification().getSound();

     }

     if(remoteMessage.getData()!=null){

         this.additionalData=remoteMessage.getData();
         //this.badge=Integer.parseInt(remoteMessage.getNotification().getBody());
         if(remoteMessage.getData().get("badge")!=null && !remoteMessage.getData().get("badge").isEmpty()){
             this.badge=Integer.parseInt(remoteMessage.getData().get("badge"));
         }else{
             this.badge=0;
         }
         if(remoteMessage.getData().get("imageUrl")!=null && !remoteMessage.getData().get("imageUrl").isEmpty()){
             this.imageUrl=remoteMessage.getData().get("imageUrl");
         }else{
             this.imageUrl="";
         }

         if(remoteMessage.getData().get("channelId")!=null && !remoteMessage.getData().get("channelId").isEmpty()){
             this.channelId=remoteMessage.getData().get("channelId");
         }else{
             this.channelId="";
         }

         if(remoteMessage.getData().get("ticker")!=null && !remoteMessage.getData().get("ticker").isEmpty()){
             this.ticker=remoteMessage.getData().get("ticker");
         }else{
             this.ticker="";
         }
     }

     this.rawPayload=remoteMessage;

     this.sticky=false;
     if(remoteMessage.getCollapseKey()!=null && !remoteMessage.getCollapseKey().isEmpty()){
         this.sticky=true;
     }


     messaging.utils.showDebugLog(this,nameMethod, "silent: "+silent);
     messaging.utils.showDebugLog(this,nameMethod, "sticky: "+sticky);

    if(this.notification!=null){
         messaging.utils.showDebugLog(this,nameMethod, "Notification "
                 +"Title "+title+" "+"Body "+body);
         messaging.utils.showDebugLog(this, nameMethod, "Notification "
                + "click_action " + clickAction + " Uri Link " + deepUriLink);
         if(clickAction!=null) {
            messaging.utils.showDebugLog(this, nameMethod, "Notification "
                     + "name class destiny "+clickAction);

         launchNotification(clickAction,context,additionalData);

         }
        if(deepUriLink!=null) {
            messaging.utils.showDebugLog(this, nameMethod, "Notification "
                    + "name Url schema or Link Universal "+deepUriLink);
            launchBrowser(deepUriLink,context,additionalData);
        }

    }


   if(this.additionalData!=null && additionalData.size()>0){
         this.nameMethod="MessagingNotification";
         messaging.utils.showDebugLog(this,nameMethod, "additionalData: "+additionalData);
         messaging.utils.showDebugLog(this,nameMethod, "ticker: "+ticker);
         messaging.utils.showDebugLog(this,nameMethod, "icon: "+icon);
         messaging.utils.showDebugLog(this,nameMethod, "badge: "+badge);

     }

     messaging.setLastMessangiNotifiction(this);
     sendEventToActivity(Messaging.ACTION_GET_NOTIFICATION,this,this.context);

    }

    private void launchBrowser(Uri deepUriLink, Context context, Map<String, String> additionalData) {
        nameMethod="launchBrowser";
        messaging.utils.showDebugLog(this, nameMethod, "Notification "
                + "Link " + deepUriLink);
        try {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(String.valueOf(deepUriLink)));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (Map.Entry<String, String> entry : additionalData.entrySet()) {
                browserIntent .putExtra(entry.getKey(),  entry.getValue());
            }
            context.startActivity(browserIntent);
        }catch (Exception e){
            e.printStackTrace();
            messaging.utils.showErrorLog(this,nameMethod,e.getMessage(),"");

        }

    }

    private void launchNotification(String clickAction, Context context, Map<String,String> additionalData) {
        nameMethod="launchNotification";
        messaging.utils.showDebugLog(this, nameMethod, "Notification "
                + "click_action " + clickAction);
        Intent notificationIntent=null;
        try {

            notificationIntent = new Intent(context, Class.forName(clickAction));
            for (Map.Entry<String, String> entry : additionalData.entrySet()) {
                notificationIntent.putExtra(entry.getKey(),  entry.getValue());
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }catch (NullPointerException e){
            e.printStackTrace();

            notificationIntent = new Intent("android.intent.action.MAIN");
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //Setting notification for Android Oreo or higer.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels();
            }
            int notificationId = new Random().nextInt(60000);

            // Create the notification.
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                    .setSmallIcon(messaging.icon)  //a resource for your custom small icon
                    .setContentTitle(title) //the "title" value you sent in your notification
                    .setContentText(body) //ditto
                    .setAutoCancel(true)  //dismisses the notification on click
                    .setContentIntent(pendingIntent)
                    .setSound(defaultSoundUri);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = context.getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = context.getString(R.string.notifications_admin_channel_description);
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }

    }

    public MessagingNotification(Bundle extras, Context context) {
        this.context=context;
        this.nameMethod="MessagingNotification";
        boolean send=true;


        if(extras!=null){
            additionalData=new HashMap<>();
            for(String key:extras.keySet()){
                additionalData.put(key,extras.getString(key));
             if(key.equals("profile")){
                send=false;
                }
            }
            this.nameMethod="MessagingNotification";
            messaging.utils.showDebugLog(this,nameMethod,"Data: " +additionalData);
            if(send) {
                messaging.setLastMessangiNotifiction(this);

            }

        }

    }

    public MessagingNotification() {

    }

    //method

    public RemoteMessage.Notification getNotification() {
        return notification;
    }

    public Uri getDeepUriLink() {
        return deepUriLink;
    }

    public String getIcon() {
        return icon;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isSticky() {
        return sticky;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTicker() {
        return ticker;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public boolean isSilent() {
        return silent;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

//    public String getDeepLink() {
//        return deepLink;
//    }

    public String getClickAction() {
        return clickAction;
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public int getBadge() {
        return badge;
    }

    public RemoteMessage getRawPayload() {
        return rawPayload;
    }


    public void writeToParcel (Parcel out, int flags){

    }

    /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(String action,Serializable something, Context context) {
        this.nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Intent intent=new Intent(action);
        intent.putExtra(Messaging.INTENT_EXTRA_DATA,something);
        intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR,something==null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

}
