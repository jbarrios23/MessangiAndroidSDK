package com.messaging.sdk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MessagingDB extends SQLiteOpenHelper {

    public static final int MESSAGING_DATABASE_VERSION = 1;
    public static final String MESSAGING_DATABASE_NAME = "MessagingGeofence.db";

    public static final String MESSAGING_TABLA_GEOFENCE = "MessagingCircularRegion";
    public static final String MESSAGING_COLUMNA_ID = "_id";

    private static final String SQL_CREAR="CREATE TABLE " + MESSAGING_TABLA_GEOFENCE + "(" +
    MESSAGING_COLUMNA_ID + " INTEGER PRIMARY KEY, " +
    Messaging.GOEOFENCE_ID + " TEXT, " +
    Messaging.GOEOFENCE_LAT + " DOUBLE, " +
    Messaging.GOEOFENCE_LONG + " DOUBLE," +
    Messaging.GOEOFENCE_RADIUS + " INTEGER," +
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
                Messaging.GOEOFENCE_RADIUS,Messaging.GOEOFENCE_TYPE};

        Cursor cursor =
                db.query(MESSAGING_TABLA_GEOFENCE,
                        projection,
                        Messaging.GOEOFENCE_ID+" = ?",
                        new String[] { String.valueOf(geoFenID) },
                        null,
                        null,
                        null,
                        null);


        if (cursor != null) {
            cursor.moveToFirst();
        }

        MessagingCircularRegion.Builder builder= new MessagingCircularRegion.Builder();
        MessagingCircularRegion messagingCircularRegion=builder.setId(cursor.getString(1))
               .setLatitude(cursor.getDouble(2))
               .setLongitud(cursor.getDouble(3))
               .setRadius(cursor.getInt(4))
               .setMessagingGeoFenceTrigger(cursor.getString(5))
               .build();


        db.close();

        return messagingCircularRegion;
    }

    public ArrayList< MessagingCircularRegion> getAllGeoFenceToBd() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MessagingCircularRegion> result = new ArrayList<>();
        String[] projection = {MESSAGING_COLUMNA_ID, Messaging.GOEOFENCE_ID,
                Messaging.GOEOFENCE_LAT, Messaging.GOEOFENCE_LONG,
                Messaging.GOEOFENCE_RADIUS, Messaging.GOEOFENCE_TYPE};

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
                    .build();
            result.add(messagingCircularRegion);
        }
        Messaging messaging=Messaging.getInstance();
        String nameMethod=new Object(){}.getClass().getEnclosingMethod().getName();
        messaging.utils.showDebugLog(this,nameMethod,"get all data "+result.toString());

        db.close();
    return  result;
    }

    public void update(MessagingCircularRegion messagingCircularRegion,String geoFenID){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Messaging.GOEOFENCE_LAT, messagingCircularRegion.getLatitude());
        values.put(Messaging.GOEOFENCE_LONG, messagingCircularRegion.getLongitud());
        values.put(Messaging.GOEOFENCE_RADIUS, messagingCircularRegion.getRadius());
        values.put(Messaging.GOEOFENCE_TYPE, messagingCircularRegion.getTrigger().toString());

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

            messaging.utils.showDebugLog(this,nameMethod,"Delete data");
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
