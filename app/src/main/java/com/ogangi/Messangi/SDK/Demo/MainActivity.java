package com.ogangi.Messangi.SDK.Demo;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ogangi.messangi.sdk.Messangi;
import com.ogangi.messangi.sdk.SdkUtils;
import com.ogangi.messangi.sdk.ServiceCallback;
import com.ogangi.messangi.sdk.MessangiDev;
import com.ogangi.messangi.sdk.MessangiUserDevice;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public Messangi messangi;
    public Button device,user,tags,save;
    public TextView imprime;

    public MessangiDev messangiDev;
    public MessangiUserDevice messangiUserDevice;

    public ListView lista_device,lista_user;

    public ArrayList<String> messangiDevArrayList;
    public ArrayAdapter<String> messangiDevArrayAdapter;
    public ArrayList<String> messangiUserDeviceArrayList;
    public ArrayAdapter<String> messangiUserDeviceArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        lista_device=findViewById(R.id.lista_device);
        lista_user=findViewById(R.id.lista_user);

        device=findViewById(R.id.device);
        user=findViewById(R.id.user);
        tags=findViewById(R.id.tag);
        save=findViewById(R.id.save);

        messangi=Messangi.getInst(this);
        messangiDevArrayList=new ArrayList<>();
        messangiUserDeviceArrayList=new ArrayList<>();
        messangiDevArrayAdapter=new ArrayAdapter<>(this,R.layout.item_device,R.id.Texview_value,messangiDevArrayList);
        messangiUserDeviceArrayAdapter=new ArrayAdapter<>(this,R.layout.item_device,R.id.Texview_value,messangiUserDeviceArrayList);


        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messangi.requestDevice(false);
            }
        });

        lista_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(CLASS_TAG,"La seleccion fue: "+messangiDevArrayList.get(position));
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(CLASS_TAG,"register BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("PassDataFromoSdk"));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void creatAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

            // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                Log.e(CLASS_TAG,m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }



    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson = new Gson();
            String message=intent.getStringExtra("Message");
            int ident=intent.getIntExtra("Identifier",0);
            Log.e(CLASS_TAG,"Message:  "+message);
            Log.e(CLASS_TAG,"Ident:  "+ident);
            SdkUtils sdkUtils=new SdkUtils();
            if(ident==1){
               messangiDev=gson.fromJson(message,MessangiDev.class);
               messangiDev.requestUserByDevice(getApplicationContext(),false);
               Log.e(CLASS_TAG,"Device:  "+sdkUtils.getGsonJsonFormat(messangiDev));
               messangiDevArrayList.add("Id: "       +messangiDev.getId());
               messangiDevArrayList.add("pushToken: "+messangiDev.getPushToken());
               messangiDevArrayList.add("UserId: "   +messangiDev.getUserId());
               messangiDevArrayList.add("Type: "     +messangiDev.getType());
               messangiDevArrayList.add("Language: " +messangiDev.getLanguage());
               messangiDevArrayList.add("Model: "    +messangiDev.getModel());
               messangiDevArrayList.add("Os: "       +messangiDev.getOs());
               messangiDevArrayList.add("SdkVersion: "   +messangiDev.getSdkVersion());
               messangiDevArrayList.add("Tags: "+messangiDev.getTags());
               messangiDevArrayList.add("CreateAt: "+messangiDev.getCreatedAt());
               messangiDevArrayList.add("UpdatedAt: "+messangiDev.getUpdatedAt());
               messangiDevArrayList.add("Timestamp: "+messangiDev.getTimestamp());
               messangiDevArrayList.add("Transaction: "+messangiDev.getTransaction());
               String provDevice=sdkUtils.getGsonJsonFormat(messangiDev);
               messangiDevArrayList.add("Device: "+provDevice);
               lista_device.setAdapter(messangiDevArrayAdapter);
           }else{

               messangiUserDevice=gson.fromJson(message,MessangiUserDevice.class);
               Log.e(CLASS_TAG,"User:  "+sdkUtils.getGsonJsonFormat(messangiUserDevice));
               messangiUserDeviceArrayList.add("Device: "       +messangiUserDevice.getDevices());
               messangiUserDeviceArrayList.add("Member since: "       +messangiUserDevice.getMemberSince());
               messangiUserDeviceArrayList.add("Last Upadate: "       +messangiUserDevice.getLastUpdated());
               messangiUserDeviceArrayList.add("Mobile: "       +messangiUserDevice.getMobile());
               messangiUserDeviceArrayList.add("timestamp: "       +messangiUserDevice.getTimestamp());
               messangiUserDeviceArrayList.add("Transaction: "       +messangiUserDevice.getMobile());
               if(messangiUserDevice.getProperties().size()>0){
                   Map<String,Object> result=messangiUserDevice.getProperties();
                   for (Map.Entry<String, Object> entry : result.entrySet()) {
                       Log.e(CLASS_TAG, "Key = " + entry.getKey() +
                               ", Value = " + entry.getValue());
                       messangiUserDeviceArrayList.add(entry.getKey()+" "+entry.getValue());
                }

               }
                String proUser=sdkUtils.getGsonJsonFormat(messangiUserDevice);
                messangiUserDeviceArrayList.add("User: "       +proUser);
                lista_user.setAdapter(messangiUserDeviceArrayAdapter);
           }


        }
    };

    @Override
    protected void onDestroy() {
        Log.e(CLASS_TAG,"unregister BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.e(CLASS_TAG,"PERMISSION_GRANTED");
//                    messangi.getPhone(activity);
//                } else {
//                    Toast.makeText(activity,"Permission Denied. ", Toast.LENGTH_LONG).show();
//
//                }
//                break;
//        }
//    }
}
