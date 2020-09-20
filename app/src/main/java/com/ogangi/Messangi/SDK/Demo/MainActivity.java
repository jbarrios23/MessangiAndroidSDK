package com.ogangi.Messangi.SDK.Demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationRequest;
import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingLocation;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    public static final String DELETE_TAG = "DELETE_TAG";

    public Messaging messaging;
    public Button device,user,tags,save;
    private ImageButton login;
    public TextView imprime;
    public MessagingDevice messagingDevice;
    public MessagingUser messagingUser;
    public ListView list_device,list_user;

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

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mainActivityInstance=this;
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        //messaging = Messaging.getInstance(this);
        messaging = Messaging.getInstance();

        list_device = findViewById(R.id.lista_device);
        list_user = findViewById(R.id.lista_user);
        title = findViewById(R.id.textView_imprimir);
        device = findViewById(R.id.device);
        user = findViewById(R.id.user);
        tags = findViewById(R.id.tag);
        save = findViewById(R.id.save);
        login=findViewById(R.id.button_list);
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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goToLogin();
                //showAlertGetLogCat();
                gotoMapActivity();
                //Messaging.turnGPSOff();
                //Messaging.sendEventCustomToBackend("notificationPush");
                //stopService();

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
        Bundle extras = null;
        if(Static.extras!=null){
        extras=Static.extras;    
        }
        //Bundle extras=getIntent().getExtras();
        if(extras!=null){
            isBackground=extras.getBoolean("isInBackground",false);
            Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + isBackground);
            if(isBackground) {
                Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
                messagingNotification=(MessagingNotification)data;
                showAlertNotification(messagingNotification, data);


            }else{
                //to process notification from background mode
                MessagingNotification notification=Messaging.checkNotification(extras);
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + notification.toString());
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + notification.equals(notification));
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + notification.hashCode());
            }

        }



    }

    private void stopService() {
       messaging.stopServiceLocation();
    }

    private void gotoMapActivity() {
        Intent intent=new Intent(MainActivity.this,MapsActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void goToLogin() {
     nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
        messaging.showAnalyticAllowedState();
        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " isAnalytics_allowed: " + messaging.isAnalytics_allowed());


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
                new IntentFilter(Messaging.ACTION_GET_NOTIFICATION_OPENED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_SAVE_DEVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_REGISTER_DEVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_SAVE_USER));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_LOCATION));
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
        //verify if GPS turn on!
//        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
//            @Override
//            public void gpsStatus(boolean isGPSEnable) {
//                messaging.setGPS(isGPSEnable);
//                Log.d(CLASS_TAG,TAG+ " isGPS To Interface "+messaging.isGPS());
//            }
//        });

    }



    private void launchBrowser(String deepUriLink, Context context, Serializable additionalData) {
        nameMethod="launchBrowser";
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+":  "+deepUriLink);
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+":  "+additionalData.toString());

        try {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse(String.valueOf(deepUriLink)));
            MessagingNotification messagingNotification=(MessagingNotification)additionalData;
            Static.messagingNotification=messagingNotification;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(deepUriLink));
            //browserIntent.putExtra(Messaging.INTENT_EXTRA_DATA,messagingNotification);
            browserIntent.putExtra("enable",true);
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"ERROR: "+CLASS_TAG+": "+nameMethod+":  "+e.getMessage());

        }

    }


    //optional code
    private void launchNotification(String clickAction, Context context, Serializable additionalData) {
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        String title="";
        String body = "";
        MessagingNotification messagingNotification=(MessagingNotification)additionalData;
        if(messagingNotification.getTitle()!=null &&!messagingNotification.getTitle().equals("")) {
            title = messagingNotification.getTitle();
        }
        if(messagingNotification.getBody()!=null &&!messagingNotification.getBody().equals("")) {
            body = messagingNotification.getBody();
        }
        Intent notificationIntent=null;
        try {

            notificationIntent = new Intent(context, Class.forName(clickAction));
            notificationIntent.putExtra(Messaging.INTENT_EXTRA_DATA,additionalData);
            notificationIntent.putExtra("enable",true);
//            for (Map.Entry<String, String> entry : additionalData.entrySet()) {
//                notificationIntent.putExtra(entry.getKey(),  entry.getValue());
//            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }catch (NullPointerException e){
            e.printStackTrace();

            notificationIntent = new Intent("android.intent.action.MAIN");
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
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+" User:  "+ messagingUser.getProperties());

        if(messagingUser.getProperties().size()>0){
            Map<String,String> result= messagingUser.getProperties();
            for (Map.Entry<String, String> entry : result.entrySet()) {
                messagingUserDeviceArrayList.add(entry.getKey()+": "+entry.getValue());
            }
            //messagingUserDeviceArrayList.add("devices: "+ this.messagingUser.getDevices());

        }

        list_user.setAdapter(messagingUserDeviceArrayAdapter);
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
        messagingDevArrayList.add("Timestamp: "    + messagingDevice.getTimestamp());
        messagingDevArrayList.add("Transaction: "  + messagingDevice.getTransaction());
        messagingDevArrayList.add("ExternalId: "  + messaging.getExternalId());
        messagingDevArrayList.add("Config: ");
        messagingDevArrayList.add("Host: "+messaging.getMessagingHost());
        messagingDevArrayList.add("AppToken: "+messaging.getMessagingToken());
        messagingDevArrayList.add("LocationEnable: "+messaging.isLocation_allowed());
        messagingDevArrayList.add("AnalyticsEnable: "+messaging.isAnalytics_allowed());
        messagingDevArrayList.add("Log Enable: "+messaging.isLogging_allowed());
        messagingDevArrayList.add("Permission Automatic: "+messaging.isEnable_permission_automatic());
        list_device.setAdapter(messagingDevArrayAdapter);
        Messaging.fetchUser(getApplicationContext(),false);

    }
    @SuppressLint("SetTextI18n")
    private void showAlertNotification(MessagingNotification messagingNotification, Serializable data) {
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
        //optional code
        if(messagingNotification.getClickAction()!=null && data!=null){
            String clickAction=messagingNotification.getClickAction();
            if(onetimeFlag) {
                launchNotification(clickAction, getApplicationContext(), data);
                onetimeFlag=false;
            }
        }
        //optional code
        if(messagingNotification.getDeepUriLink()!=null && data!=null){
            String deepUriLink=messagingNotification.getDeepUriLink();
            if(onetimeFlag) {
                launchBrowser(deepUriLink, this, data);
                onetimeFlag=false;
            }
        }
        if(messagingNotification!=null){
            messangiData.add("Title: "           + messagingNotification.getTitle());
            messangiData.add("Body: "           + messagingNotification.getBody());
            messangiData.add("ClickAction: "           + messagingNotification.getClickAction());
            messangiData.add("DeepUriLink: "           + messagingNotification.getDeepUriLink());
            messangiData.add("MessageId: "           + messagingNotification.getNotificationId());
            messangiData.add("Silent: "           + messagingNotification.isSilent());
            messangiData.add("Type: "           + messagingNotification.getType());
            if(messagingNotification.getAdditionalData()!=null){
                for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                if(!entry.getKey().equals("profile")){
                    messangiData.add(entry.getKey() + ": " + entry.getValue());
                }
            }
            }
            messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
            listView.setAdapter(messangiDataArrayAdapter);
        }



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
                onetimeFlag=true;
                dialog.cancel();


            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showAlertGetLogCat() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("LogCat From Sdk");
        // set the custom layout
        //final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
        final View customLayout = getLayoutInflater().inflate(R.layout.layout_logcat, null);
        builder.setView(customLayout);
        TextView data=customLayout.findViewById(R.id.texViewLogCat);
        StringBuilder stringBuilder = new StringBuilder();
        try {
//            Process process = Runtime.getRuntime().exec("logcat -d");
//            BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream()));
//
//            StringBuilder log=new StringBuilder();
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                log.append(line);
//            }

//            Process logcat;
//            final StringBuilder log = new StringBuilder();
//
//                logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
//                BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()),4*1024);
//                String line;
//                String separator = System.getProperty("line.separator");
//                while ((line = br.readLine()) != null) {
//                    log.append(line);
//                    log.append(separator);
//                }

             String processId = Integer.toString(android.os.Process.myPid());
            String[] command = new String[] { "logcat", "-d", "-v", "threadtime" };

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(processId)) {
                    stringBuilder.append(line);
                    //Code here
                }
            }
            data.setText(stringBuilder.toString());

        }
        catch (IOException e) {
            e.printStackTrace();
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

//        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                onetimeFlag=true;
//                dialog.cancel();
//
//
//            }
//        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

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
            Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Has error:  "+ hasError);
            if (!hasError ) {
                Serializable data=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                if(intent.getAction().equals(Messaging.ACTION_FETCH_DEVICE)&& data!=null){
                    messagingDevice = (MessagingDevice) data;
                    showDevice(messagingDevice);

                }else if(intent.getAction().equals(Messaging.ACTION_FETCH_USER)&& data!=null){
                    messagingUser =(MessagingUser) data;
                    shwUser(messagingUser);

                }else if(((intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION))||
                        (intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION_OPENED)))&& data!=null){
                    messagingNotification=(MessagingNotification)data;
                    showDevice(messagingDevice);
                    showAlertNotification(messagingNotification, data);

                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_DEVICE)&& data!=null) {
                    messagingDevice = (MessagingDevice) data; //you can cast this for get information
                    //for condition of save (user or device);
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_LONG).show();
                    showDevice(messagingDevice);

                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_USER)&& data!=null) {
                    messagingUser =(MessagingUser) data; //you can cast this for get information
                    //for condition of save (user or device);
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_SHORT).show();
                    shwUser(messagingUser);
                }else if(intent.getAction().equals(Messaging.ACTION_REGISTER_DEVICE) ) {
                    messagingDevice = (MessagingDevice)data;
                    showDevice(messagingDevice);
                    Toast.makeText(mainActivityInstance, intent.getAction(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Register:  " + data);

                }else if(intent.getAction().equals(Messaging.ACTION_FETCH_LOCATION) ) {
                    MessagingLocation messagingLocation = (MessagingLocation) data;

                    Toast.makeText(mainActivityInstance, intent.getAction(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location Lat:  "
                            + messagingLocation.getLatitude()+" Long: "+messagingLocation.getLongitude());


                }else{
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_SHORT).show();
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
    //it must implement in this activity
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Messaging.fetchLocation(MainActivity.this,true, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    permissionsDenied();
                }
                break;
            }
        }
    }

    private void permissionsDenied() {
        Log.e(CLASS_TAG, TAG+" without this permission you will not have access to the device's location services");
        Toast.makeText(getApplicationContext(), "without this permission you will not have access to the device's location services", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Messaging.GPS_REQUEST) {
                messaging.setGPS(true);  // flag maintain before get location
                Log.d(CLASS_TAG, TAG+" is gps "+messaging.isGPS());
            }
        }
    }

}
