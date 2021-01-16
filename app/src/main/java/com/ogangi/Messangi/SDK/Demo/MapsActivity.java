package com.ogangi.Messangi.SDK.Demo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.messaging.sdk.Messaging;
import com.messaging.sdk.MessagingCircularRegion;
import com.messaging.sdk.MessagingDevice;
import com.messaging.sdk.MessagingLocation;
import com.messaging.sdk.MessagingNotification;
import com.messaging.sdk.MessagingUser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_BALANCED_POWER_ACCURACY;
import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_HIGH_ACCURACY;
import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_LOW_POWER;
import static com.messaging.sdk.Messaging.MessagingLocationPriority.PRIORITY_NO_POWER;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static String CLASS_TAG=MapsActivity.class.getSimpleName();
    public static String TAG="MESSAGING";
    private GoogleMap mMap;
    public Messaging messaging;
    private String nameMethod;
    private Marker locationMarker;
    MessagingNotification messagingNotification;
    ArrayList<MessagingCircularRegion> messagingCircularRegions;
    public boolean onetimeFlag=true;
    public MessagingLocation messagingLocation;
    private Button getLocation,getPermission;
    private ToggleButton turnOffLocationButton,getLocationC;
    private Circle geoFenceLimits;
    public TextView textView;
    public boolean showGofenceList=false;
    public boolean onShowDialog=true;
    private NotificationManager notificationManager;
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    private static final String CHANNEL_ID = "uno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        messaging=Messaging.getInstance();
        getLocation=findViewById(R.id.button_get_location);
        getLocationC=findViewById(R.id.button_get_location_c);
        getPermission=findViewById(R.id.button_get_permission);
        turnOffLocationButton=findViewById(R.id.button_backgroundLocation);
        textView=findViewById(R.id.textView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    if (messaging.isLocation_allowed()) {
                        Toast.makeText(getApplicationContext(), "Continue Location "+isChecked,Toast.LENGTH_SHORT).show();
                        Messaging.fetchLocation(MapsActivity.this, true);
                        Log.d(CLASS_TAG, TAG + " Continue Location "+isChecked);
                        Log.d(CLASS_TAG, TAG + " Priority " + Messaging.getLocationRequestPriority());
                        messaging.messagingStorageController.setLocationContinueAllowed(isChecked);
                    } else {
                        messaging.messagingStorageController.setLocationContinueAllowed(false);
                        Log.d(CLASS_TAG, TAG + " isLocation_allowed " + messaging.isLocation_allowed());
                    }
                } else {
                    // The toggle is disabled
                    Messaging.turnOFFUpdateLocation();
                    messaging.messagingStorageController.setLocationContinueAllowed(isChecked);
                    Log.d(CLASS_TAG, TAG + "Continue Location "+isChecked);
                    Toast.makeText(getApplicationContext(), "Continue Location "+isChecked,Toast.LENGTH_SHORT).show();
                }
            }
        });

        turnOffLocationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if (isChecked) {
                       // The toggle is enabled
                       Toast.makeText(getApplicationContext(),"Get Location in Background "+isChecked,Toast.LENGTH_SHORT).show();
                       Log.d(CLASS_TAG, TAG + "Get Location in Background "+isChecked);
                       Messaging.enableLocationBackground=true;
                       messaging.messagingStorageController.setLocationBackgroundAllowed(isChecked);
                   } else {
                       // The toggle is disabled
                       Toast.makeText(getApplicationContext(),"Get Location in Background "+isChecked,Toast.LENGTH_SHORT).show();
                       Log.d(CLASS_TAG, TAG + "Get Location in Background "+isChecked);
                       Messaging.enableLocationBackground=false;
                       Messaging.turnOFFUpdateLocation();
                       messaging.messagingStorageController.setLocationBackgroundAllowed(isChecked);
                   }
               }
           });


        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                messaging.setGPS(isGPSEnable);
                Log.d(CLASS_TAG,TAG+ " isGPS To Interface two "+messaging.isGPS());
            }
        });

        if(messaging.messagingStorageController.hasLocationContinueAllowed()==1){
            getLocationC.setChecked(messaging.messagingStorageController.isLocationContinueAllowed());
        }
        if(messaging.messagingStorageController.hasLocationBackgroundAllowed()==1){
            turnOffLocationButton.setChecked(messaging.messagingStorageController.isLocationBackgroundAllowed());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        //menu.findItem(R.id.action_visibility).setIcon(R.drawable.ic_baseline_visibility_24);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_get_geofences:
                Toast.makeText(getApplicationContext(), "Load geofence List....", Toast.LENGTH_SHORT).show();
                Messaging.fetchGeofence();
                if (geoFenceMarker != null) {
                    geoFenceMarker.remove();
                }
                if (geoFenceLimits != null ) {
                    geoFenceLimits.remove();
                }
                showGofenceList=true;
                return true;
            case R.id.action_get_geofences_service:
                Toast.makeText(getApplicationContext(), "Load geofence List....", Toast.LENGTH_SHORT).show();
                Messaging.fetchGeofence(true);
                showGofenceList=true;
                return true;
            case R.id.action_sinc:
                //gotoMapActivity();
                Messaging.fetchGeofence(true);
                showGofenceList=false;
                return true;
            case R.id.action_permission:
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " Verify permission Automatic : "
                        + messaging.isEnable_permission_automatic());
                if(messaging.isEnable_permission_automatic() ){
                    Messaging.requestPermissions(MapsActivity.this);
                }
                return true;
            case R.id.action_setpriority:
                showAlertGetPriority();
                return true;
            case R.id.action_getLocation:
                if(messaging.isLocation_allowed()) {
                    Messaging.fetchLocation(MapsActivity.this, false);
                    Log.d(CLASS_TAG,TAG+ " Priority "+Messaging.getLocationRequestPriority());
                }else{
                    Log.d(CLASS_TAG,TAG+ " isLocation_allowed "+messaging.isLocation_allowed());
                }
                return true;
            case R.id.action_sendEvent:
                String provReason="Lorem Ipsum es simplemente el texto de relleno de las imprentas y archivos de texto Lorem Ipsum ha sido el texto de relleno estándar de las industrias desde el año 1500 cuando un impresor desconocido usó una galería de textos y los mezcló de tal manera que logró hacer un libro de textos especimen No sólo sobrevivió 500 años, sino que tambien ingresó como texto de relleno en documentos electrónicos, quedando esencialmente igual al original. Fue popularizado en los 60s con la creación de las hojas \"Letraset\", las cuales contenian pasajes de Lorem Ipsum, y más recientemente con software de autoedición, como por ejemplo Aldus PageMaker, el cual incluye versiones de Lorem Ipsum.";
                //String provReason="Invalid push send";
                String key="noti push";
                Log.d(CLASS_TAG,TAG+ " provReason "+provReason.replaceAll("\\s",""));
                createAlertCustomEvent(key,provReason);
                Messaging.checkGPlayServiceStatus();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void createAlertCustomEvent(String key1, String provReason) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Send Event Custom");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout_user, null);
        builder.setView(customLayout);
        EditText editText_key = customLayout.findViewById(R.id.editText_key);
        editText_key.setHint("Key");
        editText_key.setText(key1);

        EditText editText_value = customLayout.findViewById(R.id.editText_value);
        editText_value.setHint("Reason");
        editText_value.setText(provReason);


        // add a button
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
               dialog.dismiss();

            }
        });

        builder.setNeutralButton("Send", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String key = editText_key.getText().toString();
                String value = editText_value.getText().toString();
                if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)){
                    Messaging.sendEventCustom(key,value);
                }else{
                    createAlertCustomEvent(key,value);
                }
                dialog.cancel();

            }
        });
        // create and show the alert dialog
        // AlertDialog dialog = builder.create();
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    private void showAlertGeofenceList(ArrayList<MessagingCircularRegion> geofenceFromdB) {
        // create an alert builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        // builder.setTitle("Notification");
        // set the custom layout
        //final View customLayout = getLayoutInflater().inflate(R.layout.custom_notification_layout, null);
        final View customLayout = getLayoutInflater().inflate(R.layout.notification_layout, null);
        builder.setView(customLayout);
        TextView title=customLayout.findViewById(R.id.textView_title_geofence_list);
        title.setText(getResources().getString(R.string.geofence_info));
        ArrayList<String> messangiGeofenceData = new ArrayList<>();
        ArrayAdapter<String> messangiDataArrayAdapter;
        ListView listView=customLayout.findViewById(R.id.list_data_noti);
        if(geofenceFromdB.size()>0) {
            messangiGeofenceData.add("# Geofence "+geofenceFromdB.size());
            if(Messaging.getLastLocation()!=null) {
                messangiGeofenceData.add("Last Location " + "\n" + "Lat: "
                        + Messaging.getLastLocation().getLatitude() + " Long: "
                        + Messaging.getLastLocation().getLongitude());
                final Location provLocation = Messaging.getLastLocation();
                for (MessagingCircularRegion region : geofenceFromdB) {
                    if(Utils.isValidLatLng(region.getLatitude(),region.getLongitud())) {
                        messangiGeofenceData.add(region.toString()+"Valid Location ✅");
                    }else {
                        messangiGeofenceData.add(region.toString()+"\n"+"Invalid Location ✖");
                    }


                    if(provLocation!=null){
                        Location location1 = new Location(LOCATION_SERVICE);
                        location1.setLatitude(region.getLatitude());
                        location1.setLongitude(region.getLongitud());
                        double dist = provLocation.distanceTo(location1);
                        messangiGeofenceData.add("Distance: "+dist);
                    }
                }
            }else{
                messangiGeofenceData.add("Hasn't Last Location " );
                for (MessagingCircularRegion region : geofenceFromdB) {
                    if(Utils.isValidLatLng(region.getLatitude(),region.getLongitud())) {
                        messangiGeofenceData.add(region.toString()+"Valid Location ✅");
                    }else {
                        messangiGeofenceData.add(region.toString()+"\n"+"Invalid Location ✖");
                    }
                    String dist = "Can't calculate";
                    messangiGeofenceData.add("Distance: " + dist);
                }

            }

        }else{
            Log.d(CLASS_TAG,TAG+ " Do not have geofence ");
            messangiGeofenceData.add(" Do not have Geofences yet! ");
        }


        messangiDataArrayAdapter = new ArrayAdapter<>(this, R.layout.item_device, R.id.Texview_value, messangiGeofenceData);
        listView.setAdapter(messangiDataArrayAdapter);


        // add a button
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                onetimeFlag=true;
                dialog.dismiss();
            }
        });

        builder.show();

    }


    private void showAlertGetPriority() {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
        //AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setTitle("SELECT PRIORITY LOCATION");
        Messaging.turnOFFUpdateLocation();
        getLocationC.setChecked(false);
        String[] items = {"PRIORITY_BALANCED_POWER_ACCURACY","PRIORITY_HIGH_ACCURACY","PRIORITY_LOW_POWER","PRIORITY_NO_POWER"};
        int checkedItem = messaging.messagingStorageController.getLocationProritySelected();
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(MapsActivity.this, "PRIORITY_BALANCED_POWER_ACCURACY", Toast.LENGTH_LONG).show();
                        Messaging.setLocationRequestWithPriority(PRIORITY_BALANCED_POWER_ACCURACY);
                        messaging.messagingStorageController.setLocationProritySelected(which);
                        break;
                    case 1:
                        Toast.makeText(MapsActivity.this, "PRIORITY_HIGH_ACCURACY", Toast.LENGTH_LONG).show();
                        Messaging.setLocationRequestWithPriority(PRIORITY_HIGH_ACCURACY);
                        messaging.messagingStorageController.setLocationProritySelected(which);
                        break;
                    case 2:
                        Toast.makeText(MapsActivity.this, "PRIORITY_LOW_POWER", Toast.LENGTH_LONG).show();
                        Messaging.setLocationRequestWithPriority(PRIORITY_LOW_POWER);
                        messaging.messagingStorageController.setLocationProritySelected(which);
                        break;
                    case 3:
                        Toast.makeText(MapsActivity.this, "PRIORITY_NO_POWER", Toast.LENGTH_LONG).show();
                        Messaging.setLocationRequestWithPriority(PRIORITY_NO_POWER);
                        messaging.messagingStorageController.setLocationProritySelected(which);
                        break;

                }
            }
        });
        alertDialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //AlertDialog alert = alertDialog.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

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
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Messaging.ACTION_FETCH_GEOFENCE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(CLASS_TAG,TAG+ " Resume "+messaging.isGPS());
        Messaging.fetchGeofence();
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
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = mMap.addMarker(markerOptions);
            float zoom = 15f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);

            mMap.animateCamera(cameraUpdate);
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

    private Marker geoFenceMarker;
    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng, int radius, int monitoring, int size, String id) {
        Log.i(CLASS_TAG, "markerForGeofence("+latLng+")"+size);

        // Define marker options
        MarkerOptions markerOptions;
        if(monitoring==1) {
             String title = id+" "+latLng.latitude + ", " + latLng.longitude+"  "+"ON "+" "+radius;
             markerOptions= new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(title);
        }else{
             String title = id+" "+latLng.latitude + ", " + latLng.longitude+"  "+"OFF "+" "+radius;
             markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .title(title);
        }
        if ( mMap!=null ) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null) {
               // geoFenceMarker.remove();
            }
            geoFenceMarker = mMap.addMarker(markerOptions);
            drawGeofence(radius);
        }

    }

    private void drawGeofence(int radius) {
        //Log.d(CLASS_TAG, "drawGeofence()");
        //Log.d(TAG, "DEBUG: " + CLASS_TAG + ": drawGeofence()");

        if ( geoFenceLimits != null ) {
            //geoFenceLimits.remove();
        }

        CircleOptions circleOptions = new CircleOptions()
                .center( geoFenceMarker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( radius );
        geoFenceLimits = mMap.addCircle( circleOptions );
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();

            boolean hasError=intent.getBooleanExtra(Messaging.INTENT_EXTRA_HAS_ERROR,true);
            String alertMessage = getResources().getString(getResources().getIdentifier(intent.getAction(), "string", getPackageName()));
            Toast.makeText(getApplicationContext(), alertMessage, Toast.LENGTH_LONG).show();
            Log.d(TAG,"DEBUG: " + CLASS_TAG + ": " + nameMethod + ":   " + alertMessage);
            //Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": Has error:  "+ hasError);
            if (!hasError ) {
                Serializable data=intent.getSerializableExtra(Messaging.INTENT_EXTRA_DATA);

                if(intent.getAction().equals(Messaging.ACTION_FETCH_LOCATION) ) {

                    if(Messaging.getLastLocation()!=null) {
                        messagingLocation = new MessagingLocation(Messaging.getLastLocation());
                        Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location from storage Lat:  "
                                + messagingLocation.getLatitude() + " Long: " + messagingLocation.getLongitude());

                    }else{
                        messagingLocation = (MessagingLocation) data;

                        Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location Lat:  "
                                + messagingLocation.getLatitude()+" Long: "+messagingLocation.getLongitude());

                    }
                    writeActualLocation(messagingLocation.getLocation());
                    }else if(((intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION))||
                            (intent.getAction().equals(Messaging.ACTION_GET_NOTIFICATION_OPENED)))&& data!=null) {
                        messagingNotification = (MessagingNotification) data;

                        showAlertNotificationTwo(messagingNotification, data);

                    }else if(intent.getAction().equals(Messaging.ACTION_FETCH_GEOFENCE) && data!=null) {
                    Messaging.fetchLocation(MapsActivity.this,false);
                    messagingCircularRegions = (ArrayList<MessagingCircularRegion>) data;
                    if (geoFenceMarker != null) {

                        geoFenceMarker.remove();
                    }
                    if (geoFenceLimits != null ) {

                        geoFenceLimits.remove();
                    }
                    for(MessagingCircularRegion temp:messagingCircularRegions){
//
                        LatLng prov=new LatLng(temp.getLatitude(),temp.getLongitud());
                        markerForGeofence(prov,temp.getRadius(),temp.getMonitoring()
                                ,messagingCircularRegions.size(),temp.getId());
                    }

                    if(showGofenceList) {
                        showAlertGeofenceList(messagingCircularRegions);
                        showGofenceList=false;
                    }


                    }else{
                    Toast.makeText(getApplicationContext(),alertMessage,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),alertMessage,Toast.LENGTH_SHORT).show();
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
                        Messaging.fetchLocation(MapsActivity.this,false);
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
    @SuppressLint("SetTextI18n")
    private void showAlertNotificationTwo(MessagingNotification messagingNotification, Serializable data) {
        // create an alert builder
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
                    //Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": key: "+entry.getKey() + " value: " + entry.getValue());
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
                        onShowDialog=true;
                        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": onshowdialog "+onShowDialog);
                    }

                }
            }
            if(showCustomNotification){
                showCustomNotification(Title,Text,Image);

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
                //launchNotification(clickAction, getApplicationContext(), data);
                onetimeFlag=false;
            }
        }

        //optional code
        if(messagingNotification.getDeepUriLink()!=null && data!=null){
            String deepUriLink = messagingNotification.getDeepUriLink();
            if(onetimeFlag) {
                //launchBrowser(deepUriLink, this, data);
                onetimeFlag = false;
            }
        }

        if(messagingNotification != null){
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
//                        if(entry.getKey().equals("show") && entry.getValue().equals(true)){
//                            onShowDialog=false;
//                            break;
//                        }
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
        Log.d(TAG,"DEBUG: "+CLASS_TAG+": "+nameMethod+": start  ");
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
}