package com.messaging.sdk;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessagingNotification implements Serializable {

    //common

    private String notificationId;
    private String type;
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
    private String msgAppId="";
    private boolean matchAppId;
    private String provRegisterLogs;
    private boolean registerLogs;
    private String messagingConfiguration;

    public MessagingNotification(RemoteMessage remoteMessage) {
     this.nameMethod="MessagingNotification";

     RemoteMessage.Notification notification=remoteMessage.getNotification();
     //common
     //this.notificationId=remoteMessage.getMessageId();
     this.silent=true;
     Messaging messaging = Messaging.getInstance();
     messaging.utils.showDebugLog(this,nameMethod, "silent: "+silent);
     if(notification!=null){
         this.silent=false;
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
         this.notificationId=additionalData.get(Messaging.MESSAGING_ID);
         this.type=additionalData.get(Messaging.MESSAGING_TYPE);
         this.title=additionalData.get(Messaging.MESSAGING_TITLE);
         this.body=additionalData.get(Messaging.MESSAGING_BODY);
         this.msgAppId=additionalData.get(Messaging.MESSAGING_APP_ID);
         if(msgAppId!=null && msgAppId!="") {
             this.matchAppId = messaging.utils.verifyMatchAppId(msgAppId);
             messaging.utils.showDebugLog(this,nameMethod, "MSGI_APPID: "
                     +msgAppId+" Verify: "+matchAppId);
         }else{
             this.matchAppId=true;
         }
         this.provRegisterLogs= additionalData.get(Messaging.MESSAGING_LOGGING_ENABLE);
         if(provRegisterLogs!=null && provRegisterLogs!=""){
             if(provRegisterLogs.equals("true")){
                 this.registerLogs=true;
             }else{
                 this.registerLogs=false;
             }
             messaging.utils.setLogging_allowed(registerLogs);
             messaging.utils.showDebugLog(this,nameMethod, "MSGI_REGISTER_LOGS: "
                     +registerLogs);
             messaging.utils.showConfigParameter();
         }else{
             this.registerLogs=true;
             messaging.utils.setLogging_allowed(registerLogs);
             messaging.utils.showDebugLog(this,nameMethod, "AMSGI_REGISTER_LOGS: "
                     +registerLogs);

             messaging.utils.showConfigParameter();
         }

         if(additionalData.get(Messaging.MESSAGING_CONFIGURATION)!=null &&
                 !additionalData.get(Messaging.MESSAGING_CONFIGURATION).isEmpty()){
             this.messagingConfiguration=additionalData.get(Messaging.MESSAGING_CONFIGURATION);
             messaging.utils.showDebugLog(this,nameMethod, "has Configuration: "
                     +additionalData.get(Messaging.MESSAGING_CONFIGURATION));
             messaging.utils.saveConfigParameter(messagingConfiguration,messaging);
             messaging.utils.showConfigParameter();

         }

         messaging.utils.showDebugLog(this,nameMethod, "additionalData: "+additionalData);


     }

     RemoteMessage rawPayload=remoteMessage;
        if(rawPayload!=null){
            this.from=rawPayload.getFrom();
            this.messageType=rawPayload.getMessageType();
            this.priority=rawPayload.getPriority();
            this.originalPriority=rawPayload.getOriginalPriority();
            this.senderId=rawPayload.getSenderId();
            this.sentTime=rawPayload.getSentTime();
            this.toSomeBody=rawPayload.getTo();

            messaging.utils.showDebugLog(this,nameMethod, "from: "+from);
            messaging.utils.showDebugLog(this,nameMethod, "messageId: "+notificationId);
            messaging.utils.showDebugLog(this,nameMethod, "messageType: "+messageType+" priority "+priority);
            messaging.utils.showDebugLog(this,nameMethod, "originalPriority: "+originalPriority+" senderId "+senderId);
            messaging.utils.showDebugLog(this,nameMethod, "sentTime: "+sentTime+" toSomeBody "+toSomeBody);

        }

    }

    public MessagingNotification(Bundle extras) {
        this.nameMethod="MessagingNotification";
        Messaging messaging = Messaging.getInstance();
        boolean send=true;
        if(extras!=null){
             additionalData=new HashMap<>();
            for(String key:extras.keySet()){
                additionalData.put(key, extras.get(key).toString());
                if(key.equals("profile")){
                    send=false;
                }else if(key.equals(Messaging.MESSAGING_ID)){
                    this.notificationId=extras.getString(key);
                }else if(key.equals(Messaging.MESSAGING_TITLE)){
                    this.title=extras.getString(key);
                }else if(key.equals(Messaging.MESSAGING_BODY)){
                    this.body=extras.getString(key);
                }else if(key.equals(Messaging.MESSAGING_TYPE)){
                    this.type=extras.getString(key);
                }else if(key.equals(Messaging.MESSAGING_APP_ID)) {
                    this.msgAppId = extras.getString(key);
                }else if(key.equals(Messaging.MESSAGING_CONFIGURATION)) {
                    this.messagingConfiguration=additionalData.get(key);
                    messaging.utils.showDebugLog(this,nameMethod, "has Configuration: "
                            +additionalData.get(key));
                    messaging.utils.saveConfigParameter(messagingConfiguration, messaging);
                    messaging.utils.showConfigParameter();
                }else if(key.equals(Messaging.MESSAGING_LOGGING_ENABLE)) {
                   this.provRegisterLogs=extras.getString(key);
                }
            }
            if(msgAppId!=null && msgAppId!="") {
                this.matchAppId=messaging.utils.verifyMatchAppId(msgAppId);
                messaging.utils.showDebugLog(this,nameMethod, "MSGI_APPID: "+msgAppId+" Verify: "+matchAppId);
            }else{
                this.matchAppId=true;
            }
            if(provRegisterLogs!=null && provRegisterLogs!=""){
                if(provRegisterLogs.equals("true")){
                    this.registerLogs=true;
                }else{
                    this.registerLogs=false;
                }
                messaging.utils.setLogging_allowed(registerLogs);
                messaging.utils.showDebugLog(this,nameMethod, "MSGI_REGISTER_LOGS: "
                        +registerLogs);
                messaging.utils.showConfigParameter();
            }else{
                this.registerLogs=true;
                messaging.utils.setLogging_allowed(registerLogs);
                messaging.utils.showDebugLog(this,nameMethod, "AMSGI_REGISTER_LOGS: "
                        +registerLogs);
                messaging.utils.showConfigParameter();
            }

            messaging.utils.showDebugLog(this,nameMethod,"Data: " +additionalData);

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
    public String getType() {
        return type;
    }

    public boolean isMatchAppId() {
        return matchAppId;
    }

    public void writeToParcel (Parcel out, int flags){

    }


    @Override
    public String toString() {
        return "MessagingNotification{" +
                "notificationId='" + notificationId + '\'' +
                ", type='" + type + '\'' +
                ", silent=" + silent +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", clickAction='" + clickAction + '\'' +
                ", deepUriLink='" + deepUriLink + '\'' +
                ", additionalData=" + additionalData +
                ", badge=" + badge +
                ", icon='" + icon + '\'' +
                ", imageUrl=" + imageUrl +
                ", sticky=" + sticky +
                ", channelId='" + channelId + '\'' +
                ", ticker='" + ticker + '\'' +
                ", sound='" + sound + '\'' +
                ", nameMethod='" + nameMethod + '\'' +
                ", from='" + from + '\'' +
                ", messageType='" + messageType + '\'' +
                ", priority=" + priority +
                ", originalPriority=" + originalPriority +
                ", senderId='" + senderId + '\'' +
                ", sentTime=" + sentTime +
                ", toSomeBody='" + toSomeBody + '\'' +
                ", bodyLocalizationArgs=" + Arrays.toString(bodyLocalizationArgs) +
                ", bodyLocalizationKey='" + bodyLocalizationKey + '\'' +
                ", color='" + color + '\'' +
                ", defaultLightSettings=" + defaultLightSettings +
                ", defaultSound=" + defaultSound +
                ", defaultVibrateSettings=" + defaultVibrateSettings +
                ", vibrateTimings=" + Arrays.toString(vibrateTimings) +
                ", localOnly=" + localOnly +
                ", visibility=" + visibility +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessagingNotification that = (MessagingNotification) o;
        return notificationId.equals(that.notificationId) &&
                type.equals(that.type);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(notificationId, type);
    }
}
