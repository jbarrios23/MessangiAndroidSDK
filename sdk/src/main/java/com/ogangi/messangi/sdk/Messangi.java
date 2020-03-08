package com.ogangi.messangi.sdk;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class Messangi {

    private static final String CLASS_TAG = Messangi.class.getSimpleName();
    private static Messangi mInstance;
    private static Context contexto;
    public String Nameclass;
    public String sdkVersion;
    public String lenguaje;

    public String externalId;
    public String email;
    public String phone;
    public Activity activity;
    public Timer timer;
    public int icon;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    public Messangi(Context context){
        contexto=context;
        this.sdkVersion="0";
        this.lenguaje="0";
        this.icon=-1;


    }

    public static synchronized Messangi getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Messangi(context);
        }
        contexto = context;
        return mInstance;
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

    @SuppressLint("MissingPermission")
    public String getExternalId() {
        TelephonyManager tMgr = (TelephonyManager)contexto
                .getSystemService(Context.TELEPHONY_SERVICE);

        externalId=tMgr.getDeviceId();
        Log.e(CLASS_TAG," SEND IMEI "+ externalId);
        return externalId;
    }

    public String getEmail(Context context) {

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

//    @SuppressLint({"MissingPermission", "HardwareIds"})
//    public String getPhone() {
//        TelephonyManager tMgr = (TelephonyManager)contexto
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        try{
//            phone=tMgr.getLine1Number();
//        }catch (NullPointerException e){
//           phone="No tiene numero registtrado";
//        }
//
//        Log.e(CLASS_TAG,"SEND PHONE NUMBER "+ phone);
//        return phone;
//    }

    @SuppressLint("HardwareIds")
    public String getPhone(String wantPermission) {
        TelephonyManager phoneMgr = (TelephonyManager) contexto.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, wantPermission) != PackageManager.PERMISSION_GRANTED) {
            return "No tiene numero registrado";
        }

        try{
            phone=phoneMgr.getLine1Number();
            if(phone.equals("")){
                phone="No tiene numero registrado";
            }
        }catch (NullPointerException e){
           phone="No tiene numero registrado";
        }

        Log.e(CLASS_TAG,"FOR SENDING PHONE NUMBER "+ phone);
        return phone;
    }

    public void requestPermission(String permission, int PERMISSION_REQUEST_CODE,Activity activity1){
        activity=activity1;
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            Toast.makeText(activity, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    public boolean checkPermission(String permission,Activity activity1){
        activity=activity1;
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

//    public String getConfigValue(Context context, String name) {
//        Resources resources = context.getResources();
//
//        try {
//            InputStream rawResource = resources.openRawResource(R.raw.config);
//            Properties properties = new Properties();
//            properties.load(rawResource);
//            return properties.getProperty(name);
//        } catch (Resources.NotFoundException e) {
//            Log.e(CLASS_TAG, "Unable to find the config file: " + e.getMessage());
//        } catch (IOException e) {
//            Log.e(CLASS_TAG, "Failed to open config file.");
//        }
//
//        return null;
//    }

    public  String getMetaData(Context context, String name) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unable to load meta-data: " + e.getMessage());
        }
        return null;
    }

    private void verifiSdkVersion() {
        int sdkVersionInt = android.os.Build.VERSION.SDK_INT; // sdk version;
        String sdkVersion=String.valueOf(sdkVersionInt);

        Log.e(CLASS_TAG, "SDK VERSION "+sdkVersion );
        if(getSdkVersion().equals("0") || !getSdkVersion().equals(sdkVersion)){
            setSdkVersion(sdkVersion);
            Log.e(CLASS_TAG, "SE ACTUALIZO LA VERSION DEL SDK " );
        }else{
            Log.e(CLASS_TAG, "no se actulizo el SDK Version " );
        }
        String lenguaje= Locale.getDefault().getDisplayLanguage();
        Log.e(CLASS_TAG, "DEVICE LENGUAJE "+lenguaje );

        if(getLenguaje().equals("0") || !getLenguaje().equals(lenguaje)){
            setLenguaje(lenguaje);
            Log.e(CLASS_TAG, "SE ACTUALIZO EL LENGUAJE DEL DISPOSITIVO " );

        }else{
            Log.e(CLASS_TAG, "no se actulizo el Lenguaje " );
        }

//        getPhone();
//        getExternalId();
    }

    public void startTimerForSendData() {
        Log.e(CLASS_TAG,"timer activo ");
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.e(CLASS_TAG,"repeat "+timer.purge());
                verifiSdkVersion();
            }
        },20000,10000);


    }

    public void cancelTimerForSendData() {
        timer.cancel();
        Log.e(CLASS_TAG, "timer cancel " );
    }

    public int getIcon() {

        if(icon!=-1){
            return icon;
        }

        return R.drawable.common_full_open_on_phone;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    public String getType(){
        String type="ANDROID";
        return type;
    }

    public String getOS(){
        String os = Build.VERSION.RELEASE; // os
        return os;
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

    public void setMessangiObserver(LifecycleObserver context){
        ProcessLifecycleOwner.get().getLifecycle().addObserver(context);
    }






}
