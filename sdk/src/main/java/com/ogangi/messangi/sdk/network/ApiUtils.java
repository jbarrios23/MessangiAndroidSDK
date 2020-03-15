package com.ogangi.messangi.sdk.network;

import android.content.Context;

import com.ogangi.messangi.sdk.Messangi;

public class ApiUtils {

   public static Messangi messangi;

   public static EndPoint getSendMessageFCM(Context context){
        messangi=Messangi.getInstance(context);
        String url=messangi.getMessangi_host();
        String token=messangi.getMessangi_token();
        return RetrofitClient.getClient(url,token).create(EndPoint.class);
   }

    public static EndPoint getSendMessageFCMAlt(Context context){
        messangi=Messangi.getInstance(context);
        String url=messangi.getMessangi_host();
        return RetrofitClient.getClientAlt(url).create(EndPoint.class);
    }


}


