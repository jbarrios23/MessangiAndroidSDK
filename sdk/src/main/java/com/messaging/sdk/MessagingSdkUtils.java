package com.messaging.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.GeofenceStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

class MessagingSdkUtils {


    public static String TAG="MESSAGING";
    //    private static String messaging_host;
//    private static String messaging_token;
    private  String messagingHost;
    private  String messagingToken;
    private  String messagingTokenDefault;
//    private  boolean analytics_allowed;
//    private  boolean location_allowed;
    //private  boolean logging_allowed;
    private  boolean enable_permission_automatic=true;
    public String provHost;
    private MessagingStorageController messagingStorageController;
    private MessagingDevice messagingDevice;
    private MessagingUser messagingUser;
    private Context context;


    /**
     * Method init Resources system from config file
     * @param context
     * @param messaging
     *
     */
    public void initResourcesConfigFile(Context context, Messaging messaging){
        this.context=context;
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messagingStorageController=messaging.messagingStorageController;
        try {
            showDebugLog(this,nameMethod," logging_allowed "+isLogging_allowed());
            showDebugLog(this,nameMethod," messagingHost "+getMessagingHost());
            showDebugLog(this,nameMethod, " messagingToken "+getMessagingToken());
            showDebugLog(this,nameMethod, " analytics_allowed "+isAnalytics_allowed());
            showDebugLog(this,nameMethod, " location_allowed "+isLocation_allowed());


        }catch (Resources.NotFoundException e){
            showErrorLog(this,nameMethod,"Hasn't config file",e.getStackTrace().toString());
        }


        if(messagingStorageController.isRegisterDevice()){
            showDebugLog(this,nameMethod,"DeviceId "+messagingStorageController.getDevice().getId());


        }else{
            showDebugLog(this,nameMethod,"DeviceId "+"does not have deviceId yet");


        }
        if(messagingStorageController.hasTokenRegister()){
            showDebugLog(this,nameMethod,"PushToken "+messagingStorageController.getToken());
        }else{
            showDebugLog(this,nameMethod,"PushToken "+"does not have PushToken yet");
        }

        if(messagingStorageController.hasMessagingToken()) {
            getMessagingToken();
        }
        if(messagingStorageController.hasMessagingHost()) {
            getMessagingHost();
        }
        if(messagingStorageController.hasAnalyticsAllowed()==1) {
            isAnalytics_allowed();
        }
        if(messagingStorageController.hasLocationAllowed()==1) {
            isLocation_allowed();
        }
        if(messagingStorageController.hasLoggingAllowed()==1) {
            isLogging_allowed();
        }
        showConfigParameter();

    }

    /**
     * Method Show Error log
     @param instance: instance for Tag.
     @param message : message to show
     */
    public  void showErrorLog(Object instance,String nameMethod,Object message,String statk_trace){
        if(isLogging_allowed()) {
            Log.e(TAG,"ERROR:"+instance.getClass().getSimpleName()+": "+nameMethod+": "+message+": "+statk_trace);
        }
    }

    /**
     * Method Show Debug log
     @param instance: instance for Tag.
     @param nameMethod: name of method.
     @param message : message to show.
     */
    public  void showDebugLog(Object instance,String nameMethod,Object message){
        if(isLogging_allowed()){
            Log.d(TAG,"DEBUG: "+instance.getClass().getSimpleName()+": "+nameMethod+": "+message);
        }

    }
    /**
     * Method Show Info log
     @param instance: instance for Tag.
     @param nameMethod: name of method.
     @param message : message to show.
     */
    public  void showInfoLog(Object instance,String nameMethod,Object message){
        if(isLogging_allowed()){
            Log.i(TAG,"INFO: "+instance.getClass().getSimpleName()+": "+nameMethod+": "+message);
        }
    }

    /**
     * Method Show Info log
     @param instance: instance for Tag.
     @param nameMethod: name of method.
     @param message : message to show.
     */
    public  void showWarningLog(Object instance,String nameMethod,Object message){
        if(isLogging_allowed()){
            Log.w(TAG,"WARNING: "+instance.getClass().getSimpleName()+": "+nameMethod+": "+message);
        }
    }

    /**
     * Method get MessagingHost
     */
    public  String getMessagingHost() {
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(messagingStorageController.hasMessagingHost()){
            return messagingStorageController.getMessagingHost();
        }else{
            int key_messaging_host = context.getResources()
                    .getIdentifier("messaging_host", "string", context.getPackageName());

            String value=context.getString(key_messaging_host);
            return value;
        }

    }

    public void setMessagingHost(String messaging_host) {
        messagingStorageController.saveMessagingHost(messaging_host);
        //messagingHost = messaging_host;
    }

    /**
     * Method get MessagingToken
     */
    public String getMessagingToken() {
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(messagingStorageController.hasMessagingToken()){
            return messagingStorageController.getMessagingToken();
        }else{
            int key_messaging_app_token = context.getResources()
                    .getIdentifier("messaging_app_token", "string", context.getPackageName());

            String value=context.getString(key_messaging_app_token);
            return value;
        }

    }

    public void setMessagingToken(String messaging_token) {
        messagingStorageController.saveMessagingToken(messaging_token);
        //messagingToken = messaging_token;
    }

    public  boolean isAnalytics_allowed() {
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(messagingStorageController.hasAnalyticsAllowed()==1){
            return messagingStorageController.isAnalyticsAllowed();

        }else{
            int key_analytics_allowed = context.getResources()
                    .getIdentifier("analytics_allowed", "bool", context.getPackageName());
            boolean value= context.getResources().getBoolean(key_analytics_allowed);
            return value;
        }

    }

    public  boolean isLocation_allowed() {
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(messagingStorageController.hasLocationAllowed()==1){
            return messagingStorageController.isLocationAllowed();
        }else{
            int key_location_allowed = context.getResources()
                    .getIdentifier("location_allowed", "bool", context.getPackageName());
            boolean value = context.getResources().getBoolean(key_location_allowed);
            return value;
        }

    }

    public  boolean isLogging_allowed() {
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(messagingStorageController.hasLoggingAllowed()==1){
            return messagingStorageController.isLoggingAllowed();
        }else{
            int key_logging_allowed = context.getResources()
                    .getIdentifier("logging_allowed", "bool", context.getPackageName());
            boolean value=context.getResources().getBoolean(key_logging_allowed);
            return value;
        }

    }

    public void setAnalytics_allowed(boolean analytics_allowed) {
        messagingStorageController.setAnalyticsAllowed(analytics_allowed);
        //this.analytics_allowed = analytics_allowed;
    }

    public void setLocation_allowed(boolean location_allowed) {
        messagingStorageController.setLocationAllowed(location_allowed);
        //this.location_allowed = location_allowed;
    }

    public void setLogging_allowed(boolean logging_allowed) {
        messagingStorageController.setLoggingAllowed(logging_allowed);
        //this.logging_allowed = logging_allowed;
    }

    public boolean isEnable_permission_automatic() {
        return enable_permission_automatic;
    }

    public void setEnable_permission_automatic(boolean enable_permission_automatic) {
        this.enable_permission_automatic = enable_permission_automatic;
    }

    public MessagingDevice getMessagingDevFromJson(JSONObject resp, JSONObject body, String id){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        MessagingDevice messagingDevice =new MessagingDevice();
        try {
            if(resp.has(Messaging.MESSAGING_DEVICE_ID)){
                messagingDevice.setId(resp.getString(Messaging.MESSAGING_DEVICE_ID));
            }else{
                messagingDevice.setId(id);
                showDebugLog(this,nameMethod,"Set ID update "
                        +id);
            }
            if(body.has(Messaging.MESSAGING_PUSH_TOKEN)){
                messagingDevice.setPushToken(body.getString(Messaging.MESSAGING_PUSH_TOKEN));
            }

            if(body.has(Messaging.MESSAGING_DEVICE_TYPE)){
                messagingDevice.setType(body.getString(Messaging.MESSAGING_DEVICE_TYPE));
            }
            if(body.has(Messaging.MESSAGING_DEVICE_LANGUAGE)){
                messagingDevice.setLanguage(body.getString(Messaging.MESSAGING_DEVICE_LANGUAGE));
            }
            if(body.has(Messaging.MESSAGING_DEVICE_MODEL)){
                messagingDevice.setModel(body.getString(Messaging.MESSAGING_DEVICE_MODEL));
            }
            if(body.has(Messaging.MESSAGING_DEVICE_OS)){
                messagingDevice.setOs(body.getString(Messaging.MESSAGING_DEVICE_OS));
            }
            if(body.has(Messaging.MESSAGING_DEVICE_SDK_VERSION)){
                messagingDevice.setSdkVersion(body.getString(Messaging.MESSAGING_DEVICE_SDK_VERSION));
            }
            if(body.has(Messaging.MESSAGING_DEVICE_TAGS)){
                List<String> prvTag=new ArrayList<>();
                JSONArray jsonArray=body.getJSONArray(Messaging.MESSAGING_DEVICE_TAGS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    prvTag.add(jsonArray.getString(i));
                }
                messagingDevice.setTags(prvTag);
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_CREATE_AT)){
                messagingDevice.setCreatedAt(resp.getString(Messaging.MESSAGING_DEVICE_CREATE_AT));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_UPDATE_AT)){
                messagingDevice.setUpdatedAt(resp.getString(Messaging.MESSAGING_DEVICE_UPDATE_AT));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_TIMESTAMP)){
                messagingDevice.setTimestamp(resp.getString(Messaging.MESSAGING_DEVICE_TIMESTAMP));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_TRANSACTION)){
                messagingDevice.setTransaction(resp.getString(Messaging.MESSAGING_DEVICE_TRANSACTION));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messagingDevice;
    }

    public MessagingDevice getMessagingDevFromJsonOnlyResp(JSONObject resp, String pushToken){
        MessagingDevice messagingDevice =new MessagingDevice();
        try {
            if(resp.has(Messaging.MESSAGING_DEVICE_ID)){
                messagingDevice.setId(resp.getString(Messaging.MESSAGING_DEVICE_ID));
            }
            if(resp.has(Messaging.MESSAGING_PUSH_TOKEN)){
                messagingDevice.setPushToken(resp.getString(Messaging.MESSAGING_PUSH_TOKEN));
            }else{
                messagingDevice.setPushToken(pushToken);
            }

            if(resp.has(Messaging.MESSAGING_DEVICE_TYPE)){
                messagingDevice.setType(resp.getString(Messaging.MESSAGING_DEVICE_TYPE));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_LANGUAGE)){
                messagingDevice.setLanguage(resp.getString(Messaging.MESSAGING_DEVICE_LANGUAGE));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_MODEL)){
                messagingDevice.setModel(resp.getString(Messaging.MESSAGING_DEVICE_MODEL));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_OS)){
                messagingDevice.setOs(resp.getString(Messaging.MESSAGING_DEVICE_OS));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_SDK_VERSION)){
                messagingDevice.setSdkVersion(resp.getString(Messaging.MESSAGING_DEVICE_SDK_VERSION));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_TAGS)){
                List<String> prvTag=new ArrayList<>();
                JSONArray jsonArray=resp.getJSONArray(Messaging.MESSAGING_DEVICE_TAGS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    prvTag.add(jsonArray.getString(i));
                }
                messagingDevice.setTags(prvTag);
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_CREATE_AT)){
                messagingDevice.setCreatedAt(resp.getString(Messaging.MESSAGING_DEVICE_CREATE_AT));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_UPDATE_AT)){
                messagingDevice.setUpdatedAt(resp.getString(Messaging.MESSAGING_DEVICE_UPDATE_AT));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_TIMESTAMP)){
                messagingDevice.setTimestamp(resp.getString(Messaging.MESSAGING_DEVICE_TIMESTAMP));
            }
            if(resp.has(Messaging.MESSAGING_DEVICE_TRANSACTION)){
                messagingDevice.setTransaction(resp.getString(Messaging.MESSAGING_DEVICE_TRANSACTION));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messagingDevice;
    }



    /**
     * Method Show Error log
     @param instance: instance for Tag.
     @param url_has_code: url
     @param nameMethod: name of method
     @param type: POST/GET/PUT/DELETE
     @param body: Body
     */
    public void showHttpRequestLog(String url_has_code,Object instance,String nameMethod,
                                   String type,String body){
        if(isLogging_allowed()){
            Log.d(TAG,"HTTP_REQUEST: "+url_has_code.hashCode()+": "+instance.getClass().getSimpleName()+": "+ nameMethod+": "+type
                    +": "+url_has_code+": "+body);
        }

    }

    /**
     * Method Show Error log
     @param instance: instance for Tag.
     @param url_has_code: url
     @param nameMethod: name of method
     @param status: Status
     @param response: Response.
     */
    public void showHttpResponseLog(String url_has_code,Object instance,String nameMethod,
                                    String status,String response){
        if(isLogging_allowed()){
            Log.d(TAG,"HTTP_REQUEST: "+url_has_code.hashCode()+": "+instance.getClass().getSimpleName()+": "+ nameMethod+": "
                    +status+": "+response);
        }

    }

    public boolean verifyMatchAppId(String mgsAppId){
        boolean result=false;
        if(mgsAppId.equals(getMessagingToken())){
            result=true;
        }
        //Log.d(TAG,"verifyMatchAppId "+mgsAppId+" host "+messagingToken+" result "+result);
        return result;

    }

    public void showConfigParameter(){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        showDebugLog(this,nameMethod," logging_allowed "+isLogging_allowed());
        showDebugLog(this,nameMethod," messagingHost "+getMessagingHost());
        showDebugLog(this,nameMethod, " messagingToken "+getMessagingToken());
        showDebugLog(this,nameMethod, " analytics_allowed "+isAnalytics_allowed());
        showDebugLog(this,nameMethod, " location_allowed "+isLocation_allowed());
        showDebugLog(this,nameMethod, " permission_enable "+isEnable_permission_automatic());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveConfigParameter(String parameter, Messaging messaging){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        try {
            showDebugLog(this,nameMethod,"parameter "+parameter);
            JSONObject jsonObject=new JSONObject(parameter);
            showDebugLog(this,nameMethod,"parameter Json "+jsonObject.toString());
            if(jsonObject.has(Messaging.MESSAGING_APP_TOKEN)){
                String provEnable=jsonObject.getString(Messaging.MESSAGING_APP_TOKEN);
                showDebugLog(this,nameMethod, "appToken : "
                        +provEnable);
                setMessagingToken(provEnable);

            }
            if(jsonObject.has(Messaging.MESSAGING_APP_HOST)){
                provHost=jsonObject.getString(Messaging.MESSAGING_APP_HOST);
                showDebugLog(this,nameMethod, "host : "
                        +provHost);
                if(isValidURL(provHost)) {
                    setMessagingHost(provHost);
                }else{
                    showErrorLog(this,nameMethod, "invalid Url will not update," +
                            " please check the url submitted ","");
                }

            }
            if(jsonObject.has(Messaging.MESSAGING_LOCATION_ENABLE)){
                boolean provEnable=jsonObject.getBoolean(Messaging.MESSAGING_LOCATION_ENABLE);
                showDebugLog(this,nameMethod, "locationEnable : "
                        +provEnable);

                setLocation_allowed(provEnable);
            }
            if(jsonObject.has(Messaging.MESSAGING_ANALYTICS_ENABLE)){
                boolean provEnable=jsonObject.getBoolean(Messaging.MESSAGING_ANALYTICS_ENABLE);
                showDebugLog(this,nameMethod, "analyticsEnable : "
                        +provEnable);

                setAnalytics_allowed(provEnable);
            }


            if((jsonObject.has(Messaging.MESSAGING_APP_TOKEN))||
                    (jsonObject.has(Messaging.MESSAGING_APP_HOST))){
                //create device
                showDebugLog(this,nameMethod, "Reload SDK ans create device ");
                if(messagingStorageController.isRegisterDevice()){
                    messagingStorageController.saveDevice(null,null,null);
                    messagingStorageController.saveUserByDevice(null);
                    messagingDevice=null;
                    messagingUser=null;
                    Messaging.logOutProcess();

                }
                messaging.createDeviceParameters();
            }else{
                showDebugLog(this,nameMethod, "Not Reload SDK");

            }

        } catch (JSONException e) {
            e.printStackTrace();
            showErrorLog(this,nameMethod,"error ",e.getMessage());
        }
    }

    public void saveConfigParameterFromApp(String token, String Host){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        setMessagingToken(token);
        setMessagingHost(Host);
    }

    public boolean isValidURL(String url) {

        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }

        return true;
    }

    public String toUpperSnakeCase(String variableName) {
        StringBuilder builder = new StringBuilder();
        char[] nameChars = variableName.toCharArray();
        for (int i = 0; i < nameChars.length; i++) {
            char ch = nameChars[i];
            if (i != 0 && Character.isWhitespace(ch)) {
                builder.append('_');
            } else if(i != 0 && Character.isUpperCase(ch)) {
                builder.append('_').append(ch);

            } else {
                builder.append(Character.toUpperCase(ch));
            }//from  w  w  w .  j  a va2 s . c o m
        }
        return builder.toString();
    }

    public boolean verifyIsValidGeoPush(JSONObject jsonObject, Messaging messaging){
        boolean result=false;
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showDebugLog(this,nameMethod," Dont have permission for GeoPush yet! ");
            Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,
                    Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING, "");

        }else {

            if(messaging.isGPS()){
                showDebugLog(this,nameMethod,"location enable "
                        +isLocation_allowed());

                if(isLocation_allowed()){
                    if(jsonObject.has(Messaging.GOEOFENCE_LONG) && jsonObject.has(Messaging.GOEOFENCE_LAT)
                            && jsonObject.has(Messaging.GOEOFENCE_RADIUS)){
                        try {
                            double provLongitude=jsonObject.getDouble(Messaging.GOEOFENCE_LONG);
                            double provLatitude=jsonObject.getDouble(Messaging.GOEOFENCE_LAT);
                            double provRadius=jsonObject.getInt(Messaging.GOEOFENCE_RADIUS);
                            Location location=new Location(LOCATION_SERVICE);
                            location.setLatitude(provLatitude);
                            location.setLongitude(provLongitude);
                            showDebugLog(this,nameMethod,"GeoPush location lat: "+provLatitude
                                    +" Long: "+provLongitude);
                            if(messaging.messagingStorageController.hasLastLocation()){
                                Location lastLocation=messaging.messagingStorageController.getLastLocationSaved();
                                showDebugLog(this,nameMethod,"last location lat: "+lastLocation.getLatitude()
                                        +" Long: "+lastLocation.getLongitude());
                                showDebugLog(this,nameMethod,"distance calculate "+lastLocation.distanceTo(location)
                                        +" Radius "+provRadius);
                                if(lastLocation.distanceTo(location)<=provRadius){
                                    result=true;
                                }

                            }else{
                                result=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showErrorLog(this,nameMethod,"Error "+e.getStackTrace(),"");
                            result=false;
                        }

                    }

                }else{
                    showDebugLog(this,nameMethod,"Disable location Config for Geopush "
                            +Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG);
                    Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,
                            Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG, "");

                }
            }else{
                showDebugLog(this,nameMethod,"Disable location for Geopush "
                        +Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION);
                Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,
                        Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION, "");

            }

        }

        return result;
    }

    public boolean verifyIsValidGeoPushTwo(JSONArray jsonArray, Messaging messaging){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        boolean result=false;
        for(int i=0;i<jsonArray.length();i++){
            try {
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            if(jsonObject.has(Messaging.GOEOFENCE_LONG) && jsonObject.has(Messaging.GOEOFENCE_LAT)
                    && jsonObject.has(Messaging.GOEOFENCE_RADIUS)){

                    double provLongitude=jsonObject.getDouble(Messaging.GOEOFENCE_LONG);
                    double provLatitude=jsonObject.getDouble(Messaging.GOEOFENCE_LAT);
                    double provRadius=jsonObject.getInt(Messaging.GOEOFENCE_RADIUS);
                    Location location=new Location(LOCATION_SERVICE);
                    location.setLatitude(provLatitude);
                    location.setLongitude(provLongitude);
                    showDebugLog(this,nameMethod,"GeoPush location lat: "+provLatitude
                            +" Long: "+provLongitude);
                    if(messaging.messagingStorageController.hasLastLocation()){
                        Location lastLocation=messaging.messagingStorageController.getLastLocationSaved();
                        showDebugLog(this,nameMethod,"last location lat: "+lastLocation.getLatitude()
                                +" Long: "+lastLocation.getLongitude());
                        showDebugLog(this,nameMethod,"distance calculate "+lastLocation.distanceTo(location)
                                +" Radius "+provRadius);
                        if(lastLocation.distanceTo(location)<=provRadius){
                            result=true;
                        }

                    }else{
                        result=false;
                    }
               }
            } catch (JSONException e) {
                e.printStackTrace();
                showErrorLog(this,nameMethod,"Error "+e.getStackTrace(),"");
                result=false;
            }

        }
        //Log.d(TAG,"verifyMatchAppId "+mgsAppId+" host "+messagingToken+" result "+result);
        return result;
    }

    public void handleGeoFencePushParameter(String messagingGeoFencePush, Messaging messaging) {

        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        MessagingCircularRegion.Builder builder= new MessagingCircularRegion.Builder();
        MessagingDB db=new MessagingDB(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showDebugLog(this,nameMethod," Dont have permission for GeoFence yet! ");
            Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,
                    Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_MISSING, "");

        }else {

            if(messaging.isGPS()){
                    try {
                        JSONArray jsonArray=new JSONArray(messagingGeoFencePush);
                        messaging.utils.showDebugLog(this,nameMethod,"GeoFence Array "+jsonArray);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject temp=jsonArray.getJSONObject(i);
                            if(temp.has(Messaging.GOEOFENCE_OPERATION) &&
                                    temp.getString(Messaging.GOEOFENCE_OPERATION)
                                            .equals(Messaging.GOEOFENCE_OPERATION_CREATE)){

                                String provOperation=temp.getString(Messaging.GOEOFENCE_OPERATION);
                                long provExpiration;
                                if(temp.has(Messaging.GOEOFENCE_EXPIRATION)) {
                                    provExpiration= temp.getLong(Messaging.GOEOFENCE_EXPIRATION);
                                }else{
                                    provExpiration=Messaging.NEVER_EXPIRE;
                                }

                                //metodo para guardar en la BD cree el objeto MCR
                                MessagingCircularRegion geofence=builder.setId(temp.getString(Messaging.GOEOFENCE_ID_OTHER))
                                        .setLatitude(temp.getDouble(Messaging.GOEOFENCE_LAT))
                                        .setLongitud(temp.getDouble(Messaging.GOEOFENCE_LONG))
                                        .setRadius(temp.getInt(Messaging.GOEOFENCE_RADIUS))
                                        .setMessagingGeoFenceTrigger(temp.getString(Messaging.GOEOFENCE_TYPE))
                                        .setExpiration(provExpiration)
                                        .build();
                                showDebugLog(this,nameMethod,"MessagingCircularRegion geofence "
                                        +geofence.messagingGeoFenceTrigger+" operation "+provOperation);
                                //save Geofence in BD
                                db.addGeoFenceToBd(geofence);

                            }else if(temp.has(Messaging.GOEOFENCE_OPERATION) &&
                                    temp.getString(Messaging.GOEOFENCE_OPERATION)
                                            .equals(Messaging.GOEOFENCE_OPERATION_UPDATE)){


                                String provOperation=temp.getString(Messaging.GOEOFENCE_OPERATION);
                                long provExpiration;
                                if(temp.has(Messaging.GOEOFENCE_EXPIRATION)) {
                                    provExpiration= temp.getLong(Messaging.GOEOFENCE_EXPIRATION);
                                }else{
                                    provExpiration=Messaging.NEVER_EXPIRE;
                                }
                                //metodo para guardar en la BD cree el objeto MCR
                                String provId=temp.getString(Messaging.GOEOFENCE_ID_OTHER);
                                MessagingCircularRegion geofence=builder.setId(temp.getString(Messaging.GOEOFENCE_ID_OTHER))
                                        .setLatitude(temp.getDouble(Messaging.GOEOFENCE_LAT))
                                        .setLongitud(temp.getDouble(Messaging.GOEOFENCE_LONG))
                                        .setRadius(temp.getInt(Messaging.GOEOFENCE_RADIUS))
                                        .setMessagingGeoFenceTrigger(temp.getString(Messaging.GOEOFENCE_TYPE))
                                        .setExpiration(provExpiration)
                                        .build();
                                //save Geofence in BD
                                showDebugLog(this,nameMethod,"MessagingCircularRegion geofence "
                                        +geofence.messagingGeoFenceTrigger+" operation "+provOperation);

                                db.update(geofence,provId);



                            }else{
                                if(temp.has(Messaging.GOEOFENCE_OPERATION) &&
                                        temp.getString(Messaging.GOEOFENCE_OPERATION).equals(Messaging.GOEOFENCE_OPERATION_DELETE)){

                                    //String provId=temp.getString(Messaging.GOEOFENCE_ID);
                                    String provId=temp.getString(Messaging.GOEOFENCE_ID_OTHER);
                                    String provOperation=temp.getString(Messaging.GOEOFENCE_OPERATION);

                                    db.delete(provId);
                                    //delete id geofence

                                    //metodo para guardar en la BD cree el objeto MCR
                                    //delete Geofence in BD

                                }
                            }
                        }
                        if(db.getAllGeoFenceToBd().size()>0 ) {//aca sera
                            // colocar otra validacion preguntando si la bd tiene 100 GF
                            if(isLocation_allowed()) {
                                messaging.stopGeofenceSupervition();
                                messaging.startGeofence();
                            }else{
                                showDebugLog(this,nameMethod,"Disable location config for GeoFence "
                                        +Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG);
                                Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,
                                        Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_CONFIG, "");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }else{
                showDebugLog(this,nameMethod,"Disable location for GeoFence "
                        +Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION);
                Messaging.sendEventToBackend(Messaging.MESSAGING_INVALID_DEVICE_LOCATION,
                        Messaging.MESSAGING_INVALID_DEVICE_LOCATION_REASON_LOCATION, "");

            }

        }
    }

    public void processGeofenceList(JSONArray jsonArrayItems) {
        Messaging messaging=Messaging.getInstance();
        MessagingDB db=new MessagingDB(context);
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        MessagingCircularRegion.Builder builder= new MessagingCircularRegion.Builder();
        try {
         long provExpiration = Messaging.NEVER_EXPIRE;
         for(int i=0;i<jsonArrayItems.length();i++){
            JSONObject temp=jsonArrayItems.getJSONObject(i);

            //if(isValidLatLng(temp.getDouble(Messaging.GOEOFENCE_LAT),temp.getDouble(Messaging.GOEOFENCE_LONG))) {
                MessagingCircularRegion geofence = builder.setId(temp.getString(Messaging.GOEOFENCE_ID))
                        .setLatitude(temp.getDouble(Messaging.GOEOFENCE_LAT))
                        .setLongitud(temp.getDouble(Messaging.GOEOFENCE_LONG))
                        .setRadius(temp.getInt(Messaging.GOEOFENCE_RADIUS))
                        .setMessagingGeoFenceTrigger(temp.getString(Messaging.GOEOFENCE_TYPE))
                        .setExpiration(provExpiration)
                        .build();
                db.addGeoFenceToBd(geofence);
             /*}else{
                messaging.utils.showDebugLog(this,nameMethod,"Invalid latitude "
                        +temp.getDouble(Messaging.GOEOFENCE_LAT));

             }*/
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getListOfId(ArrayList<MessagingCircularRegion> prMessagingCircularRegions) {
        String nameMethod="getListOfId";
        List<String> result=new ArrayList<>();
        for (MessagingCircularRegion temp: prMessagingCircularRegions){

            result.add(temp.id);
            if(result.size()==99){
                break;
            }
        }
        showDebugLog(this, nameMethod, "result : "
                + " id to remove " + result.size());
        return result;
    }

    public void handleGeoFencePushParameterSinc(String messagingGeoFencePushSinc, Messaging messaging) {
        String nameMethod="handleGeoFencePushParameterSinc";
        try {

            JSONObject jsonArray=new JSONObject(messagingGeoFencePushSinc);
            showDebugLog(this,nameMethod,"Sinc"
                    +jsonArray.toString());
            showDebugLog(this, nameMethod, "state : "
                    + " is F " + Messaging.isForeground);
            showDebugLog(this, nameMethod, "state : "
                    + " is B " + Messaging.isBackground);

            if(Messaging.isForeground) {
                showDebugLog(this, nameMethod, "Sinc Enable call service : "
                        + " is F " + Messaging.isForeground);

                Messaging.fetchGeofence(true,null);
            }else {
                Messaging.flagSinc=true;
                Messaging.isBackground=true;
                showDebugLog(this, nameMethod, "Sinc Enable call service : "
                        + " is B " + Messaging.isBackground+" sinc flag "+Messaging.flagSinc);
                messaging.messagingStorageController.setSincAllowed(Messaging.flagSinc);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void deleteBdAndGeofenceLocal() {
        String nameMethod="deleteGeofenceLocal";
        MessagingDB db=new MessagingDB(context);
        Messaging messaging=Messaging.getInstance();
        if(db.getAllGeoFenceToBd().size()>0) {
            ArrayList<MessagingCircularRegion> prMessagingCircularRegions = db.getAllGeoFenceToBd();
            List<String> removeIds = getListOfId(prMessagingCircularRegions);
            messaging.removeGeofence(removeIds);
            db.deleteAll();
        }else{
            messaging.utils.showDebugLog(this,nameMethod,"Don not have GF in DB");
        }

    }

    public void deleteGeofenceLocal() {
        String nameMethod="deleteGeofenceLocalOnly";
        MessagingDB db=new MessagingDB(context);
        Messaging messaging=Messaging.getInstance();
        if(db.getAllGeoFenceToBd().size()>0) {
            ArrayList<MessagingCircularRegion> prMessagingCircularRegions = db.getAllGeoFenceToBd();
            List<String> removeIds = getListOfId(prMessagingCircularRegions);
            messaging.removeGeofence(removeIds);
        }else{
            messaging.utils.showDebugLog(this,nameMethod,"Don not have GF in DB");
        }

    }

    public void handlePublishLogcat() {
        Messaging messaging=Messaging.getInstance();
        String nameMethod="handlePublishLogcat";
        String prvLogcatMessages=Messaging.getLocat();
        if(prvLogcatMessages!=null &&! prvLogcatMessages.equals("")){
            messaging.utils.showDebugLog(messaging,nameMethod+" "+prvLogcatMessages.length(),prvLogcatMessages);
            //send to service
            messaging.postLogs(prvLogcatMessages);
        }

    }

    public boolean isValidLatLng(double lat, double lng){

        if(lat < -90 || lat > 90 || lng < -180 || lng > 180)
        {
            return false;
        }

        return true;
    }

    /**
     * Returns the error string for a geofencing error code.
     */
    public  String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence is not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error: " + Integer.toString(errorCode);
        }
    }
}