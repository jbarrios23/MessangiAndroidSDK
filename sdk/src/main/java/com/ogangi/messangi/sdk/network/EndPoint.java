package com.ogangi.messangi.sdk.network;


import com.ogangi.messangi.sdk.network.model.MessangiDev;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface EndPoint {

    @Headers({
            "Content-Type: application/json;charset=UTF-8",

    })


    @GET("/v1/devices/{deviceId}")
    Call<MessangiDev> getDeviceParameter(@Path("deviceId") String deviceId);

//    @POST("/v1/devices/")
//    Call<MessangiDev> postDeviceParameter(@Body Map<String, String> body);

    @POST("/v1/devices/")
    Call<MessangiDev> postDeviceParameter(@Body JSONObject body);

    @PUT
    Call<MessangiDevice> putDeviceParameter(@Url String url,
                                            @Header("Token") String Token,
                                            @Body Map<String, String> body);


}
