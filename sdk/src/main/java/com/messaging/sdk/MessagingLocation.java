package com.messaging.sdk;

import android.location.Location;

import java.io.Serializable;

/**
 * class MessagingLocation
 * handle location from any class.
 */
public class MessagingLocation implements Serializable {


    private Location location;

    public MessagingLocation(Location location) {
        this.location=location;
    }

    public MessagingLocation() {

    }
    /**
     * Method to getLatitude
     */
    public double getLatitude() {
        return location.getLatitude();
    }
    /**
     * Method to getLongitude
     */
    public double getLongitude() {
        return location.getLongitude();
    }
    /**
     * Method to getLocation
     */
    public Location getLocation() {
        return location;
    }
}
