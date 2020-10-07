package com.messaging.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class MessaginGeofenceBroadcastReceiver extends BroadcastReceiver {
    public Messaging messaging;
    public String nameMethod;
    public MessagingCircularRegion messagingCircularRegion;

    public MessagingDB db;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // an Intent broadcast.
        db=new MessagingDB(context);

        nameMethod = new Object() {}.getClass().getEnclosingMethod().getName();
        messaging=Messaging.getInstance();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);


        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            messaging.utils.showErrorLog(this,nameMethod,errorMessage,"");
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        messaging.utils.showDebugLog(this,nameMethod,geofenceTransition);

        // Test that the reported transition was of interest.

        if (geofenceTransition == Messaging.MessagingGeoFenceTrigger.ENTER.getTrigger()){

            messaging.sendGlobalEventToActivity(Messaging.ACTION_GEOFENCE_ENTER,
                    convertGeofenceToCircularregion(geofencingEvent));


        }else if (geofenceTransition == Messaging.MessagingGeoFenceTrigger.EXIT.getTrigger()){

            messaging.sendGlobalEventToActivity(Messaging.ACTION_GEOFENCE_EXIT,
                    convertGeofenceToCircularregion(geofencingEvent));
        }

//        if (geofenceTransition == Messaging.MessagingGeoFenceTrigger.ENTER.getTrigger() ||
//                geofenceTransition == Messaging.MessagingGeoFenceTrigger.EXIT.getTrigger()) {
//
//            // Get the geofences that were triggered. A single event can trigger
//            // multiple geofences.
//            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
//
//            convertGeofenceToCircularregion(geofencingEvent);
//            messaging.sendGlobalLocationToActivity(Messaging.ACTION_FETCH_GEOFENCE);
//
//            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    this,
//                    geofenceTransition,
//                    triggeringGeofences
//            );
//
//            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails,context);
//
//        } else {
//            messaging.utils.showErrorLog(this,nameMethod,"Error ","");
//
//        }
    }

    private ArrayList<MessagingCircularRegion> convertGeofenceToCircularregion(GeofencingEvent geofencingEvent) {
        ArrayList<MessagingCircularRegion> regions = new ArrayList<>();
        for(Geofence temp:geofencingEvent.getTriggeringGeofences()){
            MessagingCircularRegion.Builder builder= new MessagingCircularRegion.Builder();
            MessagingCircularRegion messagingCircularRegion=db.getGeoFenceToBd(temp.getRequestId());
            regions.add(messagingCircularRegion);
            Messaging.sendEventGeofenceToBackend(messagingCircularRegion.getTrigger().toString(),temp.getRequestId());
        }
        return regions;
    }

    private void sendNotification(String geofenceTransitionDetails, Context context) {
        nameMethod = new Object() {}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod,geofenceTransitionDetails);
        Toast.makeText(context,geofenceTransitionDetails,Toast.LENGTH_LONG).show();
        messaging.sendGlobalEventToActivity(Messaging.ACTION_GET_GEOFENCE_TRANSITION_DETAILS,
                geofenceTransitionDetails);
        //aca se debe notificar a las analiticas de un geofence.

    }

    private String getGeofenceTransitionDetails(MessaginGeofenceBroadcastReceiver geofenceBroadcastReceiver,
                                                int geofenceTransition,
                                                List<Geofence> triggeringGeofences) {

        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if (geofenceTransition == Messaging.MessagingGeoFenceTrigger.ENTER.getTrigger() )
            status = "in";
        else if ( geofenceTransition == Messaging.MessagingGeoFenceTrigger.EXIT.getTrigger() )
            status = "out ";

        //Messaging.sendEventGeofenceToBackend(status,ge);

        return status + TextUtils.join( ", ", triggeringGeofencesList);

    }

    // Handle errors
    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
