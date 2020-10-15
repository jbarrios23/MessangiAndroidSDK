package com.messaging.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import static com.messaging.sdk.Messaging.getLocationRequestPriority;


public class MessagingLocationService extends Service {
    public Messaging messaging;
    private String nameMethod;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private Timer mTimer = null;




    public MessagingLocationService() {
        this.messaging=Messaging.getInstance();
        this.nameMethod="MessagingLocationService";
        messaging.utils.showDebugLog(this,nameMethod,"Constructor ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.nameMethod="onCreate";

        mTimer=new Timer();
        //mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
        messaging.utils.showDebugLog(this,nameMethod,"Create Service");
        messaging.utils.showDebugLog(this,nameMethod,"Priority "+getLocationRequestPriority());
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //isGPS=true;
            messaging.setGPS(true);
        }else{
            //isGPS=false;
            messaging.setGPS(false);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.nameMethod="onStartCommand";
        messaging.utils.showDebugLog(MessagingLocationService.this,nameMethod,""+messaging.getNameClass());
        createNotificationChannel();
        String classNameProv="com.ogangi.Messangi.SDK.Demo.MapsActivity";
        Intent notificationIntent = null;
        try {
            notificationIntent = new Intent(this, Class.forName(classNameProv));
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Messaging SDK")
                    .setContentText("Demo location on ")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
        e.printStackTrace();

        notificationIntent = new Intent("android.intent.action.MAIN");
    }
    //fetchLocation(true, Messaging.MessagingLocationPriority.PRIORITY_HIGH_ACCURACY);
    Messaging.fetchLocation(null,true, Messaging.MessagingLocationPriority.PRIORITY_BALANCED_POWER_ACCURACY);
    //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        nameMethod="onDestroy";
        messaging.utils.showDebugLog(this,nameMethod,"Destroy Service");
    }


}
