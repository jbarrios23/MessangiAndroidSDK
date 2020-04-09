package com.messaging.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessagingSdkUtils {


    public static String TAG="MESSANGING";
    static int icon;
    public static String nameClass;
    private static String messangi_host;
    private static String messangi_token;
    private static boolean analytics_allowed;
    private static boolean location_allowed;
    private static boolean logging_allowed;

    /**
     * Method init Resourses system from config file
     * @param context
     */
    public void initResourcesConfigFile(Context context){

        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

        try {

            int key_logging_allowed = context.getResources()
                    .getIdentifier("logging_allowed", "bool", context.getPackageName());
            logging_allowed = context.getResources().getBoolean(key_logging_allowed);
            showDebugLog(this,nameMethod,location_allowed);
            int key_messagi_host = context.getResources()
                    .getIdentifier("messangi_host", "string", context.getPackageName());
            messangi_host = context.getString(key_messagi_host);
            showDebugLog(this,nameMethod,messangi_host);
            int key_messangi_app_token = context.getResources()
                    .getIdentifier("messangi_app_token", "string", context.getPackageName());
            messangi_token = context.getString(key_messangi_app_token);
            showDebugLog(this,nameMethod, messangi_token);
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
        return messangi_host;
    }

    public static void setMessangi_host(String messangi_host) {
        MessagingSdkUtils.messangi_host = messangi_host;
    }

    /**
     * Method get messangi_token
     */
    public static String getMessangi_token() {
        return messangi_token;
    }

    public static void setMessangi_token(String messangi_token) {
        MessagingSdkUtils.messangi_token = messangi_token;
    }

    public MessagingDev getMessangiDevFromJson(JSONObject resp){
        MessagingDev messagingDev =new MessagingDev();
        try {
            if(resp.has("id")){

                messagingDev.setId(resp.getString("id"));

            }
            if(resp.has("pushToken")){
                messagingDev.setPushToken(resp.getString("pushToken"));
            }
            if(resp.has("userId")){
                messagingDev.setUserId(resp.getString("userId"));
            }
            if(resp.has("type")){
                messagingDev.setType(resp.getString("type"));
            }
            if(resp.has("language")){
                messagingDev.setLanguage(resp.getString("language"));
            }
            if(resp.has("model")){
                messagingDev.setModel(resp.getString("model"));
            }
            if(resp.has("os")){
                messagingDev.setOs(resp.getString("os"));
            }
            if(resp.has("sdkVersion")){
                messagingDev.setSdkVersion(resp.getString("sdkVersion"));
            }
            if(resp.has("tags")){
                List<String> prvTag=new ArrayList<>();
                JSONArray jsonArray=resp.getJSONArray("tags");
                for (int i = 0; i < jsonArray.length(); i++) {
                    prvTag.add(jsonArray.getString(i));
                }
                messagingDev.setTags(prvTag);

            }
            if(resp.has("createdAt")){
                messagingDev.setCreatedAt(resp.getString("createdAt"));
            }
            if(resp.has("updatedAt")){
                messagingDev.setUpdatedAt(resp.getString("updatedAt"));
            }
            if(resp.has("timestamp")){
                messagingDev.setTimestamp(resp.getString("timestamp"));
            }
            if(resp.has("transaction")){
                messagingDev.setTransaction(resp.getString("transaction"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messagingDev;
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
