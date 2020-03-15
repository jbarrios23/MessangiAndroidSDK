package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class SdkUtils {

    public static String CLASS_TAG=SdkUtils.class.getSimpleName();
    static int icon;
    public static String nameClass;
    private static String messangi_host;
    private static String messangi_token;
    private static boolean analytics_allowed;
    private static boolean location_allowed;
    private static boolean logging_allowed;

    public static void initResourcesConfigFile(Context context){

        try {

            int key_logging_allowed = context.getResources()
                    .getIdentifier("logging_allowed", "bool", context.getPackageName());
            logging_allowed = context.getResources().getBoolean(key_logging_allowed);
            SdkUtils.showErrorLog(CLASS_TAG, logging_allowed);
            int key_messagi_host = context.getResources()
                    .getIdentifier("messangi_host", "string", context.getPackageName());
            messangi_host = context.getString(key_messagi_host);
            SdkUtils.showErrorLog(CLASS_TAG, messangi_host);
            int key_messangi_app_token = context.getResources()
                    .getIdentifier("messangi_app_token", "string", context.getPackageName());
            messangi_token = context.getString(key_messangi_app_token);
            SdkUtils.showErrorLog(CLASS_TAG, messangi_token);
            int key_analytics_allowed = context.getResources()
                    .getIdentifier("analytics_allowed", "bool", context.getPackageName());
            analytics_allowed = context.getResources().getBoolean(key_analytics_allowed);
            SdkUtils.showErrorLog(CLASS_TAG, analytics_allowed);
            int key_location_allowed = context.getResources()
                    .getIdentifier("location_allowed", "bool", context.getPackageName());
             location_allowed = context.getResources().getBoolean(key_location_allowed);
            SdkUtils.showErrorLog(CLASS_TAG, location_allowed);

        }catch (Resources.NotFoundException e){
            SdkUtils.showErrorLog(CLASS_TAG,"Hasn't congifg file");
        }
    }


    public static void showErrorLog(String tag,Object message){
        if(logging_allowed) {
            Log.e(tag, String.valueOf(message));
        }
    }

    public static void showDebugLog(String tag,Object message){
        if(logging_allowed){
            Log.d(tag, String.valueOf(message));
        }

    }

    public static void showInfoLog(String tag,Object message){
        if(logging_allowed) {
            Log.i(tag, String.valueOf(message));
        }
    }

    public static String getMessangi_host() {
        return messangi_host;
    }

    public static void setMessangi_host(String messangi_host) {
        SdkUtils.messangi_host = messangi_host;
    }

    public static String getMessangi_token() {
        return messangi_token;
    }

    public static void setMessangi_token(String messangi_token) {
        SdkUtils.messangi_token = messangi_token;
    }


}
