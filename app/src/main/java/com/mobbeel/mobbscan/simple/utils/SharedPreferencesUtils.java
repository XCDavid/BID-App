package com.mobbeel.mobbscan.simple.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {
    public static final String PREF_FILE_NAME = "pref_teknei";
    public static final String TOKEN_APP = "token_app";
    public static final String OPERATION_ID = "operation_id";
    public static final String ID_SCAN = "id_scan_val";
    public static final String URL_ID_SCAN = "url_id_scan_settings";
    public static final String LICENSE_ID_SCAN = "license_id_scan_settings";
    public static final String URL_TEKNEI = "url_teknei_settings";

    public static void saveToPreferencesString(Context contex, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = contex.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferencesString(Context contex, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = contex.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, preferenceValue);
    }
    public static void saveToPreferencesBoolean(Context contex, String preferenceName, boolean preferenceValue) {
        SharedPreferences sharedPreferences = contex.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceName, preferenceValue);
        editor.apply();
    }

    public static boolean readFromPreferencesBoolean(Context contex, String preferenceName, boolean preferenceValue) {
        SharedPreferences sharedPreferences = contex.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(preferenceName, preferenceValue);
    }

    public static void deleteFromPreferences(Context contex, String preferenceName) {
        SharedPreferences sharedPreferences = contex.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(preferenceName);
        editor.apply();
    }
}
