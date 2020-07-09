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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
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

     JSONObject totalResponse=toJSON(this);
     messaging.utils.showDebugLog(this,nameMethod, "Notification "
                +"responseTotal "+totalResponse);

     messaging.setLastMessagingNotification(this);
     //sendEventToActivity(Messaging.ACTION_GET_NOTIFICATION,this,this.context);
     sendEventToActivity(Messaging.ACTION_GET_NOTIFICATION,totalResponse, this.context);


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

    public static JSONObject toJSON(Object object){
        String str = "";
        Class c = object.getClass();
        JSONObject jsonObject = new JSONObject();
        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();

            try {
                //String value = String.valueOf(field.get(object));
                jsonObject.put(name, field.get(object));
            } catch (JSONException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //return jsonObject.toString();
        return jsonObject;
    }



    /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     @param something: Object JSON for send to activity (Ej messagingDevice).
     @param context : context instance
     */
    private void sendEventToActivity(String action,JSONObject something, Context context) {
        this.nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod, "Notification "+action
                +"  "+something.toString());
        Intent intent=new Intent(action);
        intent.putExtra(Messaging.INTENT_EXTRA_DATA,something.toString());
        intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR,something==null);
        context.sendBroadcast(intent,context.getPackageName()+".permission.pushReceive");
    }




}
