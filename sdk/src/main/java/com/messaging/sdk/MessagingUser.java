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

import static com.messaging.sdk.MessagingDevice.toMap;

/**
 * class MessagingUser is used for handle User parameter in SDK and make service
 * update user.
 */
public class MessagingUser implements Serializable {


    private  ArrayList<MessagingDevice> devices;


    private  final Map<String, String> properties = new HashMap<>();

    protected String id;

    private String nameMethod;


    /**
     * direct access to the singleton instance defined in Messaging

     */
    public static synchronized MessagingUser getInstance() {
        if (Messaging.getInstance() == null) {
            return null;
        }
        //direct access to the singletone instance defined in Messangi
        return Messaging.getInstance().messagingUser;
    }



    public MessagingUser() {
        this.devices = new ArrayList<>();
        this.id="";
        this.nameMethod="";
    }
    /**
     * Method that make Update of User parameter using service
     @param context: Instance context
     */
    public void save(final Context context){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        final Messaging messaging = Messaging.getInstance(context);

        String deviceId= messaging.messagingDevice.getId();
        Map<String, String> provPro=properties;
        provPro.remove("transaction");
        provPro.remove("timestamp");
        JSONObject requestUpdateBody=new JSONObject(provPro);
        new HTTPReqTaskPutUserByDevice(deviceId,requestUpdateBody,context, messaging).execute();


    }

    public String getId() {

        return id;
    }
    /**
     * Method for add Property to user
     * example: name, lastName, email or phone
     * @param key : example name
     * @param value : example Jose
     * @return : Instance MessagingUser;
     */
    public MessagingUser addProperty(String key, String value) {
        properties.put(key, value);
        return this;
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
    public ArrayList<MessagingDevice> getDevices() {
        return devices;
    }

    //public void setDevices(ArrayList<MessagingDevice> devices) {
      //  this.devices = devices;
    //}

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



    /**
     * Method for parse data and convert to MessagingUser Object
     @param retMap: HasMap Object to convert
     */

    public static MessagingUser parseData(Map<String, String> retMap){

        MessagingUser messagingUser = new MessagingUser();

        for (Map.Entry<String, String> entry : retMap.entrySet()) {

            if (entry.getKey().equals("devices")) {
                try {
                    Messaging messaging = Messaging.getInstance();
                    JSONArray jsonArrayDevice=new JSONArray(retMap.get("devices"));
                    for(int i=0;i<jsonArrayDevice.length();i++){
                        JSONObject provDevice=jsonArrayDevice.getJSONObject(i);
                        MessagingDevice messagingDevice = messaging.utils.getMessagingDevFromJsonOnlyResp(provDevice, messaging.getPushToken());
                        messagingUser.devices.add(messagingDevice);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                messagingUser.addProperty(entry.getKey(), entry.getValue());
            }

        }

        return messagingUser;

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

    /**
     * Method for set email to user
     * example: name, lastName, email or phone
     * @param value : example JB@Jb.com
     * @return : Instance MessagingUser;
     */

    public MessagingUser setEmail(String value){

        properties.put("email",value);
        return this;

    }
    /**
     * Method for set phone to user
     * example: name, lastName, email or phone
     * @param value : example 5555555
     * @return : Instance MessagingUser;
     */
    public MessagingUser setPhone(String value){

        properties.put("phone",value);
        return this;

    }
    /**
     * Method for set external Id to user
     * example: name, lastName, email or phone
     * @param value : example 555Jggf
     * @return : Instance MessagingUser;
     */
    public MessagingUser setExternalID(String value){

    properties.put("externalID",value);

    return this;

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

    @Override
    public String toString() {
        return "MessagingUser{" +
                ", id:'" + id + '\'' +
                ", properties:" + properties +
                '}';
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

                String authToken= messaging.utils.getMessagingToken();
                JSONObject postData = jsonObject;
                //provUrl= messaging.utils.getMessagingHost()+"/users?device="+deviceId;
                provUrl= messaging.utils.getMessagingHost()+"/users/"+deviceId;
                messaging.utils.showHttpRequestLog(provUrl, MessagingUser.this,nameMethod,"PUT",postData.toString());
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
                    //sendEventToActivity(Messaging.ACTION_SAVE_USER,null,context);
                    sendEventToActivity(Messaging.ACTION_SAVE_USER,null,context);
                    messaging.utils.showErrorLog(this,nameMethod,"Invalid response from server: " + code,"");
                    throw new IOException("Invalid response from server: " + code);
                }


                if(code == HttpURLConnection.HTTP_OK){
                    server_response = messaging.readStream(urlConnection.getInputStream());
                    messaging.utils.showDebugLog(this,nameMethod,"ok "+server_response);

                }


            } catch (Exception e) {
                e.printStackTrace();
                messaging.utils.showErrorLog(this,nameMethod,"Exception",e.getStackTrace().toString());
                sendEventToActivity(Messaging.ACTION_SAVE_USER,null,context);
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

                    messaging.utils.showHttpResponseLog(provUrl, MessagingUser.this,nameMethod,"User Update Successful",response);
//                    JSONObject resp=new JSONObject(response);
//                    JSONObject data=resp.getJSONObject("subscriber").getJSONObject("data");
                    Map<String, String> resultMap=toMap(jsonObject);
                    messaging.messagingStorageController.saveUserByDevice(resultMap);
                    messaging.messagingUser = MessagingUser.parseData(resultMap);
                    sendEventToActivity(Messaging.ACTION_SAVE_USER,messaging.messagingUser, context);


            }catch (NullPointerException e){
                sendEventToActivity(Messaging.ACTION_SAVE_USER,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"User not update! NullPointerException",e.getStackTrace().toString());
            } catch (JSONException e) {
                e.printStackTrace();
                sendEventToActivity(Messaging.ACTION_SAVE_USER,null,context);
                messaging.utils.showErrorLog(this,nameMethod,"User not update! JSONException",e.getStackTrace().toString());
            }
        }
    }

}
