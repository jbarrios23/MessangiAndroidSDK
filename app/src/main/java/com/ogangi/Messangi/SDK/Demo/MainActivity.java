package com.ogangi.Messangi.SDK.Demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ogangi.messangi.sdk.Messangi;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    Activity activity = MainActivity.this;
    public String wantPermission = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public Messangi messangi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messangi=Messangi.getInstance(this);
        if (!messangi.checkPermission(wantPermission,activity)) {
            messangi.requestPermission(wantPermission,PERMISSION_REQUEST_CODE,activity);
        } else {

            String phone=messangi.getPhone(wantPermission);
            String externalId=messangi.getExternalId();
            String email=messangi.getEmail(this);
            Log.e(CLASS_TAG, "Phone number: " + phone);
            Log.e(CLASS_TAG, "External ID: " + externalId);
            Log.e(CLASS_TAG, "Email: " + email);
            messangi.verifiSdkVersion();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String phone=messangi.getPhone(wantPermission);
                    String externalId=messangi.getExternalId();
                    String email=messangi.getEmail(this);
                    messangi.verifiSdkVersion();
                    Log.e(CLASS_TAG, "Phone number: " + phone);
                    Log.e(CLASS_TAG, "External ID: " + externalId);
                    Log.e(CLASS_TAG, "Email: " + email);
                } else {
                    Toast.makeText(activity,"Permission Denied. ", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
