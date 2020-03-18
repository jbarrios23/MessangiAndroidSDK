package com.ogangi.messangi.sdk;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessangiDev { // para hacer la prieba del BR
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("pushToken")
    @Expose
    private String pushToken;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("os")
    @Expose
    private String os;
    @SerializedName("sdkVersion")
    @Expose
    private String sdkVersion;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("transaction")
    @Expose
    private String transaction;


    /**
     * Method that Update Device
     @param context

     */

    public void save(Context context){

        final Messangi messangi=Messangi.getInst(context);
        final StorageController storageController=Messangi.getInst().storageController;
        EndPoint endPoint= ApiUtils.getSendMessageFCM(context);
        messangi.utils.showErrorLog(this," Id "+id+" pushToken "+pushToken);
        JsonObject gsonObject = new JsonObject();
        JSONObject requestUpdatebody=requestJsonBodyForUpdate(pushToken,context);
        JsonParser jsonParser=new JsonParser();
        gsonObject=(JsonObject) jsonParser.parse(requestUpdatebody.toString());
        endPoint.putDeviceParameter(id,gsonObject).enqueue(new Callback<MessangiDev>() {
                @Override
                public void onResponse(Call<MessangiDev> call, Response<MessangiDev> response) {
                    messangi.utils.showInfoLog(this,"put Device good "+new Gson().toJson(response.body()));
                    if(response.isSuccessful()){
                        storageController.saveDevice(response.body());
                        //llamado al BR para actualizar
                    }else{
                        int code=response.code();
                        messangi.utils.showErrorLog(this,"Code update error "+code);
                    }
                }

                @Override
                public void onFailure(Call<MessangiDev> call, Throwable t) {
                    messangi.utils.showErrorLog(this,"onfailure put "+t.getMessage());
                }
            });

    }

    /**
     * Method that get Device registered
     @param context
     */

    public void requestUserByDevice(Context context, boolean forsecallservice, final ServiceCallback serviceCallback){
        final Messangi messangi=Messangi.getInst(context);
        final StorageController storageController=Messangi.getInst().storageController;
        if(!forsecallservice && messangi.messangiUserDevice!=null){
            serviceCallback.handlerGetMessangiUser(messangi.messangiUserDevice); //cambiar al boradcastreceiver
        }else {
            if (!forsecallservice && storageController.isRegisterUserByDevice()) {
                messangi.messangiUserDevice = storageController.getUserByDevice();
                serviceCallback.handlerGetMessangiUser(messangi.messangiUserDevice);
            } else {

                EndPoint endPoint = ApiUtils.getSendMessageFCM(context);
                endPoint.getUserByDevice(id).enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful()) {
                            MessangiUserDevice messangiUserDevice = new MessangiUserDevice();
                            Map<String, Object> responseBody = response.body();
                            for (Map.Entry<String, Object> entry : responseBody.entrySet()) {
                                messangi.utils.showErrorLog(this, "Key = " + entry.getKey() +
                                        ", Value = " + entry.getValue());

                                if (entry.getKey().equals("devices")) {
                                    messangiUserDevice.setDevices((String) responseBody.get("devices"));
                                } else if (entry.getKey().contains("member since")) {
                                    messangiUserDevice.setMemberSince((String) responseBody.get("member since"));
                                } else if (entry.getKey().contains("last updated")) {
                                    messangiUserDevice.setLastUpdated((String) responseBody.get("last updated"));
                                } else if (entry.getKey().contains("mobile")) {
                                    messangiUserDevice.setMobile((String) responseBody.get("mobile"));
                                } else if (entry.getKey().contains("timestamp")) {
                                    messangiUserDevice.setTimestamp((String) responseBody.get("timestamp"));
                                } else if (entry.getKey().contains("transaction")) {
                                    messangiUserDevice.setTransaction((String) responseBody.get("transaction"));
                                } else {
                                    messangiUserDevice.addProperties(entry.getKey(), entry.getValue());

                                }

                            }

                            storageController.saveUserByDevice(messangiUserDevice);
                            serviceCallback.handlerGetMessangiUser(messangiUserDevice);
                            //llamado al BR


                        } else {
                            //llamado al BR
                            int code = response.code();
                            messangi.utils.showErrorLog(this, "code getUser by device " + code);
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                                messangi.utils.showErrorLog(this,"onFailure "+t.getMessage());
                                //llmado al BR
                    }
                });

            }
        }

    }

    public boolean isEnableNotificationPush(){
        return pushToken!=null && pushToken!="";
    }

    public void setStatusNotificationPush(boolean enable){
        final StorageController storageController=Messangi.getInst().storageController;
        storageController.setNotificationManually(true);
        if(storageController.hasTokenRegiter()&& enable){
            pushToken=storageController.getToken();
        }else{
            pushToken="";
        }

    }

    public void verifiSdkVersion(Context context) {
        final Messangi messangi=Messangi.getInst(context);
        String sdkVersionInt = BuildConfig.VERSION_NAME; // sdk version;
        messangi.utils.showDebugLog(this,"SDK VERSION "+sdkVersionInt);
        if(getSdkVersion().equals("0") || !getSdkVersion().equals(sdkVersionInt)){
            setSdkVersion(sdkVersionInt);
            messangi.utils.showDebugLog(this,"New SDK O SE ACTUALIZO LA VERSION DEL SDK ");
            save(context);
        }else{

            messangi.utils.showDebugLog(this,"No se actulizo el SDK Version ");
        }
        String lenguaje= Locale.getDefault().getDisplayLanguage();
        messangi.utils.showInfoLog(this,"DEVICE LENGUAJE "+lenguaje);
        if(getLanguage().equals("0") || !getLanguage().equals(lenguaje)){
            setLanguage(lenguaje);
            messangi.utils.showDebugLog(this,"New Lenguaje O SE ACTUALIZO EL LENGUAJE DEL DISPOSITIVO ");
            try {
                Thread.sleep(3000);
                save(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{

            messangi.utils.showDebugLog(this,"No se actulizo el Lenguaje ");
        }

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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

    private JSONObject requestJsonBodyForUpdate(String pushToken,Context context){
        final Messangi messangi=Messangi.getInst(context);

        JSONObject requestBody=new JSONObject();
        JSONArray jsonArray=new JSONArray(tags);

        try {
            if(!pushToken.equals("")) {
                requestBody.put("pushToken", pushToken);
                requestBody.put("type", type);
                requestBody.put("tags", jsonArray);
                requestBody.put("language", language);
                requestBody.put("model", model);
                requestBody.put("os", os);
                requestBody.put("sdkVersion", sdkVersion);
            }else{
                requestBody.put("pushToken", pushToken);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messangi.utils.showInfoLog(this,"Json for update "+requestBody.toString());
        return requestBody;
    }

}
