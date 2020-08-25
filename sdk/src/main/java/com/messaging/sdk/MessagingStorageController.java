package com.messaging.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * class MessagingStorageController is a singleton to save in sharepreference data user and data device
 */
public class MessagingStorageController {

    //private static MessagingStorageController mInstance;
    private static Context context;
    private SharedPreferences mSharedPreferences;
    private Messaging messaging;
    private String nameMethod;

    public MessagingStorageController(Context context, Messaging messaging){

        this.context=context;
        this.messaging = messaging;
        this.nameMethod="";
        this.mSharedPreferences = context.getApplicationContext().getSharedPreferences("StorageCallback", 0);

    }
    /**
     * Method save token
     * @param token: token push of Firebase
     */
    public void saveToken(String token){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putString("Token",token);
        datosuser.apply();
        messaging.utils.showDebugLog(this,nameMethod,"Token Push Saved");

    }
    /**
     * Method hasTokenRegister lets Know if token is registered in local storage
     *
     */
    public boolean hasTokenRegister(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        boolean hasToken = false;
        String token=mSharedPreferences.getString("Token","");
        if(token.length()>0){
            hasToken=true;

        }
        //messaging.utils.showDebugLog(this,nameMethod,"Token Push from storage controller");
        return hasToken;
    }
    /**
     * Method get token registered in local storage
     *
     */
    public String getToken(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        String token=mSharedPreferences.getString("Token","");

        //messaging.utils.showDebugLog(this,nameMethod,"get token push"+token);
        return token;
    }

    public void deleteToken(){
        mSharedPreferences=context.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }

    /**
     * Method save Device registered in local storage
     *
     */

    public void saveDevice(JSONObject value){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        try {
            datosuser.putString("id", value.getString("id"));
            datosuser.putString("pushToken", value.getString("pushToken"));
            datosuser.putString("userId", value.getString("userId"));
            datosuser.putString("type", value.getString("type"));
            datosuser.putString("language", value.getString("language"));
            datosuser.putString("model", value.getString("model"));
            datosuser.putString("os", value.getString("os"));
            datosuser.putString("sdkVersion", value.getString("sdkVersion"));
            JSONArray jsonArray=value.getJSONArray("tags");
            List<String> prvTag=new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                prvTag.add(jsonArray.getString(i));

            }
            Set<String> set=new HashSet<String>();
            set.addAll(prvTag);
            datosuser.putStringSet("tags",set);
            datosuser.putString("createdAt", value.getString("createdAt"));
            datosuser.putString("updatedAt", value.getString("updatedAt"));
            datosuser.putString("timestamp", value.getString("timestamp"));
            datosuser.putString("transaction", value.getString("transaction"));
            datosuser.apply();
        }catch (JSONException e){

        }
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod,"Device Saved in Storage Controller ");

    }
    /**
     * Method isRegisterDevice lets Know if Device is registered in local storage
     *
     */
    public boolean isRegisterDevice(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        boolean hasToken = false;
        String token=mSharedPreferences.getString("id","");
        if(token.length()>0){
            hasToken=true;

        }
        //messaging.utils.showDebugLog(this,nameMethod,"From Local Storage isRegisterDevice "+hasToken);
        return hasToken;
    }
    /**
     * Method get Device registered in local storage
     *
     */
    public MessagingDevice getDevice(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

        String id=mSharedPreferences.getString("id","");
        String pushToken=mSharedPreferences.getString("pushToken","");
        String userId=mSharedPreferences.getString("userId","");
        String type=mSharedPreferences.getString("type","");
        String language=mSharedPreferences.getString("language","");
        String model=mSharedPreferences.getString("model","");
        String os=mSharedPreferences.getString("os","");
        String sdkVersion=mSharedPreferences.getString("sdkVersion","");
        Set<String> set=mSharedPreferences.getStringSet("tags",null);
        String createdAt=mSharedPreferences.getString("createdAt","");
        String updatedAt=mSharedPreferences.getString("updatedAt","");
        String timestamp=mSharedPreferences.getString("timestamp","");
        String transaction=mSharedPreferences.getString("transaction","");
        //create Object MessagingDev
        MessagingDevice messagingDevice =new MessagingDevice();
        messagingDevice.setId(id);
        messagingDevice.setPushToken(pushToken);
        messagingDevice.setUserId(userId);
        messagingDevice.setType(type);
        messagingDevice.setLanguage(language);
        messagingDevice.setModel(model);
        messagingDevice.setOs(os);
        messagingDevice.setSdkVersion(sdkVersion);
        List<String> provTags=new ArrayList<String>(set);
        messagingDevice.setTags(provTags);
        messagingDevice.setCreatedAt(createdAt);
        messagingDevice.setUpdatedAt(updatedAt);
        messagingDevice.setTimestamp(timestamp);
        messagingDevice.setTransaction(transaction);

        //messaging.utils.showDebugLog(this,nameMethod,"get Device from Local Storage ");
        return messagingDevice;
    }

    public void deleteDeviceTags(){
        mSharedPreferences=context.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }

    /**
     * Method save User By Device registered in local storage
     *
     */
    public void saveUserByDevice(Map<String,String> inputMap){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        JSONObject jsonObject = new JSONObject(inputMap);
        String jsonString = jsonObject.toString();
        datosuser.putString("MessagingUser",jsonString);
        datosuser.apply();
        messaging.utils.showDebugLog(this,nameMethod,"User Saved in Storage Controller ");

    }
    /**
     * Method isRegisterUserByDevice lets Know if Device is registered in local storage
     *
     */
    public boolean isRegisterUserByDevice(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        boolean hasToken = false;
        String token=mSharedPreferences.getString("MessagingUser","");
        if(token.length()>0){
            hasToken=true;

        }
        //messaging.utils.showDebugLog(this,nameMethod,"From Local Storage isRegisterUser "+hasToken);
        return hasToken;
    }

    /**
     * Method get User registered in local storage
     *
     */

    public Map<String,String> getUserByDevice(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Map<String,String> outputMap = new HashMap<String,String>();
        String jsonString=mSharedPreferences.getString("MessagingUser",(new JSONObject()).toString());
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keysItr = jsonObject.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                String value = (String) jsonObject.get(key);
                outputMap.put(key, value);
            }
            //messaging.utils.showDebugLog(this,nameMethod,"get User from Local Storage ");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //messaging.utils.showDebugLog(this,nameMethod,"get User from Local Storage ");
        return outputMap;
    }

    public void deleteUserByDevice(){
        mSharedPreferences=context.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }
    /**
     * Method setNotificationManually let enable Notification Manually
     * @param enable : enable
     */
    public void setNotificationManually(boolean enable){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putBoolean("DisableForUser",enable);
        datosuser.apply();
        messaging.utils.showDebugLog(this,nameMethod,"Set disable for user "+enable);

    }

    public boolean isNotificationManually(){

     return mSharedPreferences.getBoolean("DisableForUser",false);
    }

    /**
     * Method save messagingHost
     * @param host: messagingHost
     */
    public void saveMessagingHost(String host){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferences.edit();
        data.putString("messagingHost",host);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"messagingHost Saved");

    }
    /**
     * Method hasTokenRegister lets Know if messagingHost is registered in local storage
     *
     */
    public boolean hasMessagingHost(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        boolean hasToken = false;
        String token=mSharedPreferences.getString("messagingHost","");
        if(token.length()>0){
            hasToken=true;

        }

        return hasToken;
    }
    /**
     * Method get MessagingHost registered in local storage
     *
     */
    public String getMessagingHost(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        String token=mSharedPreferences.getString("messagingHost","");
        return token;
    }

    public void deleteMessagingHost(){
        mSharedPreferences=context.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }

    /**
     * Method save messagingHost
     * @param token: messagingHost
     */
    public void saveMessagingToken(String token){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferences.edit();
        data.putString("messagingToken",token);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"messagingToken Saved "+token);

    }
    /**
     * Method hasTokenRegister lets Know if messagingHost is registered in local storage
     *
     */
    public boolean hasMessagingToken(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        boolean hasToken = false;
        String token=mSharedPreferences.getString("messagingToken","");
        if(token.length()>0){
            hasToken=true;

        }

        return hasToken;
    }
    /**
     * Method get MessagingToken registered in local storage
     *
     */
    public String getMessagingToken(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        String token=mSharedPreferences.getString("messagingToken","");
        messaging.utils.showDebugLog(this,nameMethod,"messagingToken from Storage ");
        return token;
    }

    public void deleteMessagingToken(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        mSharedPreferences=context.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
        messaging.utils.showDebugLog(this,nameMethod," Delete messagingToken of Storage");
    }

    /**
     * Method setAnalyticsAllowed
     * @param enable : enable
     */
    public void setAnalyticsAllowed(boolean enable){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferences.edit();
        data.putBoolean("AnalyticsAllowed",enable);
        data.putInt("AnalyticsAllowed",1);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"Set AnalyticsAllowed "+enable);

    }
    /**
     * Method hasTokenRegister lets Know if messagingHost is registered in local storage
     *
     */
    public int hasAnalyticsAllowed(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        int token=mSharedPreferences.getInt("AnalyticsAllowed",0);
        return token;
    }

    public boolean isAnalyticsAllowed(){
        messaging.utils.showDebugLog(this,nameMethod,"Get AnalyticsAllowed from storage ");
        return mSharedPreferences.getBoolean("AnalyticsAllowed",false);
    }

    /**
     * Method setLocationAllowed
     * @param enable : enable
     */
    public void setLocationAllowed(boolean enable){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferences.edit();
        data.putBoolean("LocationAllowed",enable);
        data.putInt("LocationAllowed",1);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"Set LocationAllowed "+enable);

    }

    public int hasLocationAllowed(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        int token=mSharedPreferences.getInt("LocationAllowed",0);
        return token;
    }

    public boolean isLocationAllowed(){
        messaging.utils.showDebugLog(this,nameMethod,"Get LocationAllowed from storage ");
        return mSharedPreferences.getBoolean("LocationAllowed",false);
    }

    /**
     * Method setLoggingAllowed
     * @param enable : enable
     */
    public void setLoggingAllowed(boolean enable){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferences.edit();
        data.putBoolean("LoggingAllowed",enable);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"Set LoggingAllowed "+enable);

    }

    public boolean isLoggingAllowed(){
        return mSharedPreferences.getBoolean("LoggingAllowed",false);
    }



}
