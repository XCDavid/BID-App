package com.teknei.bid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {
    public static final String PREF_FILE_NAME = "pref_teknei";
    public static final String TOKEN_APP = "token_app";
    public static final String USERNAME = "username";

    public static final String OPERATION_ID = "operation_id";
    public static final String SCAN_SAVE_ID = "id_scan_save_val";
    public static final String ID_SCAN = "id_scan_val";
    public static final String FACE_OPERATION = "face_operation";
    public static final String DOCUMENT_OPERATION = "document_operation";
    public static final String FINGERS_OPERATION = "fingers_operation";
    public static final String PAY_OPERATION = "pay_operation";

    public static final String URL_ID_SCAN = "url_id_scan_settings";
    public static final String LICENSE_ID_SCAN = "license_id_scan_settings";
    public static final String URL_TEKNEI = "url_teknei_settings";
    //MOBBSIGN
    public static final String URL_MOBBSIGN = "url_mobbsign";
    public static final String MOBBSIGN_LICENSE = "mobbsign_license";
    //JSON de Formulario inicial
    public static final String JSON_INIT_FORM = "json_init_form";
    //JSON de respuesta MobbScan frontal y trasera
    public static final String JSON_CREDENTIALS_RESPONSE = "json_mabbscan_front";
    //TimeStamp para tiempos de guardado
    public static final String TIMESTAMP_CREDENTIALS = "timestamp_credential";
    public static final String TIMESTAMP_FACE = "timestamp_face";
    public static final String TIMESTAMP_DOCUMENT = "timestamp_document";
    public static final String TIMESTAMP_FINGERPRINTS = "timestamp_fingerprints";


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
    public static void cleanSharedPreferencesOperation(Activity context) {
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.OPERATION_ID);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.ID_SCAN);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.SCAN_SAVE_ID);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.FACE_OPERATION);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.DOCUMENT_OPERATION);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.FINGERS_OPERATION);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.PAY_OPERATION);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.JSON_INIT_FORM);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.TIMESTAMP_CREDENTIALS);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.TIMESTAMP_FACE);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.TIMESTAMP_DOCUMENT);
        SharedPreferencesUtils.deleteFromPreferences(context,SharedPreferencesUtils.TIMESTAMP_FINGERPRINTS);
    }
}
