package com.messaging.sdk;


import com.google.android.gms.location.Geofence;

public interface MessagingRegion {

    String getId();
    Messaging.MessagingGeoFenceTrigger getTrigger();
    int getExpiration();
    Geofence getGeofence();

}
