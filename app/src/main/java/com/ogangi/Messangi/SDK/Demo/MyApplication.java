package com.ogangi.Messangi.SDK.Demo;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class MyApplication extends Application {
    public static String CLASS_TAG=MyApplication.class.getSimpleName();
    public static String TAG="MESSAGING";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(CLASS_TAG,TAG+"App created "+this.getClass().getSimpleName());

    }



}
