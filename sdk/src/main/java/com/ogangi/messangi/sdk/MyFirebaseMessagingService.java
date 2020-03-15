package com.ogangi.messangi.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MyFirebaseMessagingService extends FirebaseMessagingService  {


    private NotificationManager notificationManager;
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    public String body ;
    public String title ;
    public String icon;
    public String nameClass;
    public static String CLASS_TAG=MyFirebaseMessagingService.class.getSimpleName();
    public StorageController storageController;
    public Messangi messangi;
    public Activity activity;


    @Override
    public void onNewToken(String s) {
        /*
            En este método recibimos el 'token' del dispositivo.
            Lo necesitamos si vamos a comunicarnos con el dispositivo directamente.
        */
        super.onNewToken(s);
        Log.e("NEW_TOKEN FOR SEND",s);


        try {
            Thread.sleep(3000);
            sendTokenToBackend(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
    private void sendTokenToBackend(String s) {
        messangi = Messangi.getInstance(this);
        Log.e(CLASS_TAG,"SEND TOKEN TO BACKEND "+s);
        storageController=StorageController.getInstance(this);
        storageController.saveToken("Token",s);
        //createParameters();
        messangi.createDeviceParameters();

    }



    @SuppressLint("PrivateApi")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // En este método recibimos el mensaje
        //verifiPermission();
        messangi = Messangi.getInstance(this);
        nameClass= messangi.getNameclass();
        Log.e(CLASS_TAG, "NOMBRE DE LA CLASE " + nameClass );

        try {
            Log.e(CLASS_TAG, "EST MENS " + remoteMessage.getData() );
            body = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();
            icon = remoteMessage.getNotification().getIcon();

            Log.e(CLASS_TAG, "MENSAJE IN " + body);
            Log.e(CLASS_TAG, "TITULO IN " + title);
            Log.e(CLASS_TAG, "IMAGE IN " + title);

        }catch (NullPointerException e){
            Log.e(CLASS_TAG,"error "+e.getMessage());
            body = remoteMessage.getData().get("message");
            title = remoteMessage.getData().get("title");
            icon = remoteMessage.getData().get("image");
            Log.e(CLASS_TAG, "MENSAJE " + body);
            Log.e(CLASS_TAG, "TITULO " + title);
            Log.e(CLASS_TAG, "IMAGE " + icon);

        }
        Intent notificationIntent=null;

        try {
            //accion por defecto
            notificationIntent = new Intent(this,Class.forName(nameClass));
            Log.e(CLASS_TAG, "PIRMERO " );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e(CLASS_TAG, "PIRMERO error " );
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.e(CLASS_TAG, "PIRMERO error null " );
            notificationIntent = new Intent("android.intent.action.MAIN");
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Configuramos la notificación para Android Oreo o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);
        // Creamos la notificación en si
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(messangi.getIcon())  //a resource for your custom small icon
                .setContentTitle(title) //the "title" value you sent in your notification
                .setContentText(body) //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}
