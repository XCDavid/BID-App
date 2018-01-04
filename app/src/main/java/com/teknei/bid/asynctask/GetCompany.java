package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.BankAccountRegistrationActivity;
import com.teknei.bid.activities.SettingsActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.domain.CompanyDTO;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rgarciav on 04/01/2018.
 */

public class GetCompany extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = "GetCompany";

    private List<CompanyDTO> listValue;

    private Activity activityOrigin;
    private String   errorMessage;
    private String   token;

    private boolean hasConecction  = false;
    private Integer responseStatus = 0;

    private long endTime;

    public GetCompany (Activity activityOrigin, String token) {
        this.token          = token;
        this.activityOrigin = activityOrigin;
        listValue = new ArrayList<CompanyDTO>();
    }

    @Override
    protected void onPreExecute() {
        endTime = System.currentTimeMillis() + 1000;
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

            Log.d(CLASS_NAME, "token        :" + token);
            Log.d(CLASS_NAME, "endpoint     :" + endPoint);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<List<CompanyDTO>> call = api.managementAdminEmpr(token);

            call.enqueue(new Callback<List<CompanyDTO>>() {

                @Override
                public void onResponse(Call<List<CompanyDTO>> call, Response<List<CompanyDTO>> response) {

                    Log.d(CLASS_NAME, response.code() + " ");

                    responseStatus = response.code();

                    if (responseStatus >= 200 && responseStatus < 300) {

                        if (response.body()!= null) {

                            listValue = response.body();

                        }

                    } else {

                        if (responseStatus >= 300 && responseStatus < 400) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                        } else if (responseStatus >= 400 && responseStatus < 500) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);

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
                public void onFailure(Call<List<CompanyDTO>> call, Throwable t) {
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
}
