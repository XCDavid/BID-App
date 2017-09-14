package com.teknei.bid.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class PhoneSimUtils {

    public static String getImei(Context context) {
        TelephonyManager mTelephonyMgr = getTelephonyManager(context);
//        String imei = mTelephonyMgr.getDeviceId();
        String deviceId = "";

        if (mTelephonyMgr.getDeviceId() != null){
            deviceId = mTelephonyMgr.getDeviceId();
            Log.w("device ID","imei:"+deviceId);
        }else{
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.w("device ID","android id:"+deviceId);
        }
        return deviceId;
    }

//    public static String getImsi(Context context) {
//        TelephonyManager mTelephonyMgr = getTelephonyManager(context);
//        String imsi = mTelephonyMgr.getSubscriberId();
//        return imsi;
//    }
//
//    public static String getSimSerialNumber(Context context) {
//        TelephonyManager mTelephonyMgr = getTelephonyManager(context);
//        String simno = mTelephonyMgr.getSimSerialNumber();
//        return simno;
//    }
//
//    public static String getCountryIso(Context context) {
//        TelephonyManager mTelephonyMgr = getTelephonyManager(context);
//        String countryIso = mTelephonyMgr.getSimCountryIso ();
//        return countryIso;
//    }

    public static TelephonyManager getTelephonyManager(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr;
    }

    public static String getLocalDateAndTime(){
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        /* debug: is it local time? */
        Log.d("Time zone: ", tz.getDisplayName());
        /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(tz);
        /* print your timestamp and double check it's the date you expect */
        long timestamp = System.currentTimeMillis();
        String localTime = sdf.format(new Date(timestamp)); // I assume your timestamp is in seconds and you're converting to milliseconds?
        Log.d("Time: ", localTime);
        return localTime;
    }
}
