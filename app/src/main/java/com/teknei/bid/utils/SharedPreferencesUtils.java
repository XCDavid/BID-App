package com.teknei.bid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.teknei.bid.activities.IcarScanActivity;

import java.io.File;


public class SharedPreferencesUtils {
    public static final String ID_DEVICE      = "id_device";
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
    //MOBBSIGN
    public static final String URL_AUTHACCESS = "url_authaccess";

    //JSON de Formulario inicial
    public static final String JSON_INIT_FORM = "json_init_form";
    //JSON de respuesta MobbScan frontal y trasera
    public static final String JSON_CREDENTIALS_RESPONSE = "json_mabbscan_front";
    //TimeStamp para tiempos de guardado
    public static final String TIMESTAMP_CREDENTIALS = "timestamp_credential";
    public static final String TIMESTAMP_FACE = "timestamp_face";
    public static final String TIMESTAMP_DOCUMENT = "timestamp_document";
    public static final String TIMESTAMP_FINGERPRINTS = "timestamp_fingerprints";

    //Empresa
    public static final String ID_ENTERPRICE = "id_enterprice";
    public static final String CUSTOMER_TYPE = "customer_type";

    //Lector de huellas digitales
    public static final String FINGERPRINT_READER = "fingerprint_reader";


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
        //DeleteFiles
        //ICAR FILES
        String operationID = SharedPreferencesUtils.readFromPreferencesString(context, SharedPreferencesUtils.OPERATION_ID, "");
        File fIdFront = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_front" + operationID + ".jpg");
        if (fIdFront.exists()) {
            fIdFront.delete();
        }
        File fIdBack = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_back" + operationID + ".jpg");
        if (fIdBack.exists()) {
            fIdBack.delete();
        }
        File fileIdJson = new File(Environment.getExternalStorageDirectory() + File.separator + "json" + ".json");
        if (fileIdJson.exists()) {
            fileIdJson.delete();
        }
        //FACE FILES
        File faceJPG = new File(Environment.getExternalStorageDirectory() + File.separator + "face_" + operationID + ".jpg");
        if (faceJPG.exists()) {
            faceJPG.delete();
        }
        File fileFaceJson = new File(Environment.getExternalStorageDirectory() + File.separator + "rostro" + ".json");
        if (fileFaceJson.exists()) {
            fileFaceJson.delete();
        }
        //DOCUMENT FILES
        File documentJPG = new File(Environment.getExternalStorageDirectory()+ File.separator + "document_"+operationID+".jpg");
        if(documentJPG.exists()){
            documentJPG.delete();
        }
        File fileDocumentJson = new File(Environment.getExternalStorageDirectory() + File.separator + "document" + ".json");
        if (fileDocumentJson.exists()) {
            fileDocumentJson.delete();
        }
        //FINGERS FILES
        String fingersNames[] = new String[]{"I5","I4","I3","I2","I1","D5","D4","D3","D2","D1"};
        for (int i=0; i<fingersNames.length;i++) {
            File fileFingerJPG = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + fingersNames[i] + "_" + operationID + ".jpg");
            if (fileFingerJPG.exists()) {
                fileFingerJPG.delete();
            }
        }
        File fileFingerJson = new File(Environment.getExternalStorageDirectory() + File.separator + "fingers" + ".json");
        if(fileFingerJson.exists()){
            fileFingerJson.delete();
        }
        //CONTRACT FILE
        File fileContract = new File(Environment.getExternalStorageDirectory() + File.separator + "contract_" + operationID + ".pdf");
        if(fileContract.exists()){
            fileContract.delete();
        }
        //Delete Preferences
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
