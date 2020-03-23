# MessangiAndroidSDK

![Cirrus CI - Specific Branch Build Status](https://img.shields.io/cirrus/github/jbarrios23/MessangiAndroidSDK/master)
![Bintray](https://img.shields.io/bintray/v/jbarrios23/TestLibraryMessangiSDK/com.android.testdefsdknotificactionpush)
[![Platform](https://img.shields.io/cocoapods/p/MessangiSDK.svg?style=flat)](https://cocoapods.org/pods/MessangiSDK)


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
- Select Cloud Messangin and Set up Firebase Cloud Messanging.
- Connet your app to Firebase and create or select Firebase project in console.
- Add FCM to your project

### 3) Create MyApplication file in your project.

Please create class MyApplication in project and extends to Application: 
```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        }
}

```
**Note:** don't forget to make the declaration of the class MyApplication in AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.testimplemtationsdkmessangi">

    <application
        android:name=".MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        tools:ignore="GoogleAppIndexingWarning">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```
### 4) Put Messangi.xlm file in values project.
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
### 5) Put BroadcastReceiver in Activity project.
Put BroadcastReceiver in Activity file project:

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
            
            if ((message instanceof MessangiDev) && (message!=null)){
               //do something


            }else if((message instanceof MessangiUserDevice) && (message!=null)){
               //do something

        }
    };
   
```

## Usage
To make use of the functionalities that Messangi SDK offers, the Messangi class is available, to obtain the instance of this class you can do 
```java
Messangi messangi =Messangi.getInst(this);
MessangiDev messangiDev;
MessangiUserDevice messangiUserDevice;
```
By doing this you have access to
```java
messangi.requestDevice(true);//get device
messangiDev.addTagsToDevice(tags);// add tags to device
messangiDev.save(getApplicationContext());
messangiDev.requestUserByDevice(getApplicationContext(),false);//get User By Device
messangiDev=storageController.getDevice();//get device from local storage
messangiUserDevice.save(getApplicationContext());//save or update User parameter
messangiUserDevice.addProperties(key,value);//add properties to User.

```

## Example - Getting MessangiDevice
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
    protected void onResume() {
        super.onResume();
        messangi.requestDevice(false);
    }
```

## Example - Getting MessangiUser
```java
...
import com.ogangi.messangi.sdk.MessangiUserDevice;

public class MainActivity extends AppCompatActivity{
    private MessangiUserDevice messangiUserDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    }
    ...
      @Override
    protected void onResume() {
        super.onResume();
        messangi.requestDevice(false);
        messangiDev.requestUserByDevice(getApplicationContext(),false);
    }
   
```
## Full implementation example
 in  MainActivity.java
```java
package com.testimplemtationsdkmessangi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.ogangi.messangi.sdk.Messangi;
import com.ogangi.messangi.sdk.MessangiDev;
import com.ogangi.messangi.sdk.MessangiUserDevice;
import com.ogangi.messangi.sdk.SdkUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity{


    public static String CLASS_TAG=MainActivity.class.getSimpleName();
    public static String TAG="MessangiSDK";

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
    public ProgressBar progressBar;
    public TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        lista_device=findViewById(R.id.lista_device);
        lista_user=findViewById(R.id.lista_user);
        title=findViewById(R.id.textView_imprimir);

        device=findViewById(R.id.device);
        user=findViewById(R.id.user);
        tags=findViewById(R.id.tag);
        save=findViewById(R.id.save);
        progressBar=findViewById(R.id.progressBar);
        Switch simpleSwitch = findViewById(R.id.simpleSwitch);

        messangi=Messangi.getInst(this);
        messangiDevArrayList=new ArrayList<>();
        messangiUserDeviceArrayList=new ArrayList<>();
        messangiDevArrayAdapter=new ArrayAdapter<>(this,R.layout.item_device,R.id.Texview_value,messangiDevArrayList);
        messangiUserDeviceArrayAdapter=new ArrayAdapter<>(this,R.layout.item_device,R.id.Texview_value,messangiUserDeviceArrayList);
        title.setText(getResources().getString(R.string.title)+"\n"+messangi.getExternalId());

        device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messangiDevArrayList.clear();
                messangiUserDeviceArrayList.clear();
                progressBar.setVisibility(View.VISIBLE);
                messangi.requestDevice(true);
                Log.e(TAG,CLASS_TAG+": "+messangi.getExternalId());
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
                if(messangiDev.getTags().size()>0) {
                    progressBar.setVisibility(View.VISIBLE);
                    messangiDev.save(getApplicationContext());
                }else{
                    Toast.makeText(getApplicationContext(),"Nothing to save",Toast.LENGTH_LONG).show();
                }
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    Toast.makeText(getApplicationContext(),"Enable Notification Push",Toast.LENGTH_LONG).show();
                    messangiDev.setStatusNotificationPush(isChecked,getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                }else{

                    Toast.makeText(getApplicationContext(),"Disable Notification Push",Toast.LENGTH_LONG).show();
                    messangiDev.setStatusNotificationPush(isChecked,getApplicationContext());
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,CLASS_TAG+": register BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("PassDataFromoSdk"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG,CLASS_TAG+": onResume");
        messangiDevArrayList.clear();
        messangiUserDeviceArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        messangi.requestDevice(false);
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
                messangiUserDevice.addProperties(key,value);
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

        Log.i(TAG,CLASS_TAG+": For update"+messangiUserDevice.getProperties());
        progressBar.setVisibility(View.VISIBLE);
        messangiUserDevice.save(getApplicationContext());

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
        vista.setText("Select: "+messangiDev.getTags());
        clear.setText("Clear");
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messangiDev.clearTags();
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
                messangiDev.addTagsToDevice(tags);
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

        Log.i(TAG,CLASS_TAG+": Tags selection final was "+messangiDev.getTags());
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Serializable message=intent.getSerializableExtra("message");
            SdkUtils sdkUtils=new SdkUtils();
            if ((message instanceof MessangiDev) && (message!=null)){
                messangiDevArrayList.clear();

                messangiDev=(MessangiDev) message;
                Log.i(TAG,CLASS_TAG+": Device:  "+sdkUtils.getGsonJsonFormat(messangiDev));
                messangiDevArrayList.add("Id: "           +messangiDev.getId());
                messangiDevArrayList.add("pushToken: "    +messangiDev.getPushToken());
                messangiDevArrayList.add("UserId: "       +messangiDev.getUserId());
                messangiDevArrayList.add("Type: "         +messangiDev.getType());
                messangiDevArrayList.add("Language: "     +messangiDev.getLanguage());
                messangiDevArrayList.add("Model: "        +messangiDev.getModel());
                messangiDevArrayList.add("Os: "           +messangiDev.getOs());
                messangiDevArrayList.add("SdkVersion: "   +messangiDev.getSdkVersion());
                messangiDevArrayList.add("Tags: "         +messangiDev.getTags());
                messangiDevArrayList.add("CreateAt: "     +messangiDev.getCreatedAt());
                messangiDevArrayList.add("UpdatedAt: "    +messangiDev.getUpdatedAt());
                messangiDevArrayList.add("Timestamp: "    +messangiDev.getTimestamp());
                messangiDevArrayList.add("Transaction: "  +messangiDev.getTransaction());


                lista_device.setAdapter(messangiDevArrayAdapter);
                messangiDev.requestUserByDevice(getApplicationContext(),false);



            }else if((message instanceof MessangiUserDevice) && (message!=null)){
                messangiUserDeviceArrayList.clear();
                messangiUserDevice=(MessangiUserDevice) message;
                Log.i(TAG,CLASS_TAG+" User:  "+sdkUtils.getGsonJsonFormat(messangiUserDevice));


                if(messangiUserDevice.getProperties().size()>0){
                    Map<String,String> result=messangiUserDevice.getProperties();
                    for (Map.Entry<String, String> entry : result.entrySet()) {

                        messangiUserDeviceArrayList.add(entry.getKey()+": "+entry.getValue());
                    }


                }

                lista_user.setAdapter(messangiUserDeviceArrayAdapter);
            }else{
                Log.i(TAG,CLASS_TAG+": do nothing");
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
        Log.i(TAG,CLASS_TAG+": unregister BroadcastReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}

```
in  activity_main.xlm
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_gravity="center"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    tools:ignore="ScrollViewCount"
    xmlns:android="http://schemas.android.com/apk/res/android">

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        >
        <TextView
            android:id="@+id/textView_imprimir"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:lines="2"
            android:gravity="center"
            android:text="@string/title"
            android:textSize="15sp"
            />

        <!--gravity of the Switch-->

        <Switch
            android:id="@+id/simpleSwitch"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_below="@+id/textView_imprimir"
            android:layout_centerHorizontal="true"
            android:background="@color/greyColor"
            android:checked="true"
            android:drawableStart="@android:drawable/ic_menu_view"
            android:drawableLeft="@android:drawable/ic_menu_view"
            android:gravity="center" />

        <TextView
            android:id="@+id/textView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/greyColor"
            android:gravity="center"
            android:layout_below="@+id/simpleSwitch"
            android:lines="2"
            android:text="@string/device_info"
            android:textColor="@color/whiteColor"
            android:textSize="10sp"
            />

        <ListView
            android:id="@+id/lista_device"
            android:layout_width="wrap_content"
            android:layout_height="300dp"
            android:layout_below="@+id/textView"
            tools:listitem="@layout/item_device" />

        <TextView
            android:id="@+id/textView_two"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_below="@+id/lista_device"
            android:background="@color/greyColor"
            android:gravity="center"
            android:lines="2"
            android:text="@string/user_device_info"
            android:textColor="@color/whiteColor"
            android:textSize="10sp" />
        <ProgressBar
            android:id="@+id/progressBar"
            style="?android:attr/progressBarStyle"
            android:layout_width="100dp"
            android:layout_height="100dp"
            android:layout_centerInParent="true"
            android:progress="25"
            android:visibility="gone"
            tools:visibility="visible"
            android:indeterminate="true"
            android:indeterminateTintMode="src_atop"
            android:indeterminateTint="@color/greyColor"
            />
        <ListView
            android:id="@+id/lista_user"
            android:layout_width="wrap_content"
            android:layout_height="200dp"
            android:layout_below="@+id/textView_two"
            tools:listitem="@layout/item_device" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_below="@+id/lista_user"
            android:background="@color/greyColor"
            android:gravity="center_horizontal"
            android:orientation="horizontal">

            <Button
                android:id="@+id/device"
                android:layout_width="80dp"
                android:layout_height="40dp"
                android:textSize="11sp"
                android:background="@color/greyColor"
                android:drawableStart="@android:drawable/ic_menu_directions"
                android:text="Device"
                android:textColor="@color/whiteColor"
                android:drawableLeft="@android:drawable/ic_menu_directions" />

            <Button
                android:id="@+id/user"
                android:layout_width="80dp"
                android:layout_height="40dp"
                android:layout_marginStart="30dp"
                android:layout_marginLeft="30dp"
                android:background="@color/greyColor"
                android:drawableStart="@android:drawable/ic_dialog_map"
                android:textSize="12sp"
                android:text="User"
                android:textColor="@color/whiteColor"
                android:drawableLeft="@android:drawable/ic_dialog_map" />

            <Button
                android:id="@+id/tag"
                android:layout_width="80dp"
                android:layout_height="40dp"
                android:background="@color/greyColor"
                android:drawableStart="@android:drawable/ic_menu_today"
                android:drawableLeft="@android:drawable/ic_menu_today"
                android:text="Tags"
                android:textSize="12sp"
                android:textColor="@color/whiteColor" />



            <Button
                android:id="@+id/save"
                android:layout_width="80dp"
                android:layout_height="40dp"
                android:layout_marginStart="30dp"
                android:layout_marginLeft="30dp"
                android:background="@color/greyColor"
                android:textColor="@color/whiteColor"
                android:drawableStart="@android:drawable/ic_menu_save"
                android:drawableLeft="@android:drawable/ic_menu_today"
                android:text="Save"
                android:textSize="12sp"/>


        </LinearLayout>

    </RelativeLayout>
</LinearLayout>
```
in  item_device.xlm
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="10dp"
    android:orientation="horizontal">
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/Texview_value"
        android:textStyle="bold"
        android:textSize="12sp"
        android:layout_marginStart="16dp"
        android:text="Value"
        android:layout_marginLeft="16dp" />

</LinearLayout>
```

in  custom_layout.xlm
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:gravity="center_horizontal"
    android:orientation="vertical"
    android:layout_height="match_parent">

    <EditText
        android:id="@+id/editText_tag"
        android:layout_width="wrap_content"
        android:hint="@string/hint"
        android:layout_height="wrap_content"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="15sp"
        android:id="@+id/tag_selection"
        android:text="selection"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="15sp"
        android:id="@+id/tag_clear"
        android:text="selection"/>

</LinearLayout>

```

in  custom_layout_user.xlm
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:layout_height="wrap_content">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="18sp"
        android:id="@+id/tag_selection"
        android:text="Type a new property for this user"/>

    <EditText
        android:id="@+id/editText_key"
        android:layout_width="match_parent"
        android:hint="@string/hint_key"
        android:layout_height="wrap_content"/>
    <EditText
        android:id="@+id/editText_value"
        android:layout_width="match_parent"
        android:hint="@string/hint_value"
        android:layout_height="wrap_content"/>
</LinearLayout>

```

## Author
Messangi

## License
MessangiSDK is available under the MIT license. See the LICENSE file for more info.
