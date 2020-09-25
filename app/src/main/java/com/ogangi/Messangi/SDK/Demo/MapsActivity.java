package com.ogangi.Messangi.SDK.Demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingLocation;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_BALANCED_POWER_ACCURACY;
import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_HIGH_ACCURACY;
import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_LOW_POWER;
import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_NO_POWER;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static String CLASS_TAG=MapsActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    private GoogleMap mMap;
    public Messaging messaging;
    private String nameMethod;
    private Marker locationMarker;
    MessagingNotification messagingNotification;
    public boolean onetimeFlag=true;
    public MessagingLocation messagingLocation;
    private Button getLocation,getPermission,getLocationC,turnOffLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        messaging=Messaging.getInstance();
        getLocation=findViewById(R.id.button_get_location);
        getLocationC=findViewById(R.id.button_get_location_c);
        getPermission=findViewById(R.id.button_get_permission);
        turnOffLocationButton=findViewById(R.id.button_turnOffLocation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messaging.isLocation_allowed()) {
                    Messaging.fetchLocation(MapsActivity.this, false,PRIORITY_BALANCED_POWER_ACCURACY);
                    Log.d(CLASS_TAG,TAG+ " Priority "+Messaging.getLocationRequestPriority());
                }else{
                    Log.d(CLASS_TAG,TAG+ " isLocation_allowed "+messaging.isLocation_allowed());
                }
            }
        });

        getLocationC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messaging.isLocation_allowed()) {
                    Messaging.fetchLocation(MapsActivity.this, true,PRIORITY_HIGH_ACCURACY);
                    Log.d(CLASS_TAG,TAG+ " Priority "+Messaging.getLocationRequestPriority());
                }else{
                    Log.d(CLASS_TAG,TAG+ " isLocation_allowed "+messaging.isLocation_allowed());
                }
            }
        });



        getPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verify permission get
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " has verify permission : "
                        + messaging.isEnable_permission_automatic());
                if(messaging.isEnable_permission_automatic() ){
                    Messaging.requestPermissions(MapsActivity.this);
                }

            }
        });

        turnOffLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Messaging.turnOFFUpdateLocation();
                stopService();
            }
        });

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                Log.d(CLASS_TAG,TAG+ " isGPS To Interface one "+isGPSEnable);
                messaging.setGPS(isGPSEnable);
                Log.d(CLASS_TAG,TAG+ " isGPS To Interface two "+messaging.isGPS());
            }
        });

    }
    private void stopService() {
        messaging.stopServiceLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": register LocalBroadcastReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_LOCATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_GET_NOTIFICATION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(CLASS_TAG,TAG+ " Resume "+messaging.isGPS());


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void writeActualLocation(Location location) {

        markerLocation(new LatLng(location.getLatitude(),location.getLongitude()));
    }

    private void markerLocation(LatLng latLng) {
        Log.i(CLASS_TAG, "markerLocation("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if ( mMap!=null ) {
            // Remove the anterior marker
//            if ( locationMarker != null )
//                locationMarker.remove();
            locationMarker = mMap.addMarker(markerOptions);
            float zoom = 15f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);

            //mMap.animateCamera(cameraUpdate);
            mMap.moveCamera(cameraUpdate);
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

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

                if(intent.getAction().equals(Messaging.ACTION_FETCH_LOCATION) ) {

                    if(Messaging.getLastLocation()!=null) {
                        messagingLocation = new MessagingLocation(Messaging.getLastLocation());
                        Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location from storage Lat:  "
                                + messagingLocation.getLatitude() + " Long: " + messagingLocation.getLongitude());

                    }else{
                        messagingLocation = (MessagingLocation) data;
                        Toast.makeText(getApplicationContext(), intent.getAction(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location Lat:  "
                                + messagingLocation.getLatitude()+" Long: "+messagingLocation.getLongitude());

                    }
                    writeActualLocation(messagingLocation.getLocation());
                    }else if(((intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION))||
                            (intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION_OPENED)))&& data!=null) {
                        messagingNotification = (MessagingNotification) data;
                        showAlertNotification(messagingNotification, data);

                    }else{
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_SHORT).show();
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
                        Messaging.fetchLocation(MapsActivity.this,false,PRIORITY_LOW_POWER);
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
//        if(messagingNotification.getClickAction()!=null && data!=null){
//            String clickAction=messagingNotification.getClickAction();
//            if(onetimeFlag) {
//                launchNotification(clickAction, getApplicationContext(), data);
//                onetimeFlag=false;
//            }
//        }
        //optional code
//        if(messagingNotification.getDeepUriLink()!=null && data!=null){
//            String deepUriLink=messagingNotification.getDeepUriLink();
//            if(onetimeFlag) {
//                launchBrowser(deepUriLink, this, data);
//                onetimeFlag=false;
//            }
//        }
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
}