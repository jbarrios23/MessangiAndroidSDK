package com.messaging.sdk;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;


@SuppressLint("NewApi")
public class NotificationListener extends NotificationListenerService {
    public static String CLASS_TAG=NotificationListener.class.getSimpleName();
    public static String TAG="MESSANGING";
    private String nameMethod;
    private RemoteMessage remoteMessage;
    private Messaging messaging;
    private String myPackage;
    private Map<String,String> data;

    @Override
    public void onCreate() {
        super.onCreate();
        messaging=Messaging.getInst();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        myPackage = messaging.getPackageName();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            super.onNotificationPosted(sbn);
        }

        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(sbn.getPackageName().equals(myPackage)) {


            messaging.utils.showInfoLog(this,nameMethod,sbn.getNotification().deleteIntent);
            Bundle extras=sbn.getNotification().extras;
            for(String key:extras.keySet()){
//                messaging.utils.showDebugLog(this,nameMethod,"Extras received:  Key: "
//                        + key + " Value: " + extras.getString(key));
                if(extras.getString(key) !=null) {
                    data.put(key, extras.getString(key));
                }

            }
            messaging.utils.showInfoLog(this,nameMethod,"Map data generate: " +data);
            PendingIntent pendingIntent=sbn.getNotification().deleteIntent;
            messaging.messagingStorageController.setNotificationWasDismiss(true);
            messaging.messagingStorageController.saveDataNotification(data);

        }


    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            super.onNotificationRemoved(sbn);
        }
        data=new HashMap<>();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(sbn.getPackageName().equals(myPackage)) {
            Bundle extras=sbn.getNotification().extras;
            for(String key:extras.keySet()){
//                messaging.utils.showDebugLog(this,nameMethod,"Extras received:  Key: "
//                        + key + " Value: " + extras.getString(key));
                if(extras.getString(key) !=null) {
                    data.put(key, extras.getString(key));
                }

            }
            messaging.utils.showInfoLog(this,nameMethod,"Map data generate: " +data);
            PendingIntent pendingIntent=sbn.getNotification().deleteIntent;
            messaging.messagingStorageController.setNotificationWasDismiss(true);
            messaging.messagingStorageController.saveDataNotification(data);

        }

    }
}
