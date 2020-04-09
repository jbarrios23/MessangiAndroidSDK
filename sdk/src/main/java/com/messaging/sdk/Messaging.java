package com.messaging.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;


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
import java.util.Locale;


/**
 * class Messaging let stablish Instances, handle services "get" and "create device" and LifecycleObserver
 * for handle event to foreground and background.
 */
public class Messaging implements LifecycleObserver{


    private static Messaging mInstance;
    private static Context context;
    public String Nameclass;
    public Activity activity;
    public int icon;



    private String pushToken;
    private String email;
    private String type;
    private String model;
    private String os;
    private ArrayList<String> tags;

    MessagingUserDevice messagingUserDevice;
    MessagingDev messagingDev;
    MessagingSdkUtils utils;
    public MessagingStorageController messagingStorageController;
    private String sdkVersion;
    private String lenguaje;
    private int identifier;
    private MessagingNotification messagingNotification;
    private ArrayList<MessagingNotification> messagingNotifications;
    private String nameMethod;
    private String packageName;



    public Messaging(Context context){
        this.context =context;
        this.utils=new MessagingSdkUtils();
        this.messagingStorageController =new MessagingStorageController(context,this);
        this.icon=-1;
        this.sdkVersion= BuildConfig.VERSION_NAME;
        this.lenguaje= Locale.getDefault().getDisplayLanguage();
        this.type="android";
        this.model=getDeviceName();
        this.os=Build.VERSION.RELEASE;
        this.initResource();
        this.tags=new ArrayList<String>();
        this.messagingUserDevice =null;
        this.messagingDev =null;
        this.identifier=0;
        this.messagingNotifications =new ArrayList<>();
        this.messagingNotification =null;
        this.nameMethod="";

    }

    public static synchronized Messaging getInst(Context context) {
        if (mInstance == null) {
            mInstance = new Messaging(context);
        }
        //context = context;
        return mInstance;
    }

    public static synchronized Messaging getInst() {

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
            messagingDev = messagingStorageController.getDevice();
            messagingDev.checkSdkVersion(context);
            identifier=1;
            if(!messagingStorageController.isNotificationManually() && messagingStorageController.hasTokenRegiter()
                    && messagingDev.getPushToken().equals("")){
                utils.showInfoLog(this,nameMethod,"Save device with token");
                messagingDev.setPushToken(messagingStorageController.getToken());
                messagingDev.save(context);
            }
        }else{
            utils.showErrorLog(this,nameMethod,"Device not found!","");

        }
        if(getLastMessangiNotifiction()!=null){
            sendEventToActivity(messagingNotification,context);
            setLastMessangiNotifiction(null);
        }

    }

    public String getPackageName() {
        return packageName;
    }



    /**
     * Method that initializes OnLifecycleEvent
     * EnterBackground
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        utils.showDebugLog(this,nameMethod,"Background");
    }

    public ArrayList<MessagingNotification> getMessagingNotifications() {

        return messagingNotifications;
    }
    /**
     * Method that Get Last Notification
     *
     */

    public MessagingNotification getLastMessangiNotifiction() {
        return messagingNotification;
    }


    /**
     * Method that Set Las notification
     * @param messagingNotification
     */
    public void setLastMessangiNotifiction(MessagingNotification messagingNotification) {

        this.messagingNotification = messagingNotification;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getPushToken() {
        return pushToken;
    }
    /**
     * Method that Set pushToken
     * @param pushToken
     */
    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public static void reset() {
        mInstance = null;
    }
    /**
     * Method that Get name of class principal
     */
    public String getNameclass() {
        return Nameclass;
    }
    /**
     * Method that Set name class
     * @param nameclass
     */
    public void setNameclass(String nameclass) {
        Nameclass = nameclass;
    }
    /**
     * Method that Set Firebase topic for use to Backend
     *
     */
    public void setFirebaseTopic(){

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

    public void setSdkVersion(String sdkVersion) {

        this.sdkVersion = sdkVersion;
    }

    public String getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }


    /**
     * Method get type of device
     *
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Method get OS of device
     */
    public String getOs() {

        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Method for init resources system from config file
     * setMessangiObserver init Observer for foreground and Background event
     * setFirebaseTopic init topic firebase for notification push
     */
    private void initResource(){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        setMessangiObserver();
        setFirebaseTopic();
        icon=context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName());
        packageName= context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            try {
                Nameclass = launchIntent.getComponent().getClassName();
            }catch (NullPointerException e){
                e.getStackTrace();
            }
        }
        utils.initResourcesConfigFile(context);

    }

    /**
     * Method for create device parameter and create device from FirebaseContenProvider
     * this method make update when pushTokem is getting by Services
     **/

    public void createDeviceParameters() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        String type=getType();
        utils.showInfoLog(this,nameMethod,"Create device type "+type);
        String lenguaje=getLenguaje();
        utils.showDebugLog(this,nameMethod,"Create device Lenguaje "+lenguaje);
        String model=getDeviceName();
        utils.showDebugLog(this,nameMethod,"Create Device model "+model);
        String os = getOs();
        utils.showDebugLog(this,nameMethod,"Create Device  OS "+ os);
        String sdkVersion=getSdkVersion();
        utils.showDebugLog(this,nameMethod,"Create Device SDK version "+ sdkVersion);
        pushToken= messagingStorageController.getToken();
        setPushToken(pushToken);
        createDevice(pushToken,type,lenguaje,model,os,sdkVersion);

    }

    /**
     * Method for get icon reference
     */

    public int getIcon() {
    return icon;
    }

    public void setIcon(int icon) {
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
    private void setMessangiObserver(){
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
    utils.showInfoLog(this,nameMethod,"setMessangiObserver ");
    }

    /**
     * Method that get Device registered
     @param forsecallservice: It allows effective device search in three ways: by instance, by shared variable or by service.
     */
    public void requestDevice(boolean forsecallservice){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        if(!forsecallservice && this.messagingDev !=null){
            utils.showDebugLog(this,nameMethod,"Device From RAM ");
            sendEventToActivity(messagingDev,context);

        }else{
            if(!forsecallservice && messagingStorageController.isRegisterDevice()){
                messagingDev = messagingStorageController.getDevice();
                utils.showDebugLog(this,nameMethod,"Device From Local Storage ");
                sendEventToActivity(messagingDev,context);
            }else{

                if(messagingStorageController.isRegisterDevice()){
                    utils.showDebugLog(this,nameMethod,"Device From Service ");
                    messagingDev = messagingStorageController.getDevice();
                    String provId= messagingDev.getId();
                    new HttpRequestTaskGet(provId).execute();
                }else{

                utils.showErrorLog(this,nameMethod,"Device not found! ",null);
                }

            }
        }

    }
    /**
     * Method that send Parameter (Ej: messagingDev or MessagingUserDevice) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(Serializable something, Context context) {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Intent intent=new Intent("PassDataFromSdk");
        intent.putExtra("message",something);
        if(something!=null){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            utils.showErrorLog(this,nameMethod,"Not Send Broadcast ",null);
        }

    }
    /**
     * Method create Device
     @param pushToken: token for notification push.
     @param type : type device
     @param lenguaje : languaje of device setting
     @param model : model of device
     @param os : operating system version
     @param sdkVersion: SDK version

     */
    private void createDevice(String pushToken, String type, String lenguaje,
                              String model, String os, String sdkVersion){

        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final JSONObject requestBody=new JSONObject();
        try {
            requestBody.put("pushToken",pushToken);
            requestBody.put("type",type);
            requestBody.put("language",lenguaje);
            requestBody.put("model",model);
            requestBody.put("os",os);
            requestBody.put("sdkVersion",sdkVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HTTPReqTaskPost(requestBody).execute();
    }

    private class HttpRequestTaskGet extends AsyncTask<Void,Void,String> {

        public String provIdDevice;
        private String server_response;
        private String provUrl;


        public HttpRequestTaskGet(String provId) {
            this.provIdDevice=provId;
        }


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                String authToken= MessagingSdkUtils.getMessangi_token();

                String param ="Bearer "+authToken;
                provUrl= MessagingSdkUtils.getMessangi_host()+"/v1/devices/"+provIdDevice;
                utils.showHttpRequestLog(provUrl,Messaging.this,nameMethod,"GET","");
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    sendEventToActivity(null,context);
                    utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,null);
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());

                }

            } catch (Exception e) {
                e.printStackTrace();
                utils.showErrorLog(this,nameMethod,"Exception ",e.getStackTrace().toString());
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
                    utils.showHttpResponsetLog(provUrl,Messaging.this,nameMethod,"Successful",response);
                    JSONObject resp=new JSONObject(response);
                    messagingDev =utils.getMessangiDevFromJson(resp);
                    //messagingStorageController.saveDevice(messagingDev);
                    messagingStorageController.saveDevice(resp);
                    sendEventToActivity(messagingDev,context);


                }
            }catch (NullPointerException e){
                sendEventToActivity(null,context);
                utils.showErrorLog(this,nameMethod,"Device not Get! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                sendEventToActivity(null,context);
                utils.showErrorLog(this,nameMethod,"Device not Get! JSONException",e.getStackTrace().toString());
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

    private class HTTPReqTaskPost extends AsyncTask<Void,Void,String>{

        private String server_response;
        private JSONObject provRequestBody;
        private String provUrl;
        public HTTPReqTaskPost(JSONObject requestBody) {
            this.provRequestBody=requestBody;
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;

            try {
                String authToken= MessagingSdkUtils.getMessangi_token();
                JSONObject postData = provRequestBody;
                provUrl= MessagingSdkUtils.getMessangi_host()+"/v1/devices/";
                utils.showHttpRequestLog(provUrl,Messaging.this,nameMethod,"POST",postData.toString());
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
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  201) {
                    sendEventToActivity(null,context);
                    utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,"");
                    throw new IOException("Invalid response from server: " + code);
                }


                if(code == HttpURLConnection.HTTP_CREATED){
                    server_response = readStream(urlConnection.getInputStream());

                }


            } catch (Exception e) {
                e.printStackTrace();
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
                    JSONObject resp=new JSONObject(response);
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    utils.showHttpResponsetLog(provUrl,Messaging.this,nameMethod,"Successful",response);
                    messagingDev =utils.getMessangiDevFromJson(resp);
                    messagingStorageController.saveDevice(resp);
                    if(messagingStorageController.hasTokenRegiter()&&
                            !messagingStorageController.isNotificationManually()){
                        String token= messagingStorageController.getToken();
                        messagingDev.setPushToken(token);
                        messagingDev.save(context);
                    }
                    sendEventToActivity(messagingDev,context);

                }
            }catch (NullPointerException e){
                sendEventToActivity(null,context);
                utils.showErrorLog(this,nameMethod,"Device not create! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                sendEventToActivity(null,context);
                utils.showErrorLog(this,nameMethod,"Device not create! JSONException ",e.getStackTrace().toString());
            }

        }
    }
}
