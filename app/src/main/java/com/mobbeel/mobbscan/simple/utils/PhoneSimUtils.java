package com.mobbeel.mobbscan.simple.utils;

import android.content.Context;
import android.telephony.TelephonyManager;


public class PhoneSimUtils {

    public static String getImei(Context context) {
        TelephonyManager mTelephonyMgr = getTelephonyManager(context);
        String imei = mTelephonyMgr.getDeviceId();
        return imei;
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
}
