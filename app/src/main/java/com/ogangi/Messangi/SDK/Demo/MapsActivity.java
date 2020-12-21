package com.ogangi.Messangi.SDK.Demo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
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

import java.io.Serializable;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                        Log.d(CLASS_TAG, TAG + "Continue Location "+isChecked);
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
                Log.d(CLASS_TAG,TAG+ " isGPS To Interface one "+isGPSEnable);
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
                Toast.makeText(getApplicationContext(), "Geting geofence List....", Toast.LENGTH_SHORT).show();
                Messaging.fetchGeofence(false,"");
                showGofenceList=true;
                return true;
            case R.id.action_get_geofences_service:
                Toast.makeText(getApplicationContext(), "Geting geofence List....", Toast.LENGTH_SHORT).show();
                Messaging.fetchGeofence(true,"");
                showGofenceList=true;
                return true;
            case R.id.action_sinc:
                //gotoMapActivity();
                Messaging.fetchGeofence(true,"");
                showGofenceList=false;
                return true;
            case R.id.action_permission:
                Log.i(TAG, "INFO: " + CLASS_TAG + ": " + nameMethod + " has verify permission manual : "
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
                Log.d(CLASS_TAG,TAG+ " provReason "+provReason.replaceAll("\\s",""));
                Messaging.sendEventCustom("noti push",provReason);
                Messaging.checkGPlayServiceStatus();
                return true;

//            case R.id.action_delete:
//                //Messaging.deleteAlldB();
//                return true;
        }

        return super.onOptionsItemSelected(item);
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
            for (MessagingCircularRegion region : geofenceFromdB) {
               messangiGeofenceData.add(region.toString());
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

    private Marker geoFenceMarker;
    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng,int radius) {
        Log.i(CLASS_TAG, "markerForGeofence("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        if ( mMap!=null ) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null) {
                //geoFenceMarker.remove();
            }
            geoFenceMarker = mMap.addMarker(markerOptions);
            drawGeofence(radius);


        }

    }

    private void drawGeofence(int radius) {
        //Log.d(CLASS_TAG, "drawGeofence()");
        Log.d(TAG, "DEBUG: " + CLASS_TAG + ": drawGeofence()");

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
            Toast.makeText(getApplicationContext(), alertMessage, Toast.LENGTH_SHORT).show();
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
                        showAlertNotification(messagingNotification, data);

                    }else if(intent.getAction().equals(Messaging.ACTION_FETCH_GEOFENCE) && data!=null) {

                    messagingCircularRegions = (ArrayList<MessagingCircularRegion>) data;

                    for(MessagingCircularRegion temp:messagingCircularRegions){
                    Log.d(TAG, "DEBUG: " + CLASS_TAG + ": " + nameMethod + ": Data Location Lat:  "
                                + temp.getLatitude()+" Long: "+temp.getLongitud()+" radius "+temp.getRadius()
                                +" trigger "+temp.getTrigger());
                        LatLng prov=new LatLng(temp.getLatitude(),temp.getLongitud());
                        markerForGeofence(prov,temp.getRadius());
                    }

                    if(showGofenceList) {
                        showAlertGeofenceList(messagingCircularRegions);
                        showGofenceList=false;
                    }
                    Messaging.fetchLocation(MapsActivity.this,false);

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