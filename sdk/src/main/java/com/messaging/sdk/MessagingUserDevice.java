package com.messaging.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

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
import java.util.Map;

import static com.messaging.sdk.MessagingDev.toMap;

/**
 * class MessagingUserDevice is used for handle User paramenter in SDK and service
 * update user
 */
public class MessagingUserDevice implements Serializable {


    private  ArrayList<MessagingDev> devices;


    private  final Map<String, String> properties = new HashMap<>();

    protected String id;

    private String nameMethod;

    public MessagingUserDevice() {
        this.devices = new ArrayList<>();
        this.id="";
        this.nameMethod="";
    }
    /**
     * Method that make Update of User using the service Put
     @param context
     @serialData :MessagingUserDevice
     */
    public void save(final Context context){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging = Messaging.getInst(context);

        String deviceId= messaging.messagingDev.getId();
        Map<String, String> provPro=properties;
        JSONObject requestUpdatebody=new JSONObject(provPro);
        new HTTPReqTaskPutUserByDevice(deviceId,requestUpdatebody,context, messaging).execute();


    }

    public String getId() {

        return id;
    }
    /**
     * Method for add properties to user
     */
    public void addProperties(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Method for get Properties of user
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Method for get Device of user
     */
    public ArrayList<MessagingDev> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<MessagingDev> devices) {
        this.devices = devices;
    }

    /**
     * Method that send Parameter (Ej: messagingDev or MessagingUserDevice) registered to Activity
     @param something: Object Serializable for send to activity (Ej MeesangiDev).
     @param context : context instance
     */
    private void sendEventToActivity(Serializable something, Context context) {
        Messaging messaging = Messaging.getInst(context);
        Intent intent=new Intent("PassDataFromSdk");
        messaging.utils.showDebugLog(this,nameMethod,"Broadcasting message");
        intent.putExtra("message",something);
        if(something!=null){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            messaging.utils.showErrorLog(this,nameMethod,"Don't Send Broadcast ","");
        }
    }

    /**
     * Method for parse data and conver to MessagingUserDevice Object
     @param retMap: HasMap Object to convert
     */

    public static MessagingUserDevice parseData(Map<String, String> retMap){

        MessagingUserDevice messagingUserDevice = new MessagingUserDevice();

        for (Map.Entry<String, String> entry : retMap.entrySet()) {

            if (entry.getKey().equals("devices")) {
                try {
                    Messaging messaging = Messaging.getInst();
                    JSONArray jsonArrayDevice=new JSONArray(retMap.get("devices"));
                    for(int i=0;i<jsonArrayDevice.length();i++){
                        JSONObject provDevice=jsonArrayDevice.getJSONObject(i);
                        MessagingDev messagingDev = messaging.utils.getMessangiDevFromJson(provDevice);
                        messagingUserDevice.devices.add(messagingDev);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                messagingUserDevice.addProperties(entry.getKey(), entry.getValue());
            }

        }

        return messagingUserDevice;

    }
    /**
     * Method for get Email user
     */

    public String getEmail(){


        if(properties.containsKey("email")){
            return (String) properties.get("email");
        }

        return "";
    }

    /**
     * Method for get Phone user
     */
    public String getPhone(){

        if(properties.containsKey("phone")){
            return (String) properties.get("phone");
        }

        return "";
    }

    /**
     * Method for get External ID user
     */
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

    /**
     * Method for get some property of user
     */
    public String getProperty(String key){

        if(properties.containsKey(key)){
            return (String) properties.get(key);
        }

        return "";
    }

    private class HTTPReqTaskPutUserByDevice extends AsyncTask<Void,Void,String> {

        private String deviceId;
        private JSONObject jsonObject;
        private Context context;
        private String server_response;
        private Messaging messaging;
        private String provUrl;


        public HTTPReqTaskPutUserByDevice(String deviceId, JSONObject gsonObject, Context context, Messaging messaging) {

            this.deviceId=deviceId;
            this.jsonObject=gsonObject;
            this.context=context;
            this.messaging = messaging;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {

                String authToken= MessagingSdkUtils.getMessangi_token();
                JSONObject postData = jsonObject;
                provUrl= MessagingSdkUtils.getMessangi_host()+"/v1/users?device="+deviceId;
                messaging.utils.showHttpRequestLog(provUrl,MessagingUserDevice.this,nameMethod,"PUT",postData.toString());
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
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,"");
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
                    messaging.utils.showHttpResponsetLog(provUrl,MessagingUserDevice.this,nameMethod,"Successful",response);
                    JSONObject resp=new JSONObject(response);
                    JSONObject data=resp.getJSONObject("subscriber").getJSONObject("data");
                    Map<String, String> resultMap=toMap(data);
                    messaging.messagingStorageController.saveUserByDevice(resultMap);
                    MessagingUserDevice messagingUserDevice;
                    messagingUserDevice = MessagingUserDevice.parseData(resultMap);
                    sendEventToActivity(messagingUserDevice, context);

                }
            }catch (NullPointerException e){
                sendEventToActivity(null,context);
                messaging.utils.showErrorLog(this,nameMethod,"User not update! NullPointerException",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                sendEventToActivity(null,context);
                messaging.utils.showErrorLog(this,nameMethod,"User not update! JSONException",e.getStackTrace().toString());
            }
        }
    }


}
