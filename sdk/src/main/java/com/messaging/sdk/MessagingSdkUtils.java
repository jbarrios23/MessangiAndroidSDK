package com.messaging.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_HIGH_ACCURACY;

class MessagingSdkUtils {


    public static String TAG="MESSAGING";
    //    private static String messaging_host;
//    private static String messaging_token;
    private  String messagingHost;
    private  String messagingToken;
    private  String messagingTokenDefault;
    private  boolean analytics_allowed;
    private  boolean location_allowed;
    //private  boolean logging_allowed;
    private  boolean enable_permission_automatic=true;
    public String provHost;
    private MessagingStorageController messagingStorageController;
    private MessagingDevice messagingDevice;
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
            showDebugLog(this,nameMethod,"UserId "+messagingStorageController.getDevice().getUserId());

        }else{
            showDebugLog(this,nameMethod,"DeviceId "+"does not have deviceId yet");
            showDebugLog(this,nameMethod,"UserId "+"does not have UserId yet");

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

    public MessagingDevice getMessagingDevFromJson(JSONObject resp, JSONObject body, String id, String userId){
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
            if(resp.has(Messaging.MESSAGING_USER_ID)){
                messagingDevice.setUserId(resp.getString(Messaging.MESSAGING_USER_ID));
            }else{
                messagingDevice.setUserId(userId);
                showDebugLog(this,nameMethod,"Set userID update "
                        +userId);
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
            if(resp.has(Messaging.MESSAGING_USER_ID)){
                messagingDevice.setUserId(resp.getString(Messaging.MESSAGING_USER_ID));
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
            JSONObject jsonObject=new JSONObject(parameter);
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
                if(provEnable){
                    if(Messaging.isForeground){
                        showDebugLog(this,nameMethod, "locationEnable : "
                                +provEnable+" is F "+Messaging.isForeground);
                        Messaging.fetchLocation(null,true,PRIORITY_HIGH_ACCURACY);
                    }else{
                        showDebugLog(this,nameMethod, "locationEnable : "
                                +provEnable+" is b "+Messaging.isBackground);
                        Intent intent = new Intent(context, MessagingLocationService.class);
                        context.startForegroundService(intent);
                    }
//                    if(Messaging.isBackground){
//                        showDebugLog(this,nameMethod, "locationEnable : "
//                                +provEnable+" is b "+Messaging.isBackground);
//                        Intent intent = new Intent(context, MessagingLocationService.class);
//                        context.startForegroundService(intent);
//                    }

                }else{
                    if(Messaging.isBackground){
                        showDebugLog(this,nameMethod, "locationEnable : "
                                +provEnable+" is b "+Messaging.isBackground);
                        Messaging.turnOFFUpdateLocation();
                        messaging.stopServiceLocation();

                    }
                }
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
                showDebugLog(this,nameMethod, "Reload SDK ");
                messaging.createDeviceParameters();
            }else{
                showDebugLog(this,nameMethod, "Not Reload SDK");

            }

        } catch (JSONException e) {
            e.printStackTrace();
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

}