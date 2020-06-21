package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExampleActivity extends AppCompatActivity {
    public static String CLASS_TAG=ExampleActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    public static final String DELETE_TAG = "DELETE_TAG";
    MessagingNotification messagingNotification;
    private String nameMethod;
    private TextView printData;

    public Messaging messaging;
    public Map<String,String> additionalData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_example);
        printData=findViewById(R.id.textView_data);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging = Messaging.getInstance(this);
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod);
        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            additionalData = new HashMap<>();
            for (String key : extras.keySet()) {
                Log.i(TAG, "INFO DATA: " + CLASS_TAG + ": " + nameMethod + " " + "Extras received:  Key: " + key + " Value: " + extras.getString(key));
                additionalData.put(key, extras.getString(key));
            }
            Log.i(TAG, "INFO DATA BUILD: " + CLASS_TAG + " data: " + additionalData);
            printData.setText("data "+additionalData);
        }


    }
}