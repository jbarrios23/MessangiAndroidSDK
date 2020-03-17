package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.ogangi.messangi.sdk.network.model.MessangiDev;
import com.ogangi.messangi.sdk.network.model.MessangiUserDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageController {

    private static final String CLASS_TAG = StorageController.class.getSimpleName();

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

    public boolean hasTokenRegiter(String key){
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

    public void saveIdParameter(String key,String idDevice){

        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putString(key,idDevice);
        datosuser.apply();
        SdkUtils.showErrorLog(CLASS_TAG,"ID Parameter SAVED "+key+" "+idDevice);

    }

    public boolean isRegisterIdParamenter(String key){
        boolean hasToken = false;
        String token=mSharedPreferences.getString(key,"");
        if(token.length()>0){
            hasToken=true;

        }
        SdkUtils.showDebugLog(CLASS_TAG,"HAS ID PARAMETER "+hasToken);
        return hasToken;
    }

    public String getIdParameter(String key){

        String token=mSharedPreferences.getString(key,"");
        SdkUtils.showDebugLog(CLASS_TAG,token);

        return token;
    }

    public void deleteIdParameter(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }



    public void saveDevice(String key, MessangiDev value){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        Gson gson = new Gson();
        String jsonTags = gson.toJson(value);
        datosuser.putString(key,jsonTags);
        datosuser.apply();
        SdkUtils.showErrorLog(CLASS_TAG,"Device Saved "+key+" "+value.getId());

    }

    public boolean isRegisterDevice(String key){
        boolean hasToken = false;
        String token=mSharedPreferences.getString(key,"");
        if(token.length()>0){
            hasToken=true;

        }
        SdkUtils.showDebugLog(CLASS_TAG,"HAS Device PARAMETER "+hasToken);
        return hasToken;
    }

    public MessangiDev getDevice(String key){

        Gson gson = new Gson();
        String values=mSharedPreferences.getString(key,"");
        MessangiDev messangiDev=gson.fromJson(values,MessangiDev.class);
        SdkUtils.showDebugLog(CLASS_TAG,messangiDev.getId());
        SdkUtils.showDebugLog(CLASS_TAG,messangiDev.getUserId());
        return messangiDev;
    }

    public void deleteDeviceTags(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }


    public void saveUserByDevice(String key, MessangiUserDevice value){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        Gson gson = new Gson();
        String jsonTags = gson.toJson(value);
        datosuser.putString(key,jsonTags);
        datosuser.apply();
        SdkUtils.showErrorLog(CLASS_TAG,"UserByDevice Saved "+key+" "+value.getMobile());

    }

    public boolean isRegisterUserByDevice(String key){
        boolean hasToken = false;
        String token=mSharedPreferences.getString(key,"");
        if(token.length()>0){
            hasToken=true;

        }
        SdkUtils.showDebugLog(CLASS_TAG,"HAS UserByDevice PARAMETER "+hasToken);
        return hasToken;
    }

    public MessangiUserDevice getUserByDevice(String key){

        Gson gson = new Gson();
        String values=mSharedPreferences.getString(key,"");
        MessangiUserDevice messangiUserDevice=gson.fromJson(values,MessangiUserDevice.class);
        SdkUtils.showDebugLog(CLASS_TAG,messangiUserDevice.getMobile());
        return messangiUserDevice;
    }

    public void deleteUserByDevice(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }



}
