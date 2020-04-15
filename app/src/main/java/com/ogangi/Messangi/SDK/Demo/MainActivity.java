package com.ogangi.Messangi.SDK.Demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;
import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingDev;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUserDevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {




    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public static String TAG="MESSANGING";
    public static final String DELETE_TAG = "DELETE_TAG";

    public Messaging messaging;
    public Button device,user,tags,save;
    public TextView imprime;
    public MessagingDev messagingDev;
    public MessagingUserDevice messagingUserDevice;
    public ListView lista_device,lista_user;

    public ArrayList<String> messangiDevArrayList;
    public ArrayAdapter<String> messangiDevArrayAdapter;
    public ArrayList<String> messangiUserDeviceArrayList;
    public ArrayAdapter<String> messangiUserDeviceArrayAdapter;
    public ProgressBar progressBar;
    public TextView title;
    public Button pressButton;
    MessagingNotification messagingNotification;
    private String nameMethod;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": register BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("PassDataFromSdk"));

        messaging = Messaging.getInst(this);

        lista_device = findViewById(R.id.lista_device);
        lista_user = findViewById(R.id.lista_user);
        title = findViewById(R.id.textView_imprimir);
        device = findViewById(R.id.device);
        user = findViewById(R.id.user);
        tags = findViewById(R.id.tag);
        save = findViewById(R.id.save);
        pressButton=findViewById(R.id.button_lista);
        progressBar = findViewById(R.id.progressBar);
        Switch simpleSwitch = findViewById(R.id.simpleSwitch);


        messangiDevArrayList = new ArrayList<>();
        messangiUserDeviceArrayList = new ArrayList<>();
        messangiDevArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiDevArrayList);
        messangiUserDeviceArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiUserDeviceArrayList);
        title.setText(getResources().getString(R.string.title) + "\n" + messaging.getExternalId());

        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messangiDevArrayList.clear();
                messangiUserDeviceArrayList.clear();
                progressBar.setVisibility(View.VISIBLE);
                messaging.requestDevice(true);
                Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+": "+messaging.getExternalId());

                messagingDev.requestUserByDevice(getApplicationContext(), true);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertUser();
            }
        });

        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatAlert();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messagingDev.getTags().size() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    messagingDev.save(getApplicationContext());
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_LONG).show();
                }
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    Toast.makeText(getApplicationContext(), "Enable Notification Push", Toast.LENGTH_LONG).show();
                    messagingDev.setStatusNotificationPush(isChecked, getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                } else {

                    Toast.makeText(getApplicationContext(), "Disable Notification Push", Toast.LENGTH_LONG).show();
                    messagingDev.setStatusNotificationPush(isChecked, getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        //for handle notification from background
        Bundle extras=getIntent().getExtras();
        messagingNotification =new MessagingNotification(extras,getApplicationContext());
        pressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoListaActivity();
            }
        });

//        Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//        startActivity(intent);


    }


    private void gotoListaActivity() {
        Intent intent=new Intent(MainActivity.this,ListNotification.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        messangiDevArrayList.clear();
        messangiUserDeviceArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        messaging.requestDevice(false);
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+"onResume: ");
        if(messaging.messagingStorageController.isNotificationWasDismiss()){
            Bundle extras=getIntent().getExtras();
            messagingNotification =new MessagingNotification(extras,getApplicationContext());
        }


    }

    @SuppressLint("SetTextI18n")
    private void showAlertNotificaction(MessagingNotification messagingNotification) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
        builder.setView(customLayout);

        TextView data=customLayout.findViewById(R.id.data_noti);

        if(messagingNotification.getNotification()!=null&&(messagingNotification.getData()!=null && messagingNotification.getData().size() > 0)){
            data.append("Has Notification"+"\n");
            data.append(""+ messagingNotification.getNotification().getTitle()+"\n");
            data.append(""+ messagingNotification.getNotification().getBody());
            data.append("Has data"+"\n");
            for (Map.Entry entry : messagingNotification.getData().entrySet()) {
                if(!entry.getKey().equals("profile")){
                    data.append(" "+entry.getKey() + " , " + entry.getValue()+"\n");
                }else{
                    showMessage(data);
                    break;
                }

            }


        }else if(messagingNotification.getData()!=null && messagingNotification.getData().size() > 0) {
            data.append("Has only Data"+"\n");
            for (Map.Entry entry : messagingNotification.getData().entrySet()) {
                if(!entry.getKey().equals("profile")){
                    data.append(" "+entry.getKey() + " , " + entry.getValue()+"\n");
                }else{
                    showMessage(data);
                    break;
                }

            }

        }else if(messagingNotification.getNotification()!=null) {

            data.append("Has only Notification"+"\n");
            data.append(""+ messagingNotification.getNotification().getTitle()+"\n");
            data.append(""+ messagingNotification.getNotification().getBody());

        }else{
            showMessage(data);
        }

        // add a button
        builder.setPositiveButton("Save Notification", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                messaging.messagingStorageController.setNotificationWasDismiss(false);
                gotoListaActivity();
                dialog.dismiss();


            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                messaging.messagingStorageController.setNotificationWasDismiss(false);

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showMessage(TextView data) {
        data.setText("The Notification was Dismiss by User"+"\n");
        if(messaging.messagingStorageController.isDataNotification()){
            Map<String,String> provMap=messaging.messagingStorageController.getDataNotification();
            data.append(""+provMap);
            MessagingNotification messagingNotification=new MessagingNotification();
            messagingNotification.setData(provMap);
        }

    }

    private void createAlertUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout_user, null);
        builder.setView(customLayout);


        // add a button
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                EditText editText_key = customLayout.findViewById(R.id.editText_key);
                String key=editText_key.getText().toString();
                EditText editText_value = customLayout.findViewById(R.id.editText_value);
                String value=editText_value.getText().toString();
                messagingUserDevice.addProperties(key,value);
                createAlertUser();


            }
        });

        builder.setNegativeButton("Close And Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sendDialogDataToUser();

            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();



    }

    private void sendDialogDataToUser() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": For update"+ messagingUserDevice.getProperties());
        progressBar.setVisibility(View.VISIBLE);
        messagingUserDevice.save(getApplicationContext());

    }

    private void creatAlert() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
        builder.setView(customLayout);
        TextView vista=customLayout.findViewById(R.id.tag_selection);
        TextView clear=customLayout.findViewById(R.id.tag_clear);
        vista.setText("Select: "+ messagingDev.getTags());
        clear.setText("Clear");
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messagingDev.clearTags();
                creatAlert();
                return false;
            }


        });
        // add a button
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity

                EditText editText = customLayout.findViewById(R.id.editText_tag);
                String tags=editText.getText().toString();
                messagingDev.addTagsToDevice(tags);
                creatAlert();



            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sendDialogDataToActivity();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void sendDialogDataToActivity() {

        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Tags selection final was "+ messagingDev.getTags());
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
            Serializable message=intent.getSerializableExtra("message");
            boolean wasNotiDismiss=intent.getBooleanExtra("DismissNoti",false);
            Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": wasNotiDismiss:  "+ wasNotiDismiss);
            if ((message instanceof MessagingDev) && (message!=null)){
                messangiDevArrayList.clear();

                messagingDev =(MessagingDev) message;


                Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Device:  "+ messagingDev.getId());
                messangiDevArrayList.add("Id: "           + messagingDev.getId());
                messangiDevArrayList.add("pushToken: "    + messagingDev.getPushToken());
                messangiDevArrayList.add("UserId: "       + messagingDev.getUserId());
                messangiDevArrayList.add("Type: "         + messagingDev.getType());
                messangiDevArrayList.add("Language: "     + messagingDev.getLanguage());
                messangiDevArrayList.add("Model: "        + messagingDev.getModel());
                messangiDevArrayList.add("Os: "           + messagingDev.getOs());
                messangiDevArrayList.add("SdkVersion: "   + messagingDev.getSdkVersion());
                messangiDevArrayList.add("Tags: "         + messagingDev.getTags());
                messangiDevArrayList.add("CreateAt: "     + messagingDev.getCreatedAt());
                messangiDevArrayList.add("UpdatedAt: "    + messagingDev.getUpdatedAt());
                messangiDevArrayList.add("Timestamp: "    + messagingDev.getTimestamp());
                messangiDevArrayList.add("Transaction: "  + messagingDev.getTransaction());


                lista_device.setAdapter(messangiDevArrayAdapter);
                messagingDev.requestUserByDevice(getApplicationContext(),false);


            }else if((message instanceof MessagingUserDevice) && (message!=null)){
                messangiUserDeviceArrayList.clear();
                messagingUserDevice =(MessagingUserDevice) message;

                Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+" User:  "+ messagingUserDevice.getDevices());

                if(messagingUserDevice.getProperties().size()>0){
                    Map<String,String> result= messagingUserDevice.getProperties();
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        messangiUserDeviceArrayList.add(entry.getKey()+": "+entry.getValue());
                    }
                    messangiUserDeviceArrayList.add("devices: "+ messagingUserDevice.getDevices());

                }

                lista_user.setAdapter(messangiUserDeviceArrayAdapter);
            }else if((message instanceof MessagingNotification) && (message!=null)){
                messagingNotification =(MessagingNotification) message;
                showAlertNotificaction(messagingNotification);

            }else{

                if(wasNotiDismiss){
                 showAlertNotificaction(messagingNotification);
                }
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }

            }
            if(progressBar.isShown()){
                progressBar.setVisibility(View.GONE);
            }


        }


    };

    @Override
    protected void onDestroy() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": unregister BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }


}
