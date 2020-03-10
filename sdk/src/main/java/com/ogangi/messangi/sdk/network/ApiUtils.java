package com.ogangi.messangi.sdk.network;

import android.content.Context;

import com.ogangi.messangi.sdk.Messangi;

public class ApiUtils {

    public static final String BASE_URL = "http://api-seca.exiresoft.com.ve";
    public static final String BASE_URL_QA = "http://api-seca.exiresoft.com.ve";
    public static Messangi messangi;

    public static EndPoint getSendMessageFCM(){

        return RetrofitClient.getClient(BASE_URL).create(EndPoint.class);
    }

    public static EndPoint getSendMessageFCM1(Context context){
        messangi=Messangi.getInstance(context);
        String url=messangi.getConfigValue(context,"api_host");
        return RetrofitClient.getClient(url).create(EndPoint.class);
    }

    public static EndPoint getSendMessageFCMQA(){

        RetrofitClient.putNull();

        return RetrofitClient.getClient(BASE_URL_QA).create(EndPoint.class);
    }







}


