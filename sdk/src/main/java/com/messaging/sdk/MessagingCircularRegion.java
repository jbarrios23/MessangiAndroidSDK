package com.messaging.sdk;

import com.google.android.gms.location.Geofence;

import java.io.Serializable;

public class MessagingCircularRegion extends MessagingGeofence implements Serializable {

    private double latitude;
    private double longitud;
    private int radius;
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private MessagingSdkUtils messagingSdkUtils;


    @Override
    public Geofence getGeofence() {
        messagingSdkUtils=new MessagingSdkUtils();
        Messaging messaging=Messaging.getInstance();
//        messaging.utils.showDebugLog(this,"GetGeofence Valid Location",
//                messagingSdkUtils.isValidLatLng(latitude,longitud));
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

    }

    @Override
    public String toString() {
        return "MessagingCircularRegion" +"\n"+
                "latitude=" + latitude +"\n"+
                "longitud=" + longitud +"\n"+
                "radius=" + radius +"\n"+
                "id='" + id + '\'' +"\n"+
                "messagingGeoFenceTrigger=" + messagingGeoFenceTrigger +"\n"+
                "expiration=" + expiration+"\n";
    }
}
