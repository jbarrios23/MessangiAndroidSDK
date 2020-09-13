package com.messaging.sdk;

import android.location.Location;

import java.io.Serializable;

public class MessagingLocation implements Serializable {


    private Location location;

    public MessagingLocation(Location location) {
        this.location=location;
    }

    public MessagingLocation() {

    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public Location getLocation() {
        return location;
    }
}
