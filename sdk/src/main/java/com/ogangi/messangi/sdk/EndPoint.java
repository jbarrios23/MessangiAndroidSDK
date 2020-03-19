package com.ogangi.messangi.sdk;


import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EndPoint {

    @Headers({"Content-type: application/json",
            "Accept: */*"})


    @GET("/v1/devices/{deviceId}")
    Call<MessangiDev> getDeviceParameter(@Path("deviceId") String deviceId);


    @POST("/v1/devices/")
    Call<MessangiDev> postDeviceParameter(@Body JsonObject body);

    @PUT("/v1/devices/{deviceId}")
    Call<MessangiDev> putDeviceParameter(@Path("deviceId") String deviceId
                                        ,@Body JsonObject body);

    // for device user

    @GET("/v1/users?")
    Call<Map<String, Object>> getUserByDevice(@Query("device") String deviceId);

    @PUT("/v1/users?")
    Call<JsonObject> putUserByDeviceParameter(@Query("device") String deviceId
                                               , @Body JsonObject body);

}
