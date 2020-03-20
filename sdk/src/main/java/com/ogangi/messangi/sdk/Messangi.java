package com.ogangi.messangi.sdk;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Messangi implements LifecycleObserver{


    private static Messangi mInstance;
    private static Context context;
    public String Nameclass;
    public Activity activity;
    public int icon;
    private static final int PERMISSION_REQUEST_CODE = 1;
    static EndPoint endPoint;

    public String wantPermission = Manifest.permission.READ_PHONE_STATE;


    private String pushToken;
    private String sdkVersion;
    private String lenguaje;
    private String externalId;
    private String email;
    private String phone;

    private String type;
    private String model;
    private String os;
    private ArrayList<String> tags;

    MessangiUserDevice messangiUserDevice;
    MessangiDev messangiDev;
    SdkUtils utils;
    public StorageController storageController;


    public Messangi(Context context){
        this.context =context;
        this.utils=new SdkUtils();
        this.storageController=new StorageController(context,this);
        this.sdkVersion="0";
        this.lenguaje="0";
        this.icon=-1;
        this.type="android";
        this.model=getDeviceName();
        this.os=Build.VERSION.RELEASE;
        this.initResource();
        this.tags=new ArrayList<String>();
        this.messangiUserDevice=null;
        this.messangiDev=null;

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

        utils.showErrorLog(this,"foreground");
        if(storageController.isRegisterDevice()){

            messangiDev=storageController.getDevice();
            utils.showInfoLog(this,"Tag "+messangiDev.getTags());
            messangiDev.verifiSdkVersion(context);


        }else{
            utils.showInfoLog(this,"Device not found!");

        }

    }

    /**
     * Method that initializes OnLifecycleEvent
     * EnterBackground
     */

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        utils.showErrorLog(this,"Background");
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

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public static void reset() {
        mInstance = null;
    }

    public String getNameclass() {
        return Nameclass;
    }

    public void setNameclass(String nameclass) {
        Nameclass = nameclass;
    }

    public void setFirebaseTopic(){
        //Log.e(this,"subscribeToTopic");
        FirebaseMessaging.getInstance().subscribeToTopic("topic_general");
    }
    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        //Log.e(this,"SET SDK VERSION");
        this.sdkVersion = sdkVersion;
    }

    public String getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }

    public String getExternalId(){
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        utils.showErrorLog(this,androidId);


        return androidId;
    }

    public String getEmail() {

        String gmail = null;

        Pattern gmailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (gmailPattern.matcher(account.name).matches()) {
                gmail = account.name;
            }
        }
        email=gmail;
        utils.showInfoLog(this," SEND Email "+ email);
        return email;
    }

    /**
     * Method that get Phone number of device
     * EnterForeground
     * @param activity
     */

//    public String getPhone(Activity activity){
//        this.activity=activity;
//        if(!checkPermission(wantPermission,context)){
//            requestPermission(wantPermission,PERMISSION_REQUEST_CODE,this.activity);
//        }else{
//            return getPhoneEspecific(activity);
//        }
//        return "";
//
//    }

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


    private void initResource(){
        setMessangiObserver();
        setFirebaseTopic();
        icon=context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName());
        Nameclass=context.getPackageName()+"."+ context.getClass().getSimpleName();
        utils.showErrorLog(this,Nameclass);
        
        utils.initResourcesConfigFile(context);

    }

//    @SuppressLint("HardwareIds")
//    public String getPhoneEspecific(Activity activity1) {
//        activity=activity1;
//        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(activity, wantPermission) != PackageManager.PERMISSION_GRANTED) {
//            return "";
//        }
//
//        try{
//            phone=phoneMgr.getLine1Number();
//
//        }catch (NullPointerException e){
//            phone="";//for previus version
//            Log.e(this,"NO TIENE NUMERO REGISTRADO "+ phone);
//
//        }catch (SecurityException e){
//            phone="";
//            Log.e(this,"NOT PERMISES PHONE NUMBER "+ phone);
//        }
//
//        Log.e(this,"FOR SENDING PHONE NUMBER "+ phone);
//
//        return phone;
//    }

    public void requestPermission(@NonNull String permission, int PERMISSION_REQUEST_CODE, Activity activity1){
        activity=activity1;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
                }
            });
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    public boolean checkPermission(String permission,Context context){
        //activity=activity1;
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }





    public void createDeviceParameters() {

        String type=getType();
        utils.showDebugLog(this,"create type "+type);
        String lenguaje=getLenguaje();
        utils.showDebugLog(this,"create Lenguaje "+lenguaje);
        String model=getDeviceName();
        utils.showDebugLog(this,"create model "+model);
        String os = getOs();
        utils.showDebugLog(this,"OS "+ os);
        String sdkVersion=getSdkVersion();
        utils.showDebugLog(this,"SDK version "+ sdkVersion);
        pushToken= storageController.getToken();
        setPushToken(pushToken);
        createDevice(pushToken,type,lenguaje,model,os,sdkVersion,context);

    }

    public int getIcon() {
    return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

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

    private void setMessangiObserver(){

    utils.showErrorLog(this,"setMessangiObserver ");
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Method that get Device registered
     @param forsecallservice
     */
    public void requestDevice(boolean forsecallservice){
        if(!forsecallservice && this.messangiDev!=null){
            utils.showErrorLog(this,"Device From RAM ");
            sendEventToActivity(messangiDev,context);
              //cambiar al boradcastreceiver
        }else{
            if(!forsecallservice && storageController.isRegisterDevice()){
                messangiDev=storageController.getDevice();
                utils.showErrorLog(this,"Device From Local Storage ");
                sendEventToActivity(messangiDev,context);
            }else{

                endPoint= ApiUtils.getSendMessageFCM(context);
                if(storageController.isRegisterDevice()){
                    utils.showErrorLog(this,"Device From Service ");
                    messangiDev=storageController.getDevice();
                    String provId=messangiDev.getId();
                    endPoint.getDeviceParameter(provId).enqueue(new Callback<MessangiDev>() {
                        @Override
                        public void onResponse(Call<MessangiDev> call, Response<MessangiDev> response) {

                            if(response.isSuccessful()){

                                utils.showErrorLog(this,"response Device: "+new Gson().toJson(response.body()));
                                messangiDev=response.body();
                                storageController.saveDevice(response.body());
                                sendEventToActivity(messangiDev,context);

                            }else{
                                int code=response.code();
                                sendEventToActivity(null,context);
                                utils.showErrorLog(this,"code for get device error "+code);

                            }


                        }

                        @Override
                        public void onFailure(Call<MessangiDev> call, Throwable t) {
                            sendEventToActivity(null,context);
                            utils.showErrorLog(this,"onfailure get "+t.getCause());


                        }
                    });
                }else{

                utils.showInfoLog(this,"Device not found! ");
                }

            }
        }

    }

    private void sendEventToActivity(Serializable something, Context context) {
        Intent intent=new Intent("PassDataFromoSdk");
        utils.showErrorLog(this,"Broadcasting message");
        intent.putExtra("message",something);
        if(something!=null){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            utils.showErrorLog(this,"Dont Send Broadcast ");
        }

    }

    private void createDevice(String pushToken, String type, String lenguaje,
                              String model, String os, String sdkVersion,
                               final Context context){

        JsonObject gsonObject = new JsonObject();
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

        JsonParser jsonParser=new JsonParser();
        gsonObject=(JsonObject) jsonParser.parse(requestBody.toString());

        endPoint= ApiUtils.getSendMessageFCM(context);
        endPoint.postDeviceParameter(gsonObject).enqueue(new Callback<MessangiDev>() {
            @Override
            public void onResponse(Call<MessangiDev> call, Response<MessangiDev> response) {
                if(response.isSuccessful()) {
                    utils.showErrorLog(this, "response post Device: " + new Gson().toJson(response.body()));
                    messangiDev=response.body();
                    storageController.saveDevice(response.body());
                    if(storageController.hasTokenRegiter()&&
                            !storageController.isNotificationManually()){
                        String token=storageController.getToken();
                        messangiDev.setPushToken(token);
                        messangiDev.save(context);
                    }
                    //llamar al BR
                    sendEventToActivity(messangiDev,context);

                }else{
                    int code=response.code();
                    utils.showErrorLog(this,"Error code post "+code);
                    //llamar al BR null
                    sendEventToActivity(null,context);
                }
            }

            @Override
            public void onFailure(Call<MessangiDev> call, Throwable t) {
                //llamar al BR
                sendEventToActivity(null,context);
                utils.showErrorLog(this,"Failure code post "+t.getMessage());
            }
        });
    }

}
