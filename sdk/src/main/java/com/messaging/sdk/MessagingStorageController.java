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
    private SharedPreferences mSharedPreferencesConfig;
    private Messaging messaging;
    private String nameMethod;

    public MessagingStorageController(Context context, Messaging messaging){

        this.context=context;
        this.messaging = messaging;
        this.nameMethod="";
        this.mSharedPreferences = context.getApplicationContext().getSharedPreferences("StorageCallback", 0);
        this.mSharedPreferencesConfig = context.getApplicationContext().getSharedPreferences("ConfigStorageCallback", 0);

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

    public void saveDevice(JSONObject value, String id, JSONObject provRequestBody){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        try {
            if(value.has(Messaging.MESSAGING_DEVICE_ID)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_ID, value.getString(Messaging.MESSAGING_DEVICE_ID));
            }else{
                datosuser.putString(Messaging.MESSAGING_DEVICE_ID, id);
            }
            if(value.has(Messaging.MESSAGING_PUSH_TOKEN)){
                datosuser.putString(Messaging.MESSAGING_PUSH_TOKEN, value.getString(Messaging.MESSAGING_PUSH_TOKEN));
            }else if(provRequestBody.has(Messaging.MESSAGING_PUSH_TOKEN)) {
                datosuser.putString(Messaging.MESSAGING_PUSH_TOKEN, provRequestBody.getString(Messaging.MESSAGING_PUSH_TOKEN));
            }
            if(value.has(Messaging.MESSAGING_USER_ID)){
                datosuser.putString(Messaging.MESSAGING_USER_ID, value.getString(Messaging.MESSAGING_USER_ID));
            }
            if(value.has(Messaging.MESSAGING_DEVICE_TYPE)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_TYPE, value.getString(Messaging.MESSAGING_DEVICE_TYPE));
            }else if(provRequestBody.has(Messaging.MESSAGING_DEVICE_TYPE)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_TYPE, provRequestBody.getString(Messaging.MESSAGING_DEVICE_TYPE));
            }
            if(value.has(Messaging.MESSAGING_DEVICE_LANGUAGE)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_LANGUAGE, value.getString(Messaging.MESSAGING_DEVICE_LANGUAGE));
            }else if(provRequestBody.has(Messaging.MESSAGING_DEVICE_LANGUAGE)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_LANGUAGE, provRequestBody.getString(Messaging.MESSAGING_DEVICE_LANGUAGE));
            }
            if(value.has(Messaging.MESSAGING_DEVICE_MODEL)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_MODEL, value.getString(Messaging.MESSAGING_DEVICE_MODEL));
            }
            if(value.has(Messaging.MESSAGING_DEVICE_OS)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_OS, value.getString(Messaging.MESSAGING_DEVICE_OS));
            }else if(provRequestBody.has(Messaging.MESSAGING_DEVICE_OS)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_OS, provRequestBody.getString(Messaging.MESSAGING_DEVICE_OS));
            }
            if(value.has(Messaging.MESSAGING_DEVICE_SDK_VERSION)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_SDK_VERSION, value.getString(Messaging.MESSAGING_DEVICE_SDK_VERSION));
            }else if(provRequestBody.has(Messaging.MESSAGING_DEVICE_SDK_VERSION)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_SDK_VERSION, provRequestBody.getString(Messaging.MESSAGING_DEVICE_SDK_VERSION));
            }

            if(value.has(Messaging.MESSAGING_DEVICE_TAGS)){
                JSONArray jsonArray=value.getJSONArray(Messaging.MESSAGING_DEVICE_TAGS);
                List<String> prvTag=new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    prvTag.add(jsonArray.getString(i));

                }
                Set<String> set=new HashSet<String>();
                set.addAll(prvTag);
                datosuser.putStringSet(Messaging.MESSAGING_DEVICE_TAGS,set);
            }else if(provRequestBody.has(Messaging.MESSAGING_DEVICE_TAGS)){
                JSONArray jsonArray=provRequestBody.getJSONArray(Messaging.MESSAGING_DEVICE_TAGS);
                List<String> prvTag=new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    prvTag.add(jsonArray.getString(i));

                }
                Set<String> set=new HashSet<String>();
                set.addAll(prvTag);
                datosuser.putStringSet(Messaging.MESSAGING_DEVICE_TAGS,set);
            }

            if(value.has(Messaging.MESSAGING_DEVICE_CREATE_AT)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_CREATE_AT, value.getString(Messaging.MESSAGING_DEVICE_CREATE_AT));
            }
            if(value.has(Messaging.MESSAGING_DEVICE_UPDATE_AT)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_UPDATE_AT, value.getString(Messaging.MESSAGING_DEVICE_UPDATE_AT));
            }
            if(value.has(Messaging.MESSAGING_DEVICE_TIMESTAMP)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_TIMESTAMP, value.getString(Messaging.MESSAGING_DEVICE_TIMESTAMP));
            }
            if(value.has(Messaging.MESSAGING_DEVICE_TRANSACTION)){
                datosuser.putString(Messaging.MESSAGING_DEVICE_TRANSACTION, value.getString(Messaging.MESSAGING_DEVICE_TRANSACTION));
            }

            datosuser.apply();
        }catch (JSONException e){
        messaging.utils.showErrorLog(this,nameMethod,"Error Device Saved in Storage Controller "+e.getMessage(),"");
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
        String token=mSharedPreferences.getString(Messaging.MESSAGING_DEVICE_ID,"");
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
        if(set!=null) {
            List<String> provTags = new ArrayList<String>(set);
            messagingDevice.setTags(provTags);
        }
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
        SharedPreferences.Editor data=mSharedPreferencesConfig.edit();
        data.putString("messagingHost",host);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"messagingHost Saved "+host);

    }
    /**
     * Method hasTokenRegister lets Know if messagingHost is registered in local storage
     *
     */
    public boolean hasMessagingHost(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        boolean hasToken = false;
        String token=mSharedPreferencesConfig.getString("messagingHost","");
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
        String token=mSharedPreferencesConfig.getString("messagingHost","");
        return token;
    }

    public void deleteMessagingConfig(){
        mSharedPreferencesConfig=context.getSharedPreferences("ConfigStorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferencesConfig.edit();
        editorlogin.clear();
        editorlogin.commit();
        messaging.utils.showDebugLog(this,nameMethod," Delete Config of Storage");
    }

    /**
     * Method save messagingHost
     * @param token: messagingHost
     */
    public void saveMessagingToken(String token){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferencesConfig.edit();
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
        String token=mSharedPreferencesConfig.getString("messagingToken","");
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
        String token=mSharedPreferencesConfig.getString("messagingToken","");
        messaging.utils.showDebugLog(this,nameMethod,"messagingToken from Storage ");
        return token;
    }



    /**
     * Method setAnalyticsAllowed
     * @param enable : enable
     */
    public void setAnalyticsAllowed(boolean enable){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferencesConfig.edit();
        data.putBoolean("AnalyticsAllowed",enable);
        data.putInt("AnalyticsAllowedInt",1);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"Set AnalyticsAllowed "+enable);

    }
    /**
     * Method hasTokenRegister lets Know if messagingHost is registered in local storage
     *
     */
    public int hasAnalyticsAllowed(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        int token=mSharedPreferencesConfig.getInt("AnalyticsAllowedInt",0);
        //messaging.utils.showDebugLog(this,nameMethod,"has AnalyticsAllowed "+token);
        return token;
    }

    public boolean isAnalyticsAllowed(){
        boolean result=mSharedPreferencesConfig.getBoolean("AnalyticsAllowed",false);
        //messaging.utils.showDebugLog(this,nameMethod,"Get AnalyticsAllowed from storage "+result);
        return result ;
    }

    /**
     * Method setLocationAllowed
     * @param enable : enable
     */
    public void setLocationAllowed(boolean enable){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferencesConfig.edit();
        data.putBoolean("LocationAllowed",enable);
        data.putInt("LocationAllowedInt",1);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"Set LocationAllowed "+enable);

    }

    public int hasLocationAllowed(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        int token=mSharedPreferencesConfig.getInt("LocationAllowedInt",0);
        //messaging.utils.showDebugLog(this,nameMethod,"has LocationAllowed "+token);
        return token;
    }

    public boolean isLocationAllowed(){
        boolean result=mSharedPreferencesConfig.getBoolean("LocationAllowed",false);
        //messaging.utils.showDebugLog(this,nameMethod,"Get LocationAllowed from storage "+result);
        return result ;
    }

    /**
     * Method setLoggingAllowed
     * @param enable : enable
     */
    public void setLoggingAllowed(boolean enable){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SharedPreferences.Editor data=mSharedPreferencesConfig.edit();
        data.putBoolean("LoggingAllowed",enable);
        data.putInt("LoggingAllowedInt",1);
        data.apply();
        messaging.utils.showDebugLog(this,nameMethod,"Set LoggingAllowed "+enable);

    }

    public int hasLoggingAllowed(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        int token=mSharedPreferencesConfig.getInt("LoggingAllowedInt",0);
        return token;
    }

    public boolean isLoggingAllowed(){
        boolean result=mSharedPreferencesConfig.getBoolean("LoggingAllowed",false);
        //messaging.utils.showDebugLog(this,nameMethod,"Get LoggingAllowed from storage "+result);
        return result;
    }



}
