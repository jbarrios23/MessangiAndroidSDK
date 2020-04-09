package com.messaging.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessagingNotification implements Serializable {

    private Messaging messaging = Messaging.getInst();
    private Context context;


    //For RemoteMessage

    private String collapseKey;
    private Map<String,String> data;
    private String from;
    private String messageId;
    private String messageType;
    private RemoteMessage.Notification notification;
    private int originalPriority;
    private int priority;
    private long sentTime;
    private String to;
    private int ttl;



    private RemoteMessage system;

    public String nameMethod;



    public MessagingNotification(RemoteMessage remoteMessage, Context context) {
     this.nameMethod="MessagingNotification";
     this.context=context;
     this.collapseKey=remoteMessage.getCollapseKey();
     this.data=remoteMessage.getData();
     this.from=remoteMessage.getFrom();
     this.messageId=remoteMessage.getMessageId();
     this.messageType=remoteMessage.getMessageType();
     this.notification=remoteMessage.getNotification();
     this.originalPriority=remoteMessage.getOriginalPriority();
     this.priority=remoteMessage.getPriority();
     this.sentTime=remoteMessage.getSentTime();
     this.to=remoteMessage.getTo();
     this.ttl=remoteMessage.getTtl();
     messaging.utils.showDebugLog(this,nameMethod, data);
     messaging.setLastMessangiNotifiction(this);
     messaging.getMessagingNotifications().add(0,this);
     messaging.utils.showDebugLog(this,nameMethod, messaging.getMessagingNotifications().size());
     sendEventToActivity(this,this.context);

    }

    public MessagingNotification(Bundle extras, Context context) {
        this.context=context;
        this.nameMethod="MessagingNotification";
        boolean send=true;

        if(extras!=null){
            data=new HashMap<>();
            for(String key:extras.keySet()){
                messaging.utils.showDebugLog(this,nameMethod,"Extras received:  Key: " + key + " Value: " + extras.getString(key));
                data.put(key,extras.getString(key));
             if(key.equals("profile")){
                send=false;
                }
            }
            messaging.utils.showDebugLog(this,nameMethod,"Map data generate: " +data);
            if(send) {
                messaging.setLastMessangiNotifiction(this);
                messaging.getMessagingNotifications().add(0, this);
                messaging.messagingStorageController.setNotificationWasDismiss(false);
            }else{
                if(messaging.messagingStorageController.isNotificationWasDismiss()){
                    sendEventToActivity(null,context);
                    messaging.utils.showDebugLog(this,nameMethod,"No data: ");
                }
            }
            }else{
             if(messaging.messagingStorageController.isNotificationWasDismiss()){
                    sendEventToActivity(null,context);
              messaging.utils.showDebugLog(this,nameMethod,"no extras: " +data);

             }
        }

    }

   //method
   public String getCollapseKey() {
       return collapseKey;
   }

    public Map<String, String> getData() {
        return data;
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

    public RemoteMessage.Notification getNotification() {
        return notification;
    }

    public int getOriginalPriority() {
        return originalPriority;
    }

    public int getPriority() {
        return priority;
    }

    public long getSentTime() {
        return sentTime;
    }

    public String getTo() {
        return to;
    }

    public int getTtl() {

        return this.system.getTtl();
    }

    public void writeToParcel (Parcel out, int flags){

    }

    /**
     * Method that send Parameter (Ej: messagingDev or MessagingUserDevice) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(Serializable something, Context context) {
        this.nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

        Intent intent=new Intent("PassDataFromSdk");

        if(something!=null){
        intent.putExtra("message",something);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
        Messaging messaging = Messaging.getInst();
        intent.putExtra("message",something);
        intent.putExtra("DismissNoti",messaging.messagingStorageController.isNotificationWasDismiss());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        }

    }

}
