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

    @Override
    public String toString() {
        return "MessagingCircularRegion{" +
                "latitude=" + latitude +
                ", longitud=" + longitud +
                ", radius=" + radius +
                ", id='" + id + '\'' +
                ", messagingGeoFenceTrigger=" + messagingGeoFenceTrigger +
                ", expiration=" + expiration +
                '}';
    }
}
