package com.messaging.sdk;


import com.google.android.gms.location.Geofence;

public interface MessagingRegion {

    String getId();
    Messaging.MessagingGeoFenceTrigger getTrigger();
    long getExpiration();
    Geofence getGeofence();

}
