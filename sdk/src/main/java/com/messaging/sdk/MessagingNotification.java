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

    //common

    private String notificationId;
    private boolean silent;
    private String title;
    private String body;
    private String clickAction;
    private String deepUriLink;

    private Map<String,String> additionalData;
    private int badge;


    //exclusive

    private String icon;
    private Uri imageUrl;
    private boolean sticky;
    private String channelId;
    private String ticker;
    private String sound;

    //For RemoteMessage

    private String nameMethod;
    private String from;
    private String messageId;
    private String messageType;
    private int priority;
    private int originalPriority;
    private String senderId;
    private long sentTime;
    private String toSomeBody;
    //other
    private String[] bodyLocalizationArgs;
    private String bodyLocalizationKey;
    private String color;
    private boolean defaultLightSettings;
    private boolean defaultSound;
    private boolean defaultVibrateSettings;
    private long[] vibrateTimings;
    private boolean localOnly;
    private int visibility;

    public MessagingNotification(RemoteMessage remoteMessage) {
     this.nameMethod="MessagingNotification";

     RemoteMessage.Notification notification=remoteMessage.getNotification();
     //common
     this.notificationId=remoteMessage.getMessageId();
     this.silent=true;
     Messaging messaging = Messaging.getInstance();
     messaging.utils.showDebugLog(this,nameMethod, "silent: "+silent);
     if(notification!=null){
         this.silent=false;
         this.title=remoteMessage.getNotification().getTitle();
         this.body=remoteMessage.getNotification().getBody();
         this.clickAction=remoteMessage.getNotification().getClickAction();
         if(remoteMessage.getNotification().getLink()!=null) {
             this.deepUriLink = remoteMessage.getNotification().getLink().toString();
         }else{
             this.deepUriLink=null;
         }
         this.icon=remoteMessage.getNotification().getIcon();
         this.sound=remoteMessage.getNotification().getSound();
         this.imageUrl=remoteMessage.getNotification().getImageUrl();
         this.channelId=remoteMessage.getNotification().getChannelId();
         this.sticky=remoteMessage.getNotification().getSticky();
         if(remoteMessage.getNotification().getNotificationCount()!=null) {
             this.badge = remoteMessage.getNotification().getNotificationCount();
         }else{
             this.badge=0;
         }
         this.ticker=remoteMessage.getNotification().getTicker();
         //other

         this.bodyLocalizationArgs=remoteMessage.getNotification().getBodyLocalizationArgs();
         this.bodyLocalizationKey=remoteMessage.getNotification().getBodyLocalizationKey();
         this.color=remoteMessage.getNotification().getColor();
         this.defaultLightSettings=remoteMessage.getNotification().getDefaultVibrateSettings();
         this.defaultVibrateSettings=remoteMessage.getNotification().getDefaultVibrateSettings();
         this.defaultSound=remoteMessage.getNotification().getDefaultSound();
         this.vibrateTimings=remoteMessage.getNotification().getVibrateTimings();
         this.localOnly=remoteMessage.getNotification().getLocalOnly();
         if(remoteMessage.getNotification().getVisibility()!=null) {
             this.visibility = remoteMessage.getNotification().getVisibility();
         }else{
             this.visibility=0;
         }
         //show parameter

         messaging.utils.showDebugLog(this,nameMethod, "Notification "
                 +"Title "+title+" "+"Body "+body);
         messaging.utils.showDebugLog(this, nameMethod, "Notification "
                 + "click_action " + clickAction + " Uri Link " + deepUriLink);

         if(deepUriLink!=null) {
             messaging.utils.showDebugLog(this, nameMethod, "Notification "
                     + "name Url schema or Link Universal " + deepUriLink);

         }
         messaging.utils.showDebugLog(this,nameMethod, "silent: "+silent);
         messaging.utils.showDebugLog(this,nameMethod, "sticky: "+sticky);
         messaging.utils.showDebugLog(this,nameMethod, "defaultVibrateSettings: "+defaultVibrateSettings);
         messaging.utils.showDebugLog(this,nameMethod, "icon: "+icon+" sound "+sound);
         messaging.utils.showDebugLog(this,nameMethod, "imageUrl: "+imageUrl+" chanelId "+channelId);
         messaging.utils.showDebugLog(this,nameMethod, "badge: "+badge+" ticker "+ticker);
         //other
         messaging.utils.showDebugLog(this,nameMethod, "bodyLocalizationArgs: "+bodyLocalizationArgs);
         messaging.utils.showDebugLog(this,nameMethod, "bodyLocalizationKey: "+bodyLocalizationKey);
         messaging.utils.showDebugLog(this,nameMethod, "color: "+color+" defaultLightSettings "+defaultLightSettings);
         messaging.utils.showDebugLog(this,nameMethod, "defaultSound: "+defaultSound+" vibrateTimings "+vibrateTimings);
         messaging.utils.showDebugLog(this,nameMethod, "localOnly: "+localOnly+" visibility "+visibility);


     }

     if(remoteMessage.getData()!=null){

         this.additionalData=new HashMap<>(remoteMessage.getData());
         this.nameMethod="MessagingNotification";
         //Messaging messaging = Messaging.getInstance();
         messaging.utils.showDebugLog(this,nameMethod, "additionalData: "+additionalData);

     }

     RemoteMessage rawPayload=remoteMessage;
        if(rawPayload!=null){
            this.from=rawPayload.getFrom();
            this.messageId=rawPayload.getMessageId();
            this.messageType=rawPayload.getMessageType();
            this.priority=rawPayload.getPriority();
            this.originalPriority=rawPayload.getOriginalPriority();
            this.senderId=rawPayload.getSenderId();
            this.sentTime=rawPayload.getSentTime();
            this.toSomeBody=rawPayload.getTo();

            messaging.utils.showDebugLog(this,nameMethod, "from: "+from);
            messaging.utils.showDebugLog(this,nameMethod, "messageId: "+messageId);
            messaging.utils.showDebugLog(this,nameMethod, "messageType: "+messageType+" priority "+priority);
            messaging.utils.showDebugLog(this,nameMethod, "originalPriority: "+originalPriority+" senderId "+senderId);
            messaging.utils.showDebugLog(this,nameMethod, "sentTime: "+sentTime+" toSomeBody "+toSomeBody);

        }

    }



    public MessagingNotification() {

    }

    //method
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

    public String getClickAction() {
        return clickAction;
    }

    public String getDeepUriLink() {
        return deepUriLink;
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public int getBadge() {
        return badge;
    }

    public String getIcon() {
        return icon;
    }

    public Uri getImageUrl() {
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

    public String getSound() {
        return sound;
    }

    public String getFrom() {
        return from;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getPriority() {
        return priority;
    }

    public int getOriginalPriority() {
        return originalPriority;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getSentTime() {
        return sentTime;
    }

    public String getToSomeBody() {
        return toSomeBody;
    }

    public String[] getBodyLocalizationArgs() {
        return bodyLocalizationArgs;
    }

    public String getBodyLocalizationKey() {
        return bodyLocalizationKey;
    }

    public String getColor() {
        return color;
    }

    public boolean isDefaultLightSettings() {
        return defaultLightSettings;
    }

    public boolean isDefaultSound() {
        return defaultSound;
    }

    public boolean isDefaultVibrateSettings() {
        return defaultVibrateSettings;
    }

    public long[] getVibrateTimings() {
        return vibrateTimings;
    }

    public boolean isLocalOnly() {
        return localOnly;
    }

    public int getVisibility() {
        return visibility;
    }

    public void writeToParcel (Parcel out, int flags){

    }






}
