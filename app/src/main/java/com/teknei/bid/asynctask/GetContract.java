package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.ServerConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class GetContract extends AsyncTask<String, Void, Void> {
    private String token;
    private String authorization;

    private String userToCheck;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    public GetContract(Activity context, String userString, String passString, String tokenOld, String autho) {
        this.activityOrigin = context;
        this.userToCheck = userString;
        this.token = tokenOld;
        this.authorization = autho;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(
                activityOrigin,
                activityOrigin.getString(R.string.get_user_log_in));
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
        endTime = System.currentTimeMillis() + 2000;
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
            try {
                ServerConnection serverConnection = new ServerConnection();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(activityOrigin, null, endPoint + ApiConstants.LOG_IN_USER , token, ServerConnection.METHOD_GET,null,authorization);
                if (arrayResponse[1] != null) {
                    manageResponse(arrayResponse);
                } else {
                    errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
            }
            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
            while (System.currentTimeMillis() < endTime) {
                //espera hasta que pasen los 2 segundos en caso de que halla terminado muy rapido el hilo
            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());
        }
        return null;
    }


    private void manageResponse(Object arrayResponse[]) {
        responseJSONObject = (JSONObject) arrayResponse[0];
        responseStatus = (Integer) arrayResponse[1];
        String tokenGet = null;
        if (responseStatus >= 200 && responseStatus < 300) {
            try {
                tokenGet = responseJSONObject.getString("token"); //obtiene los datos del json de respuesta
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (tokenGet != null && !tokenGet.equals("")) {
                responseOk = true;
            } else {
                errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
            }
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
                String tokenGet = "";
                try {
                    tokenGet = responseJSONObject.getString("token");
                    SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TOKEN_APP,tokenGet);
                    SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.USERNAME,userToCheck);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((BaseActivity) activityOrigin).goNext();
            } else {
                //Borrar
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TOKEN_APP,"123");
                ((BaseActivity) activityOrigin).goNext();
//                AlertDialog dialogoAlert;
//                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN);
//                dialogoAlert.setCancelable(false);
//                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                dialogoAlert.show();
            }
        } else {
            //Borrar
            SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TOKEN_APP,"123");
            ((BaseActivity) activityOrigin).goNext();
//            errorMessage = activityOrigin.getString(R.string.message_ws_no_internet);
//            AlertDialog dialogoAlert;
//            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN);
//            dialogoAlert.setCancelable(false);
//            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            dialogoAlert.show();
        }

    }

}
