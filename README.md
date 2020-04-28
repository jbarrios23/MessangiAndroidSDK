# MessangiAndroidSDK

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

## Requirements
---
To use the Messangi SDK is required:
- A registered apple account in the development program.
- Follow the n steps for installation.

## Installation
----
### Step 1: Create Android Studio Project
Open Android Studio IDE and start new project you are working on.
#### Implicit Implementation
Place the "MessangiSDK" dependency in app.gradle
```Gradle 
implementation 'com.android.testdefsdknotificactionpush:sdk:1.0'
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
</resources>
```
### 4) Put BroadcastReceiver in Activity project.
Put BroadcastReceiver in Activity file project, example:

```java
...
import com.ogangi.messangi.sdk.Messangi;

public class MainActivity extends AppCompatActivity{
    ...
    private Messangi messangi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    }
       ...
        @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter("PassDataFromSdk"));
    }
    
     @Override
    protected void onDestroy() {
       LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }
    
private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            
            Serializable message=intent.getSerializableExtra("message");
            boolean hasError=intent.getBooleanExtra("hasError",true);
           if ( (!hasError) && (message instanceof MessagingDevice)){
                do something
            }else if((!hasError) && (message instanceof MessagingUser) ){
                 do something
            }else if((!hasError) && (message instanceof MessagingNotification)){
                 do something

            }else{
                Toast.makeText(getApplicationContext(),"An error occurred while consulting the service",Toast.LENGTH_LONG).show();
                 do something
            }
            
        }
    };
   //please see all the implementation in example app
```

## Usage
To make use of the functionalities that Messanging SDK offers, the Messanging class is available, to obtain the instance of this class you can do: 
```java
Messaging messaging=Messaging.getInstance(this);//get Instance of Messanging for use method. 
MessangingDevice messangingDevice; //class MessangingDevice is used for handle Device paramenter in SDK and service for update device and requestUserByDevice
MessagingUser messagingUser; // class MessagingUser is used for handle User paramenter in SDK and make service update user.
```
By doing this you have access to
```java
    /**
     * Method that get Device registered
     @param forsecallservice: It allows effective device search in three ways: by instance,
     by shared variable or by service.
     when forsecallservice=true, search device parameters through the service. 
     */
Messaging.fetchDevice(true);
    /**
     * Method for add new Tags to Device, then you can do save and
     * immediately it is updated in the database.
     @param newTags: new Tags for add
     */
messagingDev.addTagToDevice(tags);
    /**
     * Method that make Update of paramenter Device using service
     @param context: Instance context.
     */
messangingDevice.save(getApplicationContext());
    /**
     * Method for get User by Device registered from service
     @param context: instance context
     @param forsecallservice : allows effective device search in three ways: by instance, by shared variable or by service.
     when forsecallservice=true, search device parameters through the service.
     */
Messaging.fetchUser(getApplicationContext(), true);
    **
     * Method get Device registered in local storage
    */
messangingDevice =messagingStorageController.getDevice();//get device saved from local storage
    /**
     * Method that make Update of User parameter using service 
     @param context: Instance context
     */
messagingUser.save(getApplicationContext());
   /**
     * Method for add Property to user
     * example: name, lastname, email or phone
     * @param key : example name
     * @param value : example Jose
     */
messagingUser.addProperty(key,value);

```

## Example - Getting MessangiDevice
```java
...
import com.ogangi.messangi.sdk.Messangi;

public class MainActivity extends AppCompatActivity{
    ...
    private Messaging messaging;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    }
       ...
      @Override
    protected void onResume() {
        super.onResume();
       Messaging.fetchDevice(false);
    }
```

## Example - Getting MessangiUser
```java
...
import com.ogangi.messangi.sdk.MessangiUserDevice;

public class MainActivity extends AppCompatActivity{
    private MessagingUser messagingUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    }
    ...
      @Override
    protected void onResume() {
        super.onResume();
       Messaging.fetchDevice(true);
       Messaging.fetchUser(getApplicationContext(), true);
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
    public MessangingDevice messangingDevice;
    public MessagingUser messagingUser;
    MessagingNotification messagingNotification;
    

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();


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
                    messangingDevice.save(getApplicationContext());//save parameter in backend using service.
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_LONG).show();
                }
            }
        });

        //for handle notification from background
        Bundle extras=getIntent().getExtras();
        messagingNotification =new MessagingNotification(extras,getApplicationContext());

    }

    @Override
    protected void onStart() {
        ....
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("PassDataFromSdk"));

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
           
            Serializable message=intent.getSerializableExtra("message");
            boolean hasError=intent.getBooleanExtra("hasError",true);
            if ( (!hasError) && (message instanceof MessagingDevice)){
                messangiDevArrayList.clear();

                messagingDevice =(MessagingDevice) message;

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


                lista_device.setAdapter(messangiDevArrayAdapter);
                Messaging.fetchUser(getApplicationContext(),false);


            }else if((!hasError) && (message instanceof MessagingUser) ){
                messangiUserDeviceArrayList.clear();
                messagingUser =(MessagingUser) message;

                if(messagingUser.getProperties().size()>0){
                    Map<String,String> result= messagingUser.getProperties();
                    for (Map.Entry<String, String> entry : result.entrySet()) {
                        messangiUserDeviceArrayList.add(entry.getKey()+": "+entry.getValue());
                    }
                    messangiUserDeviceArrayList.add("devices: "+ messagingUser.getDevices());

                }

                lista_user.setAdapter(messangiUserDeviceArrayAdapter);
            }else if((!hasError) && (message instanceof MessagingNotification)){
                messagingNotification =(MessagingNotification) message;
                showAlertNotificaction(messagingNotification);

            }else{

                Toast.makeText(getApplicationContext(),"An error occurred while consulting the service",Toast.LENGTH_LONG).show();
                
                }

            }
            .....
            }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
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
        MessagingNotification messagingNotification = new MessagingNotification(remoteMessage, this);
        messagingNotification.getAdditionalData();
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

## Author
Messanging

## License
MessangiingSDK is available under the MIT license. See the LICENSE file for more info.
