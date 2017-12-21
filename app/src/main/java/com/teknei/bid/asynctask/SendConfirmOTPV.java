package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.domain.SearchDTO;
import com.teknei.bid.domain.ValidateOtpDTO;
import com.teknei.bid.response.ResponseServicesBID;
import com.teknei.bid.response.ResponseStep;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rgarciav on 14/12/2017.
 */

public class SendConfirmOTPV extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = "SendConfirmOTPV";

    private String token;
    private String otpvCode;
    private int    operationID;

    private ProgressDialog progressDialog;
    private Activity       activityOrigin;
    private String         errorMessage;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    private ResponseServicesBID responseValue;

    public SendConfirmOTPV (Activity context, String tokenOld, int idOpe, String code) {
        this.activityOrigin = context;
        this.token          = tokenOld;
        this.operationID    = idOpe;
        this.otpvCode       = code;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog( activityOrigin, activityOrigin.getString(R.string.message_search_operation));
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
    protected Void doInBackground(String... params) {
        if (hasConecction) {
            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
            while (System.currentTimeMillis() < endTime) {
                //espera hasta que pasen los 2 segundos en caso de que halla terminado muy rapido el hilo
            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());

            String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,
                    SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));

            Log.d(CLASS_NAME, "token        :" + token);
            Log.d(CLASS_NAME, "endpoint     :" + endPoint);
            Log.d(CLASS_NAME, "code         :" + otpvCode);
            Log.d(CLASS_NAME, "operation    :" + operationID);

            ValidateOtpDTO valueDTO = new ValidateOtpDTO(new Long(operationID),otpvCode);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<String> call = api.enrollmentMailValidateOTP(token, valueDTO);

            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, response.code() + " ");

                    responseStatus = response.code();

                    if (responseStatus == 200) {

                            ((BaseActivity) activityOrigin).goNext();

                    } else {

                        if (responseStatus >= 300 && responseStatus < 400) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                        } else if (responseStatus >= 400 && responseStatus < 500) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);

                        } else if (responseStatus >= 500 && responseStatus < 600) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);

                        }

                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
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

    @Override
    protected void onPostExecute(Void result) {

    }
}
