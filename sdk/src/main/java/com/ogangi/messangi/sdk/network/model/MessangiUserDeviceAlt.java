package com.ogangi.messangi.sdk.network.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessangiUserDeviceAlt {

    @SerializedName("devices")
    @Expose
    private String devices;
    @SerializedName("member since")
    @Expose
    private String memberSince;
    @SerializedName("last updated")
    @Expose
    private String lastUpdated;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("transaction")
    @Expose
    private String transaction;

    @SerializedName("userIdDevice")
    @Expose
    private  final JSONObject userIdDevice = new JSONObject();

    public void addUserIdDevice(String key, Object value)
    {
        try {
            userIdDevice.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getUserIdDevice() {
        return userIdDevice;
    }

    public String getDevices() {
        return devices;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

}
