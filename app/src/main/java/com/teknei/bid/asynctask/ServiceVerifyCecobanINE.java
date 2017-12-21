package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.DialogINEValidationResult;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.domain.VerifyCecobanDTO;
import com.teknei.bid.response.ResponseServicesBID;
import com.teknei.bid.response.ResponseVerifyCecoban;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class ServiceVerifyCecobanINE extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = "ServiceVerifyCecobanINE";
    private String token;

    private Activity activityOrigin;
    private String errorMessage;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    private AlertDialog dialogoAlert;
    private ResponseServicesBID responseFinger;
    private ProgressDialog progressDialog;
    private VerifyCecobanDTO requestValue;
    private ResponseVerifyCecoban responseValue;

    public ServiceVerifyCecobanINE(Activity context, String tokenOld, VerifyCecobanDTO requestValue) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.requestValue = requestValue;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(activityOrigin, activityOrigin.getString(R.string.message_search_operation));
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();

        endTime = System.currentTimeMillis() + 1500;
        Log.i("Wait", "Timer Start: " + System.currentTimeMillis());
        Log.i("Wait", "Timer END: " + endTime);

        ConnectivityManager check = (ConnectivityManager) activityOrigin.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = check.getAllNetworkInfo();
        for (int i = 0; i < info.length; i++) {
            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                hasConecction = true;
            }
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        if (hasConecction) {
            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
            while (System.currentTimeMillis() < endTime) {
                //espera hasta que pasen los 2 segundos en caso de que halla terminado muy rapido el hilo
            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());

            String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,
                    SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));

            Log.v(CLASS_NAME, "token        :" + token);
            Log.v(CLASS_NAME, "endpoint     :" + endPoint);
            Log.v(CLASS_NAME, "idOperation  :" + requestValue.getIdOperation());

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<ResponseVerifyCecoban> call = api.enrollmentCredentialsVerifyCecoban(token, requestValue);

            call.enqueue(new Callback<ResponseVerifyCecoban>() {

                @Override
                public void onResponse(Call<ResponseVerifyCecoban> call, Response<ResponseVerifyCecoban> response) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, response.code() + " ");

                    responseStatus = response.code();

                    if (response.body() != null) {

                        responseValue = (ResponseVerifyCecoban) response.body();
                    }

                    if (responseStatus >= 200 && responseStatus < 300) {

                        DialogINEValidationResult dialogoAlert = new DialogINEValidationResult(activityOrigin, responseValue);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();

                    } else {

                        if (responseStatus >= 300 && responseStatus < 400) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                        } else if (responseStatus >= 400 && responseStatus < 500) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);

                            if (responseStatus == 422) {

                                errorMessage = responseStatus + " - " + "No se pudo obtener verificación";

                            }

                        } else if (responseStatus >= 500 && responseStatus < 600) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);

                        }

                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_CANCEL_OPERATION);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();

                    }
                }

                @Override
                public void onFailure(Call<ResponseVerifyCecoban> call, Throwable t) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, activityOrigin.getString(R.string.message_ws_response_500));

                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice),
                            activityOrigin.getString(R.string.message_ws_response_500), ApiConstants.ACTION_CANCEL_OPERATION);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();

                    t.printStackTrace();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

    }
}