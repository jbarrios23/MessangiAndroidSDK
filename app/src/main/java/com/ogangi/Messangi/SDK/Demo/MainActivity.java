package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ogangi.messangi.sdk.Messangi;
import com.ogangi.messangi.sdk.SdkUtils;
import com.ogangi.messangi.sdk.network.ServiceCallback;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, ServiceCallback {

    Activity activity = MainActivity.this;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public Messangi messangi;
    public Button getPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messangi=Messangi.getInstance(this);
        getPhone=findViewById(R.id.button_getPhone);

        getPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messangi.getPhone(activity);
                messangi.makeGetDevice(MainActivity.this,getApplicationContext());


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(CLASS_TAG,"PERMISSION_GRANTED");
                    messangi.getPhone(activity);
                } else {
                    Toast.makeText(activity,"Permission Denied. ", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    @Override
    public void handleData(Object result) {

    }

    @Override
    public void handleIndividualData(Object result) {

    }
}
