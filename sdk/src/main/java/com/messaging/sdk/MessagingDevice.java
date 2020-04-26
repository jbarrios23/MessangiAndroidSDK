package com.messaging.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * class MessagingDevice is used for handle Device paramenter in SDK
 * and service for update device and requestUserByDevice.
 */

public class MessagingDevice implements Serializable {
    private String id;
    private String pushToken;
    protected String userId;
    private String type;
    private String language;
    private String model;
    private String os;
    private String sdkVersion;
    private List<String> tags = null;
    private String createdAt;
    private String updatedAt;
    private String timestamp;
    private String transaction;
    private String nameMethod;

    /**
     * direct access to the singletone instance defined in Messangi

     */
    public static synchronized MessagingDevice getInstance() {
        if (Messaging.getInstance() == null) {
            return null;
        }
        //direct access to the singletone instance defined in Messangi
        return Messaging.getInstance().messagingDevice;
    }

    /**
     * Method that make Update of paramenter Device using service
     @param context: Instance context.
     */

    public void save(final Context context){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        JSONObject requestUpdatebody=requestJsonBodyForUpdate(pushToken);
        new HTTPReqTaskPut(id,requestUpdatebody,context).execute();

    }

    /**
     * Method for get User by Device registered from service
     @param context: instance context
     @param forsecallservice : allows effective device search in three ways: by instance, by shared variable or by service.
     */
    public  void requestUserByDevice(final Context context, boolean forsecallservice){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging = Messaging.getInstance(context);
        final MessagingStorageController messagingStorageController = Messaging.getInstance().messagingStorageController;
        if(!forsecallservice && messaging.messagingUser !=null){
            messaging.utils.showDebugLog(this,nameMethod,"User From RAM ");
            sendEventToActivity(messaging.messagingUser,context);
        }else {
            if (!forsecallservice && messagingStorageController.isRegisterUserByDevice()) {
                messaging.utils.showDebugLog(this,nameMethod,"User From Local storage ");
                Map<String, String> resultMap= messagingStorageController.getUserByDevice();
                messaging.messagingUser = MessagingUser.parseData(resultMap) ;
                sendEventToActivity(messaging.messagingUser,context);

            } else {
                messaging.utils.showDebugLog(this,nameMethod, "User From Service ");
                new HTTPReqTaskGetUser(id, messaging,context).execute();
            }
        }

    }

    /**
     * Method for get status of enable notification push
     */

    public boolean isEnableNotificationPush(){
        return pushToken!=null && pushToken!="";
    }

    /**
     * Method for get Device registered from service
     @param context: instance context
     @param enable : boolean enable.
     */
    public void setStatusNotificationPush(boolean enable,Context context){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final MessagingStorageController messagingStorageController = Messaging.getInstance().messagingStorageController;
        messagingStorageController.setNotificationManually(true);
        if(messagingStorageController.hasTokenRegiter()&& enable){
            pushToken= messagingStorageController.getToken();

        }else{
            pushToken="";

        }
        save(context);
    }

    /**
     * Method for check Sdk Veriosn and Languaje, if one of these parameters changes
     * immediately it is updated in the database.
     @param context: instance context
     */

    public void checkSdkVersion(Context context) {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging = Messaging.getInstance(context);
        String sdkVersionInt = BuildConfig.VERSION_NAME; // sdk version;
        if(getSdkVersion().equals("0") || !getSdkVersion().equals(sdkVersionInt)){
            setSdkVersion(sdkVersionInt);
            messaging.utils.showInfoLog(this,nameMethod,"Update sdk version");
            try {
                Thread.sleep(1000);
                save(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            messaging.utils.showInfoLog(this,nameMethod,"Not update sdk version");
        }
        String lenguaje= Locale.getDefault().getDisplayLanguage();

        if(getLanguage().equals("0") || !getLanguage().equals(lenguaje)){
            setLanguage(lenguaje);
            messaging.utils.showInfoLog(this,nameMethod,"Update lenguaje");
            try {
                Thread.sleep(3000);
                save(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{
            messaging.utils.showInfoLog(this,nameMethod,"Not update lenguaje");
        }

    }
    /**
     * Method for add new Tags to Device, then you can do save and
     * immediately it is updated in the database.
     @param newTags: new Tags for add
     */

    public void addTagsToDevice(String newTags){

        if(tags!=null){
            tags.add(newTags);

        }else{
            tags=new ArrayList<>();
            tags.add(newTags);

        }

    }
    /**
     * Method for clear all Tags selected in local
     *
     */
    public void clearTags(){
        tags.clear();

    }


    /**
     * Method for get Id Device
     *
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method for get Push Token
     *
     */
    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    /**
     * Method for get User Id
     *
     */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Method for get Type of Device
     *
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    /**
     * Method for get Leanguaje of Device
     *
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    /**
     * Method for get Model od Device
     *
     */
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Method for get OS version of Device
     *
     */
    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Method for get Sdk version of Device
     *
     */
    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
    /**
     * Method for get Tags of Device
     *
     */
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

    /**
     * Method for get JSON body for make Update Device
     * @param pushToken : push token parameter
     */

    private JSONObject requestJsonBodyForUpdate(String pushToken){

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

        return requestBody;
    }

    /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(Serializable something, Context context) {

        Intent intent=new Intent("PassDataFromSdk");
        intent.putExtra("message",something);
        intent.putExtra("hasError",something==null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private class HTTPReqTaskPut extends AsyncTask<Void,Void,String> {

        private JSONObject jsonObject;
        private String Id;
        private Messaging messaging;
        private String server_response;
        private Context context;
        private String provUrl;
        private MessagingDevice messagingDevice;

        public HTTPReqTaskPut(String id, JSONObject gsonObject, Context context) {
            this.jsonObject=gsonObject;
            this.Id=id;
            this.context=context;
            this.messaging = Messaging.getInstance(this.context);

        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                String authToken= MessagingSdkUtils.getMessangi_token();
                JSONObject postData = jsonObject;
                provUrl=MessagingSdkUtils.getMessangi_host()+"/v1/devices/"+Id;
                messaging.utils.showHttpRequestLog(provUrl, MessagingDevice.this,nameMethod,"PUT",postData.toString());
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("PUT");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        out, "UTF-8"));
                writer.write(postData.toString());
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    sendEventToActivity(null,context);
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,null);
                    throw new IOException("Invalid response from server: " + code);
                }


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = messaging.readStream(urlConnection.getInputStream());

                }


            } catch (Exception e) {
                e.printStackTrace();
                sendEventToActivity(null,context);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try{
                if(!response.equals("")) {
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    messaging.utils.showHttpResponsetLog(provUrl, MessagingDevice.this,nameMethod,"Successful",response);
                    JSONObject resp=new JSONObject(response);
                    messagingDevice = messaging.utils.getMessangiDevFromJson(resp);
                    messaging.messagingStorageController.saveDevice(resp);
                    sendEventToActivity(messagingDevice,context);

                }
            }catch (NullPointerException e){
                messaging.utils.showErrorLog(this,nameMethod,"device not update! NullPointerException",e.getStackTrace().toString());
                sendEventToActivity(null,context);
            } catch (JSONException e) {
                e.printStackTrace();
                messaging.utils.showErrorLog(this,nameMethod,"device not update! JSONException",e.getStackTrace().toString());
                sendEventToActivity(null,context);
            }
        }
    }

    private class HTTPReqTaskGetUser extends AsyncTask<Void,Void,String> {

        public String deviceId;
        private String server_response;
        private Messaging messaging;
        private Context context;
        private String provUrl;

        public HTTPReqTaskGetUser(String deviceId, Messaging messaging, Context context) {
            this.deviceId=deviceId;
            this.messaging = messaging;
            this.context=context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                String authToken= MessagingSdkUtils.getMessangi_token();
                String param ="Bearer "+authToken;
                provUrl= MessagingSdkUtils.getMessangi_host()+"/v1/users?device="+deviceId;
                messaging.utils.showHttpRequestLog(provUrl, MessagingDevice.this,nameMethod,"GET","");
                URL url = new URL(provUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization","Bearer "+authToken);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestMethod("GET");
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    sendEventToActivity(null,context);
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,"");
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = messaging.readStream(urlConnection.getInputStream());

                }

            } catch (Exception e) {
                e.printStackTrace();
                messaging.utils.showErrorLog(this,nameMethod,"Get User error Exception",e.getStackTrace().toString());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return server_response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            try{
                if(!response.equals("")) {
                    nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
                    messaging.utils.showHttpResponsetLog(provUrl,this,nameMethod,"Successful",response);
                    JSONObject resp=new JSONObject(response);
                    Map<String, String> resultMap=toMap(resp);
                    messaging.messagingStorageController.saveUserByDevice(resultMap);
                    MessagingUser messagingUser;
                    messagingUser = MessagingUser.parseData(resultMap);
                    messagingUser.id = userId;
                    sendEventToActivity(messagingUser, context);


                }
            }catch (NullPointerException e){
                sendEventToActivity(null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Get error User! NullPointerException ",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                sendEventToActivity(null,context);
                messaging.utils.showErrorLog(this,nameMethod,"Get error User! JSONException ",e.getStackTrace().toString());

            }

        }
    }

    public static Map<String, String> toMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            map.put(key, String.valueOf(value));
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @NonNull
    @Override
    public String toString() {
        return "device: "+id+" "+tags;
    }

}
