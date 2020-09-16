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
import java.io.BufferedWriter;
import java.io.IOException;
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
 * class MessagingDevice is used for handle Device parameter in SDK
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
    private static MessagingDevice mInstance;

    /**
     * direct access to the singleton instance defined in Messaging

     */
    public static synchronized MessagingDevice getInstance() {
        if (Messaging.getInstance() == null) {
            return null;
        }
        //direct access to the singleton instance defined in Messaging
        return Messaging.getInstance().messagingDevice;
    }


    /**
     * Method that make Update of parameter Device using service
     @param context: Instance context.
     */

    public void save(final Context context){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        JSONObject requestUpdateBody=requestJsonBodyForUpdate(pushToken);
        new HTTPReqTaskPut(id,requestUpdateBody,context,userId).execute();

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
        if(messagingStorageController.hasTokenRegister()&& enable){
            pushToken= messagingStorageController.getToken();

        }else{
            pushToken="";

        }
        save(context);
    }

    /**
     * Method for check Sdk Version and Language, if one of these parameters changes
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
            messaging.utils.showInfoLog(this,nameMethod,"Update language");
            try {
                Thread.sleep(3000);
                save(context);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{
            messaging.utils.showInfoLog(this,nameMethod,"Not update language");
        }

    }
    /**
     * Method for add new Tags to Device, then you can do save and
     * immediately it is updated in the database.
     @param newTags: new Tags for add
     */

    public MessagingDevice addTagToDevice(String newTags){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Messaging messaging = Messaging.getInstance();
        messaging.utils.showInfoLog(this,nameMethod,"tags "+newTags);
        if(tags!=null){
            tags.add(newTags);

        }else{
            tags=new ArrayList<>();
            tags.add(newTags);

        }

        messaging.utils.showInfoLog(this,nameMethod,"tags "+tags);
        return this;

    }
    /**
     * Method for add new Tags to Device, then you can do save and
     * immediately it is updated in the database.
     @param newTags: new Tags for add
     */

    public MessagingDevice removeTagToDevice(String newTags){

        if(tags!=null){
            tags.remove(newTags);

        }else {
            tags = new ArrayList<>();
        }
        return this;

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

    void setId(String id) {
        this.id = id;
    }

    /**
     * Method for get Push Token
     *
     */
    public String getPushToken() {
        Messaging messaging=Messaging.getInstance();
        messaging.utils.showDebugLog(messaging,nameMethod,"pushToken "+pushToken);
        return pushToken;
    }

    void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    /**
     * Method for get User Id
     *
     */
    public String getUserId() {
        return userId;
    }

    MessagingDevice setUserId(String userId) {
        this.userId = userId;

        return this;
    }

    /**
     * Method for get Type of Device
     *
     */
    public String getType() {
        return type;
    }

    MessagingDevice setType(String type) {
        this.type = type;
        return this;
    }
    /**
     * Method for get Languaje of Device
     *
     */
    public String getLanguage() {
        return language;
    }

    MessagingDevice setLanguage(String language) {
        this.language = language;
        return this;
    }
    /**
     * Method for get Model od Device
     *
     */
    public String getModel() {
        return model;
    }

    MessagingDevice setModel(String model)
    {
        this.model = model;
        return this;
    }

    /**
     * Method for get OS version of Device
     *
     */
    public String getOs() {
        return os;
    }

    MessagingDevice setOs(String os)
    {
        this.os = os;
        return this;
    }

    /**
     * Method for get Sdk version of Device
     *
     */
    public String getSdkVersion() {
        return sdkVersion;
    }

    MessagingDevice setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
        return this;
    }
    /**
     * Method for get Tags of Device
     *
     */
    public List<String> getTags() {

        return tags;
    }

    MessagingDevice setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    MessagingDevice setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    MessagingDevice setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    MessagingDevice setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getTransaction() {
        return transaction;
    }

    MessagingDevice setTransaction(String transaction) {
        this.transaction = transaction;
        return this;
    }

    /**
     * Method for get JSON body for make Update Device
     * @param pushToken : push token parameter
     */

    private JSONObject requestJsonBodyForUpdate(String pushToken){
        Messaging messaging=Messaging.getInstance();
        JSONObject requestBody=new JSONObject();
        JSONArray jsonArray=new JSONArray(tags);

        try {
            requestBody.put(Messaging.MESSAGING_PUSH_TOKEN, pushToken);
            requestBody.put(Messaging.MESSAGING_DEVICE_TYPE, getType());
            requestBody.put(Messaging.MESSAGING_DEVICE_TAGS, jsonArray);
            requestBody.put(Messaging.MESSAGING_DEVICE_LANGUAGE, getLanguage());
            requestBody.put(Messaging.MESSAGING_DEVICE_MODEL, getModel());
            requestBody.put(Messaging.MESSAGING_DEVICE_OS, getOs());
            requestBody.put(Messaging.MESSAGING_DEVICE_SDK_VERSION, getSdkVersion());
            messaging.utils.showDebugLog(this,"requestJsonBodyForUpdate Type ",getType());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestBody;
    }

    /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     @param something: Object Serializable for send to activity (Ej MessagingDev).
     @param context : context instance
     */
    private void sendEventToActivity(String action,Serializable something, Context context) {

        Intent intent=new Intent(action);
        intent.putExtra(Messaging.INTENT_EXTRA_DATA,something);
        intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR,something==null);
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
        private String userId;

        public HTTPReqTaskPut(String id, JSONObject gsonObject, Context context, String userId) {
            this.jsonObject=gsonObject;
            this.Id=id;
            this.context=context;
            this.messaging = Messaging.getInstance(this.context);
            this.userId=userId;

        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {

                String authToken= messaging.utils.getMessagingToken();
                JSONObject postData = jsonObject;
                provUrl=messaging.utils.getMessagingHost()+"/devices/"+Id;
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
                    sendEventToActivity(Messaging.ACTION_SAVE_DEVICE,null,context);
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,null);
                    throw new IOException("Invalid response from server: " + code);
                }


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = messaging.readStream(urlConnection.getInputStream());

                }


            } catch (Exception e) {
                e.printStackTrace();

                sendEventToActivity(Messaging.ACTION_SAVE_DEVICE,null,context);
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
                    messaging.utils.showHttpResponseLog(provUrl, MessagingDevice.this,nameMethod,"Update Successful",response);
                    JSONObject resp=new JSONObject(response);
                    messagingDevice = messaging.utils.getMessagingDevFromJson(resp, jsonObject,Id,userId);
                    messaging.messagingStorageController.saveDevice(resp,Id, jsonObject);
                    sendEventToActivity(Messaging.ACTION_SAVE_DEVICE,messagingDevice,context);

                }
            }catch (NullPointerException e){
                messaging.utils.showErrorLog(this,nameMethod,"device not update! NullPointerException",e.getStackTrace().toString());
                sendEventToActivity(Messaging.ACTION_SAVE_DEVICE,null,context);
            } catch (JSONException e) {
                e.printStackTrace();
                messaging.utils.showErrorLog(this,nameMethod,"device not update! JSONException",e.getStackTrace().toString());
                sendEventToActivity(Messaging.ACTION_SAVE_DEVICE,null,context);
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
