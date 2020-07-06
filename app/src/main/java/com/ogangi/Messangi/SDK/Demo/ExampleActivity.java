package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExampleActivity extends AppCompatActivity {
    public static String CLASS_TAG=ExampleActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    private String nameMethod;
    private ListView printData;
    public Map<String,String> additionalData;
    public ArrayList<Map.Entry<String, Object>> dataArrayList;
    public ArrayAdapter dataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_example);
        printData=findViewById(R.id.lista_data);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod);
        Bundle extras=getIntent().getExtras();
        additionalData = new HashMap<>();
        if(extras!=null) {
            Log.i(TAG,"INFO: "+CLASS_TAG+" title: "+extras.get("title"));
            Log.i(TAG,"INFO: "+CLASS_TAG+" body: "+extras.get("body"));

            for (String key : extras.keySet()) {
                Log.i(TAG, "INFO DATA: " + CLASS_TAG + ": " + nameMethod + " " + "Extras received:  Key: " + key + " Value: " + extras.getString(key));
                additionalData.put(key, extras.getString(key));
            }

            Log.i(TAG, "INFO DATA BUILD: " + CLASS_TAG + " data: " + additionalData);
        }
        if(additionalData!=null&& additionalData.size()>0) {
            dataArrayList = new ArrayList(additionalData.entrySet());
            dataAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, dataArrayList);
            printData.setAdapter(dataAdapter);
        }


    }
}