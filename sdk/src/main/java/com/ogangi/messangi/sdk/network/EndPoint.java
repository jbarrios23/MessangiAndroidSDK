package com.ogangi.messangi.sdk.network;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface EndPoint {

    @Headers({
            "Content-Type: application/json",

    })
    @GET("/TempDevice?")
    Call<MessangiDevice> getDeviceParameter(@Header("Token") String Token);

    @POST("/TempDevice")
    Call<MessangiDevice> postDeviceParameter(@Header("Token") String Token,
                                      @Body Map<String, String> body);

    @PUT
    Call<MessangiDevice> putDeviceParameter(@Url String url,
                                            @Header("Token") String Token,
                                            @Body Map<String, String> body);


}
