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
        Log.e(CLASS_TAG,"App created ");
        messangi=Messangi.getInstance(this);
        messangi.setIcon(R.mipmap.ic_launcher);
        String name=getPackageName()+"."+MainActivity.class.getSimpleName();
        messangi.setNameclass(name);
        messangi.setFirebaseTopic();

    }
}
