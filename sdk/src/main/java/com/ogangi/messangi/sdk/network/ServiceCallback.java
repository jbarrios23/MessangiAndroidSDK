package com.ogangi.messangi.sdk.network;

public interface ServiceCallback {

    void handleData(Object result);
    void handleIndividualData(Object result);

}
