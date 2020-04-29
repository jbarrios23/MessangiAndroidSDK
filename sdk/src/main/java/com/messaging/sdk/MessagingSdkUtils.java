package com.messaging.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

 class MessagingSdkUtils {


    public static String TAG="MESSAGING";
    private static String messaging_host;
    private static String messaging_token;
    private static boolean analytics_allowed;
    private static boolean location_allowed;
    private static boolean logging_allowed;
    private MessagingStorageController messagingStorageController;
    private MessagingDevice messagingDevice;

    /**
     * Method init Resourses system from config file
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
            showDebugLog(this,nameMethod,location_allowed);
            int key_messagi_host = context.getResources()
                    .getIdentifier("messangi_host", "string", context.getPackageName());
            messaging_host = context.getString(key_messagi_host);
            showDebugLog(this,nameMethod,messaging_host);
            int key_messangi_app_token = context.getResources()
                    .getIdentifier("messangi_app_token", "string", context.getPackageName());
            messaging_token = context.getString(key_messangi_app_token);
            showDebugLog(this,nameMethod, messaging_token);
            int key_analytics_allowed = context.getResources()
                    .getIdentifier("analytics_allowed", "bool", context.getPackageName());
            analytics_allowed = context.getResources().getBoolean(key_analytics_allowed);
            showDebugLog(this,nameMethod, analytics_allowed);
            int key_location_allowed = context.getResources()
                    .getIdentifier("location_allowed", "bool", context.getPackageName());
             location_allowed = context.getResources().getBoolean(key_location_allowed);
            showDebugLog(this,nameMethod, location_allowed);

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
        if(messagingStorageController.hasTokenRegiter()){
            showDebugLog(this,nameMethod,"PushToken "+messagingStorageController.getToken());
        }else{
            showDebugLog(this,nameMethod,"PushToken "+"does not have PushToken yet");
        }


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
     @param instance: intance for Tag.
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
     @param instance: intance for Tag.
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
     @param instance: intance for Tag.
     @param nameMethod: name of method.
     @param message : message to show.
     */
    public  void showWarningLog(Object instance,String nameMethod,Object message){
        if(logging_allowed){
            Log.w(TAG,"WARNING: "+instance.getClass().getSimpleName()+": "+nameMethod+": "+message);
        }
    }

    /**
     * Method get messangi_host
     */

    public static String getMessangi_host() {
        return messaging_host;
    }

    public static void setMessangi_host(String messangi_host) {
        MessagingSdkUtils.messaging_host = messangi_host;
    }

    /**
     * Method get messangi_token
     */
    public static String getMessaging_token() {
        return messaging_token;
    }

    public static void setMessangi_token(String messangi_token) {
        MessagingSdkUtils.messaging_token = messangi_token;
    }

    public MessagingDevice getMessangiDevFromJson(JSONObject resp){
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
     @param instance: intance for Tag.
     @param url_hascode: url
     @param nameMethod: name of method
     @param type: POST/GET/PUT/DELETE
     @param body: Body
     */
    public  void showHttpRequestLog(String url_hascode,Object instance,String nameMethod,
                                    String type,String body){
        if(logging_allowed){
            Log.d(TAG,"HTTP_REQUEST: "+url_hascode.hashCode()+": "+instance.getClass().getSimpleName()+": "+ nameMethod+": "+type
                    +": "+url_hascode+": "+body);
        }

    }

    /**
     * Method Show Error log
     @param instance: intance for Tag.
     @param url_hascode: url
     @param nameMethod: name of method
     @param status: Status
     @param response: Response.
     */
    public  void showHttpResponsetLog(String url_hascode,Object instance,String nameMethod,
                                      String status,String response){
        if(logging_allowed){
            Log.d(TAG,"HTTP_REQUEST: "+url_hascode.hashCode()+": "+instance.getClass().getSimpleName()+": "+ nameMethod+": "
                    +status+": "+response);
        }

    }

}
