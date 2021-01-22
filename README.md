# MessagingAndroidSDK

[![CI Status](https://img.shields.io/badge/build-v1.0.0-blue)](https://git.messangi.com/messangi/messangi-ios-sdk)
![Bintray](https://img.shields.io/bintray/v/jbarrios23/TestLibraryMessangiSDK/com.android.testdefsdknotificactionpush)
[![CI Status](https://img.shields.io/badge/platform-Android-blue)](https://git.messangi.com/messangi/messangi-ios-sdk)



## Description
---
It is a tool that allows you to add the following functionalities to your solution
- Send notifications through messangi services.
- Enable or disable notifications by the user.
- Register device characteristics (UUID, Type, Language, OS Version, Model).
- Associate labels to the device.
- Save customizable user information.
- Set up for Geofence monitoring.
- Receive push notification and add Geofence.
- Receive push notification and update Geofence.
- Receive push notification and delete Geofence.
- Create Geofence Object builder.
- Specfy Geofence and initial triggers.
- Use a boadcast receiver for Geofence Transition.
- Sort the geofences based on the last location obtained and record up to 100 geofences.
- Handle geofence transitions.
- Stop geofence Monitoring.
- Reduction power consumption.
- Re-register geofences only when required.
- Handle InAppPush and Silent Push.

## Requirements
---
To use the Messangi SDK is required:
- A registered apple account in the development program.
- Follow the next steps for installation.

## Installation
----
### Step 1: Create Android Studio Project
Open Android Studio IDE and start new project you are working on.
#### Implicit Implementation
Place the "MessagiSDK" dependency in app.gradle
```Gradle 
implementation 'com.android.testdefsdknotificactionpush:sdk:1.0'
```
or
The SDK folder can only be obtained from this repo, put folder new project project directory and put this line in app.gradle:
```Gradle 
 implementation project(path: ':sdk')
```
and in settings.gradle:
```Gradle 
 include ':app', ':sdk'
```
### 2) Configure FCM in Android Project Project
Select your project and go to the **Tools** tab, select a Firebase and open the assistant then:
<img src="step0.jpg" />

1.- Select Cloud Messangin and Set up Firebase Cloud Messanging.
<img src="step1.jpg" />
<img src="step1b.jpg" />

2.- Connect app to Firebase and create new or select Firebase project in console.
<img src="step2a.jpg" />
<img src="step2a1.jpg" />
<img src="step2b1.jpg" />
<img src="step2b.jpg" />

3.- Do not perform step 3 "Handle message", as the SDK will do it for you.

### 3) Put Messangi.xlm file in values project.
Put the configuration file in the values folder of the Android project

```xml
<resources>
  <string name="messangi_host" translatable="false" templateMergeStrategy="preserve"><"Url services"></string>
  <string name="messangi_app_token" translatable="false"><"Auth token"></string>
  <bool name="analytics_allowed">boolean condition</bool>
  <bool name="location_allowed">boolean condition</bool>
  <bool name="logging_allowed">boolean condition</bool>
  <bool name="locationPermissionAtStartup">boolean condition</bool>
</resources>
```
### 4) Put LocalBroadcastReceiver in Activity project.
Put LocalBroadcastReceiver in Activity file project, example:

```java
...
import com.ogangi.messangi.sdk.Messangi;

public class MainActivity extends AppCompatActivity{
    ...
       ...
        @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_DEVICE));
                .....
    }
    
```
The Receiver of the LocalBroadcastManager instances must be registered using the corresponding Intentfilte, example: Messaging.ACTION_FETCH_DEVICE 
```
    
    public void dothisForRequestUser() {
        Messaging.fetchDevice(false);
    }
    
```
Using fetchDevice device delivers the instance of a device, by internal memory, by local memory or a service.


     @Override
    protected void onDestroy() {
       LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }
Unregister Receiver in main Activity.

If you want to get a response from the fetch device request you should implement a LocalBroadcastReceiver in the main Activity:
 
```java
private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            
            if (!hasError ) {
                Serializable data=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                switch (intent.getAction()){
                    case Messaging.ACTION_REGISTER_DEVICE:
                        messagingDevice = (MessagingDevice) data;
                       ...
                    break;
                    case Messaging.ACTION_FETCH_DEVICE:
                        messagingDevice = (MessagingDevice) data;
                        //or messagingDevice = MessagingDevice.getInstance();
                        ....
                        if(messagingUser == null){
                            Messaging.fetchUser(getApplicationContext(),false);
                        }
                        ....
                    break;
                    case Messaging.ACTION_SAVE_DEVICE:
                        messagingDevice = (MessagingDevice) data;
                        ....
                        if(messagingUser != null){
                            Messaging.fetchUser(getApplicationContext(),true);
                        }
                        ......
                        break;

                    case Messaging.ACTION_FETCH_USER:
                        messagingUser = (MessagingUser) data;
                        //or messagingUser = MessagingUser.getInstance();
                        .....
                        break;
                    case Messaging.ACTION_SAVE_USER:
                        messagingUser = (MessagingUser) data;
                        .....
                        break;
                    case Messaging.ACTION_GET_NOTIFICATION:
                        messagingNotification = (MessagingNotification) data;
                        ....
                        break;
                    case Messaging.ACTION_GET_NOTIFICATION_OPENED:
                        messagingNotification = (MessagingNotification) data;
                        ...
                        break;
                    case Messaging.ACTION_FETCH_LOCATION:
                    MessagingLocation messagingLocation = (MessagingLocation) data;
                        .....
                        break;
                    default:
                        break;
                }
            }else{
                Toast.makeText(getApplicationContext(),"An error occurred on action "
                        +intent.getAction(),Toast.LENGTH_LONG).show();
            }
            ......
        }
    };
   //you can observe all the implementation in example app
```
### 5) Put BroadcastReceiver in app project.
Put BroadcastReceiver in app project, example:

```java
...
import com.ogangi.messangi.sdk.Messangi;

public class MessagingNotificationReceiver extends BroadcastReceiver{
    ...
      ...
        @Override
    public void onReceive(Context context, Intent intent) {
       boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
        
        if (!hasError ) {
            String action=intent.getAction();
            Serializable data = intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
            //optional code to determinate if app is Background or not
            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            
            if(intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION)&& data!=null){
            ........
            }else if(intent.getAction().equals(Messaging.ACTION_FETCH_LOCATION)){
                wayLatitude = intent.getDoubleExtra(Messaging.INTENT_EXTRA_DATA_lAT,0.00);
                 wayLongitude = intent.getDoubleExtra(Messaging.INTENT_EXTRA_DATA_lONG,0.00);
                 Location location=new Location(LOCATION_SERVICE);
                 location.setLatitude(wayLatitude);
                 location.setLongitude(wayLongitude);
                 MessagingLocation messagingLocation=new MessagingLocation(location);
                 if(isInBackground){
                    .......
                 }else{
                     sendEventToActivity(Messaging.ACTION_FETCH_LOCATION,messagingLocation,context);
                     .......
                 }
            }else if(intent.getAction().equals(Messaging.ACTION_GEOFENCE_ENTER)
                     ||intent.getAction().equals(Messaging.ACTION_GEOFENCE_EXIT)
                     ||intent.getAction().equals(Messaging.ACTION_FETCH_GEOFENCE)){
                        if(isInBackground){
                        ..... 
                        }else{
                         sendEventToActivity(Messaging.ACTION_FETCH_GEOFENCE,messagingCircularRegions,context);
                         }
                         
            }
        }
        .....
    }
    //to Handle InAppPush, SIlent Push and Geopush
    private void handleDataNotification(Serializable data, Intent intent,
                                        Context context, String action, boolean isInBackground) {
        if(isInBackground){
            messagingNotification = (MessagingNotification) data;
            String subject="";
            String content = "";
            String Title="";
            String Text = "";
            String Image="";
            boolean showCustomNotification=false;
            boolean showCustomNotificationGeoPush=false;
            for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                if(!entry.getKey().equals("profile")){
                    if(entry.getKey().equals("subject")) {
                        subject= (String) entry.getValue();
                    }else if(entry.getKey().equals("content")){
                        content= (String) entry.getValue();
                    }else if(entry.getKey().equals("Title")){
                        Title= (String) entry.getValue();
                    }else if(entry.getKey().equals("Text")){
                        Text= (String) entry.getValue();
                    }else if(entry.getKey().equals("Image")){
                        Image= (String) entry.getValue();
                        showCustomNotification=true;
                    }else if(entry.getKey().equals("MSGI_GEOPUSH")){
                        showCustomNotificationGeoPush=true;
                    }
                }
            }
            if(showCustomNotification){
                showCustomNotification(Title,Text,Image,context,messagingNotification);
            }
            if(showCustomNotificationGeoPush){
                showNotificationGP(messagingNotification,context);
            }

        }else{
            sendEventToActivity(action,data,context);
        }
    }
    //to Handle SIlent Push with image and data
    private void showCustomNotification(String title, String text, String image, 
                                        Context context, MessagingNotification messagingNotification) {
        nameMethod="showCustomNotification";
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(image);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Intent notificationIntent=null;
                    try {
                    notificationIntent = new Intent(context,
                                Class.forName(messaging.getNameClass()));
                        if(messagingNotification.getAdditionalData().size()>0) {
                            notificationIntent.putExtra(Messaging.INTENT_EXTRA_DATA, messagingNotification);
                            Static.messagingNotification=messagingNotification;
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                       ....
                    }catch (NullPointerException e){
                        e.printStackTrace();
                        notificationIntent = new Intent("android.intent.action.MAIN");
                    }
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    final PendingIntent pendingIntent = PendingIntent.getActivity(context
                            , 0, notificationIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setLargeIcon(bmp)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setNotificationSilent()
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(bmp)
                                    .bigLargeIcon(null))
                            .build();

                notificationManager.notify(1 /* ID of notification */, notification);
                    
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    ....
                } catch (IOException e) {
                    e.printStackTrace();
                    .....
                }
            }
        }).start();
    }
    
    //to handle Geopush in Background
    private void showNotificationGP(MessagingNotification notification, Context context) {
        String classNameProv=messaging.getNameClass();
        Intent notificationIntent=null;
        String Title="";
        String Body="";
        Title=notification.getTitle();
        Body=notification.getBody();
        try {
            notificationIntent = new Intent(context, Class.forName(classNameProv));
            if(messagingNotification.getAdditionalData().size()>0) {
                notificationIntent.putExtra(Messaging.INTENT_EXTRA_DATA, messagingNotification);
                Static.messagingNotification=messagingNotification;
            }

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
            setupChannels(context);
        }
        int notificationId = new Random().nextInt(60000);

        // Create the notification.
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                .setSmallIcon(messaging.icon)  //a resource for your custom small icon
                .setContentTitle(Title) //the "title" value you sent in your notification
                .setContentText(Body) //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(Context context) {
        CharSequence adminChannelName = context.getString(com.messaging.sdk.R.string.notifications_admin_channel_name);
        String adminChannelDescription = context.getString(com.messaging.sdk.R.string.notifications_admin_channel_description);
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
    
   /**
     * Method that send Parameter (Ej: messagingDevice or MessagingUser) registered to Activity
     * @param something : Object Serializable for send to activity (Ej messagingDevice).
     * @param context : context instance
     */
    private void sendEventToActivity(String action,Serializable something, Context context) {
        if(something!=null) {
            Intent intent = new Intent(action);
            intent.putExtra(Messaging.INTENT_EXTRA_DATA, something);
            intent.putExtra(Messaging.INTENT_EXTRA_HAS_ERROR, something == null);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }else{
            .... 
        }
    }
```
Please declare BroadcastReceivers in Manifest.xml of app project, example:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.ogangi.Messangi.SDK.Demo">

    <application
        .....
        <activity 
        </activity>
        <receiver
            android:name=".MessagingNotificationReceiver"
            android:enabled="true"
            android:permission="${applicationId}.permission.pushReceive"
            android:exported="false"
            tools:ignore="Instantiatable">
            <intent-filter>
               <action android:name="com.messaging.sdk.PUSH_NOTIFICATION"/>
               <action android:name="com.messaging.sdk.ACTION_FETCH_LOCATION"/>
               <action android:name="com.messaging.sdk.ACTION_GEOFENCE_ENTER"/>
               <action android:name="com.messaging.sdk.ACTION_GEOFENCE_EXIT"/>
            </intent-filter>
        </receiver>
        <receiver
            android:name="com.messaging.sdk.MessaginGeofenceBroadcastReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </receiver>
    </application>
    <permission
        android:name="${applicationId}.permission.pushReceive"
        android:protectionLevel="signature" />
    <uses-permission android:name="${applicationId}.permission.pushReceive" />
</manifest>
```

## Usage
To make use of the functionalities that Messanging SDK offers, the Messaging class is available, to obtain the instance of this class you can do: 
```java
Messaging messaging=Messaging.getInstance(this);//get Instance of Messaging..
//or Messaging messaging=Messaging.getInstance();//get Instance of Messaging..
```
All the services offered by this library are provided by means of an instance of the Messaging class, and it can be obtained that it was indicated later.
```java
MessagingDevice messagingDevice; 
```
MessangingDevice is used for handle Device paramenter in SDK. To obtain this device instance, the SDK can provide it in three ways: by local memory, internal memory or by service. all forms use a BroadcastReceiver to get that instance. see point number 4
```java
MessagingUser messagingUser; 
```

MessagingUser is used for handle User paramenter in SDK.
To obtain this User instance, the SDK can provide it in three ways: by local memory, internal memory or by service. all forms use a BroadcastReceiver to get that instance. see point number 4

By doing this you have access to
    Method that get Device registered
    forsecallservice: It allows effective device search in three ways: by instance,
     by shared variable or by service. when forsecallservice=true, search device parameters through the service. This method use BoradcastReceiver for send Instance from SDK to Activity.See point number 4

```java
    Messaging.fetchDevice(true);
```
Method for add new Tags to Device, then you can do save and immediately it is updated in the database.

```java
     messagingDev.addTagToDevice(tags);
```
Method that make Update of paramenter Device using service, this method use BoradcastReceiver for send Instance from SDK to Activity.See point number 4.
```java
     messagingDevice.save(getApplicationContext());
```
Method to set status of Notification push receiver (true o false), this method is used to enable Notification push listening.
```java
     messagingDevice.setStatusNotificationPush(false, getApplicationContext());
```
Method for get User by Device registered from service, allows effective device search in three ways: by instance, by shared variable or by service. this method use BoradcastReceiver for send Instance from SDK to Activity.See point number 4.
When forsecallservice=true, search device parameters through the service.
```java
     Messaging.fetchUser(getApplicationContext(), true);
```
Method that make Update of User parameter using service, this method use BoradcastReceiver for send Instance from SDK to Activity.See point number 4.

```java
     messagingUser.save(getApplicationContext());
```
Method for add Property to user, example: name, lastname, email or phone,  key : example name
     value : example Jose, then you can use messagingUser.save(getApplicationContext());
     for update User data.
     
```java
     messagingUser.addProperty(key, value);
```
Method for ask if auntomatic permission is enable, use for get permission of location from app is instaled.
     
```java
     messaging.isEnable_permission_automatic();
```
Method for make request permission of location from app. 
```java
     Messaging.requestPermissions(AnyActivity.this);
```
Method for set state of GPS in SDK. 
```java
     messaging.setGPS(isGPSEnable);
```
Method to logOut process, deleting dB local of Geofence and disable listening of Notification push. 
```java
     Messaging.logOutProcess();
```
Method to set Location Allowed in sdk (true or false). 
```java
     Messaging.setLocationAllowed(isChecked);
```
Method to set Analytic Allowed in sdk (true or false). 
```java
    Messaging.setAnalytincAllowed(isChecked);
```
Method to set Loging Allowed in sdk (true or false). 
```java
    Messaging.setLogingAllowed(isChecked);
```
Method to get Location Allowed state (true or false). 
```java
    messaging.isLocation_allowed();
```
Method to get Loging Allowed state (true or false). 
```java
    messaging.isLogging_allowed();
```
Method to get Analytics Allowed state (true or false). 
```java
    messaging.isAnalytics_allowed();
```


## Example - Getting MessagingDevice
```java
...
import com.ogangi.messangi.sdk.Messangi;

public class MainActivity extends AppCompatActivity{
    ...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    }
      @Override
    protected void onStart() {
        super.onStart();
       LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_DEVICE));
      }
       ...
      @Override
    protected void onResume() {
        super.onResume();
       Messaging.fetchDevice(false);
    }
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            
            if (!hasError ) {
                Serializable data=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                if(intent.getAction().equals(Messaging.ACTION_FETCH_DEVICE)&& data!=null){
                    messagingDevice = (MessagingDevice) data; //you can cast this for get information
                    .....
                }else if(intent.getAction().equals(Messaging.ACTION_FETCH_USER)&& data!=null){
                    messagingUser =(MessagingUser) data;
                    .......
                
            }else{
                Toast.makeText(getApplicationContext(),"An error occurred on action "
                        +intent.getAction(),Toast.LENGTH_LONG).show();
            }
            ......
        }
    };
   //you can see all the implementation in example app
     @Override
    protected void onDestroy() {
     LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
     }
   ```

## Example - Getting MessagingUser
```java
...
import com.ogangi.messangi.sdk.MessangiUserDevice;

public class MainActivity extends AppCompatActivity{
    private MessagingUser messagingUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    }
    
       @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_USER));
        }
    ...
      
    protected void onGetUser() {
        Messaging.fetchUser(getApplicationContext(), true);
    }
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            
            if (!hasError ) {
                Serializable data=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                     .....
                }else if(intent.getAction().equals(Messaging.ACTION_FETCH_USER)&& data!=null){
                    messagingUser =(MessagingUser) data;
                    //or
                    messagingUser = MessagingUser.getInstance();
                    .......
                
            }else{
                Toast.makeText(getApplicationContext(),"An error occurred on action "
                        +intent.getAction(),Toast.LENGTH_LONG).show();
            }
            ......
        }
    };
   //you can see all the implementation in example app
     @Override
    protected void onDestroy() {
     LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
     }
   
```
## Full implementation example:
 in MainActivity.java
```java
package com.ogangi.Messangi.SDK.Demo;
....
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessangingDevice;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;

....

public class MainActivity extends AppCompatActivity {
    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public static String TAG="MESSANGING";
    public static final String DELETE_TAG = "DELETE_TAG";

    public Messaging messaging;
    public MessagingDevice messangingDevice;
    public MessagingUser messagingUser;
    MessagingNotification messagingNotification;
    

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        messaging = Messaging.getInstance(this);
        messaging.getExternalId());// get external ID using Sdk.

        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ...
                Messaging.fetchDevice(true);
                Messaging.fetchUser(getApplicationContext(), true);
                
            }
        });

       save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messangingDevice.getTags().size() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    messagingDevice.save(getApplicationContext());//save parameter in backend using service.
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_LONG).show();
                }
            }
        });

        //for handle notification from background
    Bundle extras=getIntent().getExtras();
        if(extras!=null){
            isBackground=extras.getBoolean("isInBackground",false);
            
            if(isBackground) {
                Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
                messagingNotification=(MessagingNotification)data;
               ......

            }else {

                //to process notification from background mode
                MessagingNotification notification=Messaging.checkNotification(extras);
                .....
            }

        }
    }

    @Override
    protected void onStart() {
        ....
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
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
       .....
       Messaging.fetchDevice(false);//get device parameter
       }
    
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            

            boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            
            if (!hasError ) {
                Serializable data=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);
                if(intent.getAction().equals(Messaging.ACTION_FETCH_DEVICE)&& data!=null){
                    messagingDevice = (MessagingDevice) data; //you can cast this for get information
                    //or messagingDevice = MessagingDevice.getInstance();
                    showdevice(messagingDevice);

                }else if(intent.getAction().equals(Messaging.ACTION_FETCH_USER)&& data!=null){
                    messagingUser =(MessagingUser) data;
                    //or messagingUser = MessagingUser.getInstance();
                    shwUser(messagingUser);

                }else if(((intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION))||(intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION_OPENED)))&& data!=null){
                    messagingNotification=(MessagingNotification)data;
                    .....

                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_DEVICE)&& data!=null) {
                    messagingDevice = (MessagingDevice) data; //you can cast this for get information
                    //or messagingDevice = MessagingDevice.getInstance();
                    //for condition of save (user or device);
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_LONG).show();
                    showdevice(messagingDevice);
                }else if(intent.getAction().equals(Messaging.ACTION_SAVE_USER)&& data!=null) {
                    messagingUser =(MessagingUser) data; //you can cast this for get information
                    //or messagingUser = MessagingUser.getInstance();
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

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
}
```
For handle Notification in Background you must use this code in Activity:
```java
        //for handle notification from background
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            isBackground=extras.getBoolean("isInBackground",false);
            if(isBackground) {
                Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
                messagingNotification=(MessagingNotification)data;
                .....
            }else {
                //to process notification from background mode
    MessagingNotification notification=Messaging.checkNotification(extras);
                .......
            }
        }
```
## more detail see example app (demoApp)

 if you want handle from app the notification you can create class in app project named CustomMessangiService, example:

```java
package com.ogangi.Messangi.SDK.Demo;

import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.messaging.sdk.MessagingFirebaseService;
import com.messaging.sdk.MessagingNotification;

public class CustomMessangiService extends MessagingFirebaseService {

  ....
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //example to custom
        MessagingNotification messagingNotification = new MessagingNotification(remoteMessage);
        messaging = Messaging.getInstance(this);
        messaging.sendGlobalEventToActivity(Messaging.ACTION_GET_NOTIFICATION,messagingNotification);
    }
}



```
 It is important to use super.onNewToken(s) (heredity), it is necessary for the proper functioning of the sdk, for this custom class, and you will can receive message using method onMessageReceived.
 **Remember** if you want to use **CustomMessangiService**, please declare service in Manifest.xml of app project, example:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.ogangi.Messangi.SDK.Demo">

    <application
        .....
        <activity 
        </activity>
        <service
                    android:name=".CustomMessangiService"
                    android:permission="com.google.android.c2dm.permission.SEND">
                    <intent-filter>
                        <action android:name="com.google.firebase.MESSAGING_EVENT" />
                        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                    </intent-filter>
        </service>
    </application>
</manifest>
```

## To use DeepLink:
1.	**By Payload:**
For this, the property "click_action" sent through the push notification will be used, which has two cases:

a.	**Notification in foreground:** In this case the notification will be handled by the SDK taking the property **"click_action"** of the notification which should have a value as shown in the example: **"click_action": "com.ogangi.Messangi.SDK.Demo.ExampleActivity"**, the which is processed by the SDK allowing the host app to create an icon in the notification tray that when pressed opens the activity selected or described in the action.

b.	**Background notification:** in this case the notification will be handled by the operating system as native behavior, where the app programmer must declare the activity to open when pressing this notification, it is important to remember that the notification must bring the field **" click_action ":" com.ogangi.Messangi.SDK.Demo.ExampleActivity "**, for example, and the declaration of the activity by the programmer must be done in the **AndroidManifest.xml** file, for example:
```xml
        <activity android: name = ". ExampleActivity">
             <intent-filter>
             <action android: name = "com.ogangi.Messangi.SDK.Demo.ExampleActivity" />
             <category android: name = "android.intent.category.DEFAULT" />
             </intent-filter>
        </activity>
```
In MainActivity of the demo app.
In foreground and notification is sent.

<img src="step3a.jpg" />

Arrival of the NP to the app that shows the detail of the same and in the upper part shows the generation of the Notification thanks to the Click-Action parameter that comes in it.

The alert is ok and the notification palette opens:

<img src="step3b.jpg" />

Pressing the notification opens the preselected activity and displays the notification data using the following structure:

```Java
Bundle extras=getIntent().getExtras();
        boolean enable=extras.getBoolean("enable",false);
        additionalData = new HashMap<>();
        if(extras!=null && !enable) {
            for (String key : extras.keySet()) {
               additionalData.put(key, extras.getString(key));
                messangiData.add(key + " , " + extras.getString(key));
            }
            .....
        }else{
            Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
            messagingNotification=(MessagingNotification)data;
            additionalData=messagingNotification.getAdditionalData();
            if(additionalData!=null&& additionalData.size()>0) {
                messangiData.add("Title: " + messagingNotification.getTitle());
                messangiData.add("Body: " + messagingNotification.getBody());
                messangiData.add("ClickAction: " + messagingNotification.getClickAction());
                messangiData.add("DeepUriLink: " + messagingNotification.getDeepUriLink());
                for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                    if (!entry.getKey().equals("profile")) {
                        messangiData.add(entry.getKey() + " , " + entry.getValue());
                    }

                }
            }
        }
        .....
```

<img src="step3c.jpg" />

In the case of the notification that arrives with the app in the background:
The notification palette opens and you press:

<img src="step3b.jpg" />

<img src="step4.jpg" />

This action was performed using the native behavior of the SO. But it is important that the notification has the field: **"click_action": "com.ogangi.Messangi.SDK.Demo.ExampleActivity"** 
Defined in this way.
And the data can also be processed using:
```Java
Bundle extras=getIntent().getExtras();
        boolean enable=extras.getBoolean("enable",false);
        additionalData = new HashMap<>();
        if(extras!=null && !enable) {
            for (String key : extras.keySet()) {
               additionalData.put(key, extras.getString(key));
                messangiData.add(key + " , " + extras.getString(key));
            }
            .....
        }else{
            Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
            messagingNotification=(MessagingNotification)data;
            additionalData=messagingNotification.getAdditionalData();
            if(additionalData!=null&& additionalData.size()>0) {
                messangiData.add("Title: " + messagingNotification.getTitle());
                messangiData.add("Body: " + messagingNotification.getBody());
                messangiData.add("ClickAction: " + messagingNotification.getClickAction());
                messangiData.add("DeepUriLink: " + messagingNotification.getDeepUriLink());
                for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                    if (!entry.getKey().equals("profile")) {
                        messangiData.add(entry.getKey() + " , " + entry.getValue());
                    }

                }
            }
        }
```

The implementation of this functionality in the demo app of this repository, in the main activity you have an **example code**.


2.	**By url schemes:**
For this, the property **"link"** sent through the push notification will be used, which has two cases:

a.	**Notification in foreground:** In this case the notification will be handled by the SDK taking the property "link" of the notification which should have a value as shown in the example:
**"link": exampleapp: // example / example? param1 = 1**, which is processed by the SDK allowing the host app to launch a navigation attempt with said Url, which if it is registered in an activity (in the file **AndroidManifest.xml**), the app will directly open that activity and handle the information sent in the notification.

b.	**Background notification:** in this case the notification will be handled by the operating system as native behavior, where the app programmer must declare the activity to open when pressing this notification, it is important to remember that the notification must bring the field **"link": exampleapp://example/example?param1=1**, for example, and the declaration of the activity by the programmer must be done in the **AndroidManifest.xml** file, for example:

```xml
        <activity android:name=".ExampleURLSchemasActivity">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    android:host="example"
                    android:pathPrefix="/example"
                    android:scheme="exampleapp" />
            </intent-filter>
        </activity>
```
c. **From a web browser:** in this case the flow will be managed by the operating system as native behavior, where the app programmer must declare the activity to open when pressing this DeepLink Url schemas.

<img src="step5.jpg" />

And the data can also be processed using:
in ExampleURLSchemasActivity.java
```Java
Bundle extras=getIntent().getExtras();
        boolean enable=extras.getBoolean("enable",false);
        additionalData = new HashMap<>();
        if(extras!=null && !enable) {
            for (String key : extras.keySet()) {
               additionalData.put(key, extras.getString(key));
                messangiData.add(key + " , " + extras.getString(key));
            }
            .....
        }else{
            Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
            messagingNotification=(MessagingNotification)data;
            additionalData=messagingNotification.getAdditionalData();
            if(additionalData!=null&& additionalData.size()>0) {
                messangiData.add("Title: " + messagingNotification.getTitle());
                messangiData.add("Body: " + messagingNotification.getBody());
                messangiData.add("ClickAction: " + messagingNotification.getClickAction());
                messangiData.add("DeepUriLink: " + messagingNotification.getDeepUriLink());
                for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                    if (!entry.getKey().equals("profile")) {
                        messangiData.add(entry.getKey() + " , " + entry.getValue());
                    }

                }
            }
        }
```

The implementation of this functionality in the demo app of this repository, in the main activity you have an **example code**.


3.	**By universal links:**
For this, the property **"link"** sent through the push notification will be used, which has two cases:

a.	**Notification in foreground:** In this case the notification will be handled by the SDK taking the property "link" of the notification which should have a value as shown in the example:
**"link":http://www.plantplaces.com/colorcapture.shtml?param1=value1&param2=value2**, which is processed by the SDK allowing the host app to launch a navigation attempt with said Url, which if it is registered in an activity (in the file **AndroidManifest.xml**), the app will directly open that activity and handle the information sent in the notification.

b.	**Background notification:** in this case the notification will be handled by the operating system as native behavior, where the app programmer must declare the activity to open when pressing this notification, it is important to remember that the notification must bring the field **"link": http://www.plantplaces.com/colorcapture.shtml?param1=value1&param2=value2**, for example, and the declaration of the activity by the programmer must be done in the **AndroidManifest.xml** file, for example:

```xml
        <activity android:name=".ExampleUrlActivity">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    android:host="www.plantplaces.com"
                    android:pathPrefix="/colorcapture.shtml"
                    android:scheme="http" />
            </intent-filter>
        </activity>

```
c. **From a web browser:** in this case the flow will be managed by the operating system as native behavior, where the app programmer must declare the activity to open when pressing this DeepLink Url schemas.

<img src="step8.jpg" />

And the data can also be processed using:
in ExampleUrlActivity.java
```Java
Bundle extras=getIntent().getExtras();
        boolean enable=extras.getBoolean("enable",false);
        additionalData = new HashMap<>();
        if(extras!=null && !enable) {
            for (String key : extras.keySet()) {
               additionalData.put(key, extras.getString(key));
                messangiData.add(key + " , " + extras.getString(key));
            }
            .....
        }else{
            Serializable data = extras.getSerializable(Messaging.INTENT_EXTRA_DATA);
            messagingNotification=(MessagingNotification)data;
            additionalData=messagingNotification.getAdditionalData();
            if(additionalData!=null&& additionalData.size()>0) {
                messangiData.add("Title: " + messagingNotification.getTitle());
                messangiData.add("Body: " + messagingNotification.getBody());
                messangiData.add("ClickAction: " + messagingNotification.getClickAction());
                messangiData.add("DeepUriLink: " + messagingNotification.getDeepUriLink());
                for (Map.Entry entry : messagingNotification.getAdditionalData().entrySet()) {
                    if (!entry.getKey().equals("profile")) {
                        messangiData.add(entry.getKey() + " , " + entry.getValue());
                    }

                }
            }
        }
```

The implementation of this functionality in the demo app of this repository, in the main activity you have an **example code**.


## Author
Messaging

## License
MessagiingSDK is available under the MIT license. See the LICENSE file for more info.
