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
import com.teknei.bid.response.ResponseStep;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindOperation extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = getClass().getSimpleName();

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

    private ResponseStep responseStep;

    public FindOperation(Activity context, String tokenOld, String curp) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.curp = curp;
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
            /*try {
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
            */

            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
            while (System.currentTimeMillis() < endTime) {
                //espera hasta que pasen los 2 segundos en caso de que halla terminado muy rapido el hilo
            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());

            String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,
                    SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));

            Log.v("token             :", "token        :" + token);
            Log.v("END POINT         :", "endpoint     :" + endPoint);
            Log.v("Curp              :", "curp         :" + curp);

            SearchDTO searchDTO = new SearchDTO();

            searchDTO.setCurp(curp);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<ResponseStep> call = api.enrollmentClientDetailStep(searchDTO);

            call.enqueue(new Callback<ResponseStep>() {

                @Override
                public void onResponse(Call<ResponseStep> call, Response<ResponseStep> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {

                        responseStep = response.body();

                        Log.d(CLASS_NAME, response.code() + " ");

                        if (responseStep.getStep() > 0) {

                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.OPERATION_ID, responseStep.getOperationId()+"");
                            new GetDetailFindOperation(activityOrigin, token,responseStep.getStep().intValue(), curp).execute();

                        } else {

                            ((BaseActivity) activityOrigin).sendPetition();

                        }

                    } else {

                        Log.d(CLASS_NAME, response.code() + "");

                        responseStatus = response.code();

                        if (responseStatus >= 300 && responseStatus < 400) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                        } else if (responseStatus >= 400 && responseStatus < 500) {

                            String errorResponse = activityOrigin.getString(R.string.message_ws_response_400);

                            if (responseStatus == 422) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());

                                    boolean dataExist = jObjError.getBoolean ("resultOK");
                                    errorResponse     = jObjError.getString  ("errorMessage");
                                } catch (Exception e) {

                                    Log.d (CLASS_NAME, e.getMessage());

                                }
                            }

                            errorMessage = responseStatus + " - " + errorResponse;

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
                public void onFailure(Call<ResponseStep> call, Throwable t) {
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

/*
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
    */

    @Override
    protected void onPostExecute(Void result) {
        /*
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {

                if (stepOperation>0) { //Existe una operacion pendeinte -> ir a recuperar la info de la operacion
                    SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.OPERATION_ID, operationID+"");
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
        */
    }
}
