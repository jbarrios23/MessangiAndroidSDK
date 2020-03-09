package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class StorageController {

    private static final String CLASS_TAG = StorageController.class.getName();

    private static StorageController mInstance;
    private static Context contexto;
    private SharedPreferences mSharedPreferences;

    public StorageController(Context context){

        this.contexto=context;
        this.mSharedPreferences = contexto.getApplicationContext().getSharedPreferences("StorageCallback", 0);

    }

    public static synchronized StorageController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StorageController(context);
        }
        contexto = context;
        return mInstance;
    }

    public static void reset() {
        mInstance = null;
    }

    public void saveToken(String key,String token){

        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putString(key,token);
        datosuser.apply();
        Log.e(CLASS_TAG,"TOKEN SAVED");

    }

    public boolean hasToken(String key){
        boolean hasToken = false;
        String token=mSharedPreferences.getString(key,"");
        if(token.length()>0){
            hasToken=true;

        }
        Log.e(CLASS_TAG,"has token "+hasToken);
        return hasToken;
    }

    public String getToken(String key){

        String token=mSharedPreferences.getString(key,"");
        Log.e(CLASS_TAG,""+token);
        return token;
    }

    public void deleteToken(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }

    public void saveIdDevice(String key,String idDevice){

        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putString(key,idDevice);
        datosuser.apply();
        Log.e(CLASS_TAG,"ID DEVICE SAVED");

    }

    public boolean hasIDDevice(String key){
        boolean hasToken = false;
        String token=mSharedPreferences.getString(key,"");
        if(token.length()>0){
            hasToken=true;

        }
        Log.e(CLASS_TAG,"has iddevice "+hasToken);
        return hasToken;
    }

    public String getIdDevice(String key){

        String token=mSharedPreferences.getString(key,"");
        Log.e(CLASS_TAG,""+token);
        return token;
    }

    public void deleteIddevice(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }



}
