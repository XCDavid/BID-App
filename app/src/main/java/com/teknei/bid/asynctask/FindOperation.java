package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.activities.FormActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.ServerConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class FindOperation extends AsyncTask<String, Void, Void> {
    //    private String newToken;
    private String token;
    private String curp;
    private int stepOperation;
    private int operationID;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    public FindOperation(Activity context, String tokenOld, String curp) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.curp = curp;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(
                activityOrigin,
                activityOrigin.getString(R.string.message_search_operation));
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
            try {
                ServerConnection serverConnection = new ServerConnection();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                //Construimos el JSON con el curp
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("curp", curp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Object arrayResponse[] = serverConnection.connection(activityOrigin, jsonObject.toString(), endPoint + ApiConstants.METHOD_CHECK_PENDING_OPERATION, token, ServerConnection.METHOD_POST,null,"");
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
//        responseJSONObject = null;
//        responseStatus = 200;
        boolean dataExist = false;
        String resultString = "";
        int operationLevel = 0;
        int operationIdJson = 0;
        if (responseStatus >= 200 && responseStatus < 300) {
            try {
                operationLevel = responseJSONObject.getInt("step");//Descomentar
                operationIdJson = responseJSONObject.getInt("operationId");//Descomentar
                dataExist = true;
//                operationLevel = Integer.valueOf(curp);
//                operationId="123";
                stepOperation = operationLevel;
                operationID = operationIdJson;
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.OPERATION_ID, operationID+"");

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dataExist) {
                responseOk = true;
            } else {
                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_fail);
            }
        } else if (responseStatus >= 300 && responseStatus < 400) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);
        } else if (responseStatus >= 400 && responseStatus < 500) {
//            resultString = responseJSONObject.optString("resultOK");
            String errorResponse = "";
            errorResponse = activityOrigin.getString(R.string.message_ws_response_400);
//            if (resultString.equals("false")) {
//                errorResponse = responseJSONObject.optString("errorMessage");
//            }
            if (responseStatus == 422){
                errorResponse = responseJSONObject.optString("errorMessage");
            }

            errorMessage = responseStatus + " - " + errorResponse;
        } else if (responseStatus >= 500 && responseStatus < 600) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {

                if (stepOperation>0) { //Existe una operacion pendeinte -> ir a recuperar la info de la operacion
                    new GetDetailFindOperation(activityOrigin, token,stepOperation, curp).execute();
                }else{
                    ((BaseActivity) activityOrigin).sendPetition();
                }
            } else {
                Log.i("Message find operation", "message: " + errorMessage);
                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        } else {
            Log.i("Message find operation", "message: " + errorMessage);
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
    }

}
