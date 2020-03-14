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
import com.ogangi.messangi.sdk.network.ServiceCallback;

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

public class Messangi implements ServiceCallback,LifecycleObserver{

    private static final String CLASS_TAG = Messangi.class.getSimpleName();
    private static Messangi mInstance;
    private static Context context;
    public String Nameclass;

    public Activity activity;
    public Timer timer;
    public int icon;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static EndPoint endPoint;
    public StorageController storageController;
    public String wantPermission = Manifest.permission.READ_PHONE_STATE;

    public int configFile;
    public String pushToken;

    public String sdkVersion;
    public String lenguaje;
    public String externalId;
    public String email;
    public String phone;

    public String type;
    public String model;
    public String os;

    public String messangi_host;
    public String messangi_token;
    public boolean analytics_allowed;
    public boolean location_allowed;
    public boolean logging_allowed;

    public Messangi(Context context){
        this.context =context;
        this.sdkVersion="0";
        this.lenguaje="0";
        this.icon=-1;
        this.storageController=StorageController.getInstance(Messangi.context);
        this.configFile=-1;
        this.type="ANDROID";
        this.model=getDeviceName();
        this.os=Build.VERSION.RELEASE; // os
        this.initResource();
        this.getExternalId();


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
     * EnterForeground
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.e(CLASS_TAG, "foreground");
        verifiSdkVersion();
    }

    /**
     * Method that initializes OnLifecycleEvent
     * EnterBackground
     */

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.e(CLASS_TAG, "Background");

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

    public void getExternalId(){
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        SdkUtils.showErrorLog(CLASS_TAG,androidId);
    }

    public void getPhone(Activity activity){
        this.activity=activity;
        if(!checkPermission(wantPermission,context)){
            requestPermission(wantPermission,PERMISSION_REQUEST_CODE,this.activity);
        }else{
            getPhoneEspecific(activity);
        }

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOs() {

        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }


    private void initResource(){
        setMessangiObserver();
        setFirebaseTopic();
        icon=context.getResources().getIdentifier("ic_launcher","mipmap",context.getPackageName());
        Nameclass= context.getPackageName()+"."+ context.getClass().getSimpleName();
        SdkUtils.showErrorLog(CLASS_TAG,Nameclass);
        try {
            int key_messagi_host = context.getResources()
                    .getIdentifier("messangi_host", "string", context.getPackageName());
            String messangi_host = context.getString(key_messagi_host);
            SdkUtils.showErrorLog(CLASS_TAG, messangi_host);
            int key_messangi_app_token = context.getResources()
                    .getIdentifier("messangi_app_token", "string", context.getPackageName());
            String messangi_app_token = context.getString(key_messangi_app_token);
            SdkUtils.showErrorLog(CLASS_TAG, messangi_app_token);
            int key_analytics_allowed = context.getResources()
                    .getIdentifier("analytics_allowed", "bool", context.getPackageName());
            boolean analytics_allowed = context.getResources().getBoolean(key_analytics_allowed);
            SdkUtils.showErrorLog(CLASS_TAG, analytics_allowed);
            int key_location_allowed = context.getResources()
                    .getIdentifier("location_allowed", "bool", context.getPackageName());
            boolean location_allowed = context.getResources().getBoolean(key_location_allowed);
            SdkUtils.showErrorLog(CLASS_TAG, location_allowed);
            int key_logging_allowed = context.getResources()
                    .getIdentifier("logging_allowed", "bool", context.getPackageName());
            boolean logging_allowed = context.getResources().getBoolean(key_logging_allowed);
            SdkUtils.showErrorLog(CLASS_TAG, logging_allowed);
        }catch (Resources.NotFoundException e){
            SdkUtils.showErrorLog(CLASS_TAG,"Hasn't congifg file");
        }

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
        //updateDevice(mInstance,"phone",phone);
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
        int sdkVersionInt = android.os.Build.VERSION.SDK_INT; // sdk version;
        String sdkVersion=String.valueOf(sdkVersionInt);
        Log.e(CLASS_TAG, "SDK VERSION "+sdkVersion );
        if(getSdkVersion().equals("0") || !getSdkVersion().equals(sdkVersion)){
            setSdkVersion(sdkVersion);
            Log.e(CLASS_TAG, "New SDK O SE ACTUALIZO LA VERSION DEL SDK " );

        }else{
            Log.e(CLASS_TAG, "no se actulizo el SDK Version " );
        }
        String lenguaje= Locale.getDefault().getDisplayLanguage();
        Log.e(CLASS_TAG, "DEVICE LENGUAJE "+lenguaje );
        if(getLenguaje().equals("0") || !getLenguaje().equals(lenguaje)){
            setLenguaje(lenguaje);
            Log.e(CLASS_TAG, "New Lenguaje O SE ACTUALIZO EL LENGUAJE DEL DISPOSITIVO " );


        }else{
            Log.e(CLASS_TAG, "no se actulizo el Lenguaje " );
        }



    }

    public void createParameters() {

//      String externalId=getExternalId();
//      Log.e(CLASS_TAG,"create externalID "+externalId);
        String type=getType();
        Log.e(CLASS_TAG,"create type "+type);
        String email=getEmail();
        Log.e(CLASS_TAG,"create email "+email);
//        String phone=getPhone(wantPermission);
//        Log.e(CLASS_TAG,"create Phone "+phone);
        String lenguaje=getLenguaje();
        Log.e(CLASS_TAG,"create Lenguaje "+lenguaje);
        String model=getDeviceName();
        Log.e(CLASS_TAG,"create model "+model);
        String os = getOs();
        Log.e(CLASS_TAG,"OS "+ os);
        String sdkVersion=getSdkVersion();
        Log.e(CLASS_TAG,"SDK version "+ sdkVersion);

        if(storageController.hasToken("Token")) {
            pushToken= storageController.getToken("Token");
            Log.e(CLASS_TAG,"create PushToken "+pushToken);
        }

        //postDataDevice(pushToken,externalId,type,email,phone,lenguaje,model,os,sdkVersion,mInstance);
        postDataDevice(pushToken,type,email,lenguaje,model,os,sdkVersion,mInstance);

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

    //Services
    /**
     * Method that get Device registered
     * EnterBackground
     */
    public  void makeGetDevice(final ServiceCallback context,Context contexto){
        Log.e(CLASS_TAG, "makeGetPetition "+context );
        endPoint= ApiUtils.getSendMessageFCM();
        String Token="ca02f42f504313228eee92da64dcd10e7f05cd77b85b0c467571aa41183de46c3f4cec0e6d5b79045018b90a32f402fbb2754d1e0b409cb4073c98b7d343859f";
        endPoint.getDeviceParameter(Token).enqueue(new Callback<MessangiDevice>() {
            @Override
            public void onResponse(Call<MessangiDevice> call, Response<MessangiDevice> response) {
                Log.e(CLASS_TAG, "response Device: "+new Gson().toJson(response.body()));
                context.handleData(new Gson().toJson(response.body()));
                context.handleIndividualData(response.body().getContent());
            }

            @Override
            public void onFailure(Call<MessangiDevice> call, Throwable t) {
                Log.e(CLASS_TAG, "onFailure: "+t.getMessage());
            }
        });



    }

    public void postDataDevice(String pushToken,String type, String email,
                               String lenguaje, String model, String os,
                               String sdkVersion,final ServiceCallback context) {

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pushToken",pushToken );
        requestBody.put("type",type );
        requestBody.put("email",email );
        requestBody.put("language",lenguaje );
        requestBody.put("model",model );
        requestBody.put("os",os );
        requestBody.put("sdkVersion",sdkVersion );



        Log.e(CLASS_TAG,"MAP "+requestBody.toString());

        endPoint= ApiUtils.getSendMessageFCM();
        String Token="ca02f42f504313228eee92da64dcd10e7f05cd77b85b0c467571aa41183de46c3f4cec0e6d5b79045018b90a32f402fbb2754d1e0b409cb4073c98b7d343859f";

        endPoint.postDeviceParameter(Token,requestBody).enqueue(new Callback<MessangiDevice>() {
            @Override
            public void onResponse(Call<MessangiDevice> call, Response<MessangiDevice> response) {
                Log.e(CLASS_TAG, "response post Device: "+new Gson().toJson(response.body()));
                if(response.body().getStatus().getCode()==200 && response.body().getStatus().getMessage().equals("Ok!")){
                    String reference=response.body().getReference();
                    Log.e(CLASS_TAG, "response post Device sucsses: "+reference);
                    storageController.saveIdDevice("IdDevice",reference);
                }
            }

            @Override
            public void onFailure(Call<MessangiDevice> call, Throwable t) {
                Log.e(CLASS_TAG, "onFailure post Device : "+t.getMessage());
            }
        });


    }

    private void updateDevice(final ServiceCallback context,String key,String value) {
        endPoint= ApiUtils.getSendMessageFCM();
        String Token="ca02f42f504313228eee92da64dcd10e7f05cd77b85b0c467571aa41183de46c3f4cec0e6d5b79045018b90a32f402fbb2754d1e0b409cb4073c98b7d343859f";
        if(storageController.hasIDDevice("IdDevice")){
            String deviceId=storageController.getIdDevice("IdDevice");
            String url= ApiUtils.BASE_URL+"/TempDevice/"+deviceId;
            Map<String, String> requestBody = new HashMap<>();
            //requestBody.put(key,value);
            requestBody.put("pushToken",value);
            requestBody.put("language",value);
            requestBody.put("model",value);
            requestBody.put("os",value);
            requestBody.put("sdkVersion",value);
//            requestBody.put("type",value);
//            requestBody.put("type",value);
//            requestBody.put("type",value);


            endPoint.putDeviceParameter(url,Token,requestBody).enqueue(new Callback<MessangiDevice>() {
                @Override
                public void onResponse(Call<MessangiDevice> call, Response<MessangiDevice> response) {
                    Log.e(CLASS_TAG, "response update Device: "+new Gson().toJson(response.body()));
                    if(response.body().getStatus().getCode()==200 && response.body().getStatus().getMessage().equals("Ok!")){
                        Log.e(CLASS_TAG, "response update Device sucsses: ");

                    }

                }

                @Override
                public void onFailure(Call<MessangiDevice> call, Throwable t) {

                    Log.e(CLASS_TAG, "onFailure put Device : "+t.getMessage());

                }
            });

        }else{
            Log.e(CLASS_TAG, "doesn't have iddevice can't update  : ");
        }




    }


    @Override
    public void handleData(Object result) {
        Log.e(CLASS_TAG, "resp: " + result);
    }

    @Override
    public void handleIndividualData(Object result) {

        List<Content> contentList= (List<Content>) result;
        Log.e(CLASS_TAG, "resp individual: " + contentList.size());


    }

}
