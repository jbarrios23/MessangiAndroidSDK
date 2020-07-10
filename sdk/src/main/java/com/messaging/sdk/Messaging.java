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
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;


/**
 * class Messaging let stablish Instances, handle services "get" and "create device" and LifecycleObserver
 * for handle event to foreground and background.
 */
public class Messaging implements LifecycleObserver{


    private static Messaging mInstance;
    private Context context;
    public String Nameclass;
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
    MessagingSdkUtils utils;
    public MessagingStorageController messagingStorageController;
    private String sdkVersion;
    private String lenguaje;
    private int identifier;
    private MessagingNotification messagingNotification;
    private String nameMethod;
    private String packageName;

//    public static String ACTION_REGISTER_DEVICE="ActionRegisterDevice";
//    public static String ACTION_FETCH_DEVICE="ActionFetchDevice";
//    public static String ACTION_SAVE_DEVICE="ActionSaveDevice";
//    public static String ACTION_FETCH_USER="ActionFetchUser";
//    public static String ACTION_SAVE_USER="ActionSaveUser";
    //public static String ACTION_GET_NOTIFICATION="ActionGetNotification";

    public static String ACTION_REGISTER_DEVICE="com.messaging.sdk.ACTION_REGISTER_DEVICE";
    public static String ACTION_FETCH_DEVICE="com.messaging.sdk.ACTION_FETCH_DEVICE";
    public static String ACTION_SAVE_DEVICE="com.messaging.sdk.ACTION_SAVE_DEVICE";
    public static String ACTION_FETCH_USER="com.messaging.sdk.ACTION_FETCH_USER";
    public static String ACTION_SAVE_USER="com.messaging.sdk.ACTION_SAVE_USER";
    public static String ACTION_GET_NOTIFICATION="com.messaging.sdk.PUSH_NOTIFICATION";

    public static String INTENT_EXTRA_DATA="messaging_data";
    public static String INTENT_EXTRA_HAS_ERROR="messaging_haserror";



    public Messaging(Context context){
        this.context =context;
        this.utils=new MessagingSdkUtils();
        this.messagingStorageController =new MessagingStorageController(context,this);
        this.icon=-1;
        this.initResource();
        this.sdkVersion= BuildConfig.VERSION_NAME;
        this.lenguaje= Locale.getDefault().getDisplayLanguage();
        this.type="android";
        this.model=getDeviceName();
        this.os=Build.VERSION.RELEASE;
        this.tags=new ArrayList<String>();
        this.messagingUser =null;
        this.messagingDevice =null;
        this.identifier=0;
        this.messagingNotification =null;
        this.nameMethod="";


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
            identifier=1;
            if(!messagingStorageController.isNotificationManually() && messagingStorageController.hasTokenRegiter()
                    && messagingDevice.getPushToken().equals("")){
                utils.showInfoLog(this,nameMethod,"Save device with token");
                messagingDevice.setPushToken(messagingStorageController.getToken());
                messagingDevice.save(context);
            }
        }else{
            utils.showErrorLog(this,nameMethod,"Device not found!","");

        }
        if(getLastMessagingNotification()!=null){
            //sendEventToActivity(ACTION_GET_NOTIFICATION,messagingNotification,context);
            JSONObject response=toJSON(messagingNotification);
            sendEventToActivityTwo(ACTION_GET_NOTIFICATION,response,context);
            setLastMessagingNotification(null);
        }

    }
    public static JSONObject toJSON(Object object){
        String str = "";
        Class c = object.getClass();
        JSONObject jsonObject = new JSONObject();
        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();

            try {
                //String value = String.valueOf(field.get(object));
                jsonObject.put(name, field.get(object));
            } catch (JSONException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //return jsonObject.toString();
        return jsonObject;
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
    void setLastMessagingNotification(MessagingNotification messagingNotification) {

        this.messagingNotification = messagingNotification;
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
        return Nameclass;
    }
    /**
     * Method that Set name class
     * @param nameclass
     */
    private void setNameClass(String nameclass) {
        Nameclass = nameclass;
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

    public String getLenguaje() {
        return lenguaje;
    }

    private void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
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
                Nameclass = launchIntent.getComponent().getClassName();
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
    utils.showInfoLog(this,nameMethod,"setMessagingObserver ");
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
            JSONObject response=toJSON(messaging.messagingDevice);
            //messaging.sendEventToActivity(ACTION_FETCH_DEVICE,messaging.messagingDevice,context);
            messaging.sendEventToActivityTwo(ACTION_FETCH_DEVICE,response,context);

        }else{
            if(!forsecallservice && messaging.messagingStorageController.isRegisterDevice()){
                messaging.messagingDevice = messaging.messagingStorageController.getDevice();
                messaging.utils.showDebugLog(messaging,nameMethod,"Device From Local Storage ");
                JSONObject response=toJSON(messaging.messagingDevice);
                //messaging.sendEventToActivity(ACTION_FETCH_DEVICE,messaging.messagingDevice,context);
                messaging.sendEventToActivityTwo(ACTION_FETCH_DEVICE,response,context);
            }else{

                if(messaging.messagingStorageController.isRegisterDevice()){
                    messaging.utils.showDebugLog(messaging,nameMethod,"Device From Service ");
                    messaging.messagingDevice = messaging.messagingStorageController.getDevice();
                    String provId= messaging.messagingDevice.getId();
                    new HttpRequestTaskGet(provId,context).execute();
                }else{

                messaging.utils.showErrorLog(messaging,nameMethod,"Device not found! ",null);
                }

            }
        }

    }
    /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
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
     @param something: Object Json for send to activity (Ej MeesangiDev).
     @param context : context instance
     */

    private void sendEventToActivityTwo(String action,JSONObject something, Context context) {
        if(something!=null) {
            this.nameMethod = new Object() {
            }.getClass().getEnclosingMethod().getName();
            utils.showDebugLog(this, nameMethod, "Action " + action
                    + "  " + something.toString());
            Intent intent = new Intent(action);
            intent.putExtra(Messaging.INTENT_EXTRA_DATA, something.toString());
            intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR, something == null);
            context.sendBroadcast(intent, context.getPackageName() + ".permission.pushReceive");
            //context.sendBroadcast(intent);
        }else{
            this.nameMethod = new Object() {
            }.getClass().getEnclosingMethod().getName();
            utils.showErrorLog(this, nameMethod, "Action " + action
                    + " service error  ","" );
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

        new HTTPReqTaskPost(requestBody,this).execute();
    }

    private static class HttpRequestTaskGet extends AsyncTask<Void,Void,String> {

        public String provIdDevice;
        private String server_response;
        private String provUrl;
        private Messaging messaging;
        private String nameMethod;
        @SuppressLint("StaticFieldLeak")
        private Context context;


        public HttpRequestTaskGet(String provId,Context context) {
            this.provIdDevice=provId;
            this.messaging=Messaging.getInstance();
            this.context=context;


        }


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                //String authToken= MessagingSdkUtils.getMessaging_token();
                String authToken= MessagingSdkUtils.getMessagingToken();
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                String param ="Bearer "+authToken;
                //provUrl= MessagingSdkUtils.getMessangi_host()+"/devices/"+provIdDevice;
                provUrl= MessagingSdkUtils.getMessagingHost()+"/devices/"+provIdDevice;
                messaging.utils.showHttpRequestLog(provUrl,messaging,nameMethod,"GET","");
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    //messaging.sendEventToActivity(ACTION_FETCH_DEVICE,null,context);
                    messaging.sendEventToActivityTwo(ACTION_FETCH_DEVICE,null,context);
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
                    messaging.utils.showHttpResponsetLog(provUrl,messaging,nameMethod,"Successful",response);
                    JSONObject resp=new JSONObject(response);
                    messaging.messagingDevice =messaging.utils.getMessagingDevFromJson(resp);
                    //messagingStorageController.saveDevice(messagingDevice);
                    messaging.messagingStorageController.saveDevice(resp);

                    messaging.sendEventToActivityTwo(ACTION_FETCH_DEVICE,resp,context);
                    //messaging.sendEventToActivity(ACTION_FETCH_DEVICE,messaging.messagingDevice,context);


                }
            }catch (NullPointerException e){
                //messaging.sendEventToActivity(ACTION_FETCH_DEVICE,null,context);
                messaging.sendEventToActivityTwo(ACTION_FETCH_DEVICE,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Device not Get! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                //messaging.sendEventToActivity(ACTION_FETCH_DEVICE,null,context);
                messaging.sendEventToActivityTwo(ACTION_FETCH_DEVICE,null,context);
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
                //String authToken= MessagingSdkUtils.getMessaging_token();
                String authToken= MessagingSdkUtils.getMessagingToken();
                nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                JSONObject postData = provRequestBody;
                //provUrl= MessagingSdkUtils.getMessangi_host()+"/devices/";
                provUrl= MessagingSdkUtils.getMessagingHost()+"/devices/";
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
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  201) {
                    //messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,null,messaging.context);
                    messaging.sendEventToActivityTwo(ACTION_REGISTER_DEVICE,null,messaging.context);
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,"");
                    throw new IOException("Invalid response from server: " + code);
                }


                if(code == HttpURLConnection.HTTP_CREATED){
                    server_response = messaging.readStream(urlConnection.getInputStream());

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
                    messaging.utils.showHttpResponsetLog(provUrl,messaging,nameMethod,"Successful",response);
                    messaging.messagingDevice =messaging.utils.getMessagingDevFromJson(resp);
                    messaging.messagingStorageController.saveDevice(resp);
                    if(messaging.messagingStorageController.hasTokenRegiter()&&
                            !messaging.messagingStorageController.isNotificationManually()){
                        String token= messaging.messagingStorageController.getToken();
                        messaging.messagingDevice.setPushToken(token);
                        messaging.messagingDevice.save(messaging.context);
                    }
                    //messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,messaging.messagingDevice,messaging.context);
                    messaging.sendEventToActivityTwo(ACTION_REGISTER_DEVICE,resp,messaging.context);
                }
            }catch (NullPointerException e){
                //messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,null,messaging.context);
                messaging.sendEventToActivityTwo(ACTION_REGISTER_DEVICE,null,messaging.context);
                messaging.utils.showErrorLog(this,nameMethod,"Device not create! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                //messaging.sendEventToActivity(ACTION_REGISTER_DEVICE,null,messaging.context);
                messaging.sendEventToActivityTwo(ACTION_REGISTER_DEVICE,null,messaging.context);
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
            //messaging.sendEventToActivity(ACTION_FETCH_USER,messaging.messagingUser,context);
            JSONObject response=toJSON(messaging.messagingUser);
            messaging.sendEventToActivityTwo(ACTION_FETCH_USER,response,context);
        }else {
            if (!forsecallservice && messagingStorageController.isRegisterUserByDevice()) {
                messaging.utils.showDebugLog(messaging,nameMethod,"User From Local storage ");
                Map<String, String> resultMap= messagingStorageController.getUserByDevice();
                messaging.messagingUser = MessagingUser.parseData(resultMap) ;
                //messaging.sendEventToActivity(ACTION_FETCH_USER,messaging.messagingUser,context);
                JSONObject response=toJSON(messaging.messagingUser);
                messaging.sendEventToActivityTwo(ACTION_FETCH_USER,response,context);

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

                String authToken= MessagingSdkUtils.getMessagingToken();
                String param ="Bearer "+authToken;
                //provUrl= MessagingSdkUtils.getMessangi_host()+"/users?device="+deviceId;
                provUrl= MessagingSdkUtils.getMessagingHost()+"/users?device="+deviceId;
                messaging.utils.showHttpRequestLog(provUrl, messaging,nameMethod,"GET","");
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    //messaging.sendEventToActivity(ACTION_FETCH_USER,null,context);
                    messaging.sendEventToActivityTwo(ACTION_FETCH_USER,null,context);
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
                messaging.sendEventToActivityTwo(ACTION_FETCH_USER,null,context);
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
                    messaging.utils.showHttpResponsetLog(provUrl,this,nameMethod,"Successful",response);
                    JSONObject resp=new JSONObject(response);
                    Map<String, String> resultMap=toMap(resp);
                    messaging.messagingStorageController.saveUserByDevice(resultMap);
                    //MessagingUser messagingUser;
                    messaging.messagingUser = MessagingUser.parseData(resultMap);
                    //messagingUser.id = deviceId;
                    //messaging.sendEventToActivity(ACTION_FETCH_USER,messagingUser, context);
                    messaging.sendEventToActivityTwo(ACTION_FETCH_USER,resp, context);

                }
            }catch (NullPointerException e){
                //messaging.sendEventToActivity(ACTION_FETCH_USER,null,context);
                messaging.sendEventToActivityTwo(ACTION_FETCH_USER,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Get error User! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                //messaging.sendEventToActivity(ACTION_FETCH_USER,null,context);
                messaging.sendEventToActivityTwo(ACTION_FETCH_USER,null,context);
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




}
