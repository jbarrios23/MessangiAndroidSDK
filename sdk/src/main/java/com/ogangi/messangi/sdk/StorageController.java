package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

public class StorageController {

    //private static StorageController mInstance;
    private static Context contexto;
    private SharedPreferences mSharedPreferences;
    private Messangi messangi;

    public StorageController(Context context,Messangi messangi){

        this.contexto=context;
        this.messangi=messangi;
        this.mSharedPreferences = contexto.getApplicationContext().getSharedPreferences("StorageCallback", 0);

    }

//    public static synchronized StorageController getInst(Context context) {
//        if (mInstance == null) {
//            mInstance = new StorageController(context);
//        }
//        contexto = context;
//        return mInstance;
//    }

//    public static void reset() {
//        mInstance = null;
//    }

    public void saveToken(String token){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putString("Token",token);
        datosuser.apply();
        messangi.utils.showErrorLog(this,"TOKEN SAVED");

    }

    public boolean hasTokenRegiter(){
        boolean hasToken = false;
        String token=mSharedPreferences.getString("Token","");
        if(token.length()>0){
            hasToken=true;

        }

        return hasToken;
    }

    public String getToken(){

        String token=mSharedPreferences.getString("Token","");

        messangi.utils.showErrorLog(this,token);
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
        messangi.utils.showErrorLog(this,"ID Parameter SAVED "+key+" "+idDevice);

    }

    public boolean isRegisterIdParamenter(String key){
        boolean hasToken = false;
        String token=mSharedPreferences.getString(key,"");
        if(token.length()>0){
            hasToken=true;

        }
        messangi.utils.showDebugLog(this,"HAS ID PARAMETER "+hasToken);
        return hasToken;
    }

    public String getIdParameter(String key){

        String token=mSharedPreferences.getString(key,"");
        messangi.utils.showDebugLog(this,token);

        return token;
    }

    public void deleteIdParameter(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }



    public void saveDevice(MessangiDev value){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        Gson gson = new Gson();
        String jsonTags = gson.toJson(value);
        datosuser.putString("MessangiDev",jsonTags);
        datosuser.apply();
        messangi.utils.showErrorLog(this,"Device Saved "+"MessangiDevice"+" "+value.getId());

    }

    public boolean isRegisterDevice(){
        boolean hasToken = false;
        String token=mSharedPreferences.getString("MessangiDev","");
        if(token.length()>0){
            hasToken=true;

        }
        messangi.utils.showDebugLog(this,"isRegiterDevice "+hasToken);
        return hasToken;
    }

    public MessangiDev getDevice(){

        Gson gson = new Gson();
        String values=mSharedPreferences.getString("MessangiDev","");
        MessangiDev messangiDev=gson.fromJson(values,MessangiDev.class);
        messangi.utils.showDebugLog(this,messangiDev.getId());
        messangi.utils.showDebugLog(this,messangiDev.getUserId());
        return messangiDev;
    }

    public void deleteDeviceTags(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }


    public void saveUserByDevice(MessangiUserDevice value){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        Gson gson = new Gson();
        String jsonTags = gson.toJson(value);
        datosuser.putString("MessangiUserDevice",jsonTags);
        datosuser.apply();
        messangi.utils.showErrorLog(this,"UserByDevice Saved ");

    }

    public boolean isRegisterUserByDevice(){
        boolean hasToken = false;
        String token=mSharedPreferences.getString("MessangiUserDevice","");
        if(token.length()>0){
            hasToken=true;

        }
        messangi.utils.showDebugLog(this,"has UserByDevice "+hasToken);
        return hasToken;
    }

    public MessangiUserDevice getUserByDevice(){

        Gson gson = new Gson();
        String values=mSharedPreferences.getString("MessangiUserDevice","");
        MessangiUserDevice messangiUserDevice=gson.fromJson(values,MessangiUserDevice.class);
        messangi.utils.showDebugLog(this,"get users by device");
        return messangiUserDevice;
    }

    public void deleteUserByDevice(){
        mSharedPreferences=contexto.getSharedPreferences("StorageCallback", 0);
        SharedPreferences.Editor editorlogin = mSharedPreferences.edit();
        editorlogin.clear();
        editorlogin.commit();
    }

    public void setNotificationManually(boolean enable){
        SharedPreferences.Editor datosuser=mSharedPreferences.edit();
        datosuser.putBoolean("DisableForUser",enable);
        datosuser.apply();
        messangi.utils.showErrorLog(this,"Set disable for user "+enable);

    }


    public boolean isNotificationManually(){

     return mSharedPreferences.getBoolean("DisableForUser",false);
    }

}
