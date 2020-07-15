package com.ogangi.Messangi.SDK.Demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    public static final String DELETE_TAG = "DELETE_TAG";

    public Messaging messaging;
    public Button device,user,tags,save;
    public TextView imprime;
    public MessagingDevice messagingDevice;
    public MessagingUser messagingUser;
    public ListView lista_device,lista_user;

    public ArrayList messagingDevArrayList;
    //public ArrayList<Map.Entry<String, Object>> messagingDevArrayList;
    public ArrayAdapter<String> messagingDevArrayAdapter;
    //public ArrayAdapter messagingDevArrayAdapter;
    public ArrayList<String> messagingUserDeviceArrayList;
    public ArrayAdapter messagingUserDeviceArrayAdapter;
    public ProgressBar progressBar;
    public TextView title;
    //public Button pressButton;
    MessagingNotification messagingNotification;
    private String nameMethod;

    private NotificationManager notificationManager;
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    public boolean onetimeFlag=true;
    public static MainActivity mainActivityInstance;
    public Map<String,String> additionalData;
    public boolean isBackground;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mainActivityInstance=this;
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        //messaging = Messaging.getInstance(this);
        messaging = Messaging.getInstance();

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
        messagingDevArrayList = new ArrayList<>();
        messagingUserDeviceArrayList = new ArrayList<>();
        messagingDevArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messagingDevArrayList);
        messagingUserDeviceArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messagingUserDeviceArrayList);
        title.setText(getResources().getString(R.string.title) );

        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagingDevArrayList.clear();
                messagingUserDeviceArrayList.clear();
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
                createAlert();
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
        if(extras!=null){
            isBackground=extras.getBoolean("isInBackground",false);
            Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + isBackground);
            if(isBackground) {
                String data = extras.getString(Messaging.INTENT_EXTRA_DATA);
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + data);
                try {
                    JSONObject data1 = new JSONObject(data);
                    showAlertNotificationAlt(data1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {

                //to process notification from background mode
                additionalData=new HashMap<>();
                for(String key:extras.keySet()){
                    additionalData.put(key,extras.getString(key));
                }
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + additionalData);
                showAlertNotificationAltPlus(additionalData);
            }

        }


    }

    private void showAlertNotificationAltPlus(Map<String, String> data1) {
            nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
            Log.d(TAG,"Data1: "+CLASS_TAG+": "+nameMethod+":  "
                + data1.toString());
            additionalData=data1;
            Log.d(TAG,"Data3: "+CLASS_TAG+": "+nameMethod+":  "
                    + additionalData);

            // create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification");
            // set the custom layout
            //final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
            final View customLayout = getLayoutInflater().inflate(R.layout.notification_layout, null);
            builder.setView(customLayout);
            //TextView data=customLayout.findViewById(R.id.data_noti);
            ArrayList<Map.Entry<String, Object>> messangiData = new ArrayList(additionalData.entrySet());
            ArrayAdapter messagingDataArrayAdapter;
            ListView listView=customLayout.findViewById(R.id.list_data_noti);
            messagingDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
            listView.setAdapter(messagingDataArrayAdapter);

            // add a button
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // send data from the AlertDialog to the Activity
                    onetimeFlag=true;

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

    @Override
    protected void onStart() {
        super.onStart();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": register LocalBroadcastReceiver");
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
        messagingDevArrayList.clear();
        messagingUserDeviceArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        Messaging.fetchDevice(false,getApplicationContext());
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+"onResume: ");

    }

    public void showAlertNotificationAlt(JSONObject data1){
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+":  "
                + data1.toString()+" has "+data1.has("additionalData"));
        Map<String, Object> additionalData;
        try {
            if(data1.has("clickAction")){
                String clickAction=data1.getString("clickAction");
                launchNotification(clickAction,getApplicationContext(),data1);
            }
            Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+":  "
                    + !data1.getString("additionalData").equals("{}"));
            if(!data1.getString("additionalData").equals("{}")) {

                additionalData = toMap(data1);

                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ":  "
                        + additionalData);

                // create an alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Notification");
                // set the custom layout
                //final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
                final View customLayout = getLayoutInflater().inflate(R.layout.notification_layout, null);
                builder.setView(customLayout);
                //TextView data=customLayout.findViewById(R.id.data_noti);
                ArrayList<Map.Entry<String, Object>> messangiData = new ArrayList(additionalData.entrySet());

                ArrayAdapter messangiDataArrayAdapter;
                ListView listView = customLayout.findViewById(R.id.list_data_noti);
                messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
                listView.setAdapter(messangiDataArrayAdapter);


                // add a button
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send data from the AlertDialog to the Activity
                        onetimeFlag = true;

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
                try {
                    dialog.show();
                }catch (Exception e){
                    e.getStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "Notification Push has Not data", Toast.LENGTH_LONG).show();
                showOtherParameter(data1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"ERROR: "+CLASS_TAG+": "+nameMethod+":  "+e.getMessage());
        }


    }

    private void showOtherParameter(JSONObject data1) {
        try {
            Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+":  "+
                    " only noti "+data1.getString("title")+" "
                    +data1.getString("body"));

            // create an alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification");
            // set the custom layout
            //final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
            final View customLayout = getLayoutInflater().inflate(R.layout.notification_layout, null);
            builder.setView(customLayout);
            //TextView data=customLayout.findViewById(R.id.data_noti);
            ArrayList<String> messagingData = new ArrayList();
            messagingData.add(data1.getString("title"));
            messagingData.add(data1.getString("body"));
            messagingData.add(data1.getString("silent"));
            messagingData.add(data1.getString("sound"));
            messagingData.add(data1.getString("sticky"));
            messagingData.add(data1.getString("badge"));
            ArrayAdapter messangiDataArrayAdapter;
            ListView listView = customLayout.findViewById(R.id.list_data_noti);
            messangiDataArrayAdapter = new ArrayAdapter<String>(this, R.layout.item_device, R.id.Texview_value, messagingData);
            listView.setAdapter(messangiDataArrayAdapter);


            // add a button
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // send data from the AlertDialog to the Activity
                    onetimeFlag = true;

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

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"ERROR: "+CLASS_TAG+": "+nameMethod+":  "+e.getMessage());
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

    public  List<Object> toList(JSONArray array) throws JSONException {
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


    //optional code
    private void launchNotification(String clickAction, Context context, JSONObject additionalData) {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        String title="";
        String body = "";
        Intent notificationIntent=null;
        try {
            title=additionalData.getString("title");
            body=additionalData.getString("body");
            notificationIntent = new Intent(context, Class.forName(clickAction));
            notificationIntent.putExtra("data",additionalData.toString());
            notificationIntent.putExtra("enable",true);
//            for (Map.Entry<String, String> entry : additionalData.entrySet()) {
//                notificationIntent.putExtra(entry.getKey(),  entry.getValue());
//            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }catch (NullPointerException e){
            e.printStackTrace();

            notificationIntent = new Intent("android.intent.action.MAIN");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Setting notification for Android Oreo or higer.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);

        // Create the notification.
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                .setSmallIcon(messaging.icon)  //a resource for your custom small icon
                .setContentTitle(title) //the "title" value you sent in your notification
                .setContentText(body) //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = getApplicationContext().getString(com.messaging.sdk.R.string.notifications_admin_channel_name);
        String adminChannelDescription = getApplicationContext().getString(com.messaging.sdk.R.string.notifications_admin_channel_description);
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
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

    @SuppressLint("SetTextI18n")
    private void createAlert() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name));
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
        builder.setView(customLayout);
        TextView vista=customLayout.findViewById(R.id.tag_selection);
        TextView clear=customLayout.findViewById(R.id.tag_clear);
        vista.setText("Select: "+ messagingDevice.getTags());
        //vista.setText("Select: ");
        clear.setText("Clear");
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messagingDevice.clearTags();
                createAlert();
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
                createAlert();



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

    private void shwUser(MessagingUser messagingUser) {
        messagingUserDeviceArrayList.clear();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+" User:  "+ messagingUser.getDevices());

        if(messagingUser.getProperties().size()>0){
            Map<String,String> result= messagingUser.getProperties();
            for (Map.Entry<String, String> entry : result.entrySet()) {
                messagingUserDeviceArrayList.add(entry.getKey()+": "+entry.getValue());
            }
            messagingUserDeviceArrayList.add("devices: "+ this.messagingUser.getDevices());

        }

        lista_user.setAdapter(messagingUserDeviceArrayAdapter);
    }

    private void showDevice(MessagingDevice messagingDevice) {
        messagingDevArrayList.clear();

        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Device:  "+ messagingDevice.getId());
        messagingDevArrayList.add("Id: "           + messagingDevice.getId());
        messagingDevArrayList.add("pushToken: "    + messagingDevice.getPushToken());
        messagingDevArrayList.add("UserId: "       + messagingDevice.getUserId());
        messagingDevArrayList.add("Type: "         + messagingDevice.getType());
        messagingDevArrayList.add("Language: "     + messagingDevice.getLanguage());
        messagingDevArrayList.add("Model: "        + messagingDevice.getModel());
        messagingDevArrayList.add("Os: "           + messagingDevice.getOs());
        messagingDevArrayList.add("SdkVersion: "   + messagingDevice.getSdkVersion());
        messagingDevArrayList.add("Tags: "         + messagingDevice.getTags());
        messagingDevArrayList.add("CreateAt: "     + messagingDevice.getCreatedAt());
        messagingDevArrayList.add("UpdatedAt: "    + messagingDevice.getUpdatedAt());
        messagingDevArrayList.add("Timestamp: "    + messagingDevice.getTimestamp());
        messagingDevArrayList.add("Transaction: "  + messagingDevice.getTransaction());
        messagingDevArrayList.add("ExternalId: "  + messaging.getExternalId());
        lista_device.setAdapter(messagingDevArrayAdapter);
        Messaging.fetchUser(getApplicationContext(),false);

    }



    @Override
    protected void onDestroy() {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": unregister LocalBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
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
                    messagingDevice = (MessagingDevice) data;
                    showDevice(messagingDevice);

                }else if(intent.getAction().equals(Messaging.ACTION_FETCH_USER)&& data!=null){
                    messagingUser =(MessagingUser) data;
                    shwUser(messagingUser);

                }else if(intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION)&& data!=null){

                            String dataNotification = intent.getStringExtra(Messaging.INTENT_EXTRA_DATA);
                            if (dataNotification != null) {
                                Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": dataNotification:  " + dataNotification);
                                try {
                                    JSONObject jsonObject = new JSONObject(dataNotification);
                                    showAlertNotificationAlt(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_DEVICE)&& data!=null) {
                    messagingDevice = (MessagingDevice) data; //you can cast this for get information
                    //for condition of save (user or device);
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_LONG).show();
                    showDevice(messagingDevice);

                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_USER)&& data!=null) {
                    messagingUser =(MessagingUser) data; //you can cast this for get information
                    //for condition of save (user or device);
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_LONG).show();
                    shwUser(messagingUser);
                }else if(intent.getAction().equals(Messaging.ACTION_REGISTER_DEVICE) ) {
                    messagingDevice = (MessagingDevice)data;
                    showDevice(messagingDevice);
                    Toast.makeText(mainActivityInstance, intent.getAction(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Debug: " + CLASS_TAG + ": " + nameMethod + ": Data Register:  " + data);

                }else{
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

}
