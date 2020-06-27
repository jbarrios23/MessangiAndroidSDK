package com.ogangi.Messangi.SDK.Demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public static String TAG="DEMO_APP";
    public static final String DELETE_TAG = "DELETE_TAG";

    public Messaging messaging;
    public Button device,user,tags,save;
    public TextView imprime;
    public MessagingDevice messagingDevice;
    public MessagingUser messagingUser;
    public ListView lista_device,lista_user;

    public ArrayList<String> messangiDevArrayList;
    public ArrayAdapter<String> messangiDevArrayAdapter;
    public ArrayList<String> messangiUserDeviceArrayList;
    public ArrayAdapter<String> messangiUserDeviceArrayAdapter;
    public ProgressBar progressBar;
    public TextView title;
    //public Button pressButton;
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


        messaging = Messaging.getInstance(this);




        lista_device = findViewById(R.id.lista_device);
        lista_user = findViewById(R.id.lista_user);
        title = findViewById(R.id.textView_imprimir);
        device = findViewById(R.id.device);
        user = findViewById(R.id.user);
        tags = findViewById(R.id.tag);
        save = findViewById(R.id.save);
        //pressButton=findViewById(R.id.button_lista);
        progressBar = findViewById(R.id.progressBar);
        Switch simpleSwitch = findViewById(R.id.simpleSwitch);


        messangiDevArrayList = new ArrayList<>();
        messangiUserDeviceArrayList = new ArrayList<>();
        messangiDevArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiDevArrayList);
        messangiUserDeviceArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiUserDeviceArrayList);
        title.setText(getResources().getString(R.string.title) );

        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messangiDevArrayList.clear();
                messangiUserDeviceArrayList.clear();
                progressBar.setVisibility(View.VISIBLE);
                Messaging.fetchDevice(true,getApplicationContext());
                Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+": "+messaging.getExternalId());
                Messaging.fetchUser(getApplicationContext(), true);
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
                if (messagingDevice.getTags().size() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    messagingDevice.save(getApplicationContext());
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
                    messagingDevice.setStatusNotificationPush(isChecked, getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                } else {

                    Toast.makeText(getApplicationContext(), "Disable Notification Push", Toast.LENGTH_LONG).show();
                    messagingDevice.setStatusNotificationPush(isChecked, getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        //for handle notification from background
        Bundle extras=getIntent().getExtras();
        messagingNotification =new MessagingNotification(extras,getApplicationContext());



    }

    @Override
    protected void onStart() {
        super.onStart();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": register BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_DEVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_USER));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_GET_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_SAVE_DEVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_REGISTER_DEVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_SAVE_USER));

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        messangiDevArrayList.clear();
        messangiUserDeviceArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        Messaging.fetchDevice(false,getApplicationContext());
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+"onResume: ");

    }

    @SuppressLint("SetTextI18n")
    private void showAlertNotificaction(MessagingNotification messagingNotification) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        // set the custom layout
        //final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
        final View customLayout = getLayoutInflater().inflate(R.layout.notification_layout, null);
        builder.setView(customLayout);
        //TextView data=customLayout.findViewById(R.id.data_noti);
         ArrayList<String> messangiData = new ArrayList<>();
         ArrayAdapter<String> messangiDataArrayAdapter;
        ListView listView=customLayout.findViewById(R.id.list_data_noti);

        if(messagingNotification.getNotification()!=null&&(messagingNotification.getAdditionalData()!=null && messagingNotification.getAdditionalData().size() > 0)){

            messangiData.add("Title: "           + messagingNotification.getNotification().getTitle());
            messangiData.add("Body: "           + messagingNotification.getNotification().getBody());
            messangiData.add("ClickAction: "           + messagingNotification.getClickAction());
            for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                if(!entry.getKey().equals("profile")){
                    messangiData.add(entry.getKey() + " , " + entry.getValue());

                }

            }
            messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
            listView.setAdapter(messangiDataArrayAdapter);

        }else if(messagingNotification.getAdditionalData()!=null && messagingNotification.getAdditionalData().size() > 0) {
            messangiData.add("Has only Data");
            for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                if(!entry.getKey().equals("profile")){
                    messangiData.add(entry.getKey() + " , " + entry.getValue());

                }

            }
            messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
            listView.setAdapter(messangiDataArrayAdapter);

        }else if(messagingNotification.getNotification()!=null) {
            messangiData.add("Has only Notification ");
            messangiData.add("Title: "           + messagingNotification.getNotification().getTitle());
            messangiData.add("Body: "           + messagingNotification.getNotification().getBody());
            messangiData.add("ClickAction: "           + messagingNotification.getClickAction());

            messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
            listView.setAdapter(messangiDataArrayAdapter);

        }

        // add a button
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity


                dialog.dismiss();


            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();


            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

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
                messagingUser.addProperty(key,value);
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
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": For update"+ messagingUser.getProperties());
        progressBar.setVisibility(View.VISIBLE);
        messagingUser.save(getApplicationContext());

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
        vista.setText("Select: "+ messagingDevice.getTags());
        clear.setText("Clear");
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messagingDevice.clearTags();
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
                String tag=editText.getText().toString();
                messagingDevice.addTagToDevice(tag);
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
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Tags selection final was "+ messagingDevice.getTags());
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

            boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            Log.d(TAG,"ERROR: "+CLASS_TAG+": "+nameMethod+": Has error:  "+ hasError);
            if (!hasError ) {
                Serializable data=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                if(intent.getAction().equals(Messaging.ACTION_FETCH_DEVICE)&& data!=null){
                    messagingDevice = (MessagingDevice) data; //you can cast this for get information

                    showdevice(messagingDevice);

                }else if(intent.getAction().equals(Messaging.ACTION_FETCH_USER)&& data!=null){
                    messagingUser =(MessagingUser) data;
                    shwUser(messagingUser);

                }else if(intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION)&& data!=null){
                    messagingNotification =(MessagingNotification) data;
                    showAlertNotificaction(messagingNotification);

                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_DEVICE)&& data!=null) {
                    messagingDevice = (MessagingDevice) data; //you can cast this for get information
                    //for condition of save (user or device);
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_LONG).show();
                    showdevice(messagingDevice);
                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_USER)&& data!=null) {
                    messagingUser =(MessagingUser) data; //you can cast this for get information
                    //for condition of save (user or device);
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_LONG).show();
                    shwUser(messagingUser);
                } else {
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_LONG).show();
                }

            }else{

                Toast.makeText(getApplicationContext(),"An error occurred on action "
                        +intent.getAction(),Toast.LENGTH_LONG).show();
                if(progressBar.isShown()){
                    progressBar.setVisibility(View.GONE);
                }

            }
            if(progressBar.isShown()){
                progressBar.setVisibility(View.GONE);
            }

        }


    };

    private void shwUser(MessagingUser messagingUser) {
        messangiUserDeviceArrayList.clear();


        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+" User:  "+ this.messagingUser.getDevices());

        if(this.messagingUser.getProperties().size()>0){
            Map<String,String> result= this.messagingUser.getProperties();
            for (Map.Entry<String, String> entry : result.entrySet()) {
                messangiUserDeviceArrayList.add(entry.getKey()+": "+entry.getValue());
            }
            messangiUserDeviceArrayList.add("devices: "+ this.messagingUser.getDevices());

        }

        lista_user.setAdapter(messangiUserDeviceArrayAdapter);
    }

    private void showdevice(MessagingDevice messagingDevice) {
        messangiDevArrayList.clear();

        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Device:  "+ messagingDevice.getId());
        messangiDevArrayList.add("Id: "           + messagingDevice.getId());
        messangiDevArrayList.add("pushToken: "    + messagingDevice.getPushToken());
        messangiDevArrayList.add("UserId: "       + messagingDevice.getUserId());
        messangiDevArrayList.add("Type: "         + messagingDevice.getType());
        messangiDevArrayList.add("Language: "     + messagingDevice.getLanguage());
        messangiDevArrayList.add("Model: "        + messagingDevice.getModel());
        messangiDevArrayList.add("Os: "           + messagingDevice.getOs());
        messangiDevArrayList.add("SdkVersion: "   + messagingDevice.getSdkVersion());
        messangiDevArrayList.add("Tags: "         + messagingDevice.getTags());
        messangiDevArrayList.add("CreateAt: "     + messagingDevice.getCreatedAt());
        messangiDevArrayList.add("UpdatedAt: "    + messagingDevice.getUpdatedAt());
        messangiDevArrayList.add("Timestamp: "    + messagingDevice.getTimestamp());
        messangiDevArrayList.add("Transaction: "  + messagingDevice.getTransaction());
        messangiDevArrayList.add("ExternalId: "  + messaging.getExternalId());
        lista_device.setAdapter(messangiDevArrayAdapter);
        Messaging.fetchUser(getApplicationContext(),false);

    }

    @Override
    protected void onDestroy() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": unregister BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }


}
