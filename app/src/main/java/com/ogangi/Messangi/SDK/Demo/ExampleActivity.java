package com.ogangi.Messangi.SDK.Demo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExampleActivity extends AppCompatActivity {
    public static String CLASS_TAG=ExampleActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    private String nameMethod;
    private ListView printData;
    public Map<String, String> additionalData;
    public ArrayList<String> messangiData;
    public ArrayAdapter<String> messangiDataArrayAdapter;
    public MessagingNotification messagingNotification;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        printData=findViewById(R.id.lista_data);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod);
        messangiData=new ArrayList<>();
        Bundle extras=getIntent().getExtras();
        boolean enable=extras.getBoolean("enable",false);
        Log.i(TAG,"INFO: "+CLASS_TAG+" enable: "+enable);
        additionalData = new HashMap<>();
        if(extras!=null && !enable) {
            Log.i(TAG,"INFO: "+CLASS_TAG+" title: "+extras.get("title"));
            Log.i(TAG,"INFO: "+CLASS_TAG+" body: "+extras.get("body"));

            for (String key : extras.keySet()) {
                Log.i(TAG, "INFO DATA: " + CLASS_TAG + ": " + nameMethod + " " + "Extras received:  Key: " + key + " Value: " + extras.getString(key));
                additionalData.put(key, extras.getString(key));
                messangiData.add(key + " , " + extras.getString(key));
            }

            Log.i(TAG, "INFO DATA BUILD: " + CLASS_TAG + " data: " + additionalData);
            Log.i(TAG, "INFO DATA BUILD: " + CLASS_TAG + " data 2: " + messangiData);
            Messaging.checkNotification(extras);


        }else{
            Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
            messagingNotification=(MessagingNotification)data;
            additionalData=messagingNotification.getAdditionalData();
            if(additionalData!=null&& additionalData.size()>0) {
                messangiData.add("Title: " + messagingNotification.getTitle());
                messangiData.add("Body: " + messagingNotification.getBody());
                messangiData.add("ClickAction: " + messagingNotification.getClickAction());
                messangiData.add("DeepUriLink: " + messagingNotification.getDeepUriLink());
                for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                    if (!entry.getKey().equals("profile")) {
                        messangiData.add(entry.getKey() + " , " + entry.getValue());

                    }

                }
            }

        }
        messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
        printData.setAdapter(messangiDataArrayAdapter);


    }


}