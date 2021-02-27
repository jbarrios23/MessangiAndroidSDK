package com.ogangi.Messangi.SDK.Demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingLocation;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.Map;
import java.util.Random;

import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_BALANCED_POWER_ACCURACY;

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

    public BottomNavigationView bottomNavigation;

    public ArrayList messagingDevArrayList;
    public ArrayAdapter<String> messagingDevArrayAdapter;
    public ArrayList<String> messagingUserDeviceArrayList;
    public ArrayAdapter messagingUserDeviceArrayAdapter;
    public ProgressBar progressBar;
    public TextView title;

    MessagingNotification messagingNotification;
    private String nameMethod;

    private NotificationManager notificationManager;
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    public boolean onetimeFlag=true;
    public boolean onShowDialog=true;
    public static MainActivity mainActivityInstance;
    public Map<String,String> additionalData;
    public boolean isBackground;
    public TextView messageInapp,title_device;
    public LinearLayout layoutInApp;
    public MessagingNotification notification;
    private static final String CHANNEL_ID = "uno";
    public MessagingNotification provNoti=null;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivityInstance=this;
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        //messaging = Messaging.getInstance(this);
        messaging = Messaging.getInstance();

        list_device = findViewById(R.id.lista_device);
        list_user = findViewById(R.id.lista_user);

        bottomNavigation = findViewById(R.id.bottom_navigation);


        title_device=findViewById(R.id.textView);
        messageInapp=findViewById(R.id.texview_inapp);
        layoutInApp=findViewById(R.id.botones);


        progressBar = findViewById(R.id.progressBar);

        messagingDevArrayList = new ArrayList<>();
        messagingUserDeviceArrayList = new ArrayList<>();

        messagingUserDeviceArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messagingUserDeviceArrayList);



        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.action_add_info:
                        createAlertUser();
                        return true;
                    case R.id.action_refresh:
                        Log.i(TAG,"INFO: " + CLASS_TAG + ": " + nameMethod + ": " + messaging.getExternalId());
                        progressBar.setVisibility(View.VISIBLE);
                        messagingDevArrayList.clear();
                        messagingUserDeviceArrayList.clear();
                        Messaging.fetchDevice(true, getApplicationContext());
                        Messaging.fetchUser(getApplicationContext(), true);

                        return true;
                }

                return false;
            }
        });



        //for handle notification from background
        Bundle extras = null;
        if(Static.extras!=null){
        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "Static: " + Static.extras);
        extras=Static.extras;
        Static.extras=null;
        }else{
            extras=getIntent().getExtras();
            Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "extras: " + extras);
        }

        if(extras!=null){
            isBackground=extras.getBoolean("isInBackground",false);
            Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + isBackground);
            if(isBackground) {
//                Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
//                messagingNotification=(MessagingNotification)data;
//                showAlertNotification(messagingNotification, data);
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + messagingNotification.toString());
            }else{
                //to process notification from background mode
                notification=Messaging.checkNotification(extras);
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + notification.toString());
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + notification.equals(notification));
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + notification.hashCode());
            }

        }

        list_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + ": " + position);
                if((position==11)||(position==12)||(position==13)) {
                    showDialogSelectionConfig();
                }
                if((position==07)) {
                    createAlert();
                }
            }
        });

        title_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutInApp.setVisibility(View.GONE);

            }
        });
        Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + "Verify permission Automatic : "
                + messaging.isEnable_permission_automatic());
        if(messaging.isEnable_permission_automatic() ){
            Messaging.requestPermissions(MainActivity.this);
        }


        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                messaging.setGPS(isGPSEnable);
                Log.d(CLASS_TAG,TAG+ " isGPS To Interface two "+messaging.isGPS());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_visibility).setIcon(R.drawable.ic_baseline_visibility_24);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_visibility:
                if(messagingDevice.isEnableNotificationPush()) {
                    Toast.makeText(getApplicationContext(), "Disable Notification Push", Toast.LENGTH_LONG).show();
                    messagingDevice.setStatusNotificationPush(false, getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                    item.setIcon(R.drawable.ic_baseline_visibility_off_24);
                } else {
                    Toast.makeText(getApplicationContext(), "Enable Notification Push", Toast.LENGTH_LONG).show();
                    messagingDevice.setStatusNotificationPush(true, getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                    item.setIcon(R.drawable.ic_baseline_visibility_24);
                }
                return true;
            case R.id.action_location:
                gotoMapActivity();
                return true;
            case R.id.action_logout:
                goToLogin();
                return true;
            case R.id.action_getLog:
                showAlertGetLogCat();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void gotoMapActivity() {
        Intent intent=new Intent(MainActivity.this,MapsActivity.class);
        startActivity(intent);

    }

    private void goToLogin() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MESSAGING_LOGIN", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("IS_LOGGED", false).apply();
        Messaging.logOutProcess();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": register LocalBroadcastReceiver");
        messagingDevArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messagingDevArrayList);
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
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messagingDevArrayList.clear();
        messagingUserDeviceArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG,"INFO: "+CLASS_TAG+": "+nameMethod+": "+notification);
        Messaging.fetchDevice(false, getApplicationContext());
        messaging.showAnalyticAllowedState();
        Log.d(CLASS_TAG, TAG+" state GPS "+messaging.isGPS());
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



    public void showDialogSelectionConfig(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        Messaging messaging=Messaging.getInstance();
        String[] selection=new String[]{"LocationEnable ","AnanlyticsEnable ","Log Enable "};
        boolean[] selectionChecked=new boolean[]{messaging.isLocation_allowed(),
                messaging.isAnalytics_allowed(),messaging.isLogging_allowed()};
        builder.setTitle("config")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(selection, selectionChecked,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+":  "+which+"  "+isChecked);
                                    handleSelection(isChecked,which);
                                }else{

                                    Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+":  "+which+"  "+isChecked);
                                    handleSelection(isChecked,which);
                                }
                                showData();
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog

                    }
                });

        builder.show();

    }

    private void handleSelection(boolean isChecked, int which) {
        switch (which){
            case 0:
            Messaging.setLocationAllowed(isChecked);
            break;
            case 1:
            Messaging.setAnalytincAllowed(isChecked);
            break;
            case 2:
            Messaging.setLogingAllowed(isChecked);
            break;
            default:
            break;


        }


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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Add User Information");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout_user, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("Add Other", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                EditText editText_key = customLayout.findViewById(R.id.editText_key);
                String key = editText_key.getText().toString();
                EditText editText_value = customLayout.findViewById(R.id.editText_value);
                String value = editText_value.getText().toString();
                if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)){
                    messagingUser.addProperty(key, value);
                    createAlertUser();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Field", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNeutralButton("Close And Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText_key = customLayout.findViewById(R.id.editText_key);
                String key = editText_key.getText().toString();
                EditText editText_value = customLayout.findViewById(R.id.editText_value);
                String value = editText_value.getText().toString();
                if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)){
                    messagingUser.addProperty(key, value);
                }
                dialog.cancel();
                sendDialogDataToUser();
            }
        });
        // create and show the alert dialog
        // AlertDialog dialog = builder.create();
        builder.show();
    }

    private void sendDialogDataToUser() {
        nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": For update" + messagingUser.getProperties());
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
        messagingDevice.save(this);
    }

    private void showData() {
        nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
        if(messagingDevice!=null) {
            messagingDevArrayList.clear();
            Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + " Device: " + messagingDevice.toString());
            messagingDevArrayList.add("Id: " + messagingDevice.getId());
            messagingDevArrayList.add("pushToken: " + messagingDevice.getPushToken());
            messagingDevArrayList.add("Type: " + messagingDevice.getType());
            messagingDevArrayList.add("Language: " + messagingDevice.getLanguage());
            messagingDevArrayList.add("Model: " + messagingDevice.getModel());
            messagingDevArrayList.add("Os: " + messagingDevice.getOs());
            messagingDevArrayList.add("SdkVersion: " + messagingDevice.getSdkVersion());
            messagingDevArrayList.add("Tags: " + messagingDevice.getTags());
            messagingDevArrayList.add("Config Parameter: ");
            messagingDevArrayList.add("Host: " + messaging.getMessagingHost());
            messagingDevArrayList.add("AppToken: " + messaging.getMessagingToken());
            messagingDevArrayList.add("LocationEnable: " + messaging.isLocation_allowed());
            messagingDevArrayList.add("AnalyticsEnable: " + messaging.isAnalytics_allowed());
            messagingDevArrayList.add("Log Enable: " + messaging.isLogging_allowed());
            messagingDevArrayList.add("Automatic Permission: " + messaging.isEnable_permission_automatic());
            messagingDevArrayList.add("Enable Background location: " + Messaging.enableLocationBackground);
            list_device.setAdapter(messagingDevArrayAdapter);
        }
        if(messagingUser!=null){
            messagingUserDeviceArrayList.clear();
            Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + " User: "+ messagingUser.toString());
            if(messagingUser.getProperties().size()>0){
                Map<String,String> result= messagingUser.getProperties();
                for (Map.Entry<String, String> entry : result.entrySet()) {
                    messagingUserDeviceArrayList.add(entry.getKey()+": "+entry.getValue());
                }
            }
            list_user.setAdapter(messagingUserDeviceArrayAdapter);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showAlertNotification(MessagingNotification messagingNotification, Serializable data) {
        // create an alert builder
        String nameMethod="showAlertNotification";
        if(messagingNotification.getAdditionalData()!=null){
            String titleData="";
            String bodyData = "";
            String textData = "";
            String Title="";
            String Text = "";
            String Image="";

            boolean showCustomNotification=false;

            Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": data "+messagingNotification.getAdditionalData());
            for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                if(!entry.getKey().equals("profile")){
                    Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": key: "+entry.getKey() + " value: " + entry.getValue());
                    if(entry.getKey().equals(Messaging.MESSAGING_TITLE)) {
                        titleData= (String) entry.getValue();
                    }else if(entry.getKey().equals(Messaging.MESSAGING_BODY)){
                        bodyData= (String) entry.getValue();
                    }else if(entry.getKey().equals("text")){

                        textData= (String) entry.getValue();
                    }else if(entry.getKey().equals("Title")){

                        Title= (String) entry.getValue();
                    }else if(entry.getKey().equals("Text")){

                        Text= (String) entry.getValue();
                    }else if(entry.getKey().equals("Image")){

                        Image= (String) entry.getValue();
                        showCustomNotification=true;
                    }

                    if(entry.getKey().equals("show")||entry.getKey().equals("Image")){
                        onShowDialog=false;
                        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": onshowdialog "+onShowDialog);
                    }

                }
            }
            if(showCustomNotification){
                showCustomNotification(Title,Text,Image);

            }
            if(!onShowDialog) {
                if (!titleData.equals("") && !textData.equals("")) {
                    layoutInApp.setVisibility(View.VISIBLE);
                    messageInapp.setText(titleData + "\n " + textData);
                }else if(!titleData.equals("") && !bodyData.equals("")){
                    layoutInApp.setVisibility(View.VISIBLE);
                    messageInapp.setText(titleData + "\n " + bodyData);
                }else{
                    if(Image.equals("")) {
                        layoutInApp.setVisibility(View.VISIBLE);
                        messageInapp.setText("Message empty");
                    }
                }
            }
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        // builder.setTitle("Notification");
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
            String clickAction = messagingNotification.getClickAction();
            if(onetimeFlag) {
                launchNotification(clickAction, getApplicationContext(), data);
                onetimeFlag=false;
            }
        }

        //optional code
        if(messagingNotification.getDeepUriLink()!=null && data!=null){
            String deepUriLink = messagingNotification.getDeepUriLink();
            if(onetimeFlag) {
                launchBrowser(deepUriLink, this, data);
                onetimeFlag = false;
            }
        }

        if(messagingNotification != null){

            if(Static.messagingNotification!=null){
                provNoti=Static.messagingNotification;

                messangiData.add("Title: "           + provNoti.getTitle());
                messangiData.add("Body: "           + provNoti.getBody());
                messangiData.add("ClickAction: "           + provNoti.getClickAction());
                messangiData.add("DeepUriLink: "           + provNoti.getDeepUriLink());
                messangiData.add("MessageId: "           + provNoti.getNotificationId());
                messangiData.add("Silent: "           + provNoti.isSilent());
                messangiData.add("Type: "           + provNoti.getType());
                if(provNoti.getAdditionalData()!=null){
                    for (Map.Entry entry : provNoti.getAdditionalData().entrySet()) {
                        if(!entry.getKey().equals("profile")){
                            messangiData.add(entry.getKey() + ": " + entry.getValue());

                        }
                    }
                }
                Static.messagingNotification=null;

            }else{
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
//
                        }
                    }
                }

            }

            messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiData);
            listView.setAdapter(messangiDataArrayAdapter);
        }

        // add a button
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                onetimeFlag=true;
                dialog.dismiss();
            }
        });
        if(onShowDialog) {
            builder.show();
        }else{
            onShowDialog=true;
        }

    }

    private void showCustomNotification(String title, String text, String image) {
        nameMethod="showCustomNotification";
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Start "+title+"\n"+text+"\n"+image);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL(image);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        //Bitmap bmp = Messaging.getBitmapFromURL(image);
                        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": bitmap "+bmp);
                        Intent notificationIntent=null;
                        try {
                            notificationIntent = new Intent(getApplicationContext(),
                                    Class.forName(messaging.getNameClass()));
                            Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": name class "
                                    +messaging.getNameClass());

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            Log.e(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": error "+e.getMessage());

                        }catch (NullPointerException e){
                            e.printStackTrace();
                            Log.e(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": error "+e.getMessage());
                            notificationIntent = new Intent("android.intent.action.MAIN");
                        }

                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext()
                                , 0, notificationIntent,
                                PendingIntent.FLAG_ONE_SHOT);
                        notificationManager =
                                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            //String CHANNEL_ID = "my_channel_01";
                            CharSequence name = "my_channel";
                            String Description = "This is my channel";
                            int importance = NotificationManager.IMPORTANCE_HIGH;
                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                            mChannel.setDescription(Description);
                            mChannel.enableLights(true);
                            mChannel.setLightColor(Color.RED);
                            mChannel.enableVibration(true);
                            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                            mChannel.setShowBadge(false);
                            notificationManager.createNotificationChannel(mChannel);
                        }

                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(title)
                                .setContentText(text)
                                .setLargeIcon(bmp)
                                .setNotificationSilent()
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(bmp)
                                        .bigLargeIcon(null))
                                .build();

                        notificationManager.notify(1 /* ID of notification */, notification);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Log.e(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": error 1 " + e.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": error 1 " + e.getMessage());
                    }


                }
            }).start();

    }

    private void showAlertGetLogCat() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logcat Resume");
        // set the custom layout
        //final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
        final View customLayout = getLayoutInflater().inflate(R.layout.layout_logcat, null);
        builder.setView(customLayout);
        TextView data=customLayout.findViewById(R.id.texViewLogCat);
        data.setText(Messaging.getLocat());

        // add a button
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                dialog.dismiss();
            }
        });


        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    @Override
    protected void onStop() {
        super.onStop();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": unregister LocalBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
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
            nameMethod = new Object(){}.getClass().getEnclosingMethod().getName();
            boolean hasError = intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Action:  "+ intent.getAction());
            String alertMessage = getResources().getString(getResources().getIdentifier(intent.getAction(), "string", getPackageName()));
            //Toast.makeText(getApplicationContext(), alertMessage, Toast.LENGTH_LONG).show();
            Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ":   " + alertMessage);

            if (!hasError) {
                Serializable data = intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                //Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Received Action :  " + intent.getAction());
                if(data == null){
                    if(progressBar.isShown()){
                        progressBar.setVisibility(View.GONE);
                    }
                    return;
                }

                switch (intent.getAction()){
                    case Messaging.ACTION_REGISTER_DEVICE:
                        messagingDevice = (MessagingDevice) data;
                        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": messagingDevice :  " + messagingDevice.toString());
                        showData();
                    break;
                    case Messaging.ACTION_FETCH_DEVICE:
                        messagingDevice = (MessagingDevice) data;
                        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": messagingUser :  " + messagingUser);
                        if(messagingUser == null){
                            Messaging.fetchUser(getApplicationContext(),false);
                        }
                        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": messagingDevice :  " + messagingDevice.toString());

                        showData();
                    break;
                    case Messaging.ACTION_SAVE_DEVICE:
                        messagingDevice = (MessagingDevice) data;
                        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": messagingDevice :  " + messagingDevice.toString());

                        if(messagingUser != null){
                            Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": messagingUser E :  " + messagingUser.toString());
                            Messaging.fetchUser(getApplicationContext(),true);
                        }
                        showData();
                        break;

                    case Messaging.ACTION_FETCH_USER:
                        messagingUser = (MessagingUser) data;
                        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": messagingUser :  " + messagingUser.toString());
                        showData();
                        break;
                    case Messaging.ACTION_SAVE_USER:
                        messagingUser = (MessagingUser) data;
                        Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ": messagingUser :  " + messagingUser.toString());
                        showData();
                        break;
                    case Messaging.ACTION_GET_NOTIFICATION:
                        messagingNotification = (MessagingNotification) data;
                        showAlertNotification(messagingNotification, data);
                        break;
                    case Messaging.ACTION_GET_NOTIFICATION_OPENED:
                        messagingNotification = (MessagingNotification) data;
                        showAlertNotification(messagingNotification, data);
                        break;
                    case Messaging.ACTION_FETCH_LOCATION:
                        MessagingLocation messagingLocation = (MessagingLocation) data;
                        Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location Lat:  " + messagingLocation.getLatitude()+" Long: "+messagingLocation.getLongitude());
                        break;
                    default:
                        break;
                }
            } else {
            Toast.makeText(getApplicationContext(),"An error occurred on action "
                    + alertMessage,Toast.LENGTH_LONG).show();
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
                    Messaging.fetchLocation(MainActivity.this,true);
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
        }else{
        messaging.setGPS(false);
        Log.d(CLASS_TAG, TAG+" Denai is gps "+messaging.isGPS());
    }

    }

}
