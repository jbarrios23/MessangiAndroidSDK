package com.messaging.sdk;

import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;

/**
 * class MessagingService let handle notification push using FirebaseMessagingService .
 *
 */
public class MessagingService extends FirebaseMessagingService  {

    public MessagingStorageController messagingStorageController;
    public Messaging messaging;
    private String nameMethod;

    /**
     * In this method we receive the 'token' of the device.
     * We need it if we are going to communicate with the device directly.
     */

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging = Messaging.getInstance(this);
        messaging.utils.showDebugLog(this,nameMethod,"New Token "+s);
        sendTokenToBackend(s);
    }

    /**
     * Method sendTokenToBackend  allows to take the push token when it is
     * created and save it to update the created device
     * @param tokenPush
     */
    private void sendTokenToBackend(String tokenPush) {
        messagingStorageController = messaging.messagingStorageController;
        messagingStorageController.saveToken(tokenPush);

        if(!messagingStorageController.isNotificationManually()&& messaging.messagingDevice !=null){
            messaging.messagingDevice.setPushToken(tokenPush);
            messaging.messagingDevice.save(this);
        }

    }


    /**
     * Method onMessageReceived  listen the push notification and send to MessagingNotification class
     * @param remoteMessage
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging = Messaging.getInstance(this);
        MessagingNotification messagingNotification=new MessagingNotification(remoteMessage);
        messaging.sendGlobalEventToActivity(Messaging.ACTION_GET_NOTIFICATION, messagingNotification);

    }

}
