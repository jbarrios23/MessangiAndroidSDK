package com.ogangi.messangi.sdk;

public interface ServiceCallback {

    void handlerGetMessangiDevice(MessangiDev result);
    void handlerGetMessangiUser(MessangiUserDevice result);
    void handlerPostDevice();

}
