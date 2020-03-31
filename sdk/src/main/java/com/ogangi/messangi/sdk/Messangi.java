package com.ogangi.messangi.sdk;

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
 * class Messangi let stablish Instances, handle services "get" and "create device" and LifecycleObserver
 * for handle event to foreground and background.
 */
public class Messangi implements LifecycleObserver{


    private static Messangi mInstance;
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

    MessangiUserDevice messangiUserDevice;
    MessangiDev messangiDev;
    MessangiSdkUtils utils;
    public MessangiStorageController messangiStorageController;
    private String sdkVersion;
    private String lenguaje;
    private int identifier;
    private MessangiNotification messangiNotification;
    private ArrayList<MessangiNotification> messangiNotifications;


    public Messangi(Context context){
        this.context =context;
        this.utils=new MessangiSdkUtils();
        this.messangiStorageController =new MessangiStorageController(context,this);
        this.icon=-1;
        this.sdkVersion=BuildConfig.VERSION_NAME;
        this.lenguaje= Locale.getDefault().getDisplayLanguage();
        this.type="android";
        this.model=getDeviceName();
        this.os=Build.VERSION.RELEASE;
        this.initResource();
        this.tags=new ArrayList<String>();
        this.messangiUserDevice=null;
        this.messangiDev=null;
        this.identifier=0;
        this.messangiNotifications =new ArrayList<>();
        this.messangiNotification =null;

    }

    public static synchronized Messangi getInst(Context context) {
        if (mInstance == null) {
            mInstance = new Messangi(context);
        }
        //context = context;
        return mInstance;
    }

    public static synchronized Messangi getInst() {

        return mInstance;
    }

    /**
     * Method that initializes OnLifecycleEvent
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {

        utils.showDebugLog(this,"Foreground");
        if(messangiStorageController.isRegisterDevice()){
            messangiDev= messangiStorageController.getDevice();
            messangiDev.checkSdkVersion(context);
            identifier=1;
            if(!messangiStorageController.isNotificationManually() && messangiStorageController.hasTokenRegiter()
                    && messangiDev.getPushToken().equals("")){
                utils.showDebugLog(this,"save device with token");
                messangiDev.setPushToken(messangiStorageController.getToken());
                messangiDev.save(context);
            }
        }else{
            utils.showErrorLog(this,"Device not found!");

        }
        if(getLastMessangiNotifiction()!=null){
            sendEventToActivity(messangiNotification,context);
            setLastMessangiNotifiction(null);
        }

    }

    /**
     * Method that initializes OnLifecycleEvent
     * EnterBackground
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        utils.showDebugLog(this,"Background");
    }

    public ArrayList<MessangiNotification> getMessangiNotifications() {

        return messangiNotifications;
    }
    /**
     * Method that Get Last Notification
     *
     */

    public MessangiNotification getLastMessangiNotifiction() {
        return messangiNotification;
    }


    /**
     * Method that Set Las notification
     * @param messangiNotification
     */
    public void setLastMessangiNotifiction(MessangiNotification messangiNotification) {
        utils.showDebugLog(this,"Set notification");
        this.messangiNotification = messangiNotification;
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

        utils.showDebugLog(this,androidId);


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
        setMessangiObserver();
        setFirebaseTopic();
        icon=context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName());
        String packageName = context.getPackageName();
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

        String type=getType();
        utils.showDebugLog(this,"Create device type "+type);
        String lenguaje=getLenguaje();
        utils.showDebugLog(this,"Create device Lenguaje "+lenguaje);
        String model=getDeviceName();
        utils.showDebugLog(this,"Create Device model "+model);
        String os = getOs();
        utils.showDebugLog(this,"Create Device  OS "+ os);
        String sdkVersion=getSdkVersion();
        utils.showDebugLog(this,"Create Device SDK version "+ sdkVersion);
        pushToken= messangiStorageController.getToken();
        setPushToken(pushToken);
        createDevice(pushToken,type,lenguaje,model,os,sdkVersion,context);

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
    utils.showDebugLog(this,"setMessangiObserver ");
    }

    /**
     * Method that get Device registered
     @param forsecallservice: It allows effective device search in three ways: by instance, by shared variable or by service.
     */
    public void requestDevice(boolean forsecallservice){
        if(!forsecallservice && this.messangiDev!=null){
            utils.showDebugLog(this,"Device From RAM ");
            sendEventToActivity(messangiDev,context);

        }else{
            if(!forsecallservice && messangiStorageController.isRegisterDevice()){
                messangiDev= messangiStorageController.getDevice();
                utils.showDebugLog(this,"Device From Local Storage ");
                sendEventToActivity(messangiDev,context);
            }else{

                if(messangiStorageController.isRegisterDevice()){
                    utils.showDebugLog(this,"Device From Service ");
                    messangiDev= messangiStorageController.getDevice();
                    String provId=messangiDev.getId();
                    new HttpRequestTaskGet(provId).execute();
                }else{

                utils.showErrorLog(this,"Device not found! ");
                }

            }
        }

    }
    /**
     * Method that send Parameter (Ej: messangiDev or MessangiUserDevice) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(Serializable something, Context context) {
        Intent intent=new Intent("PassDataFromSdk");
        intent.putExtra("message",something);
        if(something!=null){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            utils.showErrorLog(this,"Not Send Broadcast ");
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
     @param context: context instance.
     */
    private void createDevice(String pushToken, String type, String lenguaje,
                              String model, String os, String sdkVersion,
                               final Context context){


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


        public HttpRequestTaskGet(String provId) {
            this.provIdDevice=provId;
        }


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                String authToken= MessangiSdkUtils.getMessangi_token();

                String param ="Bearer "+authToken;
                utils.showDebugLog(this,"Auth Token "+param);
                String provUrl= MessangiSdkUtils.getMessangi_host()+"/v1/devices/"+provIdDevice;
                utils.showDebugLog(this,"Url "+provUrl);
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    utils.showDebugLog(this,"response"+ server_response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                utils.showErrorLog(this,"Service error "+e.getMessage());
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
                    utils.showDebugLog(this, "response on Get " + response);
                    JSONObject resp=new JSONObject(response);
                    messangiDev=utils.getMessangiDevFromJson(resp);
                    //messangiStorageController.saveDevice(messangiDev);
                    messangiStorageController.saveDevice(resp);
                    sendEventToActivity(messangiDev,context);


                }
            }catch (NullPointerException e){
                utils.showErrorLog(this,"not created!");
            } catch (JSONException e) {
                e.printStackTrace();
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
        public HTTPReqTaskPost(JSONObject requestBody) {
            this.provRequestBody=requestBody;
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;

            try {
                String authToken= MessangiSdkUtils.getMessangi_token();
                JSONObject postData = provRequestBody;
                utils.showDebugLog(this,"JSON data Post "+postData.toString());
                String provUrl= MessangiSdkUtils.getMessangi_host()+"/v1/devices/";
                utils.showDebugLog(this,"Url "+provUrl);
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
                    utils.showDebugLog(this, "response on post create device " + resp.toString());
                    messangiDev=utils.getMessangiDevFromJson(resp);
                    messangiStorageController.saveDevice(resp);
                    if(messangiStorageController.hasTokenRegiter()&&
                            !messangiStorageController.isNotificationManually()){
                        String token= messangiStorageController.getToken();
                        messangiDev.setPushToken(token);
                        messangiDev.save(context);
                    }
                    sendEventToActivity(messangiDev,context);

                }
            }catch (NullPointerException e){
                utils.showErrorLog(this,"device not create!");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
