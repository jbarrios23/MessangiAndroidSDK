package com.messaging.sdk;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * MessagingProvider allows to initialize it automatically from the SDK itself
 */
public class MessagingProvider extends ContentProvider {
    public Messaging messaging;
    private String nameMethod;
    @Override
    public boolean onCreate() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging = Messaging.getInstance(getContext());
        messaging.utils.showDebugLog(this,nameMethod,"onCreate provider");
        if(!messaging.messagingStorageController.isRegisterDevice()){
            messaging.utils.showDebugLog(this,nameMethod,"Creating Device ");
            messaging.createDeviceParameters();
        }



        return false;
    }



    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


}
