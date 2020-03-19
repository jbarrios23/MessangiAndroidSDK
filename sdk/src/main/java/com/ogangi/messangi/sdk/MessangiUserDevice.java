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

import org.json.JSONException;
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

    @SerializedName("properties")
    @Expose
    private  final Map<String, Object> properties = new HashMap<>();

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
                        MessangiUserDevice messangiUserDevice = gson.fromJson(data, MessangiUserDevice.class);
                        Log.e("Update User", "MessangiUserDevice "+messangiUserDevice.getMobile());
                        Log.e("Update User", "MessangiUserDevice "+messangiUserDevice.getDevices());
                        storageController.saveUserByDevice(messangiUserDevice);
                        //llamar al BR
                        sendEventToActivity(messangiUserDevice,context);

                    }else{
                        int code=response.code();
                        messangi.utils.showErrorLog(this,"Code Update user error "+code);
                        //llamar al BR
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


    public void addProperties(String key, Object value) {
        properties.put(key, value);
    }
    public void addPropertiesPhone(Object value) {
        properties.put("mobile", value);
    }
    public void addPropertiesEmail(Object value) {
        properties.put("email", value);
    }
    public void addPropertiesExternalId(Object value) {
        properties.put("externalID", value);
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

    private void sendEventToActivity(Object something, Context context) {
        Messangi messangi=Messangi.getInst(context);
        Intent intent=new Intent("PassDataFromoSdk");
        Gson gson = new Gson();
        messangi.utils.showErrorLog(this,"Broadcasting message");
        if ((something instanceof MessangiDev) && (something!=null)){
            messangi.utils.showErrorLog(this,"was MesangiDev");
            String jsonSome = gson.toJson((MessangiDev)something);
            intent.putExtra("Message",jsonSome);
            intent.putExtra("Identifier",1);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else if((something instanceof MessangiUserDevice) && (something!=null)){
            messangi.utils.showErrorLog(this,"was MessangioUserDev");
            String jsonSome = gson.toJson((MessangiUserDevice)something);
            intent.putExtra("Message",jsonSome);
            intent.putExtra("Identifier",2);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else {
            messangi.utils.showErrorLog(this,"Dont Send Broadcast ");
        }
    }


}
