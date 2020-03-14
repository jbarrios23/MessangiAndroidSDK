package com.ogangi.Messangi.SDK.Demo;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
    public static String CLASS_TAG=MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(CLASS_TAG,"App created "+this.getClass().getSimpleName());
    }

}
