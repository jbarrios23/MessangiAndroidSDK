package com.ogangi.messangi.sdk.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //This class will create a singleton of Retrofit

    private static Retrofit retrofit=null;

    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();


    public static Retrofit getClient(String Url){

        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(Url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        //Log.d("Retrofit", "Load Url "+ retrofit.baseUrl());
        Log.d("Retrofit", "Load Url "+ Url);
        return retrofit;
    }


    public static Retrofit getClient1(String Url){
        Gson gson  = new GsonBuilder().setLenient().create();
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(Url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        Log.d("Retrofit", "Load Url "+ retrofit.baseUrl());
        return retrofit;
    }

    public static Retrofit putNull(){


        retrofit=null;

        return retrofit;
    }


}
