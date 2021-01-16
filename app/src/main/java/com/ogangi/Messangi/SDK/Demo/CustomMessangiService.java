package com.ogangi.Messangi.SDK.Demo;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;
import com.messaging.sdk.MessagingService;
import com.messaging.sdk.MessagingNotification;

public class CustomMessangiService extends MessagingService {

    public static String CLASS_TAG= CustomMessangiService.class.getSimpleName();
    public static String TAG="CustomMessangiService";


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG,CLASS_TAG+": new token or refresh token "+s);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, CLASS_TAG + ":remote message ");
        //example to custom
        MessagingNotification messagingNotification = new MessagingNotification(remoteMessage);
        Log.d(TAG, CLASS_TAG + ":remote data "+ messagingNotification.getAdditionalData());

    }
}
