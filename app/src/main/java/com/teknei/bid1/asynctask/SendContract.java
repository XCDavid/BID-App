package com.teknei.bid1.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid1.R;
import com.teknei.bid1.activities.BaseActivity;
import com.teknei.bid1.dialogs.AlertDialog;
import com.teknei.bid1.dialogs.ProgressDialog;
import com.teknei.bid1.utils.ApiConstants;
import com.teknei.bid1.utils.SharedPreferencesUtils;
import com.teknei.bid1.ws.ServerConnection;
import com.teknei.bid1.ws.ServerConnectionUploadFileBytes;

import java.util.List;

public class SendContract extends AsyncTask<String, Void, Void> {
    private String token;
    private int idOperation;
    private List<byte []> filesList;
    private Activity activityOrigin;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;
    public SendContract(Activity context, String tokenOld, int idOperationIn, List<byte []> filesIn) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.idOperation = idOperationIn;
        this.filesList = filesIn;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(
                activityOrigin,
                activityOrigin.getString(R.string.message_load_send_contract));
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
                ServerConnectionUploadFileBytes serverConnection = new ServerConnectionUploadFileBytes();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(activityOrigin, endPoint + ApiConstants.METHOD_SEND_CONTRACT +idOperation, token, ServerConnection.METHOD_POST,filesList);
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
        responseStatus = (Integer) arrayResponse[1];
        String tokenGet = null;
        if (responseStatus >= 200 && responseStatus < 300) {
                responseOk = true;
        } else if (responseStatus >= 300 && responseStatus < 400) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);
        } else if (responseStatus >= 400 && responseStatus < 500) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);
        } else if (responseStatus >= 500 && responseStatus < 600) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {
                ((BaseActivity) activityOrigin).goNext();
            } else {
                //BORRAR
                ((BaseActivity) activityOrigin).goNext();
                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        } else {
            //BORRAR
            ((BaseActivity) activityOrigin).goNext();
            errorMessage = activityOrigin.getString(R.string.message_ws_no_internet);
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }

    }

}
