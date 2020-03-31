package com.ogangi.Messangi.SDK.Demo;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.ogangi.messangi.sdk.MessangiFirebaseMessagingService;
import com.ogangi.messangi.sdk.MessangiNotification;

public class CustomMessangiService extends MessangiFirebaseMessagingService {

    public static String CLASS_TAG= CustomMessangiService.class.getSimpleName();
    public static String TAG="MessangiSDK";
    private String body ;
    private String title ;
    private String icon;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG,CLASS_TAG+": new token or refresh token "+s);

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, CLASS_TAG + ": remote message ");
        //example
        MessangiNotification messangiNotification = new MessangiNotification(remoteMessage, this);
    }
}
