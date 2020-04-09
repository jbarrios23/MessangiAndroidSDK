package com.ogangi.Messangi.SDK.Demo;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.messaging.sdk.MessagingFirebaseService;
import com.messaging.sdk.MessagingNotification;

public class CustomMessangiFirebaseService extends MessagingFirebaseService {

    public static String CLASS_TAG= CustomMessangiFirebaseService.class.getSimpleName();
    public static String TAG="MessangiSDK";


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG,CLASS_TAG+": new token or refresh token "+s);

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, CLASS_TAG + ":remote message ");
        //example to custom
        MessagingNotification messagingNotification = new MessagingNotification(remoteMessage, this);
        Log.d(TAG, CLASS_TAG + ":remote data "+ messagingNotification.getData());

    }
}
