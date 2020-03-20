package com.ogangi.messangi.sdk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessangiUserDevice implements Serializable {


    private  ArrayList<MessangiDev> devices;
//    @SerializedName("member since")
//    @Expose
//    private String memberSince;
//    @SerializedName("last updated")
//    @Expose
//    private String lastUpdated;
//    @SerializedName("mobile")
//    @Expose
//    private String mobile;
//    @SerializedName("timestamp")
//    @Expose
//    private String timestamp;
//    @SerializedName("transaction")
//    @Expose
//    private String transaction;

    @SerializedName("properties")
    @Expose
    private  final Map<String, Object> properties = new HashMap<>();

    protected String id;

    public void save(final Context context){

        final Messangi messangi=Messangi.getInst(context);
        final StorageController storageController=Messangi.getInst().storageController;
        EndPoint endPoint= ApiUtils.getSendMessageFCM(context);
        String deviceId=messangi.messangiDev.getId();
        messangi.utils.showErrorLog(this,deviceId);
        JsonObject gsonObject = new JsonObject();
        Map<String, Object> provPro=properties;
        JSONObject requestUpdatebody=new JSONObject(provPro);
        messangi.utils.showErrorLog(this,"requestUpdatebody "+requestUpdatebody.toString());
        JsonParser jsonParser=new JsonParser();
        gsonObject=(JsonObject) jsonParser.parse(requestUpdatebody.toString());
        endPoint.putUserByDeviceParameter(deviceId,gsonObject).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.isSuccessful()){
                        JsonObject jsonObject=response.body();
                        JsonObject data=jsonObject.getAsJsonObject("subscriber").getAsJsonObject("data");
                        Log.e("Update User", "update user good "+jsonObject);
                        Log.e("Update User", "data "+data);
                        Gson gson = new Gson();
                       //MessangiUserDevice messangiUserDevice = gson.fromJson(data, MessangiUserDevice.class);
                        Map<String, Object> retMap = gson.fromJson(
                                data, new TypeToken<HashMap<String, Object>>() {}.getType()
                        );
                        MessangiUserDevice messangiUserDevice=parseData(retMap);
                        storageController.saveUserByDevice(messangiUserDevice);
                        sendEventToActivity(messangiUserDevice,context);

                    }else{
                        int code=response.code();
                        messangi.utils.showErrorLog(this,"Code Update user error "+code);
                        sendEventToActivity(null,context);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    sendEventToActivity(null,context);
                    messangi.utils.showErrorLog(this,"onFailure "+t.getMessage());

                }
            });


    }

    public String getId() {

        return id;
    }

    public void addProperties(String key, Object value) {
        properties.put(key, value);
    }


    public Map<String, Object> getProperties() {
        return properties;
    }

    public ArrayList<MessangiDev> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<MessangiDev> devices) {
        this.devices = devices;
    }

//    public String getMemberSince() {
//        return memberSince;
//    }
//
//    public void setMemberSince(String memberSince) {
//        this.memberSince = memberSince;
//    }
//
//    public String getLastUpdated() {
//        return lastUpdated;
//    }
//
//    public void setLastUpdated(String lastUpdated) {
//        this.lastUpdated = lastUpdated;
//    }
//
//    public String getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(String mobile) {
//        this.mobile = mobile;
//    }
//
//    public String getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public String getTransaction() {
//        return transaction;
//    }
//
//    public void setTransaction(String transaction) {
//        this.transaction = transaction;
//    }

    private void sendEventToActivity(Serializable something, Context context) {
        Messangi messangi=Messangi.getInst(context);
        Intent intent=new Intent("PassDataFromoSdk");
        messangi.utils.showErrorLog(this,"Broadcasting message");
        intent.putExtra("message",something);
        if(something!=null){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            messangi.utils.showErrorLog(this,"Dont Send Broadcast ");
        }
    }

    public static MessangiUserDevice parseData(Map<String, Object> retMap){
        Messangi messangi=Messangi.getInst();
        MessangiUserDevice messangiUserDevice = new MessangiUserDevice();

        for (Map.Entry<String, Object> entry : retMap.entrySet()) {


            if (entry.getKey().equals("devices")) {
                Gson gson=new Gson();
                ArrayList<MessangiDev> devices = gson.fromJson(
                        (String ) retMap.get("devices"), new TypeToken<ArrayList<MessangiDev>>() {}.getType()
                );
                messangiUserDevice.setDevices(devices);
            } else {
                messangiUserDevice.addProperties(entry.getKey(), entry.getValue());
            }

        }

        return messangiUserDevice;

    }

    public String getEmail(){


        if(properties.containsKey("email")){
            return (String) properties.get("email");
        }

        return "";
    }

    public String getPhone(){

        if(properties.containsKey("phone")){
            return (String) properties.get("phone");
        }

        return "";
    }

    public String getExternalID(){


        if(properties.containsKey("externalID")){
            return (String) properties.get("externalID");
        }

        return "";
    }

    public void setEmail(String value){

        properties.put("email",value);

    }
    public void setPhone(String value){

        properties.put("phone",value);

    }

    public void setExternalID(String value){

    properties.put("externalID",value);

    }

    public String getProperty(String key){

        if(properties.containsKey(key)){
            return (String) properties.get(key);
        }

        return "";
    }


}
