package com.messaging.sdk;

import com.google.android.gms.location.Geofence;

public class MessagingCircularRegion extends MessagingGeofence {

    private double latitude;
    private double longitud;
    private int radius;


    @Override
    public Geofence getGeofence() {
        return new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion( latitude, longitud, radius)
                .setExpirationDuration( expiration )
                .setTransitionTypes(messagingGeoFenceTrigger.getTrigger())
                .build();
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitud() {
        return longitud;
    }

    public int getRadius() {
        return radius;
    }

    public static class Builder{

        private MessagingCircularRegion product;

        public MessagingCircularRegion.Builder setId(String id) {
            prepare().id = id;
            return this;
        }

        public MessagingCircularRegion.Builder setMessagingGeoFenceTrigger(Messaging.MessagingGeoFenceTrigger messagingGeoFenceTrigger) {
            prepare().messagingGeoFenceTrigger = messagingGeoFenceTrigger;
            return this;
        }

        public MessagingCircularRegion.Builder setExpiration(int expiration) {
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

    }
}
