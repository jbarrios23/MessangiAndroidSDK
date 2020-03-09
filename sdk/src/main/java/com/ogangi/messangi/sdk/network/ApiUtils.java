package com.ogangi.messangi.sdk.network;

public class ApiUtils {

    public static final String BASE_URL = "http://api-seca.exiresoft.com.ve";
    public static final String BASE_URL_QA = "http://api-seca.exiresoft.com.ve";

    public static EndPoint getSendMessageFCM(){

        return RetrofitClient.getClient(BASE_URL).create(EndPoint.class);
    }

    public static EndPoint getSendMessageFCM1(){

        return RetrofitClient.getClient1(BASE_URL).create(EndPoint.class);
    }

    public static EndPoint getSendMessageFCMQA(){

        RetrofitClient.putNull();

        return RetrofitClient.getClient(BASE_URL_QA).create(EndPoint.class);
    }





}


