package com.ogangi.messangi.sdk.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.ogangi.messangi.sdk.Messangi;
import com.ogangi.messangi.sdk.SdkUtils;
import com.ogangi.messangi.sdk.StorageController;
import com.ogangi.messangi.sdk.network.model.MessangiDev;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessangiServicesCenter {

    public static String CLASS_TAG=MessangiServicesCenter.class.getSimpleName();
    public static EndPoint endPoint;
    public static StorageController storageController;
    public static Messangi messangi;


    /**
     * Method that get Device registered
     @param serviceCallback interface for do somethink with service result
     @param context
     */
    public static void makeGetDevice(final ServiceCallback serviceCallback, Context context){
        Log.e(CLASS_TAG, "makeGetPetition "+context );
        endPoint= ApiUtils.getSendMessageFCM(context);


        if(storageController.isRegisterDevice("id")){
            String provId=storageController.getIdParameter("id");
            endPoint.getDeviceParameter(provId).enqueue(new Callback<MessangiDev>() {
                @Override
                public void onResponse(Call<MessangiDev> call, Response<MessangiDev> response) {
                    Log.e(CLASS_TAG, "response Device: "+new Gson().toJson(response.body()));
                    Log.e(CLASS_TAG, "response Device: "+response.message());
                    Log.e(CLASS_TAG, "response Device: "+response.body().getId());

                }

                @Override
                public void onFailure(Call<MessangiDev> call, Throwable t) {

                    SdkUtils.showErrorLog(CLASS_TAG,"onfailure get "+t.getCause());

                }
            });
        }else{

            SdkUtils.showInfoLog(CLASS_TAG,"doesn't have id device can't get ");
        }

    }
    /**
     * Method that post Device
     @param serviceCallback interface for do somethink with service result
     @param context
     @param lenguaje
     @param model
     @param pushToken
     @param sdkVersion
     @param os
     @param type
     */

    public static void makePostDataDevice(String pushToken,String type, String lenguaje,
                                String model, String os, String sdkVersion,
                                final ServiceCallback serviceCallback, Context context) {

        storageController=StorageController.getInstance(context);

        final JSONObject requestBody=new JSONObject();
        try {
            requestBody.put("pushToken",pushToken);
            requestBody.put("type",type);
            requestBody.put("language",lenguaje);
            requestBody.put("model",model);
            requestBody.put("os",os);
            requestBody.put("sdkVersion",sdkVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(CLASS_TAG,"Json "+requestBody.toString());

        endPoint= ApiUtils.getSendMessageFCM(context);

        endPoint.postDeviceParameter(requestBody).enqueue(new Callback<MessangiDev>() {
            @Override
            public void onResponse(Call<MessangiDev> call, Response<MessangiDev> response) {
                SdkUtils.showErrorLog(CLASS_TAG,"response post Device: "+new Gson().toJson(response.body()));
                String provId=response.body().getId();
                String provUserId=response.body().getUserId();
                String pusT=response.body().getPushToken();
                storageController.saveIdParameter("id",provId);
                storageController.saveIdParameter("userId",provUserId);
                storageController.saveDevice("MessangiDev",response.body());
                SdkUtils.showInfoLog(CLASS_TAG,pusT);
            }

            @Override
            public void onFailure(Call<MessangiDev> call, Throwable t) {
                SdkUtils.showErrorLog(CLASS_TAG,t.getMessage());
            }
        });


    }

    /**
     * Method that put Device
     @param serviceCallback interface for do somethink with service result
     @param context
     @param pushToken
     */

    public static void makeUpdateDevice(final ServiceCallback serviceCallback, Context context,String pushToken) {
        messangi=Messangi.getInstance(context);
        endPoint= ApiUtils.getSendMessageFCM(context);
        if(storageController.isRegisterIdParamenter("id")){
            String deviceId=storageController.getIdParameter("id");
            SdkUtils.showErrorLog(CLASS_TAG,deviceId);
            JSONObject requestUpdatebody=messangi.requestJsonBodyForUpdate(pushToken);
            endPoint.putDeviceParameter(deviceId,requestUpdatebody).enqueue(new Callback<MessangiDev>() {
                @Override
                public void onResponse(Call<MessangiDev> call, Response<MessangiDev> response) {
                    SdkUtils.showInfoLog(CLASS_TAG,"put Device sussecfull "+new Gson().toJson(response.body()));
                }

                @Override
                public void onFailure(Call<MessangiDev> call, Throwable t) {
                    SdkUtils.showErrorLog(CLASS_TAG,"onfailure put "+t.getMessage());
                }
            });

        }else{
            Log.e(CLASS_TAG, "doesn't have iddevice can't update  : ");
        }

    }
}
