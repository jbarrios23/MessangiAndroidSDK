package com.messaging.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
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
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;


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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


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
    public static final String MESSAGING_PUBLISH_LOGS_KEY="log";

    public static final String MESSAGING_NOTIFICATION_OPEN="NOTIFICATION_OPEN";
    public static final String MESSAGING_NOTIFICATION_RECEIVED="NOTIFICATION_RECEIVED";
    public static final String MESSAGING_GEOPUSH_PROCESS="GEOPUSH_PROCESS";
    public static final String MESSAGING_GEOPUSH_NO_PROCESS="GEOPUSH_NOT_PROCESS";
    public static final String MESSAGING_DEVICE="device";
    public static final String MESSAGING_DATA="data";


    public static String MESSAGING_CUSTOM_EVENT="";
    public static final String MESSAGING_INVALID_DEVICE_LOCATION="INVALID_DEVICE_LOCATION";
    public static final String MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING="Missing_Permission";
    public static final String MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION="Location_Disabled";
    public static final String MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG="Configuration_Disabled";
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;
    public static final int MAXIMUM_NUMBER_OF_MONITORED_REGIONS = 99;

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
    public static final String GOEOFENCE_MONITORING="monitoring";
    public static final String GOEOFENCE_AFTER_DELETE="afterDelete";
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

    public static boolean isForeground;
    public static boolean isBackground;
    public static boolean flagSinc = false;

    private  PendingIntent geoFencePendingIntent;
    private  final int GEOFENCE_REQ_CODE = 0;

    private GeofencingClient geofencingClient;
    public static boolean enableLocationBackground=false;

    private String notificationIdParameter;

    ArrayList<MessagingCircularRegion> nearestRegions;
    ArrayList<MessagingCircularRegion> provMessagingCircularRegionsMonitoring;
    ArrayList<MessagingCircularRegion> regionsToDelete;
    ArrayList<MessagingCircularRegion> regionsToAdd;



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

        //locationManager
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
     * Method of initializes OnLifecycleEvent
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod,"Foreground");
        if(messagingStorageController.isRegisterDevice()){
            messagingDevice = messagingStorageController.getDevice();
            messagingDevice.checkSdkVersion(context);

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

            }
        }
        isForeground=true;
        isBackground=false;

        if(messagingStorageController.hasSincAllowed()==1){
            if(messagingStorageController.isSincAllowed()){
                utils.showInfoLog(this,nameMethod,"Sinc Enable F call service "+flagSinc);
                //launch fetch gofence
                fetchGeofence(true,null,
                        messagingStorageController.getMessagingNotificationId());
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

    /**
     * Method to set Configuration From Scan QR from LoginActivity
     * @param scanContent: text with values scan from LoginActivity
     */
    public static void setConfigurationFromScan(String scanContent) {
        Messaging messaging=Messaging.getInstance();
        messaging.setConfiguration(scanContent);
    }

    /**
     * Method to setConfiguration from LoginActivity
     * @param scanContent: text with values scan from LoginActivity
     */
    public void setConfiguration(String scanContent) {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod, "scanContent: " +scanContent);
        String[] prvHandlerMessage=scanContent.split(":%:");
        prvTokenApp=prvHandlerMessage[0];
        provHostApp=prvHandlerMessage[1];

        utils.showDebugLog(this,nameMethod, "Token: " +prvTokenApp+" Host "+provHostApp);
        Messaging.fetchFields(context,prvTokenApp,provHostApp);

    }
    /**
     * Method to reloadSdkParameter To LoginActivity, delete previus device and create new
     * using createDeviceParameters method
     */
    public void resetAndCreateDevice(){
        Messaging messaging=Messaging.getInstance();
        setConfigParameterFromAppToLogin(prvTokenApp,provHostApp);
        resetDevice();
        createDeviceParameters();
    }

    /**
     * Method to resetDevice, delete previus device of data local
     *
     */
    public static void resetDevice(){
        Messaging messaging=Messaging.getInstance();
        if(messaging.messagingStorageController.isRegisterDevice()){
            messaging.messagingStorageController.saveDevice(null,null,null);
            messaging.messagingStorageController.saveUserByDevice(null);
            messaging.messagingDevice=null;
            messaging.messagingUser=null;
            messaging.utils
                    .showInfoLog(messaging,"resetAndCreateDevice","reset device");
        }
    }


    /**
     * Method to resetAndCreateDeviceNew From LoginActivity, delete previus device and create new
     * using createDeviceParameters method
     */
    public static void resetAndCreateDeviceNew(){
        Messaging messaging=Messaging.getInstance();
        logOutProcess();
        messaging.resetAndCreateDevice();
    }

    /**
     * Method to sendUserUpdateData from LoginActivity only if it's necessary
     *
     */
    public void sendUserUpdateData(HashMap<String, String> dataInputToSendUser){
        for (Map.Entry<String, String> entry : dataInputToSendUser.entrySet()) {
            messagingUser.addProperty(entry.getKey(),entry.getValue());
        }
        messagingUser.save(context);
    }

    /**
     * Method to sendUserUpdateData from LoginActivity only if it's necessary
     *
     */
    public static void sendUserUpdateDataFromLogin(HashMap<String, String> dataInputToSendUser){
        Messaging messaging=Messaging.getInstance();
        messaging.sendUserUpdateData(dataInputToSendUser);
    }

    /**
     *  Method to setLocationRequestWithPriority from AnyActivity
     *  With this method, the priority of the geolocation is established and it is
     *  differentiated by the time in which the location is requested.
     */
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

    /**
     *  Method to getLocationRequestWithPriority from AnyActivity or AnyClass
     *  With this method, the priority of the geolocation is observed at any time.
     */
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


    /**
     * Method to fetchLocation from AnyActivity or AnyClass
     * @param activity: context from make the fetch.
     * @param isContinue: continuously or by a single shot.
     */
    public static void fetchLocation(Activity activity,boolean isContinue){

        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging=Messaging.getInstance();
        //messaging.utils.showInfoLog(messaging,nameMethod,"isGPS "+isGPS+" isContinue "+isContinue);

        if (!isGPS) {
            messaging.utils.showDebugLog(messaging,nameMethod,"Please turn on GPS "+isGPS);
            sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION, "");
            messaging.stopServiceLocation();
            return;
        }
        Messaging.isContinue = isContinue;
        //Messaging.setLocationRequestWithPriority(priority);
        messaging.utils.showDebugLog(messaging,nameMethod,"Priority "+Messaging.getLocationRequestPriority());
        getLastLocation(activity);

    }
    /**
     * Method to checkSelfPermissions from AnyActivity or AnyClass
     *
     */
    public static void checkSelfPermissions(){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging=Messaging.getInstance();
        if (ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            messaging.utils.showDebugLog(messaging,nameMethod,"has not permission ");
            sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING, "");
        }
    }
    /**
     * Method to requestPermissions from AnyActivity or AnyClass
     * @param activity: context from make the fetch.
     * */
    public static void requestPermissions(Activity activity){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging=Messaging.getInstance();
        if (ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            messaging.utils.showDebugLog(messaging,nameMethod,"Check permisse ");
            showProminentDisclosureLocation(activity,messaging);
            sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING, "");
        }else{
            messaging.utils.showDebugLog(messaging,nameMethod,"Permission Granted ");
            Toast.makeText(activity,"PERMISSION_GRANTED",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Method to getLastLocation from AnyActivity or AnyClass
     * @param activity: context from make the getLastLocation.
     * */
    @SuppressLint("InlinedApi")
    private static void getLastLocation(final Activity activity) {

        final String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging=Messaging.getInstance();

        if (ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(messaging.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(activity!=null) {
                showProminentDisclosureLocation(activity,messaging);
            }else{
                messaging.utils.showDebugLog(messaging,nameMethod," Activity null send event ");
                sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING, "");
                messaging.stopServiceLocation();
            }

        }else{
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
    /**
     * Method to requestPermissionLocation from this Class
     * @param activity :Activity context
     * */
    private static void requestPermissionLocation(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                LOCATION_REQUEST);
    }
    /**
     * Method to show Prominent Disclosure Location from this Class
     * @param activity :Activity context
     * @param messaging*/
    private static void showProminentDisclosureLocation(final Activity activity, final Messaging messaging) {
        final String nameMethod="showProminentDisclosureLocation";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
                .setCancelable(false);
        dialogBuilder.setTitle(activity.getResources().getString(R.string.location_acces))
                .setMessage(activity.getResources().getString(R.string.location_text))
                .setIcon(R.drawable.locationicon)
                .setPositiveButton(activity.getResources().getString(R.string.location_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        messaging.utils.showDebugLog(messaging, nameMethod, " requestPermissionLocation ");
                        Messaging.requestPermissionLocation(activity);
                    }
                })
                .setNegativeButton(activity.getResources().getString(R.string.location_not), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            messaging.sendEventToActivity(ACTION_FETCH_LOCATION,null,messaging.context);
                    }
                });
        dialogBuilder.show();
    }
    /**
     * Method to getCurrentLocation from AnyClass
     *
     * */
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
                            messaging.sendGlobalLocationToActivity(ACTION_FETCH_LOCATION,wayLatitude,wayLongitude);
                        } else {
                            messaging.utils.showDebugLog(messaging,nameMethod,"requestLocationUpdates ");
                            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                        }
                    }
                });

    }
    /**
     * Method to checkNotification from AnyActivity
     * @param extras : data of Notification
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static MessagingNotification checkNotification(Bundle extras){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging = Messaging.getInstance();
        MessagingNotification notification=new MessagingNotification(extras);
        messaging.utils.showInfoLog(messaging,nameMethod,"notification "+notification.toString());
        try {
            if (notification != null && !notification.getTitle().equals("")
                    && (!notification.getBody().equals("") && notification.getBody() != null)
                    && notification.getAdditionalData().size() > 0) {

                messaging.utils.showInfoLog(messaging, nameMethod, "The Activity was opened as a consequence of a notification");
                sendEventToBackend(Messaging.MESSAGING_NOTIFICATION_OPEN, notification);
                sendEventToBackend(Messaging.MESSAGING_NOTIFICATION_RECEIVED, notification);
                setLastMessagingNotification(notification, messaging.context);
            } else {
                setLastMessagingNotification(null, messaging.context);
                messaging.utils.showInfoLog(messaging, nameMethod, "intent.extra does not contain a notification");
            }
        }catch (NullPointerException e){
            messaging.utils.showErrorLog(messaging,nameMethod,"error ",e.getMessage());
            //for Geopush handle push from background mode.
            messaging.utils.showInfoLog(messaging, nameMethod, "The Activity was opened as a consequence of a notification");
            sendEventToBackend(Messaging.MESSAGING_NOTIFICATION_OPEN, notification);
            sendEventToBackend(Messaging.MESSAGING_NOTIFICATION_RECEIVED, notification);
            setLastMessagingNotification(notification, messaging.context);
        }

        return notification;
    }
    /**
     * Method to sendEventCustom from AnyActivity or Class
     * @param snakeCases : key for event custom.
     * @param reason : cause of event custom.
     * */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void sendEventCustom(String snakeCases, String reason){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging = Messaging.getInstance();
        String provSnake = messaging.utils.toUpperSnakeCase(snakeCases);
        String provReason="";
        if(reason!=null ) {
            if(reason.length()>512){
                reason = reason.substring(0, 512);
                messaging.utils.showInfoLog(messaging,nameMethod,
                        "ProvEventCustom reason cut "+reason);
            }

            provReason=stringProcess(reason);
            messaging.utils.showInfoLog(messaging,nameMethod,
                    "MESSAGING_NOTIFICATION_CUSTOM_EVENT "+provSnake);
        }

        sendEventToBackend(provSnake,provReason,"");
    }
    /**
     * Method to stringProcess URLEncoder.encode from Class
     * * @param reason : cause of event custom.
     * */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String stringProcess(String reason) {
        Messaging messaging=Messaging.getInstance();
        String provReason= "";
        try {
            provReason = URLEncoder.encode(reason, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {

            messaging.utils.showErrorLog(messaging, "stringProcess", "Error String process  "
                    ,e.getMessage());
            provReason=reason.replaceAll("\\s","");
            provReason=Normalizer.normalize(provReason, Normalizer.Form.NFD);
            provReason=provReason.replaceAll("[^\\p{ASCII}]", "");
            provReason = provReason.replaceAll("[-+.^:,]","");
            provReason=provReason.replace("\"", "");
        }

        return provReason;
    }
    /**
     * Method to sendEventToBackend from AnyActivity or Class
     * @param nameEvent : name of Event.
     * @param reason : cause of event custom.
     * @param externalId : id from Notification.
     * */
    public static void sendEventToBackend(String nameEvent, String reason, String externalId) {
    String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
    final Messaging messaging = Messaging.getInstance();
    String provId = "";
    String provUrl = "";
    String messageToast="";

        if (messaging.messagingDevice != null) {
            provId = messaging.messagingDevice.getId();
        } else {
            provId = messaging.messagingStorageController.getDevice().getId();
        }
        if(!externalId.equals("")){
            provUrl=messaging.utils.getMessagingHost()+"/devices/"+provId+"/event/"+nameEvent+"?externalId="+externalId+"&reason="+reason;

        }else{
            provUrl=messaging.utils.getMessagingHost()+"/devices/"+provId+"/event/"+nameEvent+"?reason="+reason;

        }

        messageToast=nameEvent+" reason="+reason;
        if (messaging.utils.isAnalytics_allowed()) {

            new HttpRequestEvent(provUrl, "GET", nameEvent, null, messaging).execute();
            messaging.utils.showInfoLog(messaging, nameMethod, "isAnalytics_allowed() "
                    + messaging.utils.isAnalytics_allowed());
        } else {
            messaging.utils.showInfoLog(messaging, nameMethod, "isAnalytics_allowed() "
                    + messaging.utils.isAnalytics_allowed());


        }
    }
    /**
     * Method to sendEventToBackend from AnyActivity or Class
     * @param nameEvent : name of Event.
     * @param messagingNotification: MessagingNotification object for get Id of Notificacion.
     * */
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

        if(messagingNotification.getNotificationId()!=null) {
            provUrl = messaging.utils.getMessagingHost() + "/devices/" + provId + "/event/" + nameEvent + "?externalId=" + messagingNotification.getNotificationId();
        }else{
            String msgId="";
            messaging.utils.showDebugLog(messaging,"sendEventToBackend","Aditional data  "
                    +messagingNotification);
            if(messaging.messagingStorageController.hasMessagingNotificationId()){
                msgId=messaging.messagingStorageController.getMessagingNotificationId();
            }
            provUrl = messaging.utils.getMessagingHost() + "/devices/" + provId + "/event/" + nameEvent + "?externalId=" + msgId;
        }
        if(messaging.utils.isAnalytics_allowed()) {

            new HttpRequestEvent(provUrl,"GET",nameEvent,null,messaging).execute();
            messaging.utils.showInfoLog(messaging,nameMethod,"isAnalytics_allowed() "
                    +messaging.utils.isAnalytics_allowed());
        }else{
            messaging.utils.showInfoLog(messaging,nameMethod,"isAnalytics_allowed() "
                    +messaging.utils.isAnalytics_allowed());

        }
    }




    /**
     * Method to sendEventGeofenceToBackend from AnyActivity or Class
     * @param typeAction : in or out.
     * @param geofenceId: Id of Geofence.
     * */
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
    /**
     * Method to  getGeofenceFromdB from AnyActivity or Class

     * */
    public static ArrayList<MessagingCircularRegion> getGeofenceFromdB() {
        final Messaging messaging = Messaging.getInstance();
        final MessagingDB db=new MessagingDB(messaging.context);
        final String  nameMethod="GetGeofenceFromdB";
        ArrayList<MessagingCircularRegion> result=null;
        if(db.getAllGeoFenceToBd().size()>0){
                result=db.getAllGeoFenceToBd();
        }else{
            messaging.utils.showDebugLog(messaging,nameMethod,"Do not Have geofence in bd ");
            result=new ArrayList<>();
        }

        return result;
    }
    /**
     * Method to fetchGeofence from AnyActivity or Class
     * */
    public static void fetchGeofence() {
        fetchGeofence(false,"","");
    }
    /**
     * Method to fetchGeofence from AnyActivity or Class
     * @param forsecallservice :allows you to make the request through the service
     * */
    public static void fetchGeofence(boolean forsecallservice) {
        fetchGeofence(forsecallservice,"","");
    }

    static void fetchGeofence(boolean forsecallservice,String next) {
        fetchGeofence(forsecallservice,next,"");
    }

    static void fetchGeofence(boolean forsecallservice, String next, final String extenalId) {
        final Messaging messaging = Messaging.getInstance();
        final MessagingDB db=new MessagingDB(messaging.context);
        final String  nameMethod="fetchGeofence\uD83D\uDE01";
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
                                           messaging.stopGeofenceSupervition();
                                           messaging.utils.deleteDbGeofenceLocal();

                                       }

                                    }
                                    if(jsonArrayItems.length()>0){
                                        messaging.utils.showDebugLog(this,nameMethod,"save Item to BD "+response);
                                        messaging.utils.processGeofenceList(jsonArrayItems);

                                    }else{
                                        Toast.makeText(messaging.context,"Has not Geofence yet!",Toast.LENGTH_LONG).show();
                                        messaging.utils.showDebugLog(this,nameMethod,"Do Not have Items of GF "+response);
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
                                                messaging.startGeofence(null);
                                                messaging.sendEventToActivity(Messaging.ACTION_FETCH_GEOFENCE,db.getAllGeoFenceToBd(),messaging.context);

                                            }else{
                                                messaging.utils.showDebugLog(this,nameMethod,
                                                        Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG +" or no data in BD");
                                                Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,
                                                        Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG, extenalId);
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
                messaging.utils.showDebugLog(messaging, nameMethod, "send to Activity GF from dB! "
                        +db.getAllGeoFenceToBd().size());
                messaging.sendEventToActivity(Messaging.ACTION_FETCH_GEOFENCE, db.getAllGeoFenceToBd(), messaging.context);
            }else{
                Toast.makeText(messaging.context,"Has not Geofence yet!",Toast.LENGTH_LONG).show();
                messaging.utils.showDebugLog(messaging, nameMethod, "Has not Geofence yet! ");

            }
        }
    }

    /**
     * Method to getPackageName from AnyActivity or Class
     *
     * */
    public String getPackageName() {
        return packageName;
    }
    /**
     * Method to stopServiceLocation from AnyActivity or Class
     * let make stop of background location
     * */
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
    /**
     * Method to getSdkVersion device from Class
     *
     * */
    public String getSdkVersion() {
        return sdkVersion;
    }

    private void setSdkVersion(String sdkVersion) {

        this.sdkVersion = sdkVersion;
    }
    /**
     * Method to getLanguage device from Class
     *
     * */
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

    /**
     * Method to setConfigParameterFromAppToLogin device from ActivityLogin
     *
     * */
    void setConfigParameterFromAppToLogin(String token, String Host){
        utils.saveConfigParameterFromApp(token,Host);
    }
    /**
     * Method to setLocationAllowed from Activity or Class
     * @param enable :true or false
     * */
    public static void setLocationAllowed(boolean enable){
        Messaging messaging=Messaging.getInstance();
        messaging.utils.setLocation_allowed(enable);

    }
    /**
     * Method to setAnalytincAllowed from Activity or Class
     * @param enable :true or false
     * */
    public static void setAnalytincAllowed(boolean enable){
        Messaging messaging=Messaging.getInstance();
        messaging.utils.setAnalytics_allowed(enable);

    }
    /**
     * Method to setLogingAllowed from Activity or Class
     * @param enable :true or false
     * */
    public static void setLogingAllowed(boolean enable){
        Messaging messaging=Messaging.getInstance();
        messaging.utils.setLogging_allowed(enable);

    }

    /**
     * Method to showAnalyticAllowedState on Activity or Class
     *
     * */
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
    /**
     * get config parameter from app
     */
    public String getMessagingToken() {

        return utils.getMessagingToken();
    }
    /**
     * get config parameter from app
     */
    public boolean isAnalytics_allowed() {

        return utils.isAnalytics_allowed();
    }
    /**
     * get config parameter from app
     */
    public boolean isLocation_allowed() {

        return utils.isLocation_allowed();
    }
    /**
     * get config parameter from app
     */
    public boolean isLogging_allowed() {

        return utils.isLogging_allowed();
    }
    /**
     * get config parameter from app
     */
    public boolean isEnable_permission_automatic() {

        return utils.islocationPermissionAtStartup();
    }
    /**
     * get state of GPS from app
     */
    public boolean isGPS() {
        return messagingStorageController.isGPSAllowed();
    }

    public void setGPS(boolean GPS) {
        messagingStorageController.setGPSAllowed(GPS);
        isGPS = GPS;
    }
    /**
     * turnOFFUpdateLocation
     */
    public static void turnOFFUpdateLocation(){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging=Messaging.getInstance();
        if(fusedLocationClient!=null) {
            messaging.utils.showDebugLog(messaging,nameMethod,"removeLocationUpdates ");
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    /**
     * Method to saveLastLocationInStorage from Activity or Class
     * @param location : Location Object
     * */
    public static void saveLastLocationInStorage(Location location){
        Messaging messaging=Messaging.getInstance();
        messaging.messagingStorageController.saveCurrentLocation(location);
    }
    /**
     * Method to getLastLocation from Storage
     *
     * */
    public static Location getLastLocation(){
        Messaging messaging=Messaging.getInstance();
        if(messaging.messagingStorageController.hasLastLocation()){
            return messaging.messagingStorageController.getLastLocationSaved();
        }else{
            return null;
        }

    }

    /**
     * Method to Know isLogged
     *
     * */
    public boolean isLogged() {
        return messagingStorageController.isRegisterDevice();
    }
    /**
     * Method to checkGPlayServiceStatus
     * installed or update
     * */
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
    /**
     * Method for capitalize
     */
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
        nameMethod="sendEventToActivity";
        Intent intent=new Intent(action);
        intent.putExtra(INTENT_EXTRA_DATA,something);
        intent.putExtra(INTENT_EXTRA_HAS_ERROR,something==null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Messaging messaging=Messaging.getInstance();
        messaging.utils.showDebugLog(messaging, nameMethod, "Action Send "
                +action);

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

    /**
     * Method post Logs
     @param logsMessage: token for notification push.
     */
    public void postLogs(String logsMessage){

        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final JSONObject requestBody=new JSONObject();
        try {
            requestBody.put(Messaging.MESSAGING_PUBLISH_LOGS_KEY,logsMessage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String provId= messagingDevice.getId();
        if(!provId.equals("")) {
            new HTTPReqTaskPostLogs(requestBody, this, provId).execute();
        }else{
            utils.showErrorLog(this,nameMethod, "id empty","");
        }
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

    private static class HTTPReqTaskPostLogs extends AsyncTask<Void,Void,String>{

        private String server_response;
        private JSONObject provRequestBody;
        private String provUrl;
        private String nameMethod;
        private Messaging messaging;
        private String provId;
        public HTTPReqTaskPostLogs(JSONObject requestBody, Messaging messaging, String provId) {
            this.provRequestBody=requestBody;
            this.messaging=messaging;
            this.provId=provId;
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;

            try {
                String authToken= messaging.utils.getMessagingToken();
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                messaging.utils.showDebugLog(this,nameMethod, "authToken "+authToken);
                JSONObject postData = provRequestBody;
                provUrl= messaging.utils.getMessagingHost()+"/devices/"+provId+"/stats";
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
                if (code!=HttpURLConnection.HTTP_OK) {

                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,"");
                    throw new IOException("Invalid response from server: " + code);
                }else{
                    server_response = messaging.readStream(urlConnection.getInputStream());
                }


            } catch (Exception e) {
                e.printStackTrace();

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
                messaging.utils.showHttpResponseLog(provUrl,messaging,nameMethod,"Post Logs Successful",response);

            }catch (NullPointerException e){
                messaging.utils.showErrorLog(this,nameMethod,"Logs Not Post ! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {

                messaging.utils.showErrorLog(this,nameMethod,"Logs Not Post ! JSONException ",e.getStackTrace().toString());
            }

        }
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
    /**
     * Method to HttpRequestCallback from AnyActivity or Class
     *
     * */
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
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Method to re_registerGeofence from Boot complete
     *
     * */
    public static void re_registerGeofence(){
        final Messaging messaging= Messaging.getInstance();
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

        if(messaging.utils.isLocation_allowed()) {
            MessagingDB db=new MessagingDB(messaging.context);
            if(db.getAllGeoFenceToBd().size()>0) {
                db.clearMonitoring();
                messaging.startGeofence(null);
                messaging.utils.showDebugLog(messaging, nameMethod, "Re-Register Geofence "+db.getAllGeoFenceToBd().size());
                Toast.makeText(messaging.context,"Re-Register Geofence..... "+db.getAllGeoFenceToBd().size(),Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(messaging.context,"Has not Geofence yet!",Toast.LENGTH_LONG).show();
                messaging.utils.showDebugLog(messaging, nameMethod, "Not Geofence in dB");
                messaging.utils.showDebugLog(messaging, nameMethod, "Has not Geofence yet!");
            }
        }else{
            messaging.utils.showDebugLog(messaging, nameMethod, "Location Config Not Enable");
            Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,
                    Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG, "");
        }

    }


    /**
     * Method to Start Geofence creation process
     * @param geofenceToRegister : list of Geofences to register
     * */
    void startGeofence(ArrayList<String> geofenceToRegister) {
    String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
    final Messaging messaging= Messaging.getInstance();
    messaging.utils.showDebugLog(messaging,nameMethod,"Register Geofence");
        MessagingDB db=new MessagingDB(context);
        nearestRegions=db.getAllGeoFenceToBd();
        provMessagingCircularRegionsMonitoring=messaging.utils.getGFMonitoringOne(nearestRegions);
        final ArrayList<MessagingCircularRegion> closestRegions = new ArrayList<>();
        for(MessagingCircularRegion messagingCircularRegion:nearestRegions){


            closestRegions.add(messagingCircularRegion);

            if(closestRegions.size()==MAXIMUM_NUMBER_OF_MONITORED_REGIONS){
                int numberClosesRegions=closestRegions.size()+1;
                messaging.utils.showInfoLog(messaging,nameMethod,"Limit to GF to add: "
                        +numberClosesRegions);
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Toast.makeText(context,"Limit to GF to add: "
                                +(closestRegions.size()+1),Toast.LENGTH_LONG).show();
                    }
                };
                mHandler.sendEmptyMessage(0);
            break;
            }
        }
        messaging.utils.showDebugLog(messaging,nameMethod,"provMessagingCircularRegionsMonitoring "
                +provMessagingCircularRegionsMonitoring.size());
        ArrayList<MessagingCircularRegion> intersection=messaging.utils.
                getIntersection(closestRegions,provMessagingCircularRegionsMonitoring);
        messaging.utils.showInfoLog(messaging,nameMethod,"intersection: "
                +(intersection.size()));
        regionsToDelete=messaging.utils
                .symmetricDifference(provMessagingCircularRegionsMonitoring,intersection);
        regionsToAdd=messaging.utils
                .symmetricDifference(intersection,closestRegions);
        if(geofenceToRegister!=null){
            for(String gf:geofenceToRegister){
                for(int i=0;i<intersection.size();i++){
                    if(intersection.get(i).getId().equals(gf)){
                        regionsToDelete.add(intersection.get(i));
                        regionsToAdd.add(intersection.get(i));
                    }
                }
            }
        }
        messaging.utils.deleteGeofenceLocal(regionsToDelete);
        if(regionsToAdd.size()>0){
            messaging.utils.showInfoLog(messaging,nameMethod,"regionsToAdd: "
                    +(regionsToAdd.size()));
            GeofencingRequest geofenceRequest = createGeofenceRequest( regionsToAdd );
            addGeofence(geofenceRequest);
        }else{
            messaging.utils.showInfoLog(messaging,nameMethod,"no regionsToAdd: "
                    +(regionsToAdd.size()));

        }
        db.deleteMarked();

    }


    /**
     * Method to Create a Geofence Request
     * @param provMessagingCircularRegions : list of CircularRegions to request
     * */
    GeofencingRequest createGeofenceRequest(ArrayList<MessagingCircularRegion> provMessagingCircularRegions ) {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        List<Geofence> geofenceList=new ArrayList<>();
        for(MessagingCircularRegion messagingCircularRegion:provMessagingCircularRegions){
            geofenceList.add(messagingCircularRegion.getGeofence());
        }
        utils.showDebugLog(this,nameMethod,"createGeofenceRequest "
                +(geofenceList.size()+1));

        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences( geofenceList )
                .build();
    }

    //
    /**
     * Method to Add the created GeofenceRequest to the device's monitoring list
     * @param request : GeofencingRequest Object.
     * */
    void addGeofence(GeofencingRequest request) {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            utils.showDebugLog(this,nameMethod," Dont have permission for Geofence add ");
            sendEventToBackend(MESSAGING_INVALID_DEVICE_LOCATION,MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING, "");

        }else{

            geofencingClient.addGeofences(request,createGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                            int reguinAdd=regionsToAdd.size()+1;
                            utils.showDebugLog(this,nameMethod," Add Geofence "
                                    +reguinAdd);
                            MessagingDB db=new MessagingDB(context);
                            MessagingCircularRegion.Builder builder= new MessagingCircularRegion.Builder();
                            for(MessagingCircularRegion messagingCircularRegion:regionsToAdd){

                                db.markRecordToMonitoring(messagingCircularRegion.getId(),true);
                            }
                            for(MessagingCircularRegion messagingCircularRegion:regionsToDelete){

                                db.markRecordToMonitoring(messagingCircularRegion.getId(),false);

                            }
                            fetchGeofence();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onFailure(@NonNull Exception e) {
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    int errorCode=Integer.parseInt(e.getMessage().split(":")[0]);
                    utils.showErrorLog(this,nameMethod," Add Geofence error "+errorCode,"");
                    String mesagError=utils.getErrorString(errorCode);
                    utils.showErrorLog(this,nameMethod," Add Geofence error "+mesagError,"");
                    sendEventCustom(mesagError,mesagError);
                    fetchGeofence();

                }
            });
        }

    }
    /**
     * Method to stopGeofenceSupervition
     * remove all Geofence list
     * */
    void stopGeofenceSupervition(){

        geofencingClient.removeGeofences(createGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                utils.showDebugLog(this,nameMethod,"onSuccess stopGeofenceSupervition");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(@NonNull Exception e) {

                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                utils.showDebugLog(this,nameMethod,"onFailure stopGeofenceSupervition "+e.getMessage());
                sendEventCustom("onFailure stopGeofenceSupervition",e.getMessage());

            }
        });

    }
    /**
     * Method to stopGeofencesSupervition
     *
     * */
    public static void stopGeofencesSupervition(){
        Messaging messaging=Messaging.getInstance();
        messaging.stopGeofenceSupervition();
    }


    /**
     * Method to removeGeofence
     * @param listIds :ids of Geofence to remove.
     * */


    void removeGeofence(final List<String> listIds){
        nameMethod="removeGeofence";
        utils.showDebugLog(this,nameMethod,"removeGeofence "+listIds.toString());
        utils.showDebugLog(this,nameMethod,"removeGeofence "+listIds.size());
        geofencingClient.removeGeofences(listIds)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                        utils.showDebugLog(this,nameMethod,"onSuccess removeGeofence ");
                        //actualizar monitor de GF a 0
                        MessagingDB db=new MessagingDB(context);
                        for(String temp:listIds){
                            db.markRecordToMonitoring(temp,false);
                        }
                        fetchGeofence();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                        utils.showDebugLog(this,nameMethod,"onFailure removeGeofence "+e.getMessage());
                        fetchGeofence();
                    }
                });

    }
    /**
     * Method to createGeofencePendingIntent
     * */
    PendingIntent createGeofencePendingIntent() {
        nameMethod="createGeofencePendingIntent";
        if ( geoFencePendingIntent != null ){
            return geoFencePendingIntent;
        }

        Intent intent = new Intent(context, MessaginGeofenceBroadcastReceiver.class);


        geoFencePendingIntent=PendingIntent.getBroadcast(
                context, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        return geoFencePendingIntent;
    }

    /**
     * Method to getLocat
     * */
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


        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * Method to logOutProcess
     * put pusToken="" and update device
     * */
    public static void logOutProcess() {//log out
        Messaging messaging=Messaging.getInstance();
        MessagingDB db=new MessagingDB(messaging.context);
        db.deleteAll();
        if(messaging.messagingDevice!=null) {
            messaging.messagingDevice.setStatusNotificationPush(false, messaging.context);
            messaging.utils
                    .showInfoLog(messaging,"LogOut Process","setStatusNotificationPush false");
        }
    }

    /**
     * Method to setLocationContinueAllowed from Activity or Class
     * @param state : ture or false)
     * */
    public static void setLocationContinueAllowed(boolean state){
        Messaging messaging=Messaging.getInstance();
        messaging.messagingStorageController.setLocationContinueAllowed(state);
    }
    /**
     * Method to Know if hasLocationContinueAllowed
     *
     * */
    public static boolean hasLocationContinueAllowed(){
        Messaging messaging=Messaging.getInstance();
        if(messaging.messagingStorageController.hasLocationContinueAllowed()==1){
            return true;
        }else {
            return false;
        }

    }

    /**
     * Method to Know if getLocationContinueAllowed
     *
     * */
    public static boolean getLocationContinueAllowed(){
        Messaging messaging=Messaging.getInstance();
        return messaging.messagingStorageController.isLocationContinueAllowed();
    }

    /**
     * Method to setLocationBackgroundAllowed from Activity or Class
     * @param state : ture or false)
     * */
    public static void setLocationBackgroundAllowed(boolean state){
        Messaging messaging=Messaging.getInstance();
        messaging.messagingStorageController.setLocationBackgroundAllowed(state);
    }
    /**
     * Method to Know if hasLocationBackgroundAllowed
     *
     * */
    public static boolean hasLocationBackgroundAllowed(){
        Messaging messaging=Messaging.getInstance();
        if(messaging.messagingStorageController.hasLocationBackgroundAllowed()==1){
            return true;
        }else {
            return false;
        }

    }

    /**
     * Method to Know if getLocationBackgroundAllowed
     *
     * */
    public static boolean getLocationBackgroundAllowed(){
        Messaging messaging=Messaging.getInstance();
        return messaging.messagingStorageController.isLocationBackgroundAllowed();
    }

    /**
     * Method to setLocationProritySelected from Activity or Class
     * @param state : ture or false)
     * */
    public static void setLocationProritySelected(int state){
        Messaging messaging=Messaging.getInstance();
        messaging.messagingStorageController.setLocationProritySelected(state);
    }
    /**
     * Method to Know if hasLocationProritySelected
     *
     * */
    public static boolean hasLocationProritySelected(){
        Messaging messaging=Messaging.getInstance();
        return messaging.messagingStorageController.hasLocationProritySelected();

    }

    /**
     * Method to Know if getLocationProritySelected
     *
     * */
    public static int getLocationProritySelected(){
        Messaging messaging=Messaging.getInstance();
        return messaging.messagingStorageController.getLocationProritySelected();
    }


}
