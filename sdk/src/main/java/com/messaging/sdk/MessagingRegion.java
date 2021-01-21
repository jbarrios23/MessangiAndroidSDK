package com.messaging.sdk;


import com.google.android.gms.location.Geofence;
/**
 * interface MessagingRegion
 *  to handle Geofence Params.
 */
public interface MessagingRegion {

    String getId();
    Messaging.MessagingGeoFenceTrigger getTrigger();
    long getExpiration();
    Geofence getGeofence();

}
