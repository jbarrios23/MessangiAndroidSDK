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
/**
 * class MessaginGeofenceBroadcastReceiver let stable BroadcastReceiver,
 * for Handle Geofence event
 */
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
        nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging=Messaging.getInstance();
        if(intent!=null && intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            messaging.utils.showDebugLog(this,nameMethod,": Boot complete:  ");
            Messaging.re_registerGeofence();
            return;
        }

        db=new MessagingDB(context);


        messaging.utils.showDebugLog(this,nameMethod,"MessaginGeofenceBroadcastReceiver "+intent.getExtras().toString());
        nameMethod = new Object() {}.getClass().getEnclosingMethod().getName();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);


        if (geofencingEvent.hasError()) {
            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            messaging.utils.showErrorLog(this,nameMethod,errorMessage,"");
            if(errorMessage.equals("GeoFence not available")){
                Messaging.re_registerGeofence();
                messaging.utils.showErrorLog(this,nameMethod,errorMessage,"");
            }
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        messaging.utils.showDebugLog(this,nameMethod,geofenceTransition
                +" "+triggeringGeofences.toString()+" "+intent.getAction());


        // Test that the reported transition was of interest.

        if (geofenceTransition == Messaging.MessagingGeoFenceTrigger.ENTER.getTrigger()){

            messaging.sendGlobalEventToActivity(Messaging.ACTION_GEOFENCE_ENTER,
                    convertGeofenceToCircularregion(geofencingEvent));


        }else if (geofenceTransition == Messaging.MessagingGeoFenceTrigger.EXIT.getTrigger()){

            messaging.sendGlobalEventToActivity(Messaging.ACTION_GEOFENCE_EXIT,
                    convertGeofenceToCircularregion(geofencingEvent));
        }

        if (geofenceTransition == Messaging.MessagingGeoFenceTrigger.ENTER.getTrigger() ||
                geofenceTransition == Messaging.MessagingGeoFenceTrigger.EXIT.getTrigger()) {

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails,context);

        } else {
            messaging.utils.showErrorLog(this,nameMethod,"Error ","");

        }
    }
    /**
     * Method to convert Geofence To Circularregion
     * @param geofencingEvent :event to convert.
     */
    private ArrayList<MessagingCircularRegion> convertGeofenceToCircularregion(GeofencingEvent geofencingEvent) {
        nameMethod="convertGeofenceToCircularregion";
        ArrayList<MessagingCircularRegion> regions = new ArrayList<>();
        MessagingCircularRegion.Builder builder= new MessagingCircularRegion.Builder();
            for(Geofence temp:geofencingEvent.getTriggeringGeofences()){

                messaging.utils.showDebugLog(this,nameMethod,"IDs "+temp.getRequestId());
                if(db.getGeoFenceToBd(temp.getRequestId())!=null) {
                    MessagingCircularRegion messagingCircularRegion = db.getGeoFenceToBd(temp.getRequestId());
                    regions.add(messagingCircularRegion);
                    String prvStatus="";
                    if (geofencingEvent.getGeofenceTransition() == Messaging.MessagingGeoFenceTrigger.ENTER.getTrigger() )
                        prvStatus = "in";
                    else if ( geofencingEvent.getGeofenceTransition() == Messaging.MessagingGeoFenceTrigger.EXIT.getTrigger() )
                        prvStatus = "out";
                    Messaging.sendEventGeofenceToBackend(prvStatus, temp.getRequestId());
                }else{
                    messaging.utils.showDebugLog(this,nameMethod,"dont send ");
                    break;
                }
            }
            //messaging.utils.showDebugLog(this,nameMethod,"Regions "+regions.toString());
            return regions;


    }
    /**
     * Method to sendNotification
     * @param geofenceTransitionDetails :geofence Transition Details.
     * @param context :context .
     */
    private void sendNotification(String geofenceTransitionDetails, Context context) {
        nameMethod = new Object() {}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod,geofenceTransitionDetails);
        Toast.makeText(context,geofenceTransitionDetails,Toast.LENGTH_LONG).show();

    }
    /**
     * Method to getGeofence Transition Details
     * @param geofenceBroadcastReceiver :MessaginGeofenceBroadcastReceiver Transition Details.
     * @param geofenceTransition:Trigger of GF.
     * @param triggeringGeofences: list of Gf triggering.
     */
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

        return status +" "+ TextUtils.join( ", ", triggeringGeofencesList);

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
