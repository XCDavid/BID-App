package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.domain.SearchDTO;
import com.teknei.bid.domain.StartOperationDTO;
import com.teknei.bid.response.ResponseStartOpe;
import com.teknei.bid.response.ResponseStep;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;
import com.teknei.bid.ws.ServerConnection;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartOperation extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = getClass().getSimpleName();

    private String token;
    private String jsonS;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    private StartOperationDTO startOperationDTO;
    private ResponseStartOpe  responseStartOpe;

    public StartOperation(Activity context, String tokenOld, String jsonString, StartOperationDTO startOperationDTO) {
        this.activityOrigin    = context;
        this.token             = tokenOld;
        this.jsonS             = jsonString;
        this.startOperationDTO = startOperationDTO;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog( activityOrigin, activityOrigin.getString(R.string.message_start_operation));
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
            /*
            try {
                ServerConnection serverConnection = new ServerConnection();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(activityOrigin, jsonS, endPoint + ApiConstants.METHOD_START_OPERATION, token, ServerConnection.METHOD_POST,null,"");
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
            Log.v("JSON              :", "json file    :" + jsonS);

            Log.v("Object            :", "{\"nombre\":\""          + startOperationDTO.getName()            + "\"," +
                                          "\"customerType\":\""    + startOperationDTO.getCustomerType()    + "\"," +
                                          "\"primerApellido\":\""  + startOperationDTO.getFirstLastName()   + "\"," +
                                          "\"email\":\""           + startOperationDTO.getEmail()           + "\"," +
                                          "\"refContrato\":\""     + startOperationDTO.getRefContract()     + "\"," +
                                          "\"telefono\":\""        + startOperationDTO.getPhoneNumber()     + "\"," +
                                          "\"curp\":\""            + startOperationDTO.getCurp()            + "\"," +
                                          "\"employee\":\""        + startOperationDTO.getEmployee()        + "\"," +
                                          "\"deviceId\":\""        + startOperationDTO.getDeviceId()        + "\"," +
                                          "\"segundoApellido\":\"" + startOperationDTO.getSecondLastName()  + "\"," +
                                          "\"emprId\":\""          + startOperationDTO.getCustomerType()    + "\"}");

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);
            Call<ResponseStartOpe> call = api.enrollmentStatusStart(startOperationDTO);

            call.enqueue(new Callback<ResponseStartOpe>() {

                @Override
                public void onResponse(Call<ResponseStartOpe> call, Response<ResponseStartOpe> response) {
                    progressDialog.dismiss();

                    if (response.isSuccessful()) {

                        responseStartOpe = response.body();

                        Log.d(CLASS_NAME, response.code() + " ");

                        if (responseStartOpe.isResultOK()) {

                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin,
                               SharedPreferencesUtils.OPERATION_ID, responseStartOpe.getOperationId() + "");

                            AlertDialog dialogoAlert;
                            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice),
                                               responseStartOpe.getErrorMessage(), ApiConstants.ACTION_GO_NEXT);
                            dialogoAlert.setCancelable(false);
                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialogoAlert.show();

                        } else {
                            Log.i(CLASS_NAME, "StartOperation: " + errorMessage);
                            AlertDialog dialogoAlert;
                            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage,
                                               ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                            dialogoAlert.setCancelable(false);
                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialogoAlert.show();
                        }

                    } else {

                        Log.d(CLASS_NAME, response.code() + " ");

                        if (responseStatus >= 300 && responseStatus < 400) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                        } else if (responseStatus >= 400 && responseStatus < 500) {

                            String errorResponse = "";
                            errorResponse = activityOrigin.getString(R.string.message_ws_response_400);

                            if (responseStatus == 422){

                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());

                                    boolean dataExist = jObjError.getBoolean ("resultOK");
                                    errorResponse      = jObjError.getString  ("errorMessage");

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
                public void onFailure(Call<ResponseStartOpe> call, Throwable t) {
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
        responseJSONObject = (JSONObject) arrayResponse[0];
        responseStatus = (Integer) arrayResponse[1];
        boolean dataExist   = false;
        String  msgError    = "";
        String resultString = "";

        if ((responseStatus >= 200 && responseStatus < 300)) {
            try {
                dataExist = responseJSONObject.getBoolean ("resultOK"); //obtiene los datos del json de respuesta
                msgError  = responseJSONObject.getString  ("errorMessage");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (dataExist) {

                responseOk = true;

            } else {

                errorMessage = ApiConstants.managerErrorServices (Integer.parseInt(msgError),activityOrigin);

            }
        } else if (responseStatus >= 300 && responseStatus < 400) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);
        } else if (responseStatus >= 400 && responseStatus < 500) {
            //errorMessage = activityOrigin.getString(R.string.message_ws_response_400);
            //resultString = responseJSONObject.optString("resultOK");
            //String errorResponse = "";
            //if (resultString.equals("false")) {
            //errorResponse = responseJSONObject.optString("errorMessage");
            //}
            if (responseStatus == 422){
                try {
                    dataExist = responseJSONObject.getBoolean("resultOK"); //obtiene los datos del json de respuesta
                    msgError  = responseJSONObject.getString ("errorMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v("Error Message " , msgError);

                errorMessage = ApiConstants.managerErrorServices (Integer.parseInt(msgError),activityOrigin);
            }

        } else if (responseStatus >= 500 && responseStatus < 600) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        /*
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {
                int operationINT=-1;
                String messageResp = "";
                try {
                    operationINT = responseJSONObject.getInt("operationId");
                    messageResp  = responseJSONObject.getString("errorMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.OPERATION_ID, operationINT+"");

                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), messageResp, ApiConstants.ACTION_GO_NEXT);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
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
