package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.ContractFingerSignatureBioSmart;
import com.teknei.bid.activities.ContractFingerSignatureMSO;
import com.teknei.bid.activities.ContractFingerSignatureWatson;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.FinishSingContractDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.domain.ConfirmFingerSingDTO;
import com.teknei.bid.domain.FingerLoginDTO;
import com.teknei.bid.domain.FingerSingDTO;
import com.teknei.bid.response.ResponseServicesBID;
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

public class SendFingerSignContract extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = "SendFingerSignContract";

    private String token;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;

    private ProgressDialog           progressDialog;
    private FinishSingContractDialog dialogoAlert;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    private ResponseServicesBID responseFinger;
    private FingerSingDTO fingerDTO;

    private String opcionReader;

    public SendFingerSignContract(Activity context, String tokenOld, FingerSingDTO fingerDTO) {
        this.activityOrigin = context;
        this.token   = tokenOld;
        this.fingerDTO = fingerDTO;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activityOrigin,activityOrigin.getString(R.string.message_finger_sing_contract));
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

            opcionReader = SharedPreferencesUtils.readFromPreferencesString
                                            (activityOrigin, SharedPreferencesUtils.FINGERPRINT_READER, "");


            Log.d(CLASS_NAME, "endpoint     :" + endPoint);
            Log.d(CLASS_NAME, "rm           :" + fingerDTO.getFingerIndex());
            Log.d(CLASS_NAME, "operation id :" + fingerDTO.getOperationId());
            Log.d(CLASS_NAME, "Content Type :" + fingerDTO.getContentType());

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<ResponseServicesBID> call = api.enrollmentContractSign(token, fingerDTO);

            call.enqueue(new Callback<ResponseServicesBID>() {

                @Override
                public void onResponse(Call<ResponseServicesBID> call, Response<ResponseServicesBID> response) {

                    Log.d(CLASS_NAME, response.code() + " ");

                    responseStatus = response.code();

                    if (response.isSuccessful() && response.body()!=null) {

                        responseFinger = response.body();

                    }

                    if ((responseStatus >= 200 && responseStatus < 300)) {

                        if (opcionReader.equals("watson")){

                            ((ContractFingerSignatureWatson) activityOrigin).confirmContract();

                        } else if (opcionReader.equals("biosmart")) {

                            ((ContractFingerSignatureBioSmart) activityOrigin).confirmContract();

                        } else {

                            ((ContractFingerSignatureMSO) activityOrigin).confirmContract();
                        }

                    } else {

                        if (responseStatus >= 300 && responseStatus < 400) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                        } else if (responseStatus >= 400 && responseStatus < 500) {

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

                            } else if (responseStatus == 422) {

                                    errorMessage = responseStatus + " - " + "Falló la generación del contrato";

                            } else if (responseStatus == 404) {

                                errorMessage = responseStatus + " - " + "Falló la autenticación del cliente";

                            } else  {

                                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);

                            }

                        } else if (responseStatus >= 500 && responseStatus < 600) {

                            errorMessage = responseStatus + " - " + "Ocurrió un problema con el servidor";

                        }

                        Log.i(CLASS_NAME, "Error Api " + errorMessage);

                        dialogoAlert = new FinishSingContractDialog (activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage,
                                                                     ApiConstants.ACTION_TRY_AGAIN_CANCEL,ApiConstants.ACTION_SEND_FINGER_CONTRACT,opcionReader);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseServicesBID> call, Throwable t) {
                    progressDialog.dismiss();

                    t.printStackTrace();

                    dialogoAlert = new FinishSingContractDialog (activityOrigin, activityOrigin.getString(R.string.message_ws_notice), "Error al conectarse al servidor",
                                                                 ApiConstants.ACTION_TRY_AGAIN_CANCEL,ApiConstants.ACTION_SEND_FINGER_CONTRACT,opcionReader);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();

                }
            });
        }
        return null;
    }

    /*
    protected void confirmSignContract () {

        String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,
                SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));

        Log.v("END POINT         :", "endpoint     :" + endPoint);

        ConfirmFingerSingDTO valueDTO = new ConfirmFingerSingDTO (fingerDTO.getOperationId());

        BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

        Call<ResponseServicesBID> call = api.enrollmentMailContractSign(token, valueDTO);

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

                    AlertDialog dialogoAlert;

                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice),
                            "Firma y Envio de Contrato Satisfactoria", ApiConstants.ACTION_GO_NEXT);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();

                } else {

                    if (responseStatus >= 300 && responseStatus < 400) {

                        errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                    } else if (responseStatus >= 400 && responseStatus < 500) {

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

                        } else if (responseStatus == 422) {

                            errorMessage = responseStatus + " - " + "Falló el envío del correo";

                        } else if (responseStatus == 404) {

                            errorMessage = responseStatus + " - " + "Falló la autenticación del cliente";

                        } else  {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);

                        }

                    } else if (responseStatus >= 500 && responseStatus < 600) {

                        errorMessage = responseStatus + " - " + "Ocurrió un problema con el servidor";

                    }

                    Log.i(CLASS_NAME, "Error Api " + errorMessage);
                    AlertDialog dialogoAlert;

                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseServicesBID> call, Throwable t) {
                progressDialog.dismiss();

                t.printStackTrace();

                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), "Error al conectarse con el servidor",
                        ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        });
    }
    */

    @Override
    protected void onPostExecute(Void result) {

    }
}
