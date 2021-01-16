package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class ExampleURLSchemasActivity extends AppCompatActivity {

    public static String CLASS_TAG=ExampleURLSchemasActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    private String nameMethod;
    private ListView printData;
    public Map<String, String> additionalData;
    public ArrayList<Map.Entry<String, Object>> dataArrayList;
    public ArrayAdapter dataAdapter;
    public ArrayList<String> messangiData;
    public ArrayAdapter<String> messangiDataArrayAdapter;
    public MessagingNotification messagingNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_u_r_l_schemas);
        printData=findViewById(R.id.lista_data);
        messangiData=new ArrayList<>();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod);
        additionalData = new HashMap<>();
        Intent intent=getIntent();

        Bundle extras=getIntent().getExtras();
        boolean enable=extras.getBoolean("enable",false);
        Log.i(TAG,"INFO: "+CLASS_TAG+" enable: "+enable);
        if(extras!=null && !enable) {
            for (String key : extras.keySet()) {
                Log.i(TAG, "INFO DATA: " + CLASS_TAG + ": " + nameMethod + " " + "Extras received:  Key: " + key + " Value: " + extras.getString(key));
                additionalData.put(key, extras.getString(key));
                messangiData.add(key + " , " + extras.getString(key));
            }

        }else{
            //Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
            if(Static.messagingNotification!=null){
                messagingNotification=Static.messagingNotification;
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

        }
        if(intent!=null ){
            Log.i(TAG,"INFO: "+CLASS_TAG+" action: "+intent.getAction());
            Log.i(TAG,"INFO: "+CLASS_TAG+" scheme: "+intent.getScheme());
            Log.i(TAG,"INFO: "+CLASS_TAG+" data: "+intent.getData());
            Log.i(TAG,"INFO: "+CLASS_TAG+" param1: "+intent.getData().getQueryParameter("param1"));
            Log.i(TAG,"INFO: "+CLASS_TAG+" Package: "+intent.getPackage());
            messangiData.add("action "+intent.getAction());
            messangiData.add("scheme "+intent.getScheme());
            messangiData.add("data "+ String.valueOf(intent.getData()));
            messangiData.add("param1 "+intent.getData().getQueryParameter("param1"));
            messangiData.add("param2 "+intent.getData().getQueryParameter("param2"));
            messangiData.add("Package "+intent.getPackage());
        }

        messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
        printData.setAdapter(messangiDataArrayAdapter);
    }


}