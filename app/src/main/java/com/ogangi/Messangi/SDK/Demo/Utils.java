package com.ogangi.Messangi.SDK.Demo;

import android.annotation.SuppressLint;
import android.location.Location;

import com.messaging.sdk.MessagingCircularRegion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

import static android.content.Context.LOCATION_SERVICE;

public class Utils {


    /**
     * Validation of Phone Number
     */
    public final static boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || target.length() < 6 || target.length() > 13) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }

    }

    /**
     * Validation of Email
     */
    public static boolean isValidMail(String email) {

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return Pattern.compile(EMAIL_STRING).matcher(email).matches();

    }

    /**
     * Validation of NullOrEmpty
     */
    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    /**
     * Validation of Numeric
     */

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static Boolean checkDateFormat(String date){
        if (date == null || !date.matches("^(1[0-9]|0[1-9]|3[0-1]|2[1-9])/(0[1-9]|1[0-2])/[0-9]{4}$"))
            return false;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
        try {
            format.parse(date);
            return true;
        }catch (ParseException e){
            return false;
        }
    }

    public static ArrayList<MessagingCircularRegion> getOrderMessagingCircularRegion(ArrayList< MessagingCircularRegion> provMessagingCircularRegions
            ,Location provLocation){

        Collections.sort(provMessagingCircularRegions,new Comparator<MessagingCircularRegion>() {
            @Override
            public int compare(MessagingCircularRegion o1, MessagingCircularRegion o2) {
                Location location1=new Location(LOCATION_SERVICE);
                location1.setLatitude(o1.getLatitude());
                location1.setLongitude(o1.getLongitud());
                double dist1=provLocation.distanceTo(location1);
                Location location2=new Location(LOCATION_SERVICE);
                location2.setLatitude(o2.getLatitude());
                location2.setLongitude(o2.getLongitud());
                double dist2=provLocation.distanceTo(location2);
                if(dist1<dist2){
                    return -1;
                }else if(dist1>dist2){
                    return 1;
                }else{
                    return 0;
                }

            }
        });
        return provMessagingCircularRegions;
    }


}
