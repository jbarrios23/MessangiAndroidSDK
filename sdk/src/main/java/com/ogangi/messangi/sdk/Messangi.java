package com.ogangi.messangi.sdk;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
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

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.ogangi.messangi.sdk.network.ApiUtils;
import com.ogangi.messangi.sdk.network.Content;
import com.ogangi.messangi.sdk.network.EndPoint;
import com.ogangi.messangi.sdk.network.MessangiDevice;
import com.ogangi.messangi.sdk.network.MessangiServicesCenter;
import com.ogangi.messangi.sdk.network.ServiceCallback;
import com.ogangi.messangi.sdk.network.model.MessangiDev;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Messangi implements LifecycleObserver,ServiceCallback{

    private static final String CLASS_TAG = Messangi.class.getSimpleName();
    private static Messangi mInstance;
    private static Context context;
    public String Nameclass;
    public Activity activity;
    public int icon;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static EndPoint endPoint;
    public StorageController storageController;
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


    public Messangi(Context context){
        this.context =context;
        this.sdkVersion="0";
        this.lenguaje="0";
        this.icon=-1;
        this.storageController=StorageController.getInstance(Messangi.context);
        this.type="ANDROID";
        this.model=getDeviceName();
        this.os=Build.VERSION.RELEASE;
        this.initResource();




    }

    public static synchronized Messangi getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Messangi(context);
        }
        //context = context;
        return mInstance;
    }

    /**
     * Method that initializes OnLifecycleEvent
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.e(CLASS_TAG, "foreground");
        if(storageController.isRegisterDevice("MessangiDev")){
            MessangiDev messangiDev=storageController.getDevice("MessangiDev");
            verifiSdkVersion();
            MessangiServicesCenter.makeGetDevice(mInstance,context);
        }else{
            SdkUtils.showInfoLog(CLASS_TAG,"New Create Device");
            createDeviceParameters();
        }

    }

    /**
     * Method that initializes OnLifecycleEvent
     * EnterBackground
     */

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.e(CLASS_TAG, "Background");

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
        Log.e(CLASS_TAG,"subscribeToTopic");
        FirebaseMessaging.getInstance().subscribeToTopic("topic_general");
    }
    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        Log.e(CLASS_TAG,"SET SDK VERSION");
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

        SdkUtils.showErrorLog(CLASS_TAG,androidId);
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
        Log.e(CLASS_TAG," SEND Email "+ email);
        return email;
    }

    /**
     * Method that get Phone number of device
     * EnterForeground
     * @param activity
     */

    public void getPhone(Activity activity){
        this.activity=activity;
        if(!checkPermission(wantPermission,context)){
            requestPermission(wantPermission,PERMISSION_REQUEST_CODE,this.activity);
        }else{
            getPhoneEspecific(activity);
        }

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


    private void initResource(){
        setMessangiObserver();
        setFirebaseTopic();
        icon=context.getResources().getIdentifier("ic_launcher", "mipmap", context.getPackageName());
        Nameclass=context.getPackageName()+"."+ context.getClass().getSimpleName();
        SdkUtils.showErrorLog(CLASS_TAG,Nameclass);
        SdkUtils.initResourcesConfigFile(context);

    }

    @SuppressLint("HardwareIds")
    public String getPhoneEspecific(Activity activity1) {
        activity=activity1;
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, wantPermission) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }

        try{
            phone=phoneMgr.getLine1Number();

        }catch (NullPointerException e){
            phone="";//for previus version
            Log.e(CLASS_TAG,"NO TIENE NUMERO REGISTRADO "+ phone);

        }catch (SecurityException e){
            phone="";
            Log.e(CLASS_TAG,"NOT PERMISES PHONE NUMBER "+ phone);
        }

        Log.e(CLASS_TAG,"FOR SENDING PHONE NUMBER "+ phone);

        return phone;
    }

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

    public void verifiSdkVersion() {
        String sdkVersionInt = BuildConfig.VERSION_NAME; // sdk version;
        SdkUtils.showDebugLog(CLASS_TAG,"SDK VERSION "+sdkVersionInt);
        if(getSdkVersion().equals("0") || !getSdkVersion().equals(sdkVersionInt)){
            setSdkVersion(sdkVersionInt);
            SdkUtils.showDebugLog(CLASS_TAG,"New SDK O SE ACTUALIZO LA VERSION DEL SDK ");
            subcribeDevice();
        }else{

            SdkUtils.showDebugLog(CLASS_TAG,"No se actulizo el SDK Version ");
        }
        String lenguaje= Locale.getDefault().getDisplayLanguage();
        SdkUtils.showInfoLog(CLASS_TAG,"DEVICE LENGUAJE "+lenguaje);
        if(getLenguaje().equals("0") || !getLenguaje().equals(lenguaje)){
            setLenguaje(lenguaje);
            SdkUtils.showDebugLog(CLASS_TAG,"New Lenguaje O SE ACTUALIZO EL LENGUAJE DEL DISPOSITIVO ");
            try {
                Thread.sleep(3000);
                subcribeDevice();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{

            SdkUtils.showDebugLog(CLASS_TAG,"No se actulizo el Lenguaje ");
        }

    }

    public void unsubcribeDevice(){
        setPushToken("");
        MessangiServicesCenter.makeUpdateDevice(this,context,getPushToken());
    }

    public void subcribeDevice(){
        String token=storageController.getToken("Token");
        MessangiServicesCenter.makeUpdateDevice(this,context,token);
    }

    public void createDeviceParameters() {

        String type=getType();
        SdkUtils.showDebugLog(CLASS_TAG,"create type "+type);
        String lenguaje=getLenguaje();
        SdkUtils.showDebugLog(CLASS_TAG,"create Lenguaje "+lenguaje);
        String model=getDeviceName();
        SdkUtils.showDebugLog(CLASS_TAG,"create model "+model);
        String os = getOs();
        SdkUtils.showDebugLog(CLASS_TAG,"OS "+ os);
        String sdkVersion=getSdkVersion();
        SdkUtils.showDebugLog(CLASS_TAG,"SDK version "+ sdkVersion);
        if(storageController.hasTokenRegiter("Token")) {
            pushToken= storageController.getToken("Token");
            setPushToken(pushToken);
            MessangiServicesCenter.makePostDataDevice(pushToken,type,lenguaje,model,os,sdkVersion,mInstance,context);
        }else{
            SdkUtils.showErrorLog(CLASS_TAG,"hasn't Token");
        }

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
    Log.e(CLASS_TAG, "setMessangiObserver ");
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }



    public JSONObject requestJsonBodyForUpdate(String pushToken){

        externalId=getExternalId();
        email=getEmail();
        JSONObject requestBody=new JSONObject();
        try {
            if(!pushToken.equals("")) {
                requestBody.put("pushToken", pushToken);
                requestBody.put("type", type);
                requestBody.put("language", lenguaje);
                requestBody.put("model", model);
                requestBody.put("os", os);
                requestBody.put("sdkVersion", sdkVersion);
            }else{
                requestBody.put("pushToken", pushToken);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SdkUtils.showInfoLog(CLASS_TAG,"Json for update "+requestBody.toString());
        return requestBody;
    }

    @Override
    public void handleData(Object result) {

    }

    @Override
    public void handleIndividualData(Object result) {

    }
}
