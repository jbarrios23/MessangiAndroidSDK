package com.messaging.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;


/**
 * class Messaging let stable Instances, handle services "get" and "create device" and LifecycleObserver
 * for handle event to foreground and background.
 */
public class Messaging implements LifecycleObserver {


    private static Messaging mInstance;
    private Context context;
    public String nameClass;
    public Activity activity;
    public int icon;

    private String pushToken;
    private String email;
    private String type;
    private String model;
    private String os;
    private ArrayList<String> tags;

    MessagingUser messagingUser;
    MessagingDevice messagingDevice;
    public MessagingSdkUtils utils;
    public MessagingStorageController messagingStorageController;
    private String sdkVersion;
    private String language;
    private int identifier;
    private MessagingNotification messagingNotification;
    private String nameMethod;
    private String prvTokenApp;
    private String provHostApp;

    private String packageName;


    public static final String ACTION_REGISTER_DEVICE="com.messaging.sdk.ACTION_REGISTER_DEVICE";
    public static final String ACTION_FETCH_DEVICE="com.messaging.sdk.ACTION_FETCH_DEVICE";
    public static final String ACTION_FETCH_FIELDS="com.messaging.sdk.ACTION_FETCH_FIELDS";
    public static final String ACTION_SAVE_DEVICE="com.messaging.sdk.ACTION_SAVE_DEVICE";
    public static final String ACTION_FETCH_USER="com.messaging.sdk.ACTION_FETCH_USER";
    public static final String FETCH_USER_SUSCRIBER="subscriber";
    public static final String FETCH_FIELDS_COLUMNS="columns";
    public static final String ACTION_SAVE_USER="com.messaging.sdk.ACTION_SAVE_USER";
    public static final String ACTION_GET_NOTIFICATION="com.messaging.sdk.PUSH_NOTIFICATION";
    public static final String ACTION_GET_GEOFENCE_TRANSITION_DETAILS="com.messaging.sdk.GEOFENCE_TRANSITION_DETAILS";
    public static final String ACTION_GET_NOTIFICATION_OPENED="com.messaging.sdk.PUSH_NOTIFICATION_TO_OPEN";
    public static final String ACTION_FETCH_LOCATION="com.messaging.sdk.ACTION_FETCH_LOCATION";
    public static final String ACTION_FETCH_GEOFENCE="com.messaging.sdk.ACTION_FETCH_GEOFENCE";
    public static final String ACTION_GEOFENCE_ENTER="com.messaging.sdk.ACTION_GEOFENCE_ENTER";
    public static final String ACTION_GEOFENCE_EXIT="com.messaging.sdk.ACTION_GEOFENCE_EXIT";


    public static final String INTENT_EXTRA_DATA="messaging_data";
    public static final String INTENT_EXTRA_DATA_FIELD="messaging_data_field";
    public static final String INTENT_EXTRA_HAS_ERROR="messaging_has_error";
    public static final String INTENT_EXTRA_DATA_lAT  = "latitude";
    public static final String INTENT_EXTRA_DATA_lONG  = "longitude";


    public static final String MESSAGING_ID="MSGI_ID";
    public static final String MESSAGING_TYPE="MSGI_TYPE";
    public static final String MESSAGING_TITLE="MSGI_TITLE";
    public static final String MESSAGING_BODY="MSGI_BODY";
    public static final String MESSAGING_APP_ID="MSGI_APPID";
    public static final String MESSAGING_CONFIGURATION="MSGI_CONFIGURATION";
    public static final String MESSAGING_GEOFENCE_PUSH="MSGI_GEOFENCES";
    public static final String MESSAGING_GEOFENCE_SINC="MSGI_GEOFENCES_SINC";
    public static final String MESSAGING_GEO_PUSH="MSGI_GEOPUSH";
    public static final String MESSAGING_PUBLISH_LOGS="MGSI_PUBLISH_LOGS";
    public static final String MESSAGING_APP_TOKEN="appToken";
    public static final String MESSAGING_LOCATION_ENABLE="locationEnable";
    public static final String MESSAGING_ANALYTICS_ENABLE="analyticsEnable";
    public static final String MESSAGING_LOGGING_ENABLE="MSGI_REGISTER_LOGS";
    public static final String MESSAGING_APP_HOST="host";

    public static final String MESSAGING_DEVICE_ID="id";
    public static final String MESSAGING_USER_ID="userId";
    public static final String MESSAGING_PUSH_TOKEN="pushToken";
    public static final String MESSAGING_DEVICE_TYPE="type";
    public static final String MESSAGING_DEVICE_LANGUAGE="language";
    public static final String MESSAGING_DEVICE_MODEL="model";
    public static final String MESSAGING_DEVICE_OS="os";
    public static final String MESSAGING_DEVICE_SDK_VERSION="sdkVersion";
    public static final String MESSAGING_DEVICE_TAGS="tags";
    public static final String MESSAGING_DEVICE_CREATE_AT="createAt";
    public static final String MESSAGING_DEVICE_UPDATE_AT="updateAt";
    public static final String MESSAGING_DEVICE_TIMESTAMP="timestamp";
    public static final String MESSAGING_DEVICE_TRANSACTION="transaction";

    public static final String MESSAGING_NOTIFICATION_OPEN="NOTIFICATION_OPEN";
    public static final String MESSAGING_NOTIFICATION_RECEIVED="NOTIFICATION_RECEIVED";
    public static final String MESSAGING_DEVICE="device";
    public static final String MESSAGING_DATA="data";


    public static String MESSAGING_CUSTOM_EVENT="";
    public static final String MESSAGING_INVALID_DEVICE_LOCATION="INVALID_DEVICE_LOCATION";
    public static final String MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING="Missing_Permission";
    public static final String MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION="Location_Disabled";
    public static final String MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG="Configuration_Disabled";
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;

    public static final String LOCATION_LAT="LOCATION_LAT";
    public static final String LOCATION_LON="LOCATION_LON";
    public static final String LOCATION_PROVIDER="LOCATION_PROVIDER";

    public static final String GOEOFENCE_ID="_id";
    public static final String GOEOFENCE_ID_OTHER="id";
    public static final String GOEOFENCE_LAT="latitude";
    public static final String GOEOFENCE_LONG="longitude";
    public static final String GOEOFENCE_RADIUS="radius";
    public static final String GOEOFENCE_TYPE="type";
    public static final String GOEOFENCE_EXPIRATION="expiration";
    public static final String GOEOFENCE_TYPE_IN="in";
    public static final String GOEOFENCE_TYPE_OUT="out";
    public static final String GOEOFENCE_TYPE_BOTH="both";
    public static final String GOEOFENCE_OPERATION="operation";
    public static final String GOEOFENCE_OPERATION_CREATE="create";
    public static final String GOEOFENCE_OPERATION_UPDATE="update";
    public static final String GOEOFENCE_OPERATION_DELETE="delete";
    public static final long NEVER_EXPIRE = Geofence.NEVER_EXPIRE;

    public static final String GET_GOEOFENCE_ITEMS="items";
    public static final String GET_GOEOFENCE_PAGINATION="pagination";
    public static final String GET_GOEOFENCE_PREV="prev";
    public static final String GET_GOEOFENCE_NEXT="next";



    private static double wayLatitude = 0.0;
    private static double wayLongitude = 0.0;


    private static boolean isContinue;
    private static boolean isGPS = false;
    private static LocationRequest locationRequest;
    private static LocationCallback locationCallback;
    private static FusedLocationProviderClient fusedLocationClient;
//    public static boolean isForeground = false;
//    public static boolean isBackground = false;
    public static boolean isForeground;
    public static boolean isBackground;
    public static boolean flagSinc = false;

    private  PendingIntent geoFencePendingIntent;
    private  final int GEOFENCE_REQ_CODE = 0;

    private GeofencingClient geofencingClient;
    public static boolean enableLocationBackground=false;

    private String notificationIdParameter;



    public enum MessagingLocationPriority{
        PRIORITY_HIGH_ACCURACY(LocationRequest.PRIORITY_HIGH_ACCURACY),
        PRIORITY_BALANCED_POWER_ACCURACY(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
        PRIORITY_LOW_POWER(LocationRequest.PRIORITY_LOW_POWER),
        PRIORITY_NO_POWER(LocationRequest.PRIORITY_NO_POWER);

        private int priority;
        private  MessagingLocationPriority(int priority){
            this.priority=priority;

        }
        public int getPriority() {
            return priority;
        }

    }

    public enum MessagingGeoFenceTrigger{
        ENTER(Geofence.GEOFENCE_TRANSITION_ENTER),
        EXIT(Geofence.GEOFENCE_TRANSITION_EXIT),
        BOTH(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

        private int trigger;
        private  MessagingGeoFenceTrigger(int trigger){
            this.trigger=trigger;

        }
        public int getTrigger() {
            return trigger;
        }

        @Override
        public String toString() {
            if(this==ENTER){
                return "in";
            }else if(this==EXIT){
                return "out";
            }else{
                return "both";
            }

        }
    }

    public Messaging(final Context context){
        this.context =context;
        this.utils=new MessagingSdkUtils();
        this.messagingStorageController =new MessagingStorageController(context,this);
        this.icon=-1;
        this.initResource();
        this.sdkVersion= BuildConfig.VERSION_NAME;
        this.language = Locale.getDefault().getDisplayLanguage();
        this.type="android";
        this.model=getDeviceName();
        this.os=Build.VERSION.RELEASE;
        this.tags=new ArrayList<String>();
        this.messagingUser =null;
        this.messagingDevice =null;
        this.identifier=0;
        this.messagingNotification =null;
        this.nameMethod="";
        this.geofencingClient = LocationServices.getGeofencingClient(this.context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        Messaging.setLocationRequestWithPriority(MessagingLocationPriority.PRIORITY_NO_POWER);
        utils.showDebugLog(this,nameMethod,"Priority "+Messaging.getLocationRequestPriority());

        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    utils.showDebugLog(this,"Messaging","locationResult==null "+locationResult.getLastLocation().toString());
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    utils.showDebugLog(this,"Messaging","locationResult");
                    if(location!=null){
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        MessagingLocation messagingLocation=new MessagingLocation(location);
                        Messaging.saveLastLocationInStorage(location);
                        //sendEventToActivity(ACTION_FETCH_LOCATION,messagingLocation,context);
                        //sendGlobalEventToActivity(ACTION_FETCH_LOCATION,messagingLocation);
                        sendGlobalLocationToActivity(ACTION_FETCH_LOCATION,wayLatitude,wayLongitude);
                        if (!isContinue) {
                        utils.showDebugLog(this,"Messaging","CLat "+wayLatitude+" CLong "+wayLongitude);
                        } else {
                        utils.showDebugLog(this,"Messaging","NCLat "+wayLatitude+" NCLong "+wayLongitude);
                        }
                        utils.showDebugLog(this,nameMethod,"update location "+utils.isLocation_allowed()
                                +" iscontinue "+isContinue);
                        if ((!isContinue && fusedLocationClient != null)||!utils.isLocation_allowed()) {
                            utils.showDebugLog(this,nameMethod,"removeLocationUpdates ");
                            fusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }

                }
            };
        };


    }

    public static synchronized Messaging getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Messaging(context);
        }
        //context = context;
        return mInstance;
    }

    public static synchronized Messaging getInstance() {

        return mInstance;
    }

    /**
     * Method that initializes OnLifecycleEvent
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod,"Foreground");
        if(messagingStorageController.isRegisterDevice()){
            messagingDevice = messagingStorageController.getDevice();
            messagingDevice.checkSdkVersion(context);
            //identifier=1;
            if(!messagingStorageController.isNotificationManually() && messagingStorageController.hasTokenRegister()
                    && messagingDevice.getPushToken().equals("")){
                utils.showInfoLog(this,nameMethod,"Save device with token");
                messagingDevice.setPushToken(messagingStorageController.getToken());
                messagingDevice.save(context);
            }
        }else{
            utils.showErrorLog(this,nameMethod,"Device not found!","");

        }
        if(getLastMessagingNotification()!=null){
            if(messagingNotification.isMatchAppId()) {
                sendEventToActivity(ACTION_GET_NOTIFICATION_OPENED, messagingNotification, context);
                setLastMessagingNotification(null, context);
            }else{
                utils.showInfoLog(this,nameMethod,"Security does not match");
                //Toast.makeText(context,"Security does not match",Toast.LENGTH_LONG).show();
            }
        }
        isForeground=true;
        isBackground=false;

        if(messagingStorageController.hasSincAllowed()==1){
            if(messagingStorageController.isSincAllowed()){
                utils.showInfoLog(this,nameMethod,"Sinc Enable F call service "+flagSinc);
                //launch fetch gofence

                fetchGeofence(true,null);
                flagSinc=false;
                messagingStorageController.setSincAllowed(flagSinc);
            }

        }

        stopServiceLocation();

    }
    /**
     * Method that initializes OnLifecycleEvent
     * EnterBackground
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod,"Background ");
        isBackground=true;
        isForeground=false;
        utils.showDebugLog(this,nameMethod,"Background "+isBackground);
        //si la bandera de location activa y (lectura continua de localizacion || Geofence)
        if(utils.isLocation_allowed() && enableLocationBackground){
            Intent intent = new Intent(context, MessagingLocationService.class);
            context.startForegroundService(intent);
        }

    }

    public void setConfiguration(String scanContent) {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod, "scanContent: " +scanContent);
        String[] prvHandlerMessage=scanContent.split(":%:");
        prvTokenApp=prvHandlerMessage[0];
        provHostApp=prvHandlerMessage[1];

        utils.showDebugLog(this,nameMethod, "Token: " +prvTokenApp+" Host "+provHostApp);
        Messaging.fetchFields(context,prvTokenApp,provHostApp);

    }

    public void reloadSdkParameter(){
        setConfigParameterFromAppToLogin(prvTokenApp,provHostApp);
        if(messagingStorageController.isRegisterDevice()){
            messagingStorageController.saveDevice(null,null,null);
            messagingStorageController.saveUserByDevice(null);
            messagingDevice=null;
            messagingUser=null;
        }
        createDeviceParameters();
    }

    public void sendUserUpdateData(HashMap<String, String> dataInputToSendUser){
        for (Map.Entry<String, String> entry : dataInputToSendUser.entrySet()) {
            messagingUser.addProperty(entry.getKey(),entry.getValue());
        }

        messagingUser.save(context);

    }

    public static LocationRequest setLocationRequestWithPriority(MessagingLocationPriority priority){
        if(locationRequest==null) {
            locationRequest = LocationRequest.create();
        }
        switch (priority){

            case PRIORITY_BALANCED_POWER_ACCURACY:
                locationRequest.setPriority(priority.getPriority());
                locationRequest.setInterval(10 * 60 * 1000);
                locationRequest.setMaxWaitTime(60 * 60 * 1000);
            break;
            case PRIORITY_HIGH_ACCURACY:
                locationRequest.setPriority(priority.getPriority());
                locationRequest.setInterval(1000); // 10 seconds
                locationRequest.setFastestInterval(900); // 5 seconds


            break;
            case PRIORITY_LOW_POWER:
                locationRequest.setPriority(priority.getPriority());
                locationRequest.setInterval(10 * 60 * 1000);
                locationRequest.setMaxWaitTime(60 * 60 * 1000);

            break;
            case PRIORITY_NO_POWER:
                locationRequest.setPriority(priority.getPriority());
                locationRequest.setInterval(15 * 60 * 1000);
                locationRequest.setFastestInterval(2 * 60 * 1000);
        break;
        }

        return locationRequest;
    }

    public static String getLocationRequestPriority(){
        String result = "";
        if(locationRequest!=null){
            if(locationRequest.getPriority()==MessagingLocationPriority.PRIORITY_HIGH_ACCURACY.getPriority()){
                result="PRIORITY_HIGH_ACCURACY";
            }else if(locationRequest.getPriority()==MessagingLocationPriority.PRIORITY_BALANCED_POWER_ACCURACY.getPriority()){
                result="PRIORITY_BALANCED_POWER_ACCURACY";
            }else if(locationRequest.getPriority()==MessagingLocationPriority.PRIORITY_LOW_POWER.getPriority()){
                result="PRIORITY_LOW_POWER";
            }else if(locationRequest.getPriority()==MessagingLocationPriority.PRIORITY_NO_POWER.getPriority()){
                result="PRIORITY_NO_POWER";
            }
            return result ;
        }
        return result;
    }



    public static void fetchLocation(Activity activity,boolean isContinue){

        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging=Messaging.getInstance();
        //messaging.utils.showInfoLog(messaging,nameMethod,"isGPS "+isGPS+" isContinue "+isContinue);

        if (!isGPS) {
            messaging.utils.showDebugLog(messaging,nameMethod,"Please turn on GPS "+isGPS);
            sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION);
            messaging.stopServiceLocation();
            return;
        }
        Messaging.isContinue = isContinue;
        //Messaging.setLocationRequestWithPriority(priority);
        messaging.utils.showDebugLog(messaging,nameMethod,"Priority "+Messaging.getLocationRequestPriority());
        getLastLocation(activity);

    }

    public static void checkSelfPermissions(){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging=Messaging.getInstance();
        if (ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            messaging.utils.showDebugLog(messaging,nameMethod,"has not permission ");
            sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING);
        }
    }

    public static void requestPermissions(Activity activity){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging=Messaging.getInstance();
        if (ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    LOCATION_REQUEST);
                messaging.utils.showDebugLog(messaging,nameMethod,"send event ");
                sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING);
        }
    }

    @SuppressLint("InlinedApi")
    private static void getLastLocation(Activity activity) {
        final String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging=Messaging.getInstance();
        if (ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(activity!=null) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        LOCATION_REQUEST);
            }else{

                messaging.utils.showDebugLog(messaging,nameMethod," Activity null send event ");
                sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING);
            }
        } else {
            if (isContinue) {

                Handler handler = new Handler(Looper.getMainLooper()) {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void handleMessage(Message inputMessage) {
                    messaging.utils.showDebugLog(messaging,nameMethod," Continue Location on ");
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,Looper.getMainLooper());


                    }
                };
                handler.sendEmptyMessage(0);

            } else {
                getCurrentLocation();

            }
        }

    }

    @SuppressLint("MissingPermission")
    private static void getCurrentLocation() {
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
                            MessagingLocation messagingLocation=new MessagingLocation(location);
                            Messaging.saveLastLocationInStorage(location);
                            //messaging.sendEventToActivity(ACTION_FETCH_LOCATION,messagingLocation,messaging.context);
                            //messaging.sendGlobalEventToActivity(ACTION_FETCH_LOCATION,messagingLocation);
                            messaging.sendGlobalLocationToActivity(ACTION_FETCH_LOCATION,wayLatitude,wayLongitude);
                        } else {
                            messaging.utils.showDebugLog(messaging,nameMethod,"requestLocationUpdates ");
                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                        }
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static MessagingNotification checkNotification(Bundle extras){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging = Messaging.getInstance();
        MessagingNotification notification=new MessagingNotification(extras);
        messaging.utils.showInfoLog(messaging,nameMethod,"notification "+notification.toString());
        if(notification!=null) {
            messaging.utils.showInfoLog(messaging,nameMethod,"The Activity was opened as a consequence of a notification");
            sendEventToBackend(Messaging.MESSAGING_NOTIFICATION_OPEN,notification);
        } else {
            messaging.utils.showInfoLog(messaging,nameMethod,"intent.extra does not contain a notification");
        }
        setLastMessagingNotification(notification,messaging.context);
        return notification;
    }

    public static void sendEventCustomToBackend(String snakeCases,String reason){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging = Messaging.getInstance();
        String provSnake = messaging.utils.toUpperSnakeCase(snakeCases);
        messaging.utils.showInfoLog(messaging,nameMethod,
        "MESSAGING_NOTIFICATION_CUSTOM_EVENT "+provSnake);

        if(reason!=null && reason.length()>512) {
            reason = reason.substring(0, 512);
        }

        sendEventToBackend(provSnake,reason);
    }

    public static void sendEventToBackend(String nameEvent,String reason) {
    String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
    final Messaging messaging = Messaging.getInstance();
    String provId = "";
    String provUrl = "";

        if (messaging.messagingDevice != null) {
            provId = messaging.messagingDevice.getId();
        } else {
            provId = messaging.messagingStorageController.getDevice().getId();
        }
        provUrl=messaging.utils.getMessagingHost()+"/devices/"+provId+"/event/"+nameEvent+"?reason="+reason;

        if (messaging.utils.isAnalytics_allowed()) {

            new HttpRequestEvent(provUrl, "GET", nameEvent, null, messaging).execute();
            messaging.utils.showInfoLog(messaging, nameMethod, "isAnalytics_allowed() "
                    + messaging.utils.isAnalytics_allowed());
        } else {
            messaging.utils.showInfoLog(messaging, nameMethod, "isAnalytics_allowed() "
                    + messaging.utils.isAnalytics_allowed());
        }
    }

    public static void sendEventToBackend(String nameEvent,MessagingNotification messagingNotification) {
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging = Messaging.getInstance();
        String provId = "";
        String provUrl = "";

        if(messaging.messagingDevice!=null) {
            provId = messaging.messagingDevice.getId();
        }else{
            provId=messaging.messagingStorageController.getDevice().getId();
        }

        provUrl = messaging.utils.getMessagingHost()+"/devices/"+provId+"/event/"+nameEvent+"?externalId="+messagingNotification.getNotificationId();

        if(messaging.utils.isAnalytics_allowed()) {
            //new HttpRequestEventGet(provId, messaging, nameEvent, reason,typeAction,geofenceId).execute();
            new HttpRequestEvent(provUrl,"GET",nameEvent,null,messaging).execute();
            messaging.utils.showInfoLog(messaging,nameMethod,"isAnalytics_allowed() "
                    +messaging.utils.isAnalytics_allowed());
        }else{
            messaging.utils.showInfoLog(messaging,nameMethod,"isAnalytics_allowed() "
                    +messaging.utils.isAnalytics_allowed());
        }
    }

    public static void sendEventGeofenceToBackend(String typeAction,String geofenceId) {
        final String nameMethod="sendEventGeofenceToBackend";
        final Messaging messaging = Messaging.getInstance();
        String provId = "";

        if(messaging.messagingDevice!=null) {
        provId = messaging.messagingDevice.getId();
        }else{
        provId=messaging.messagingStorageController.getDevice().getId();
        }
        final String provUrl = messaging.utils.getMessagingHost()+"/devices/"+provId+"/region/"+typeAction+"/"+geofenceId;
        if(messaging.utils.isAnalytics_allowed()) {
            //new HttpRequestEventGet(provId, messaging, nameEvent, reason,typeAction,geofenceId).execute();
            new HttpRequestEvent(provUrl,"GET",typeAction,new HttpRequestCallback(){
                @Override
                void onSuccess(Object o) {
                    try {

                        String response=(String)o;
                        messaging.utils.showDebugLog(this,"onSucces","response "+response);
                        if (!response.equals("")) {
                        messaging.utils.showHttpResponseLog(provUrl,this,nameMethod,"Event Successful ",response);

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        messaging.utils.showErrorLog(this, nameMethod, "Get Event error ! NullPointerException ", e.getMessage());

                    }
                }
            },messaging).execute();
            messaging.utils.showInfoLog(messaging,nameMethod,"isAnalytics_allowed() "
                    +messaging.utils.isAnalytics_allowed());
        }else{
            messaging.utils.showInfoLog(messaging,nameMethod,"isAnalytics_allowed() "
                    +messaging.utils.isAnalytics_allowed());
        }
    }

    public static void fetchGeofence(boolean forsecallservice, String next) {
        final Messaging messaging = Messaging.getInstance();
        final MessagingDB db=new MessagingDB(messaging.context);
        final String  nameMethod="fetchGeofence";
        if(forsecallservice){

            String provId = "";
            if(messaging.messagingDevice!=null) {
                provId = messaging.messagingDevice.getId();
            }else{
                provId=messaging.messagingStorageController.getDevice().getId();
            }
            String provUrl= messaging.utils.getMessagingHost() + "/devices/" + provId + "/region";
            if(next!=null && !next.equals("")) {
                 provUrl = provUrl+ "?"+next;
            }
            final String url=provUrl;
            messaging.utils.showDebugLog(messaging,nameMethod,provUrl);
            //new HttpRequestEventGet(provId, messaging, "", "","","").execute();
            final String params="";
            new HttpRequestEvent(url,"GET",params,new HttpRequestCallback(){
                @Override
                void onSuccess(Object o) {
                    try {

                        String response=(String)o;

                        if (!response.equals("")) {

                            messaging.utils.showHttpResponseLog(url,this,nameMethod,"Event Successful ",response);
                            if(params.equals("")){
                                messaging.utils.showDebugLog(this,nameMethod,"Get GeoFences and process "+response);
                                try {
                                    JSONObject jsonObject=new JSONObject(response);
                                    JSONArray jsonArrayItems=jsonObject.getJSONArray(Messaging.GET_GOEOFENCE_ITEMS);
                                    if(jsonObject.has(Messaging.GET_GOEOFENCE_PAGINATION) && jsonObject.getJSONObject(Messaging.GET_GOEOFENCE_PAGINATION)
                                            .has(Messaging.GET_GOEOFENCE_PREV)){
                                       String preProv=jsonObject.getJSONObject(Messaging.GET_GOEOFENCE_PAGINATION)
                                               .getString(Messaging.GET_GOEOFENCE_PREV);
                                       if(preProv.equals("null")){
                                           messaging.utils.deleteGeofenceLocal();
                                       }

                                    }
                                    if(jsonArrayItems.length()>0){
                                        messaging.utils.showDebugLog(this,nameMethod,"save Item to BD "+response);
                                        messaging.utils.processGeofenceList(jsonArrayItems);

                                    }else{
                                        messaging.utils.showDebugLog(this,nameMethod,"Do Not have Items "+response);
                                    }

                                    if(jsonObject.has(Messaging.GET_GOEOFENCE_PAGINATION)){
                                        String provNext=jsonObject.getJSONObject(Messaging.GET_GOEOFENCE_PAGINATION).getString(Messaging.GET_GOEOFENCE_NEXT);

                                        if(!provNext.equals("null")){
                                            messaging.utils.showDebugLog(this,nameMethod,"fetch geofence "
                                                    +jsonObject.getJSONObject(Messaging.GET_GOEOFENCE_PAGINATION)
                                                    .getString(Messaging.GET_GOEOFENCE_NEXT));
                                            //String provNext=jsonObject.getJSONObject("pagination").getString("next");
                                            fetchGeofence(true,provNext);

                                        }else{

                                            if(db.getAllGeoFenceToBd().size()>0 && messaging.utils.isLocation_allowed()) {
                                                messaging.utils.showDebugLog(this,nameMethod,"start geofence ");
                                                messaging.startGeofence();
                                                messaging.sendEventToActivity(Messaging.ACTION_FETCH_GEOFENCE,db.getAllGeoFenceToBd(),messaging.context);
                                            }else{
                                                messaging.utils.showDebugLog(this,nameMethod,"Not start geofence ");
                                            }
                                        }

                                    }else{
                                        messaging.utils.showDebugLog(this,nameMethod,"Do Not have pagination "+response);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        messaging.utils.showErrorLog(this, nameMethod, "Get Event error ! NullPointerException ", e.getMessage());

                    }
                }
            },messaging).execute();

        }else{
            if(db.getAllGeoFenceToBd().size()>0 && messaging.utils.isLocation_allowed()) {
                messaging.sendEventToActivity(Messaging.ACTION_FETCH_GEOFENCE, db.getAllGeoFenceToBd(), messaging.context);
            }else{
                messaging.utils.showErrorLog(messaging, nameMethod, "No data to send ", "");
            }
        }





    }


    public String getPackageName() {
        return packageName;
    }





    public void stopServiceLocation(){
        Intent intent = new Intent(context, MessagingLocationService.class);
        context.stopService(intent);
    }


    /**
     * Method that Get Last Notification
     *
     */

    public MessagingNotification getLastMessagingNotification() {
        return messagingNotification;
    }


    /**
     * Method that Set Las notification
     * @param messagingNotification
     */
    public static void setLastMessagingNotification(MessagingNotification messagingNotification,Context context) {
        final Messaging messaging = Messaging.getInstance(context);
        messaging.messagingNotification = messagingNotification;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    private void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getPushToken() {
        return pushToken;
    }
    /**
     * Method that Set pushToken
     * @param pushToken
     */
    private void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public static void reset() {
        mInstance = null;
    }
    /**
     * Method that Get name of class principal
     */
    public String getNameClass() {
        return nameClass;
    }
    /**
     * Method that Set name class
     * @param nameClass
     */
    private void setNameClass(String nameClass) {
        this.nameClass = nameClass;
    }
    /**
     * Method that Set Firebase topic for use to Backend
     *
     */
    private void setFirebaseTopic(){

        FirebaseMessaging.getInstance().subscribeToTopic("topic_general");
    }
    /**
     * Method that Get External Id of device
     *
     */

    public String getExternalId(){
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return androidId;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    private void setSdkVersion(String sdkVersion) {

        this.sdkVersion = sdkVersion;
    }

    public String getLanguage() {
        return language;
    }

    private void setLanguage(String language) {
        this.language = language;
    }


    /**
     * Method get type of device
     *
     */
    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    /**
     * Method get OS of device
     */
    public String getOs() {

        return os;
    }

    private void setOs(String os) {
        this.os = os;
    }

    void setConfigParameterFromAppToLogin(String token, String Host){
        utils.saveConfigParameterFromApp(token,Host);
    }

    public static void setConfigParameterTokenAndHost(String token, String host){
        Messaging messaging=Messaging.getInstance();
        messaging.utils.saveConfigParameterFromApp(token,host);

    }

    public static void setLocationAllowed(boolean enable){
        Messaging messaging=Messaging.getInstance();
        messaging.utils.setLocation_allowed(enable);

    }

    public static void setAnalytincAllowed(boolean enable){
        Messaging messaging=Messaging.getInstance();
        messaging.utils.setAnalytics_allowed(enable);

    }

    public static void setLogingAllowed(boolean enable){
        Messaging messaging=Messaging.getInstance();
        messaging.utils.setLogging_allowed(enable);

    }


    public void showAnalyticAllowedState(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.isAnalytics_allowed();
        utils.showDebugLog(this,nameMethod,"isLogging_allowed() "+utils.isAnalytics_allowed());
    }

    /**
     * get config parameter from app
     */

    public String getMessagingHost() {

        return utils.getMessagingHost();
    }

    public String getMessagingToken() {

        return utils.getMessagingToken();
    }

    public boolean isAnalytics_allowed() {

        return utils.isAnalytics_allowed();
    }

    public boolean isLocation_allowed() {

        return utils.isLocation_allowed();
    }

    public boolean isLogging_allowed() {

        return utils.isLogging_allowed();
    }

    public boolean isEnable_permission_automatic() {

        return utils.isEnable_permission_automatic();
    }

    public boolean isGPS() {
        return messagingStorageController.isGPSAllowed();
    }

    public void setGPS(boolean GPS) {
        messagingStorageController.setGPSAllowed(GPS);
        isGPS = GPS;
    }
    public static boolean isIsContinue() {
        return isContinue;
    }

    public static void setIsContinue(boolean isContinue) {
        Messaging.isContinue = isContinue;
    }

    public static void turnGPSOff(){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging=Messaging.getInstance();
        messaging.utils.showDebugLog(messaging,nameMethod,"GPS OFF ");
//        LocationManager loc = (LocationManager) messaging.context.getSystemService( Context.LOCATION_SERVICE );
//        if( loc.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) )
//        {
//            Toast.makeText(messaging.context, "Please turn off GPS", Toast.LENGTH_LONG).show();
//            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
//            myIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//            messaging.context.startActivity(myIntent);
//
//        }

        String provider = Settings.Secure.getString(messaging.context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            messaging.utils.showDebugLog(messaging,nameMethod,"GPS OFF 2 ");
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            messaging.context.sendBroadcast(poke);
        }
    }

    public static void turnOFFUpdateLocation(){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging=Messaging.getInstance();
        if(fusedLocationClient!=null) {
            messaging.utils.showDebugLog(messaging,nameMethod,"removeLocationUpdates ");
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public static void saveLastLocationInStorage(Location location){
        Messaging messaging=Messaging.getInstance();
        messaging.messagingStorageController.saveCurrentLocation(location);
    }

    public static Location getLastLocation(){
        Messaging messaging=Messaging.getInstance();
        if(messaging.messagingStorageController.hasLastLocation()){
            return messaging.messagingStorageController.getLastLocationSaved();
        }else{
            return null;
        }

    }


    public boolean isLogged() {
        return messagingStorageController.isRegisterDevice();
    }

    public static void checkGPlayServiceStatus(){
        String nameMethod="GetGPlayServiceStatus";
        Messaging messaging=Messaging.getInstance();
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable( messaging.context );
        messaging.utils.showDebugLog(messaging,nameMethod,"status GOPS "+status);
        if(status == ConnectionResult.SUCCESS) {
            //alarm to go and install Google Play Services
            messaging.utils.showDebugLog(messaging,nameMethod,"yes have Google Play Services "+status);
            //fe_geofences
        }else if(status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
            messaging.utils.showDebugLog(messaging,nameMethod,"please udpate your google play service "+status);
            Toast.makeText(messaging.context,"please udpate your google play service",Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Method for init resources system from config file
     * setMessagingObserver init Observer for foreground and Background event
     * setFirebaseTopic init topic firebase for notification push
     */
    private void initResource(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        setMessagingObserver();
        setFirebaseTopic();
        icon=context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName());
        packageName= context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            try {
                nameClass = launchIntent.getComponent().getClassName();
            }catch (NullPointerException e){
                e.getStackTrace();
            }
        }

        utils.initResourcesConfigFile(context,this);

    }

    /**
     * Method for create device parameter and create device from FirebaseContentProvider
     * this method make update when pushToken is getting by Services
     **/

    public void createDeviceParameters() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        String type=getType();
        utils.showInfoLog(this,nameMethod,"Create device type "+type);
        String language= getLanguage();
        utils.showDebugLog(this,nameMethod,"Create device Language "+language);
        String model=getDeviceName();
        utils.showDebugLog(this,nameMethod,"Create Device model "+model);
        String os = getOs();
        utils.showDebugLog(this,nameMethod,"Create Device  OS "+ os);
        String sdkVersion=getSdkVersion();
        utils.showDebugLog(this,nameMethod,"Create Device SDK version "+ sdkVersion);
        pushToken= messagingStorageController.getToken();
        setPushToken(pushToken);
        createDevice(pushToken,type,language,model,os,sdkVersion);

    }

    /**
     * Method for get icon reference
     */

    public int getIcon() {
    return icon;
    }

    private void setIcon(int icon) {
        this.icon = icon;
    }

    /**
     * Method for get Device name of user
     */

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * Method for set Observer
     */
    private void setMessagingObserver(){
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
    //utils.showInfoLog(this,nameMethod,"setMessagingObserver ");
    }

    /**
     * Method that get Field od QR code registered

     */
    public static void fetchFields(Context context,String appToken, String appHost){
        final Messaging messaging = Messaging.getInstance(context);
        new HttpRequestFieldGet(appHost,appToken,messaging,context).execute();
    }

    /**
     * Method that get Device registered
     @param forsecallservice: It allows effective device search in three ways: by instance, by shared variable or by service.
     */
    public static void fetchDevice(boolean forsecallservice,Context context){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging = Messaging.getInstance(context);
        if(!forsecallservice && messaging.messagingDevice !=null){
            messaging.utils.showDebugLog(messaging,nameMethod,"Device From RAM ");
            messaging.sendEventToActivity(ACTION_FETCH_DEVICE,messaging.messagingDevice,context);

        }else{
            if(!forsecallservice && messaging.messagingStorageController.isRegisterDevice()){
                messaging.messagingDevice = messaging.messagingStorageController.getDevice();
                messaging.utils.showDebugLog(messaging,nameMethod,"Device From Local Storage ");
                messaging.sendEventToActivity(ACTION_FETCH_DEVICE,messaging.messagingDevice,context);
            }else{

                if(messaging.messagingStorageController.isRegisterDevice()){
                    messaging.utils.showDebugLog(messaging,nameMethod,"Device From Service ");
                    messaging.messagingDevice = messaging.messagingStorageController.getDevice();
                    String provId= messaging.messagingDevice.getId();

                    new HttpRequestTaskGet(provId,context,messaging.getPushToken()).execute();
                }else{
                messaging.utils.showErrorLog(messaging,nameMethod,"Device not found! ",null);
                }

            }
        }

    }

    /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     @param something: Object Serializable for send to activity (Ej MessagingDev).
     @param context : context instance
     */
    private void sendEventToActivity(String action, Serializable something, Context context) {

        Intent intent=new Intent(action);
        intent.putExtra(INTENT_EXTRA_DATA,something);
        intent.putExtra(INTENT_EXTRA_HAS_ERROR,something==null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     @param something: Object String for send to activity (Ej MessagingDev).
     @param context : context instance
     */
    private void sendFieldToActivity(String action, String something, Context context) {
        Intent intent=new Intent(action);
        intent.putExtra(INTENT_EXTRA_DATA_FIELD,something);
        intent.putExtra(INTENT_EXTRA_HAS_ERROR,something==null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
    /**
     * Method that send GlobalEventToActivity (Ej: messagingNotification) registered to Activity
     @param something: Object Serializable for send to activity (Ej messagingNotification).
     */
    public void sendGlobalEventToActivity(String action,Serializable something) {

        this.nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod, ""+action
                +"  "+something.toString());
        Intent intent=new Intent(action);
        intent.putExtra(Messaging.INTENT_EXTRA_DATA,something);
        intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR,something==null);
        context.sendBroadcast(intent,context.getPackageName()+".permission.pushReceive");
    }

    /**
     * Method that send GlobalEventToActivity (Ej: messagingNotification) registered to Activity
     @param latitude: 0.00.
     @param longitude: 0.00.
     */
    public void sendGlobalLocationToActivity(String action, double latitude,double longitude) {

        this.nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod, ""+action
                +"  "+latitude+" "+longitude);
        Intent intent=new Intent(action);
        intent.putExtra(Messaging.INTENT_EXTRA_DATA_lAT,latitude);
        intent.putExtra(Messaging.INTENT_EXTRA_DATA_lONG,longitude);
        intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR,(latitude==0.0 || longitude==0.0));
        context.sendBroadcast(intent,getPackageName()+".permission.pushReceive");
    }


    /**
     * Method create Device
     @param pushToken: token for notification push.
     @param type : type device
     @param language : language of device setting
     @param model : model of device
     @param os : operating system version
     @param sdkVersion: SDK version

     */
    private void createDevice(String pushToken, String type, String language,
                              String model, String os, String sdkVersion){

        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final JSONObject requestBody=new JSONObject();
        try {
            requestBody.put(Messaging.MESSAGING_PUSH_TOKEN,pushToken);
            requestBody.put(Messaging.MESSAGING_DEVICE_TYPE,type);
            requestBody.put(Messaging.MESSAGING_DEVICE_LANGUAGE,language);
            requestBody.put(Messaging.MESSAGING_DEVICE_MODEL,model);
            requestBody.put(Messaging.MESSAGING_DEVICE_OS,os);
            requestBody.put(Messaging.MESSAGING_DEVICE_SDK_VERSION,sdkVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HTTPReqTaskPost(requestBody,this).execute();
    }

    private static class HttpRequestTaskGet extends AsyncTask<Void,Void,String> {

        public String provIdDevice;
        private String server_response;
        private String provUrl;
        private Messaging messaging;
        private String nameMethod;
        private Context context;
        private String pushToken;


        public HttpRequestTaskGet(String provId, Context context, String pushToken) {
            this.provIdDevice=provId;
            this.messaging=Messaging.getInstance();
            this.context=context;
            this.pushToken=pushToken;

        }


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {

                String authToken= messaging.utils.getMessagingToken();
                messaging.utils.showDebugLog(this,nameMethod,"Bearer "+authToken);
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                String param ="Bearer "+authToken;
                provUrl= messaging.utils.getMessagingHost()+"/devices/"+provIdDevice;
                messaging.utils.showHttpRequestLog(provUrl,messaging,nameMethod,"GET","");
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    messaging.sendEventToActivity(ACTION_FETCH_DEVICE,null,context);
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,null);
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = messaging.readStream(urlConnection.getInputStream());

                }

            } catch (Exception e) {
                e.printStackTrace();
                messaging.sendEventToActivity(ACTION_FETCH_DEVICE,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Exception ",e.getStackTrace().toString());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try{
                if(!response.equals("")) {
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    messaging.utils.showHttpResponseLog(provUrl,messaging,nameMethod,"Successful",response);
                    JSONObject resp=new JSONObject(response);
                    messaging.utils.showDebugLog(this,nameMethod,"Device "+resp.getJSONObject(Messaging.MESSAGING_DATA));
                    JSONObject tempResp=resp.getJSONObject(Messaging.MESSAGING_DATA);
                    //JSONObject tempRespDef=tempResp.getJSONObject(Messaging.MESSAGING_DEVICE);
                    messaging.messagingDevice =messaging.utils.getMessagingDevFromJsonOnlyResp(tempResp,pushToken);
                    messaging.messagingStorageController.saveDevice(tempResp, "", tempResp);
                    messaging.sendEventToActivity(ACTION_FETCH_DEVICE,messaging.messagingDevice,context);

                }
            }catch (NullPointerException e){
                messaging.sendEventToActivity(ACTION_FETCH_DEVICE,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Device not Get! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                messaging.sendEventToActivity(ACTION_FETCH_DEVICE,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Device not Get! JSONException",e.getStackTrace().toString());
            }
        }
    }

    public String readStream(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();

    }

    private static class HTTPReqTaskPost extends AsyncTask<Void,Void,String>{

        private String server_response;
        private JSONObject provRequestBody;
        private String provUrl;
        private String nameMethod;
        private Messaging messaging;
        public HTTPReqTaskPost(JSONObject requestBody,Messaging messaging) {
            this.provRequestBody=requestBody;
            this.messaging=messaging;
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;

            try {
                String authToken= messaging.utils.getMessagingToken();
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                messaging.utils.showDebugLog(this,nameMethod, "authToken "+authToken);
                JSONObject postData = provRequestBody;
                provUrl= messaging.utils.getMessagingHost()+"/devices/";
                messaging.utils.showHttpRequestLog(provUrl,messaging,nameMethod,"POST",postData.toString());
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setChunkedStreamingMode(0);


                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        out, "UTF-8"));
                writer.write(postData.toString());
                //writer.write(String.valueOf(postData));
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code!=HttpURLConnection.HTTP_CREATED && code!=HttpURLConnection.HTTP_ACCEPTED) {
                    messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,null,messaging.context);
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,"");
                    throw new IOException("Invalid response from server: " + code);
                }else{
                    server_response = messaging.readStream(urlConnection.getInputStream());
                }


            } catch (Exception e) {
                e.printStackTrace();
                messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,null,messaging.context);
                messaging.utils.showErrorLog(this,nameMethod,"Exception: " + e.getMessage(),"");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try{

                    JSONObject resp=new JSONObject(response);
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    messaging.utils.showHttpResponseLog(provUrl,messaging,nameMethod,"Create Device Successful",response);
                    JSONObject temp=resp.getJSONObject(Messaging.MESSAGING_DATA);
                    messaging.messagingStorageController.saveDevice(temp, "",provRequestBody);
                    messaging.messagingDevice=messaging.utils.getMessagingDevFromJson(temp,provRequestBody, "");
                    if(messaging.messagingStorageController.hasTokenRegister()&&
                            !messaging.messagingStorageController.isNotificationManually()){
                        String token= messaging.messagingStorageController.getToken();
                        messaging.messagingDevice.setPushToken(token);
                        messaging.messagingDevice.save(messaging.context);
                    }
            messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,messaging.messagingDevice,messaging.context);


            }catch (NullPointerException e){
                messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,null,messaging.context);
                messaging.utils.showErrorLog(this,nameMethod,"Device not create! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,null,messaging.context);
                messaging.utils.showErrorLog(this,nameMethod,"Device not create! JSONException ",e.getStackTrace().toString());
            }

        }
    }

    /**
     * Method for get User by Device registered from service
     @param context: instance context
     @param forsecallservice : allows effective device search in three ways: by instance, by shared variable or by service.
     */
    public static void fetchUser(final Context context, boolean forsecallservice){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging = Messaging.getInstance(context);
        final MessagingStorageController messagingStorageController = Messaging.getInstance().messagingStorageController;
        if(!forsecallservice && messaging.messagingUser !=null){
            messaging.utils.showDebugLog(messaging,nameMethod,"User From RAM ");
            messaging.sendEventToActivity(ACTION_FETCH_USER,messaging.messagingUser,context);
        }else {
            if (!forsecallservice && messagingStorageController.isRegisterUserByDevice()) {
                messaging.utils.showDebugLog(messaging,nameMethod,"User From Local storage ");
                Map<String, String> resultMap= messagingStorageController.getUserByDevice();
                messaging.messagingUser = MessagingUser.parseData(resultMap) ;
                messaging.sendEventToActivity(ACTION_FETCH_USER,messaging.messagingUser,context);

            } else {
                messaging.utils.showDebugLog(messaging,nameMethod, "User From Service ");
                if(messaging.messagingDevice!=null){
                    new HTTPReqTaskGetUser(messaging.messagingDevice.getId(), messaging,context).execute();
                }

            }
        }

    }
    private static class HTTPReqTaskGetUser extends AsyncTask<Void,Void,String> {

        public String deviceId;
        private String server_response;
        private Messaging messaging;
        private Context context;
        private String provUrl;
        private String nameMethod;

        public HTTPReqTaskGetUser(String deviceId, Messaging messaging, Context context) {
            this.deviceId=deviceId;
            this.messaging = messaging;
            this.context=context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();


                String authToken= messaging.utils.getMessagingToken();
                String param ="Bearer "+authToken;

                //provUrl= messaging.utils.getMessagingHost()+"/users?device="+deviceId;
                provUrl= messaging.utils.getMessagingHost()+"/users/"+deviceId;
                messaging.utils.showHttpRequestLog(provUrl, messaging,nameMethod,"GET","");
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    messaging.sendEventToActivity(ACTION_FETCH_USER,null,context);
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,"");
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = messaging.readStream(urlConnection.getInputStream());

                }

            } catch (Exception e) {
                e.printStackTrace();
                messaging.sendEventToActivity(ACTION_FETCH_USER,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Get User error Exception",e.getStackTrace().toString());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            try{
                if(!response.equals("")) {
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    messaging.utils.showHttpResponseLog(provUrl,this,nameMethod,"Successful",response);
                    JSONObject resp=new JSONObject(response);
                    JSONObject tempResp=resp.getJSONObject(Messaging.MESSAGING_DATA);
                    //JSONObject tempRespDef=tempResp.getJSONObject(Messaging.FETCH_USER_SUSCRIBER);
                    Map<String, String> resultMap=toMap(tempResp);
                    messaging.messagingStorageController.saveUserByDevice(resultMap);
                    messaging.messagingUser = MessagingUser.parseData(resultMap);
                    //messagingUser.id = deviceId;
                    messaging.sendEventToActivity(ACTION_FETCH_USER,messaging.messagingUser, context);

                }
            }catch (NullPointerException e){
                messaging.sendEventToActivity(ACTION_FETCH_USER,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Get error User! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                messaging.sendEventToActivity(ACTION_FETCH_USER,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Get error User! JSONException ",e.getStackTrace().toString());
            }

        }
    }

    public static Map<String, String> toMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            map.put(key, String.valueOf(value));
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    private static class HttpRequestFieldGet extends AsyncTask<Void,Void,String> {

        public String provAppHost;
        private String provAppToken;
        private String nameMethod;
        private String provUrl;
        private String server_response;
        private Messaging messaging;
        @SuppressLint("StaticFieldLeak")
        private Context context;



        public HttpRequestFieldGet(String appHost, String appToken, Messaging messaging,Context context) {
            this.provAppHost = appHost;
            this.provAppToken = appToken;
            this.messaging = messaging;
            this.context=context;

        }


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                String authToken = provAppToken;
                nameMethod = new Object() {
                }.getClass().getEnclosingMethod().getName();
                String param = "Bearer " + authToken;
                provUrl = provAppHost + "/users/fields";
                messaging.utils.showHttpRequestLog(provUrl, messaging,nameMethod,"GET","");
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code != 200) {
                    messaging.sendFieldToActivity(Messaging.ACTION_FETCH_FIELDS,null,context);
                    messaging.utils.showErrorLog(this,nameMethod," Invalid response from server: "+code,"");
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if (code == HttpURLConnection.HTTP_OK) {
                    server_response = messaging.readStream(urlConnection.getInputStream());

                }

            } catch (Exception e) {
                e.printStackTrace();
                messaging.sendFieldToActivity(Messaging.ACTION_FETCH_FIELDS,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Get Fields error Exception",e.getStackTrace().toString());

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                if (!response.equals("")) {
                    nameMethod = new Object() {}.getClass().getEnclosingMethod().getName();
                    messaging.utils.showHttpResponseLog(provUrl,this,nameMethod,"Get Field Successful ",response);
                    messaging.sendFieldToActivity(Messaging.ACTION_FETCH_FIELDS,response,context);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                messaging.sendFieldToActivity(Messaging.ACTION_FETCH_FIELDS,null,context);
                messaging.utils.showErrorLog(this, nameMethod, "Get error Field! NullPointerException ", e.getMessage());

            }
        }
    }



    public static class HttpRequestCallback{
        void onSuccess(Object o){}
        void onFailure(Object o){}
    }

    private static class HttpRequestEvent extends AsyncTask<Void,Void,String> {


        private String provUrl;
        private String httpMethod;
        private Object params;
        private HttpRequestCallback callback;
        private Messaging messaging;
        private String nameMethod;
        private String server_response;
        @SuppressLint("StaticFieldLeak")
        private Context context;

        public HttpRequestEvent(String provUrl, String httpMethod, Object params,
                                HttpRequestCallback callback, Messaging messaging) {
            this.provUrl = provUrl;
            this.httpMethod = httpMethod;
            this.params = params;
            this.callback = callback;
            this.messaging = messaging;

        }


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                String authToken = messaging.utils.getMessagingToken();
                nameMethod = new Object() {
                }.getClass().getEnclosingMethod().getName();
                String param = "Bearer " + authToken;

                messaging.utils.showHttpRequestLog(provUrl, messaging,nameMethod,"GET","");

                if(provUrl==null||!messaging.utils.isValidURL(provUrl)){
                    throw new IOException("Invalid url " );
                }
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod(httpMethod);
                int code = urlConnection.getResponseCode();
                if (code != 200) {
                    messaging.utils.showErrorLog(this,nameMethod," Invalid response from server: "+code,"");
                    callback.onFailure(code);
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if (code == HttpURLConnection.HTTP_OK) {
                    server_response = messaging.readStream(urlConnection.getInputStream());

                }

            } catch (Exception e) {
                e.printStackTrace();
                messaging.utils.showErrorLog(this,nameMethod,"Event error Exception",e.getStackTrace().toString());

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (callback!=null){
                callback.onSuccess(response);
                messaging.utils.showDebugLog(this,"onPostExecute ","callback.onSuccess(response)");
            }else{
                try {
                    JSONObject resp=new JSONObject(response);
                    JSONObject temp=resp.getJSONObject(Messaging.MESSAGING_DATA);
                    messaging.utils.showDebugLog(this,"onPostExecute ","Status "+temp.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Geofence stuff

    public static void deteAllBD(){
        Messaging messaging=Messaging.getInstance();
        MessagingDB db=new MessagingDB(messaging.context);
        db.deleteAll();
        db.getAllGeoFenceToBd();

    }

    public static void re_registerGeofence(){
        final Messaging messaging= Messaging.getInstance();
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        //fetchGeofence(true);
        if(messaging.utils.isLocation_allowed()) {
            MessagingDB db=new MessagingDB(messaging.context);
            if(db.getAllGeoFenceToBd().size()>0) {
                messaging.startGeofence();
                messaging.utils.showDebugLog(messaging, nameMethod, "Re-Register Geofence");
            }else{

                messaging.utils.showDebugLog(messaging, nameMethod, "Not Geofence in dB");
            }
        }else{
            messaging.utils.showDebugLog(messaging, nameMethod, "Location Not Enable");
        }

    }

    // Start Geofence creation process
    void startGeofence() {
    String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
    final Messaging messaging= Messaging.getInstance();
    messaging.utils.showDebugLog(messaging,nameMethod,"Register Geofence");

        ArrayList< MessagingCircularRegion> provMessagingCircularRegions;
        MessagingDB db=new MessagingDB(context);
        provMessagingCircularRegions=db.getAllGeoFenceToBd();
        messaging.utils.showDebugLog(messaging,nameMethod,"GF from DB Before validate "+provMessagingCircularRegions);
        //validate geofence To register
        if(messaging.messagingStorageController.hasLastLocation()) {
            final Location provLocation=messaging.messagingStorageController.getLastLocationSaved();
            Collections.sort(provMessagingCircularRegions,new Comparator<MessagingCircularRegion>() {
                @Override
                public int compare(MessagingCircularRegion o1, MessagingCircularRegion o2) {
                    Location location1=new Location(LOCATION_SERVICE);
                    location1.setLatitude(o1.getLatitude());
                    location1.setLongitude(o1.getLongitud());
                    double dist1=provLocation.distanceTo(location1);
                    Location location2=new Location(LOCATION_SERVICE);
                    location2.setLatitude(o2.getLatitude());
                    location2.setLongitude(o2.getLongitud());
                    double dist2=provLocation.distanceTo(location2);
                    if(dist1<dist2){
                        return -1;
                    }else if(dist1>dist2){
                        return 1;
                    }else{
                        return 0;
                    }

                }
            });
        }
        messaging.utils.showDebugLog(messaging,nameMethod,"GF from DB Sorter "+provMessagingCircularRegions);
        List<Geofence> geofencesToAdd = new ArrayList<>();
        for(MessagingCircularRegion messagingCircularRegion:provMessagingCircularRegions){
            Geofence geofence =messagingCircularRegion.getGeofence();
            messaging.utils.showDebugLog(messaging,nameMethod,"GF to add: "+geofence.toString());
            geofencesToAdd.add(geofence);
            if(geofencesToAdd.size()==100){
            break;
            }

        }
        GeofencingRequest geofenceRequest = createGeofenceRequest( geofencesToAdd );
        addGeofence(geofenceRequest);

    }

    //Create a Geofence Request

    GeofencingRequest createGeofenceRequest(List<Geofence> geofences ) {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod,"createGeofenceRequest "+geofences.toString());

        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofences( geofences )
                .build();
    }

    // Add the created GeofenceRequest to the device's monitoring list

    void addGeofence(GeofencingRequest request) {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            utils.showDebugLog(this,nameMethod," Dont have permission for Geofence add ");
            sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING);

        }else{

            geofencingClient.addGeofences(request,createGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                            utils.showDebugLog(this,nameMethod," Add Geofence ");


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    utils.showErrorLog(this,nameMethod," Add Geofence "+e.getMessage(),"");

                }
            });
        }

    }

    void stopGeofenceSupervition(){

        geofencingClient.removeGeofences(createGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                utils.showDebugLog(this,nameMethod,"onSuccess");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to remove geofences
                // ...
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                utils.showDebugLog(this,nameMethod,"onFailure "+e.getMessage());
            }
        });

    }

    void removeGeofence(List<String> listIds){
        nameMethod="removeGeofence";
        utils.showDebugLog(this,nameMethod,"removeGeofence "+listIds.toString());
        geofencingClient.removeGeofences(listIds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                        utils.showDebugLog(this,nameMethod,"onSuccess removeGeofence ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                        utils.showDebugLog(this,nameMethod,"onFailure removeGeofence "+e.getMessage());
                    }
                });

    }

    PendingIntent createGeofencePendingIntent() {
        nameMethod="createGeofencePendingIntent";
        if ( geoFencePendingIntent != null ){
            return geoFencePendingIntent;
        }
        //Intent intent = new Intent(action);
        Intent intent = new Intent(context, MessaginGeofenceBroadcastReceiver.class);
        utils.showDebugLog(this,nameMethod,"Intent "+intent);
        geoFencePendingIntent=PendingIntent.getBroadcast(
                context, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        return geoFencePendingIntent;
    }


    public static String getLocat(){
        Messaging messaging=Messaging.getInstance();
        String nameMethod="getLocat";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String processId = Integer.toString(android.os.Process.myPid());
            //String[] command = new String[] { "logcat", "-d", "-v", "threadtime" };
            String[] command = new String[] { "logcat", "-d", "-v", "MESSAGING" };
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(processId)) {
                    stringBuilder.append(line);

                }
            }
            //data.setText(stringBuilder.toString());
            messaging.utils.showDebugLog(messaging,nameMethod,stringBuilder.toString());

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


}
