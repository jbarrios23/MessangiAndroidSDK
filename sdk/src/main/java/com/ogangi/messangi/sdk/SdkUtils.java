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

    public void initResourcesConfigFile(Context context){

        try {

            int key_logging_allowed = context.getResources()
                    .getIdentifier("logging_allowed", "bool", context.getPackageName());
            logging_allowed = context.getResources().getBoolean(key_logging_allowed);
            showErrorLog(this, logging_allowed);
            int key_messagi_host = context.getResources()
                    .getIdentifier("messangi_host", "string", context.getPackageName());
            messangi_host = context.getString(key_messagi_host);
            showErrorLog(this, messangi_host);
            int key_messangi_app_token = context.getResources()
                    .getIdentifier("messangi_app_token", "string", context.getPackageName());
            messangi_token = context.getString(key_messangi_app_token);
            showErrorLog(this, messangi_token);
            int key_analytics_allowed = context.getResources()
                    .getIdentifier("analytics_allowed", "bool", context.getPackageName());
            analytics_allowed = context.getResources().getBoolean(key_analytics_allowed);
            showErrorLog(this, analytics_allowed);
            int key_location_allowed = context.getResources()
                    .getIdentifier("location_allowed", "bool", context.getPackageName());
             location_allowed = context.getResources().getBoolean(key_location_allowed);
            showErrorLog(this, location_allowed);

        }catch (Resources.NotFoundException e){
            showErrorLog(SdkUtils.class,"Hasn't congifg file");
        }
    }


    public  void showErrorLog(Object intans,Object message){
        if(logging_allowed) {
            Log.e(intans.getClass().getSimpleName(), String.valueOf(message));
        }
    }

    public  void showDebugLog(Object intans,Object message){
        if(logging_allowed){
            Log.d(intans.getClass().getSimpleName(), String.valueOf(message));
        }

    }

    public  void showInfoLog(Object intans,Object message){
        if(logging_allowed) {
            Log.i(intans.getClass().getSimpleName(), String.valueOf(message));
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
