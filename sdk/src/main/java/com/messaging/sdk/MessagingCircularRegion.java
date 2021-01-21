package com.messaging.sdk;

import com.google.android.gms.location.Geofence;

import java.io.Serializable;
/**
 * class MessagingCircularRegion let stable Instances, getGeofence and create Builder for handle
 * GeoFence stuff.
 */
public class MessagingCircularRegion extends MessagingGeofence implements Serializable {

    private double latitude;
    private double longitud;
    private int radius;
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private MessagingSdkUtils messagingSdkUtils;
    private int monitoring;
    private int isafterToDelete;


    public MessagingCircularRegion() {

        this.monitoring = 0;
        this.isafterToDelete=0;
    }
    /**
     * Method to getGeofence previus validate long and lat
     *
     * */
    @Override
    public Geofence getGeofence() {
        messagingSdkUtils=new MessagingSdkUtils();
        Messaging messaging=Messaging.getInstance();
        if(messagingSdkUtils.isValidLatLng(latitude,longitud)){
            return new Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion(latitude, longitud, radius)
                    .setExpirationDuration(expiration)
                    .setTransitionTypes(messagingGeoFenceTrigger.getTrigger())
                    .build();
        }else{
            toString();
            return new Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion(0.00, 0.00, radius)
                    .setExpirationDuration(expiration)
                    .setTransitionTypes(messagingGeoFenceTrigger.getTrigger())
                    .build();
        }
    }

    /**
     * Method to getLatitude of Geofence
     *
     * */
    public double getLatitude() {
        return latitude;
    }
    /**
     * Method to getLongitud of Geofence
     *
     * */
    public double getLongitud() {
        return longitud;
    }
    /**
     * Method to getRadius of Geofence
     *
     * */
    public int getRadius() {
        return radius;
    }
    /**
     * Method to getMonitoring of Geofence
     *
     * */
    public int getMonitoring() {
        return monitoring;
    }
    /**
     * Method to isAfterTodelete
     *
     * */
    public boolean isAfterTodelete() {

        return isafterToDelete==1;
    }

    /**
     * Method to create Builder of this Class
     *
     * */
    public static class Builder{

        private MessagingCircularRegion product;

        public MessagingCircularRegion.Builder setId(String id) {
            prepare().id = id;
            return this;
        }

        public MessagingCircularRegion.Builder setMonitoring(int monitoring) {
            prepare().monitoring = monitoring;
            return this;
        }

        public MessagingCircularRegion.Builder setIsAfterDelete(int isAfterDelete) {
            prepare().isafterToDelete = isAfterDelete;
            return this;
        }

        public MessagingCircularRegion.Builder setMessagingGeoFenceTrigger(String trigger) {
            if(trigger.equals(Messaging.GOEOFENCE_TYPE_IN)){
                prepare().messagingGeoFenceTrigger = Messaging.MessagingGeoFenceTrigger.ENTER;
            }else if(trigger.equals(Messaging.GOEOFENCE_TYPE_OUT)){
                prepare().messagingGeoFenceTrigger = Messaging.MessagingGeoFenceTrigger.EXIT;
            }else{
                prepare().messagingGeoFenceTrigger = Messaging.MessagingGeoFenceTrigger.BOTH;
            }

            return this;
        }

        public MessagingCircularRegion.Builder setExpiration(long expiration) {
            prepare().expiration = expiration;
            return this;
        }

        public MessagingCircularRegion.Builder setLatitude(double latitude) {
            prepare().latitude = latitude;
            return this;
        }

        public MessagingCircularRegion.Builder setLongitud(double longitud) {
            prepare().longitud = longitud;
            return this;
        }

        public MessagingCircularRegion.Builder setRadius(int radius) {
            prepare().radius = radius;
            return this;
        }


        private MessagingCircularRegion prepare(){
            if(product==null){
                product=new MessagingCircularRegion();
            }
            return product;

        }

        public MessagingCircularRegion build(){
            MessagingCircularRegion prov=prepare();
            this.product=null;
            return prov;
        }

        public Builder initWith(MessagingCircularRegion messagingCircularRegion) {
            this.product=messagingCircularRegion;
            return this;
        }
    }

    @Override
    public String toString() {
        return "MessagingCircularRegion" +"\n"+
                "latitude=" + latitude +"\n"+
                "longitud=" + longitud +"\n"+
                "radius=" + radius +"\n"+
                "id='" + id + '\'' +"\n"+
                "messagingGeoFenceTrigger=" + messagingGeoFenceTrigger +"\n"+
                "expiration=" + expiration+"\n"+
                "monitoring=" + monitoring+"\n";
    }
}
