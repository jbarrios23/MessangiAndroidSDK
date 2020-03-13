package com.ogangi.Messangi.SDK.Demo;

import android.app.Application;
import android.util.Log;

import com.ogangi.messangi.sdk.Messangi;

public class MyApplication extends Application {
    public static String CLASS_TAG=MyApplication.class.getSimpleName();
    public Messangi messangi;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(CLASS_TAG,"App created "+this.getClass().getSimpleName());
        messangi=Messangi.getInstance(this);

    }

}
