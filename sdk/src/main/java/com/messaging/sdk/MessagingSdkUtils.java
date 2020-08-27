package com.messaging.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

 class MessagingSdkUtils {


    public static String TAG="MESSAGING";
//    private static String messaging_host;
//    private static String messaging_token;
    private  String messagingHost;
    private  String messagingToken;
    private  String messagingTokenDefault;
    private  boolean analytics_allowed;
    private  boolean location_allowed;
    private  boolean logging_allowed;
    public String provHost;
    private MessagingStorageController messagingStorageController;
    private MessagingDevice messagingDevice;

    /**
     * Method init Resources system from config file
     * @param context
     * @param messaging
     *
     */
    public void initResourcesConfigFile(Context context, Messaging messaging){

        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

        try {

            int key_logging_allowed = context.getResources()
                    .getIdentifier("logging_allowed", "bool", context.getPackageName());
            logging_allowed = context.getResources().getBoolean(key_logging_allowed);
            showDebugLog(this,nameMethod," logging_allowed "+logging_allowed);

            int key_messagi_host = context.getResources()
                    .getIdentifier("messaging_host", "string", context.getPackageName());

            messagingHost = context.getString(key_messagi_host);
            showDebugLog(this,nameMethod," messagingHost "+messagingHost);


            int key_messaging_app_token = context.getResources()
                    .getIdentifier("messaging_app_token", "string", context.getPackageName());

            messagingToken = context.getString(key_messaging_app_token);
            messagingTokenDefault = context.getString(key_messaging_app_token);
            showDebugLog(this,nameMethod, " messagingToken "+messagingToken);

            int key_analytics_allowed = context.getResources()
                    .getIdentifier("analytics_allowed", "bool", context.getPackageName());
            analytics_allowed = context.getResources().getBoolean(key_analytics_allowed);
            showDebugLog(this,nameMethod, " analytics_allowed "+analytics_allowed);

            int key_location_allowed = context.getResources()
                    .getIdentifier("location_allowed", "bool", context.getPackageName());
            location_allowed = context.getResources().getBoolean(key_location_allowed);
            showDebugLog(this,nameMethod, " location_allowed "+location_allowed);

        }catch (Resources.NotFoundException e){
            showErrorLog(this,nameMethod,"Hasn't config file",e.getStackTrace().toString());
        }

        messagingStorageController=messaging.messagingStorageController;
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
        if(logging_allowed) {
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
        if(logging_allowed){
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
        if(logging_allowed){
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
        if(logging_allowed){
            Log.w(TAG,"WARNING: "+instance.getClass().getSimpleName()+": "+nameMethod+": "+message);
        }
    }

    /**
     * Method get MessagingHost
     */
     public  String getMessagingHost() {
         String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
         if(messagingStorageController.hasMessagingHost()){
             messagingHost=messagingStorageController.getMessagingHost();
         }else{
             showDebugLog(this,nameMethod,"Value default messagingHost "
                     +messagingHost);
         }
         return messagingHost;
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
             messagingToken=messagingStorageController.getMessagingToken();
         }else{
             showDebugLog(this,nameMethod,"Value default messagingToken "
                     +messagingToken);
         }
         return messagingToken;
     }

     public void setMessagingToken(String messaging_token) {
         messagingStorageController.saveMessagingToken(messaging_token);
         //messagingToken = messaging_token;
     }

     public  boolean isAnalytics_allowed() {
         String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
         if(messagingStorageController.hasAnalyticsAllowed()==1){
             analytics_allowed=messagingStorageController.isAnalyticsAllowed();

         }else{
             showDebugLog(this,nameMethod,"Value default analytics_allowed "
                     +messagingToken);
         }
         return analytics_allowed;
     }

     public  boolean isLocation_allowed() {
         String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
         if(messagingStorageController.hasLocationAllowed()==1){
             location_allowed=messagingStorageController.isLocationAllowed();
         }else{
             showDebugLog(this,nameMethod,"Value default location_allowed "
                     +messagingToken);
         }
         return location_allowed;
     }

     public  boolean isLogging_allowed() {
         String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
         if(messagingStorageController.hasLoggingAllowed()==1){
             logging_allowed=messagingStorageController.isLoggingAllowed();
         }else{
             showDebugLog(this,nameMethod,"Value default logging_allowed "
                     +logging_allowed);
         }
         return logging_allowed;
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

    public MessagingDevice getMessagingDevFromJson(JSONObject resp){
        MessagingDevice messagingDevice =new MessagingDevice();
        try {
            if(resp.has("id")){

                messagingDevice.setId(resp.getString("id"));

            }
            if(resp.has("pushToken")){
                messagingDevice.setPushToken(resp.getString("pushToken"));
            }
            if(resp.has("userId")){
                messagingDevice.setUserId(resp.getString("userId"));
            }
            if(resp.has("type")){
                messagingDevice.setType(resp.getString("type"));
            }
            if(resp.has("language")){
                messagingDevice.setLanguage(resp.getString("language"));
            }
            if(resp.has("model")){
                messagingDevice.setModel(resp.getString("model"));
            }
            if(resp.has("os")){
                messagingDevice.setOs(resp.getString("os"));
            }
            if(resp.has("sdkVersion")){
                messagingDevice.setSdkVersion(resp.getString("sdkVersion"));
            }
            if(resp.has("tags")){
                List<String> prvTag=new ArrayList<>();
                JSONArray jsonArray=resp.getJSONArray("tags");
                for (int i = 0; i < jsonArray.length(); i++) {
                    prvTag.add(jsonArray.getString(i));
                }
                messagingDevice.setTags(prvTag);

            }
            if(resp.has("createdAt")){
                messagingDevice.setCreatedAt(resp.getString("createdAt"));
            }
            if(resp.has("updatedAt")){
                messagingDevice.setUpdatedAt(resp.getString("updatedAt"));
            }
            if(resp.has("timestamp")){
                messagingDevice.setTimestamp(resp.getString("timestamp"));
            }
            if(resp.has("transaction")){
                messagingDevice.setTransaction(resp.getString("transaction"));
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
        if(logging_allowed){
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
        if(logging_allowed){
            Log.d(TAG,"HTTP_REQUEST: "+url_has_code.hashCode()+": "+instance.getClass().getSimpleName()+": "+ nameMethod+": "
                    +status+": "+response);
        }

    }

    public boolean verifyMatchAppId(String mgsAppId){
        boolean result=false;
        if(mgsAppId.equals(messagingTokenDefault)){
            result=true;
        }
        //Log.d(TAG,"verifyMatchAppId "+mgsAppId+" host "+messagingToken+" result "+result);
        return result;

    }

    public void showConfigParameter(){
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        showDebugLog(this,nameMethod," logging_allowed "+logging_allowed);
        showDebugLog(this,nameMethod," messagingHost "+messagingHost);
        showDebugLog(this,nameMethod, " messagingToken "+messagingToken);
        showDebugLog(this,nameMethod, " analytics_allowed "+analytics_allowed);
        showDebugLog(this,nameMethod, " location_allowed "+location_allowed);
    }

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
                setLocation_allowed(provEnable);
            }
            if(jsonObject.has(Messaging.MESSAGING_ANALYTICS_ENABLE)){
                boolean provEnable=jsonObject.getBoolean(Messaging.MESSAGING_ANALYTICS_ENABLE);
                showDebugLog(this,nameMethod, "analyticsEnable : "
                        +provEnable);
                setAnalytics_allowed(provEnable);
            }

            if(jsonObject.has(Messaging.MESSAGING_LOGGING_ENABLE)){
                boolean provEnable=jsonObject.getBoolean(Messaging.MESSAGING_LOGGING_ENABLE);
                showDebugLog(this,nameMethod, "loggingEnable : "
                        +provEnable);
                setLogging_allowed(provEnable);
            }

            if((jsonObject.has(Messaging.MESSAGING_APP_TOKEN))||
                    (jsonObject.has(Messaging.MESSAGING_APP_HOST))){
                //create device
                showDebugLog(this,nameMethod, "Create device and user new ");
                messaging.createDeviceParameters();
            }else{
                showDebugLog(this,nameMethod, "Not Create device and user new");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

     public boolean isValidURL(String url) {

         try {
             new URL(url).toURI();
         } catch (MalformedURLException | URISyntaxException e) {
             return false;
         }

         return true;
     }

}
