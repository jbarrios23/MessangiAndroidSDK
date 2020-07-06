package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExampleURLSchemasActivity extends AppCompatActivity {

    public static String CLASS_TAG=ExampleURLSchemasActivity.class.getSimpleName();
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
        setContentView(R.layout.activity_example_u_r_l_schemas);
        printData=findViewById(R.id.lista_data);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod);
        additionalData = new HashMap<>();
        Intent intent=getIntent();
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
        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            for (String key : extras.keySet()) {
                Log.i(TAG, "INFO DATA: " + CLASS_TAG + ": " + nameMethod + " " + "Extras received:  Key: " + key + " Value: " + extras.getString(key));
                additionalData.put(key, extras.getString(key));
            }

        }
        if(additionalData!=null&& additionalData.size()>0) {
            dataArrayList = new ArrayList(additionalData.entrySet());
            dataAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, dataArrayList);
            printData.setAdapter(dataAdapter);
        }
    }

}