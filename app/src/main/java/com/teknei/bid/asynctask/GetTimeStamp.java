package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.activities.FakeINEActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.domain.SearchDTO;
import com.teknei.bid.response.ResponseStep;
import com.teknei.bid.response.ResponseTimeStamp;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;
import com.teknei.bid.ws.ServerConnection;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetTimeStamp extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = getClass().getSimpleName();

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

    private ResponseTimeStamp responseLocal;

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
            /*
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
            }*/

            String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,
                    SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));

            Log.v("token             :", "token        :" + token);
            Log.v("END POINT         :", "endpoint     :" + endPoint);
            Log.v("Curp              :", "curp         :" + curp);
            Log.v("Operación         :", "id Ope       :" + idoperation);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<ResponseTimeStamp> call = api.enrollmentClientDetailSearchCustomerTs(idoperation,curp);

            call.enqueue(new Callback<ResponseTimeStamp>() {

                @Override
                public void onResponse(Call<ResponseTimeStamp> call, Response<ResponseTimeStamp> response) {
                    progressDialog.dismiss();

                    responseStatus = response.code();

                    if (responseStatus >= 200 && responseStatus < 300) {

                        responseLocal = response.body();

                        String tID = "";
                        String tFace = "";
                        String tDocument = "";
                        String tFingers = "";

                        tID       = responseLocal.getId()+"";
                        tFace     = responseLocal.getFacialStr();
                        tDocument = responseLocal.getAddressStr();
                        tFingers  = responseLocal.getFingersStr();

                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TIMESTAMP_CREDENTIALS,tID);
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TIMESTAMP_FACE,tFace);
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TIMESTAMP_DOCUMENT,tDocument);
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TIMESTAMP_FINGERPRINTS,tFingers);

                    } else {
                        if (responseStatus >= 300 && responseStatus < 400) {
                            errorMessage = activityOrigin.getString(R.string.message_ws_response_300);
                        } else if (responseStatus >= 400 && responseStatus < 500) {
                            errorMessage = activityOrigin.getString(R.string.message_ws_response_400);
                        } else if (responseStatus >= 500 && responseStatus < 600) {
                            errorMessage = activityOrigin.getString(R.string.message_ws_response_500);
                        }

                        Log.i(CLASS_NAME, "onResponse: " + errorMessage);
                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseTimeStamp> call, Throwable t) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, activityOrigin.getString(R.string.message_ws_response_500));

                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice),
                            activityOrigin.getString(R.string.message_ws_response_500), ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();

                    t.printStackTrace();
                }
            });

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
        /*
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
        */
    }
}
