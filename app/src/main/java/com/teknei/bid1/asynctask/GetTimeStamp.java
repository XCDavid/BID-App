package com.teknei.bid1.asynctask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid1.R;
import com.teknei.bid1.dialogs.AlertDialog;
import com.teknei.bid1.dialogs.ProgressDialog;
import com.teknei.bid1.utils.ApiConstants;
import com.teknei.bid1.utils.SharedPreferencesUtils;
import com.teknei.bid1.ws.ServerConnection;

import org.json.JSONObject;

public class GetTimeStamp extends AsyncTask<String, Void, Void> {
    private String token;
    private String curp;
    private String idoperation;
    private JSONObject jsonObjectResponse = null;

    private Activity activityOrigin;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private Bitmap bmp;

    public GetTimeStamp(Activity context, String curp, String id, String tokenOld) {
        this.activityOrigin = context;
        this.curp = curp;
        this.idoperation = id;
        this.token = tokenOld;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(
                activityOrigin,
                activityOrigin.getString(R.string.message_get_timestamp_operation));
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
        ConnectivityManager check = (ConnectivityManager) activityOrigin.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = check.getAllNetworkInfo();
        for (int i = 0; i < info.length; i++) {
            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                hasConecction = true;
            }
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        if (hasConecction) {
            try {
                ServerConnection serverConnection = new ServerConnection();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(
                        activityOrigin, null, endPoint + ApiConstants.METHOD_GET_TIMESTAMP+"/"+ idoperation+"/"+curp+"/", token, ServerConnection.METHOD_GET, null, "");
                if (arrayResponse[0] != null) {
                    manageResponse(arrayResponse);
                } else {
                    errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
            }
        }
        return null;
    }


    private void manageResponse(Object arrayResponse[]) {

        responseStatus = (Integer) arrayResponse[1];
        if (responseStatus >= 200 && responseStatus < 300) {
            jsonObjectResponse = (JSONObject) arrayResponse[0];
//            String statusString = "";
//            try {
//                statusString = responseJSONObject.getString("status"); //obtiene los datos del json de respuesta
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (statusString.equals("ok")) {
            responseOk = true;
//            } else {
//                errorMessage = activityOrigin.getString(R.string.message_ws_response_fail);
//            }
        } else if (responseStatus >= 300 && responseStatus < 400) {
            errorMessage = activityOrigin.getString(R.string.message_ws_response_300);
        } else if (responseStatus >= 400 && responseStatus < 500) {
            errorMessage = activityOrigin.getString(R.string.message_ws_response_400);
        } else if (responseStatus >= 500 && responseStatus < 600) {
            errorMessage = activityOrigin.getString(R.string.message_ws_response_500);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {
                String tID = "";
                String tFace = "";
                String tDocument = "";
                String tFingers = "";

                tID = jsonObjectResponse.optString("credentialsStr");
                tFace = jsonObjectResponse.optString("facialStr");
                tDocument = jsonObjectResponse.optString("addressStr");
                tFingers = jsonObjectResponse.optString("fingersStr");
//                ((FakeINEActivity) activityOrigin).setTiemStamp(tID,tFace,tDocument,tFingers);
//                SharedPreferencesUtils.deleteFromPreferences(activityOrigin,SharedPreferencesUtils.TIMESTAMP_CREDENTIALS);
//                SharedPreferencesUtils.deleteFromPreferences(activityOrigin,SharedPreferencesUtils.TIMESTAMP_FACE);
//                SharedPreferencesUtils.deleteFromPreferences(activityOrigin,SharedPreferencesUtils.TIMESTAMP_DOCUMENT);
//                SharedPreferencesUtils.deleteFromPreferences(activityOrigin,SharedPreferencesUtils.TIMESTAMP_FINGERPRINTS);

                SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TIMESTAMP_CREDENTIALS,tID);
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TIMESTAMP_FACE,tFace);
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TIMESTAMP_DOCUMENT,tDocument);
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TIMESTAMP_FINGERPRINTS,tFingers);

            } else {
                Log.i("Message logout", "logout: " + errorMessage);
                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        } else {
            Log.i("Message logout", "logout: " + errorMessage);
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
    }

}
