package com.messaging.sdk;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;

import java.io.Serializable;
/**
 * class MessagingGeofence implements MessagingRegion,
 *  to handle Geofence Params.
 */
public class MessagingGeofence implements MessagingRegion, Serializable {

    protected String id;
    protected Messaging.MessagingGeoFenceTrigger messagingGeoFenceTrigger;
    protected long expiration;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public Messaging.MessagingGeoFenceTrigger getTrigger() {
        return messagingGeoFenceTrigger;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public Geofence getGeofence() {
        return new Geofence.Builder()
                .setRequestId(id)
                .setExpirationDuration( expiration )
                .setTransitionTypes(messagingGeoFenceTrigger.getTrigger())
                .build();

    }


    /**
     * Method to create Builder of this Class
     *
     * */
    public static class Builder{

        private MessagingGeofence product;

        public MessagingGeofence.Builder setId(String id) {
            prepare().id = id;
            return this;
        }


        public MessagingGeofence.Builder setMessagingGeoFenceTrigger(String trigger) {
            if(trigger.equals(Messaging.GOEOFENCE_TYPE_IN)){
                prepare().messagingGeoFenceTrigger = Messaging.MessagingGeoFenceTrigger.ENTER;
            }else if(trigger.equals(Messaging.GOEOFENCE_TYPE_OUT)){
                prepare().messagingGeoFenceTrigger = Messaging.MessagingGeoFenceTrigger.EXIT;
            }else{
                prepare().messagingGeoFenceTrigger = Messaging.MessagingGeoFenceTrigger.BOTH;
            }

            return this;
        }

        public MessagingGeofence.Builder setExpiration(int expiration) {
            prepare().expiration = expiration;
            return this;
        }

        private MessagingGeofence prepare(){
            if(product==null){
                product=new MessagingGeofence();
            }
            return product;

        }

        public MessagingGeofence build(){
            MessagingGeofence provGeofence=prepare();
            this.product=null;
            return provGeofence;
        }

    }

    @Override
    public String toString() {
        return "MessagingGeofence{" +
                "id='" + id + '\'' +
                ", messagingGeoFenceTrigger=" + messagingGeoFenceTrigger +
                ", expiration=" + expiration +
                '}';
    }
}

