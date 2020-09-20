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

public class MessagingLocationService extends Service {
    public Messaging messaging;
    private String nameMethod;
    private FusedLocationProviderClient fusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Timer mTimer = null;
    private long notify_interval = 5000;
    private static boolean isContinue;
    private static boolean isGPS ;


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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mTimer=new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
        messaging.utils.showDebugLog(this,nameMethod,"Create Service");

//        locationRequest=LocationRequest.create();
//        locationRequest=setLocationRequestWithPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        messaging.utils.showDebugLog(this,nameMethod,"Priority "+getLocationRequestPriority());

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isGPS=true;
        }else{
            isGPS=false;
        }


        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    messaging.utils.showDebugLog(this,nameMethod,"locationResult == null ");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    if(location!=null){
                        nameMethod="onLocationResult Service";
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        //MessagingLocation messagingLocation=new MessagingLocation(location);
                        sendGlobalEventToActivity(Messaging.ACTION_FETCH_LOCATION,wayLatitude,wayLongitude);
                        if (!Messaging.isIsContinue()) {
                            messaging.utils.showDebugLog(this,nameMethod," CLat "+wayLatitude+" CLong "+wayLongitude);
                        } else {
                            messaging.utils.showDebugLog(this,nameMethod," CLat "+wayLatitude+" CLong "+wayLongitude);

                        }
                        messaging.utils.showInfoLog(this,nameMethod," isContinue "+isContinue+" isLocation_allowed() "+messaging.utils.isLocation_allowed());
                        if ((!isContinue && fusedLocationClient != null)||!messaging.utils.isLocation_allowed()) {
                            messaging.utils.showDebugLog(this,nameMethod,"removeLocationUpdates ");
                            fusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            };
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.nameMethod="onStartCommand";
        messaging.utils.showDebugLog(MessagingLocationService.this,nameMethod,"");
        createNotificationChannel();
        Intent notificationIntent = null;
        try {
            notificationIntent = new Intent(this, Class.forName(messaging.getNameClass()));
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Messaging SDK")
                    .setContentText("ServiceLocation on ")
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
    fetchLocation(true,LocationRequest.PRIORITY_HIGH_ACCURACY);
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

    public void fetchLocation(boolean isContinue, int priority){

        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

        messaging.utils.showInfoLog(this,nameMethod,"isGPS "+isGPS+" isContinue "+isContinue);

        if (!isGPS) {
            messaging.utils.showDebugLog(this,nameMethod,"Please turn on GPS "+isGPS);
            Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION);
            stopSelf();
            return;
        }
        this.isContinue = isContinue;
        setLocationRequestWithPriority(priority);
        messaging.utils.showDebugLog(this,nameMethod,"Priority "+getLocationRequestPriority());
        getLastLocation();

    }
    @SuppressLint("MissingPermission")
    private  void getLastLocation() {
        if (isContinue) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
            getCurrentLocation();
            }


    }

    @SuppressLint("MissingPermission")
    private  void getCurrentLocation() {
        final Messaging messaging=Messaging.getInstance();
        final String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            messaging.utils.showDebugLog(messaging,nameMethod," Lat "+wayLatitude+" Long "+wayLongitude);
                            //MessagingLocation messagingLocation=new MessagingLocation(location);
                            sendGlobalEventToActivity(Messaging.ACTION_FETCH_LOCATION,wayLatitude,wayLongitude);

                        } else {
                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    }
                });

    }

    /**
     * Method that send GlobalEventToActivity (Ej: messagingNotification) registered to Activity
     @param latitude: 0.00.
     @param longitude: 0.00.
     */
    public void sendGlobalEventToActivity(String action, double latitude,double longitude) {

        this.nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod, ""+action
                +"  "+latitude+" "+longitude);
        Intent intent=new Intent(action);
        intent.putExtra(Messaging.INTENT_EXTRA_DATA_lAT,latitude);
        intent.putExtra(Messaging.INTENT_EXTRA_DATA_lONG,longitude);
        intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR,(latitude==0.0 || longitude==0.0));
        sendBroadcast(intent,getPackageName()+".permission.pushReceive");
    }

    public LocationRequest setLocationRequestWithPriority(int priority){
        locationRequest = LocationRequest.create();
        switch (priority){

            case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                locationRequest.setInterval(10 * 60 * 1000);
                locationRequest.setMaxWaitTime(60 * 60 * 1000);
                break;
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(10 * 1000); // 10 seconds
                locationRequest.setFastestInterval(5 * 1000); // 5 seconds

                break;
            case LocationRequest.PRIORITY_LOW_POWER:
                locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                locationRequest.setInterval(10 * 60 * 1000);
                locationRequest.setMaxWaitTime(60 * 60 * 1000);

                break;
            case LocationRequest.PRIORITY_NO_POWER:
                locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
                locationRequest.setInterval(15 * 60 * 1000);
                locationRequest.setFastestInterval(2 * 60 * 1000);
                break;
        }

        return locationRequest;
    }

    public  String getLocationRequestPriority(){
        String result = "";
        if(locationRequest!=null){
            if(locationRequest.getPriority()==100){
                result="PRIORITY_HIGH_ACCURACY";
            }else if(locationRequest.getPriority()==102){
                result="PRIORITY_BALANCED_POWER_ACCURACY";
            }else if(locationRequest.getPriority()==104){
                result="PRIORITY_LOW_POWER";
            }else if(locationRequest.getPriority()==105){
                result="PRIORITY_NO_POWER";
            }
            return result ;
        }
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nameMethod="onDestroy";
        if(fusedLocationClient!=null){
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        messaging.utils.showDebugLog(this,nameMethod,"Destroy Service");
    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

        }
    }
}
