package com.ogangi.messangi.sdk;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessangiUserDevice {

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
    private  final Map<String, Object> properties = new HashMap<>();

    private void save(Context context){

        final Messangi messangi=Messangi.getInst(context);
        final StorageController storageController=Messangi.getInst().storageController;
        EndPoint endPoint= ApiUtils.getSendMessageFCM(context);
        MessangiDev messangiDev=storageController.getDevice();
        String deviceId=messangi.messangiDev.getId();
        messangi.utils.showErrorLog(this,deviceId);
        JsonObject gsonObject = new JsonObject();
        Map<String, Object> provPro=getProperties();
        JSONObject requestUpdatebody=new JSONObject(provPro);
        messangi.utils.showErrorLog(this,"requestUpdatebody "+requestUpdatebody.toString());
        JsonParser jsonParser=new JsonParser();
        gsonObject=(JsonObject) jsonParser.parse(requestUpdatebody.toString());
        endPoint.putUserByDeviceParameter(deviceId,gsonObject).enqueue(new Callback<MessangiUserDevice>() {
                @Override
                public void onResponse(Call<MessangiUserDevice> call, Response<MessangiUserDevice> response) {
                    if(response.isSuccessful()){
                        MessangiUserDevice messangiUserDevice=response.body();
                        storageController.saveUserByDevice(messangiUserDevice);
                        messangi.utils.showInfoLog(this,"update user good ");
                        //llamar al BR
                    }else{
                        int code=response.code();
                        messangi.utils.showErrorLog(this,"Code Update user error "+code);
                        //llamar al BR
                    }
                }

                @Override
                public void onFailure(Call<MessangiUserDevice> call, Throwable t) {
                    messangi.utils.showErrorLog(this,"onFailure "+t.getMessage());

                }
            });


    }


    public void addProperties(String key, Object value) {
        properties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
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
