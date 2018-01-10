package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.DataValidation;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.response.ResponseServicesBID;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FingersSend extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = "FingersSend";

    private String token;
    private String jsonS;
    private List<File> imageF;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    private ResponseServicesBID responseFinger;
    int tipoAct;

    AlertDialog dialogoAlert;

    public FingersSend(Activity context, String tokenOld, String jsonString, List<File> imagesFiles, int tipoActivity) {
        this.activityOrigin = context;
        this.token   = tokenOld;
        this.jsonS   = jsonString;
        this.imageF  = imagesFiles;
        this.tipoAct = tipoActivity;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activityOrigin,activityOrigin.getString(R.string.message_figerprints_check));
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

            Log.v("token             :", "token        :" + token);
            Log.v("NUM FILES         :", "NUM FILES    :" + imageF.size());
            Log.v("json SEND NO File :", "json no file :" + jsonS);
            Log.v("END POINT         :", "endpoint     :" + endPoint);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            MultipartBody.Part jsonBody =
                    MultipartBody.Part.createFormData("json", imageF.get(0).getName(),
                            RequestBody.create(MediaType.parse("application/json"), imageF.get(0)));

            Call<ResponseServicesBID> call = api.enrollmentBiometricMinuciasCyphered(token, jsonBody);

            call.enqueue(new Callback<ResponseServicesBID>() {

                @Override
                public void onResponse(Call<ResponseServicesBID> call, Response<ResponseServicesBID> response) {

                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, response.code() + " ");

                    responseStatus = response.code();

                    if (response.isSuccessful() && response.body()!=null) {

                        responseFinger = response.body();

                    }

                    if ((responseStatus >= 200 && responseStatus < 300)) {

                            if (responseFinger.isResultOK()) {

                                SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FINGERS_OPERATION, "ok");

                                if (tipoAct == ApiConstants.TYPE_ACT_BASIC)
                                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), responseFinger.getErrorMessage(), ApiConstants.ACTION_GO_NEXT);
                                else
                                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), responseFinger.getErrorMessage(), ApiConstants.ACTION_GO_NEXT_LOCAL);

                                dialogoAlert.setCancelable(false);
                                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialogoAlert.show();

                            } else {

                                Log.i(CLASS_NAME, "----" + responseFinger.getErrorMessage());

                                if (tipoAct == ApiConstants.TYPE_ACT_BASIC)
                                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), responseFinger.getErrorMessage(), ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                                else
                                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), responseFinger.getErrorMessage(), ApiConstants.ACTION_TRY_AGAIN_CONTINUE_LOCAL);

                                dialogoAlert.setCancelable(false);
                                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialogoAlert.show();

                            }

                    } else {

                        if (responseStatus >= 400 && responseStatus < 500) {

                            if (responseStatus == 409) {

                                if (response.body() == null) {

                                    errorMessage = responseStatus + " - " + response.message();

                                } else {

                                    responseFinger = response.body();

                                    if (responseFinger.getErrorMessage().length() > 6) {

                                        errorMessage = responseStatus + " - " + responseFinger.getErrorMessage();

                                    } else {

                                        errorMessage = responseStatus + " - " + ApiConstants.managerErrorServices(Integer.parseInt(responseFinger.getErrorMessage()), activityOrigin);

                                    }

                                }

                                if (tipoAct == ApiConstants.TYPE_ACT_BASIC)
                                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                                else
                                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE_LOCAL);

                                dialogoAlert.setCancelable(false);
                                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialogoAlert.show();

                            } else if (responseStatus == 422) {

                                errorMessage = responseStatus + " - " + "Usuario ya registrado en sistema";

                                Log.i(CLASS_NAME, "Face: " + errorMessage);
                                DataValidation dialogoAlert;
                                dialogoAlert = new DataValidation(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage);
                                dialogoAlert.setCancelable(false);
                                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialogoAlert.show();
                            }

                        } else if (responseStatus >= 300 && responseStatus < 400) {

                                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                            } else if (responseStatus >= 500 && responseStatus < 600) {

                                errorMessage = responseStatus + " - " + "Ocurrió un problema con el servidor";

                            }

                            Log.i(CLASS_NAME, "Error Api " + errorMessage);

                            if (tipoAct == ApiConstants.TYPE_ACT_BASIC)
                                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                            else
                                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE_LOCAL);

                            dialogoAlert.setCancelable(false);
                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialogoAlert.show();
                        }
                }

                @Override
                public void onFailure(Call<ResponseServicesBID> call, Throwable t) {
                    progressDialog.dismiss();

                    Log.d("Response Message", "--------------------------------------------------");

                    t.printStackTrace();

                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), "Error al conectarse con el servidor", ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();

                }
            });
        }
        return null;
    }


    private void manageResponse(Object arrayResponse[]) {
        responseJSONObject = (JSONObject) arrayResponse[0];
        responseStatus = (Integer) arrayResponse[1];
        boolean dataExist = false;
        int msgError = 0;

        Log.d("RESPUESTA WEB SERVICES", "-----"+responseStatus +"-----");

        if ((responseStatus >= 200 && responseStatus < 300)) {
            try {
                dataExist = responseJSONObject.getBoolean("resultOK"); //obtiene los datos del json de respuesta
                msgError  = responseJSONObject.getInt    ("errorMessage");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (dataExist) {

                responseOk = true;

            } else {

                errorMessage = ApiConstants.managerErrorServices (msgError,activityOrigin);

            }
        } else if (responseStatus >= 300 && responseStatus < 400) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);
        } else if (responseStatus >= 400 && responseStatus < 500) {

            if (responseStatus == 409) {
                try {
                    dataExist = responseJSONObject.getBoolean("resultOK"); //obtiene los datos del json de respuesta
                    msgError  = responseJSONObject.getInt    ("errorMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                errorMessage = ApiConstants.managerErrorServices (msgError,activityOrigin);
            } else if (responseStatus == 422) {
                try {
                    dataExist = responseJSONObject.getBoolean("resultOK"); //obtiene los datos del json de respuesta
                    msgError  = responseJSONObject.getInt    ("errorMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                errorMessage = ApiConstants.managerErrorServices (msgError,activityOrigin);
            } else {
                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);
            }
        } else if (responseStatus >= 500 && responseStatus < 600) {
            errorMessage = responseStatus + " - " + "Ocurrió un problema con el servidor";
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        /*
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {
                String messageResp = "";
                try {
                    messageResp = responseJSONObject.getString("errorMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FINGERS_OPERATION, "ok");

                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), messageResp, ApiConstants.ACTION_GO_NEXT);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            } else {
                Log.i("Message logout", "logout: " + errorMessage);
                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        } else {
            Log.i("Message logout", "logout: " + errorMessage);
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        */
    }

}
