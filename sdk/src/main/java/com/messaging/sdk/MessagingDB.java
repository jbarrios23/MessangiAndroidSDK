package com.messaging.sdk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.LOCATION_SERVICE;

public class MessagingDB extends SQLiteOpenHelper {

    public static final int MESSAGING_DATABASE_VERSION = 1;
    public static final String MESSAGING_DATABASE_NAME = "MessagingGeofence.db";

    public static final String MESSAGING_TABLA_GEOFENCE = "MessagingCircularRegion";
    public static final String MESSAGING_COLUMNA_ID = "numrow";

    private static final String SQL_CREAR="CREATE TABLE " + MESSAGING_TABLA_GEOFENCE + "(" +
    MESSAGING_COLUMNA_ID + " INTEGER PRIMARY KEY, " +
    Messaging.GOEOFENCE_ID + " TEXT, " +
    Messaging.GOEOFENCE_LAT + " DOUBLE, " +
    Messaging.GOEOFENCE_LONG + " DOUBLE," +
    Messaging.GOEOFENCE_RADIUS + " INTEGER," +
    Messaging.GOEOFENCE_EXPIRATION + " INTEGER," +
    Messaging.GOEOFENCE_TYPE + " TEXT)";

    public MessagingDB(@Nullable Context context) {
        super(context, MESSAGING_DATABASE_NAME, null, MESSAGING_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGING_TABLA_GEOFENCE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addGeoFenceToBd(MessagingCircularRegion messagingCircularRegion){
        SQLiteDatabase db = this.getWritableDatabase();
        //byte[] data=serializeObject(messagingCircularRegion);
        ContentValues values = new ContentValues();
        values.put(Messaging.GOEOFENCE_ID, messagingCircularRegion.getId());
        values.put(Messaging.GOEOFENCE_LAT, messagingCircularRegion.getLatitude());
        values.put(Messaging.GOEOFENCE_LONG, messagingCircularRegion.getLongitud());
        values.put(Messaging.GOEOFENCE_RADIUS, messagingCircularRegion.getRadius());
        values.put(Messaging.GOEOFENCE_TYPE, messagingCircularRegion.getTrigger().toString());
        values.put(Messaging.GOEOFENCE_EXPIRATION, messagingCircularRegion.getExpiration());
        db.insert(MESSAGING_TABLA_GEOFENCE, null,values);
        db.close();
        Messaging messaging=Messaging.getInstance();
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod,"Added data");

    }

    public MessagingCircularRegion getGeoFenceToBd(String geoFenID){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {MESSAGING_COLUMNA_ID, Messaging.GOEOFENCE_ID,
                Messaging.GOEOFENCE_LAT,Messaging.GOEOFENCE_LONG,
                Messaging.GOEOFENCE_RADIUS,Messaging.GOEOFENCE_TYPE,
                Messaging.GOEOFENCE_EXPIRATION};

        Cursor cursor =
                db.query(MESSAGING_TABLA_GEOFENCE,
                        projection,
                        Messaging.GOEOFENCE_ID+" = ?",
                        new String[] { String.valueOf(geoFenID) },
                        null,
                        null,
                        null,
                        null);


        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();

            MessagingCircularRegion.Builder builder= new MessagingCircularRegion.Builder();
            MessagingCircularRegion messagingCircularRegion=builder.setId(cursor.getString(1))
                    .setLatitude(cursor.getDouble(2))
                    .setLongitud(cursor.getDouble(3))
                    .setRadius(cursor.getInt(4))
                    .setMessagingGeoFenceTrigger(cursor.getString(5))
                    .setExpiration(cursor.getInt(6))
                    .build();
            Messaging messaging=Messaging.getInstance();
            String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
//            messaging.utils.showDebugLog(this,nameMethod,"Get data "
//                    +messagingCircularRegion.toString());

            return messagingCircularRegion;
        }



        db.close();

        return null;
    }

    public ArrayList< MessagingCircularRegion> getAllGeoFenceToBd() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MessagingCircularRegion> result = new ArrayList<>();
        String[] projection = {MESSAGING_COLUMNA_ID, Messaging.GOEOFENCE_ID,
                Messaging.GOEOFENCE_LAT, Messaging.GOEOFENCE_LONG,
                Messaging.GOEOFENCE_RADIUS, Messaging.GOEOFENCE_TYPE,
                Messaging.GOEOFENCE_EXPIRATION};

        Cursor cursor =
                db.query(MESSAGING_TABLA_GEOFENCE,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null);


        while (cursor.moveToNext()) {
            MessagingCircularRegion.Builder builder = new MessagingCircularRegion.Builder();
            MessagingCircularRegion messagingCircularRegion = builder.setId(cursor.getString(1))
                    .setLatitude(cursor.getDouble(2))
                    .setLongitud(cursor.getDouble(3))
                    .setRadius(cursor.getInt(4))
                    .setMessagingGeoFenceTrigger(cursor.getString(5))
                    .setExpiration(cursor.getInt(6))
                    .build();
            result.add(messagingCircularRegion);
        }
        Messaging messaging=Messaging.getInstance();
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod,"Get all data "+result.size());
        db.close();

        if(messaging.messagingStorageController.hasLastLocation()) {
            final Location provLocation = messaging.messagingStorageController.getLastLocationSaved();
            Collections.sort(result, new Comparator<MessagingCircularRegion>() {
                @Override
                public int compare(MessagingCircularRegion o1, MessagingCircularRegion o2) {
                    Location location1 = new Location(LOCATION_SERVICE);
                    location1.setLatitude(o1.getLatitude());
                    location1.setLongitude(o1.getLongitud());
                    double dist1 = provLocation.distanceTo(location1);
                    Location location2 = new Location(LOCATION_SERVICE);
                    location2.setLatitude(o2.getLatitude());
                    location2.setLongitude(o2.getLongitud());
                    double dist2 = provLocation.distanceTo(location2);
                    if (dist1 < dist2) {
                        return -1;
                    } else if (dist1 > dist2) {
                        return 1;
                    } else {
                        return 0;
                    }

                }
            });
        }
    return  result;
    }

    public void update(MessagingCircularRegion messagingCircularRegion,String geoFenID){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Messaging.GOEOFENCE_LAT, messagingCircularRegion.getLatitude());
        values.put(Messaging.GOEOFENCE_LONG, messagingCircularRegion.getLongitud());
        values.put(Messaging.GOEOFENCE_RADIUS, messagingCircularRegion.getRadius());
        values.put(Messaging.GOEOFENCE_TYPE, messagingCircularRegion.getTrigger().toString());
        values.put(Messaging.GOEOFENCE_EXPIRATION, messagingCircularRegion.getExpiration());

        int i = db.update(MESSAGING_TABLA_GEOFENCE,
                values,
                Messaging.GOEOFENCE_ID+" = ?",
                new String[] { String.valueOf( geoFenID ) });

        db.close();
        Messaging messaging=Messaging.getInstance();
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod,"Update data");

    }

    public void delete(String geoFenID){
        Messaging messaging=Messaging.getInstance();
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.delete(MESSAGING_TABLA_GEOFENCE,
                    Messaging.GOEOFENCE_ID+" = ?",
                    new String[] { String.valueOf (geoFenID ) });
            db.close();

            messaging.utils.showDebugLog(this,nameMethod,"Delete data ID "+geoFenID);
        }catch(Exception ex){
            messaging.utils.showErrorLog(this,nameMethod,"error delete data","");
        }
    }
    public void deleteAll(){
        Messaging messaging=Messaging.getInstance();
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            db.delete(MESSAGING_TABLA_GEOFENCE,null,null);
            db.close();

            messaging.utils.showDebugLog(this,nameMethod,"Delete all data");
        }catch(Exception ex){
            messaging.utils.showErrorLog(this,nameMethod,"error delete data","");
        }
    }


}
