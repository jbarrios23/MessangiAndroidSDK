package com.messaging.sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessagingNotification implements Serializable {

    private Messaging messaging = Messaging.getInstance();
    private Context context;

    //common

    private String notificationId;
    private boolean silent;
    private String title;
    private String body;
    private String deepLink;
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
         this.deepLink=remoteMessage.getNotification().getClickAction();
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
                 +"Title "+notification.getTitle()+" "+"Body "+notification.getBody());

    }

     if(this.additionalData!=null && additionalData.size()>0){
         messaging.utils.showDebugLog(this,nameMethod, "additionalData: "+additionalData);
         messaging.utils.showDebugLog(this,nameMethod, "ticker: "+ticker);
         messaging.utils.showDebugLog(this,nameMethod, "icon: "+icon);
         messaging.utils.showDebugLog(this,nameMethod, "badge: "+badge);

     }

     messaging.setLastMessangiNotifiction(this);
     sendEventToActivity(this,this.context);

    }

    public MessagingNotification(Bundle extras, Context context) {
        this.context=context;
        this.nameMethod="MessagingNotification";
        boolean send=true;


        if(extras!=null){
            additionalData=new HashMap<>();
            for(String key:extras.keySet()){
                //messaging.utils.showDebugLog(this,nameMethod,"Extras received:  Key: " + key + " Value: " + extras.getString(key));
                additionalData.put(key,extras.getString(key));
             if(key.equals("profile")){
                send=false;
                }
            }
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

    public String getDeepLink() {
        return deepLink;
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
    private void sendEventToActivity(Serializable something, Context context) {
        this.nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Intent intent=new Intent("PassDataFromSdk");
        intent.putExtra("message",something);
        intent.putExtra("hasError",something==null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

}
