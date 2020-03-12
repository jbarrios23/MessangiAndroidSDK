package com.ogangi.Messangi.SDK.Demo;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;


import com.ogangi.messangi.sdk.Messangi;
import com.ogangi.messangi.sdk.network.Content;
import com.ogangi.messangi.sdk.network.ServiceCallback;

import java.sql.Array;
import java.util.List;

public class MyApplication extends Application implements LifecycleObserver{
    public static String CLASS_TAG=MyApplication.class.getSimpleName();
    public Messangi messangi;
    public Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(CLASS_TAG,"App created "+this.getClass().getSimpleName());
        messangi=Messangi.getInstance(this);
        messangi.setIcon(R.mipmap.ic_launcher);
        //messangi.verifiSdkVersion();
        String name=getPackageName()+"."+MyApplication.class.getSimpleName();
        messangi.setNameclass(name);
        messangi.setFirebaseTopic();
        messangi.setMessangiObserver(this);
        //messangi.setConfigFile(R.raw.config);
        messangi.setMessangi_host(getString(R.string.messangi_host));
        messangi.setMessangi_token(getString(R.string.messangi_app_token));
        messangi.setAnalytics_allowed(getResources().getBoolean(R.bool.analytics_allowed));
        messangi.setLocation_allowed(getResources().getBoolean(R.bool.location_allowed));
        messangi.setLogging_allowed(getResources().getBoolean(R.bool.logging_allowed));


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.e(CLASS_TAG, "foreground");
        messangi.verifiSdkVersion();
        messangi.showConfigParamenter();
        // isAppInBackground(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.e(CLASS_TAG, "Background");
        //messangi.cancelTimerForSendData();
        //isAppInBackground(true);
        messangi.showConfigParamenter();
    }


}
