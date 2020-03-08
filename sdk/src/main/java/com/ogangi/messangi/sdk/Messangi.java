package com.ogangi.messangi.sdk;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class Messangi {

    private static final String CLASS_TAG = Messangi.class.getSimpleName();
    private static Messangi mInstance;
    private static Context contexto;
    public String Nameclass;
    public int sdkVersion;
    public String lenguaje;

    public String externalId;
    public String email;
    public String phone;
    public Activity activity;
    public Timer timer;
    public int icon;

    public Messangi(Context context){
        contexto=context;
        this.sdkVersion=0;
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
    public int getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(int sdkVersion) {
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

    public String getEmail() {
        return email;
    }

    @SuppressLint("MissingPermission")
    public String getPhone() {
        TelephonyManager tMgr = (TelephonyManager)contexto
                .getSystemService(Context.TELEPHONY_SERVICE);

        phone=tMgr.getLine1Number();
        Log.e(CLASS_TAG,"SEND PHONE NUMBER "+ phone);
        return phone;
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
        int sdkVersion = android.os.Build.VERSION.SDK_INT; // sdk version;
        setSdkVersion(sdkVersion);
        Log.e(CLASS_TAG, "SDK VERSION "+sdkVersion );
        if(getSdkVersion()==0||getSdkVersion()!=sdkVersion){
            setSdkVersion(sdkVersion);
            Log.e(CLASS_TAG, "SE ACTUALIZO LA VERSION DEL SDK " );

        }else{
            Log.e(CLASS_TAG, "no se actulizo el SDK Version " );
        }
        String lenguaje= Locale.getDefault().getDisplayLanguage();
        Log.e(CLASS_TAG, "DEVICE LENGUAJE "+lenguaje );

        if(getLenguaje().equals("0")||!getLenguaje().equals(lenguaje)){
            setLenguaje(lenguaje);
            Log.e(CLASS_TAG, "SE ACTUALIZO EL LENGUAJE DEL DISPOSITIVO " );

        }else{
            Log.e(CLASS_TAG, "no se actulizo el Lenguaje " );
        }

        getPhone();
        getExternalId();
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


}
