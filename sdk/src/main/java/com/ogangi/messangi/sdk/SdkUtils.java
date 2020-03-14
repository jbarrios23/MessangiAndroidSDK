package com.ogangi.messangi.sdk;

import android.util.Log;

public class SdkUtils {


    public static void showErrorLog(String tag,Object message){
        Log.e(tag, String.valueOf(message));
    }

    public static void showDebugLog(String tag,Object message){
        Log.d(tag, String.valueOf(message));
    }

    public static void showInfoLog(String tag,Object message){
        Log.i(tag, String.valueOf(message));
    }


}
