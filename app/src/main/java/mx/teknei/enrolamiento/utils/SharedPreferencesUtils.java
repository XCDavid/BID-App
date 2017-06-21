package mx.teknei.enrolamiento.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {
    public static final String PREF_FILE_NAME = "pref_teknei";
    public static final String ID_SCAN = "id_scan_val";

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
}
