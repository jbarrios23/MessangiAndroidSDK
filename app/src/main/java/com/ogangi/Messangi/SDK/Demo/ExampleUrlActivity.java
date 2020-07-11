package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExampleUrlActivity extends AppCompatActivity {

    public static String CLASS_TAG=ExampleUrlActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    private String nameMethod;
    private ListView printData;
    public Map<String, Object> additionalData;
    public ArrayList<Map.Entry<String, Object>> dataArrayList;
    public ArrayAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_example_url);
        printData=findViewById(R.id.lista_data);
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
            }
            Log.i(TAG, "INFO DATA BUILD: " + CLASS_TAG + " data: " + additionalData);

        }else{
            try {
                JSONObject jsonObjData = new JSONObject(getIntent().getStringExtra("data"));
                additionalData=toMap(jsonObjData);
                Log.i(TAG, "INFO DATA BUILD 2: " + CLASS_TAG + " data: " + additionalData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(intent!=null ){
            Log.i(TAG,"INFO: "+CLASS_TAG+" action: "+intent.getAction());
            Log.i(TAG,"INFO: "+CLASS_TAG+" scheme: "+intent.getScheme());
            Log.i(TAG,"INFO: "+CLASS_TAG+" data: "+intent.getData());
            Log.i(TAG,"INFO: "+CLASS_TAG+" param1: "+intent.getData().getQueryParameter("param1"));
            Log.i(TAG,"INFO: "+CLASS_TAG+" Package: "+intent.getPackage());

            additionalData.put("action",intent.getAction());
            additionalData.put("scheme",intent.getScheme());
            additionalData.put("data", String.valueOf(intent.getData()));
            additionalData.put("param1",intent.getData().getQueryParameter("param1"));
            additionalData.put("param2",intent.getData().getQueryParameter("param2"));
            additionalData.put("Package",intent.getPackage());
        }
        if(additionalData!=null&& additionalData.size()>0) {
            dataArrayList = new ArrayList(additionalData.entrySet());
            dataAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, dataArrayList);
            printData.setAdapter(dataAdapter);
        }
    }
    public  Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

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
            map.put(key, value);
        }
        return map;
    }

    public List<Object> toList(JSONArray array) throws JSONException {
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
}