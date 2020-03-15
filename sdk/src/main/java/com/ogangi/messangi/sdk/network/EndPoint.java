package com.ogangi.messangi.sdk.network;


import com.google.gson.JsonObject;
import com.ogangi.messangi.sdk.network.model.MessangiDev;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EndPoint {

    @Headers({
            "Content-Type: application/json; charset=utf-8",

    })


    @GET("/v1/devices/{deviceId}")
    Call<MessangiDev> getDeviceParameter(@Path("deviceId") String deviceId);


    @POST("/v1/devices/")
    Call<MessangiDev> postDeviceParameter(@Body JSONObject body);

    @PUT("/v1/devices/{deviceId}")
    Call<MessangiDev> putDeviceParameter(@Path("deviceId") String deviceId
                                        ,@Body JSONObject body);


}
