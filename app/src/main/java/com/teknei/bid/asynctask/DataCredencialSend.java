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
import com.teknei.bid.domain.CredentialDTO;
import com.teknei.bid.response.ResponseServicesBID;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rgarciav on 13/11/2017.
 */

public class DataCredencialSend extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = getClass().getSimpleName();

    private String token;
    private String operationID;
    private String type;
    private int ACTION;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    private ResponseServicesBID responseLocal;
    private CredentialDTO       credencialDto;

    public DataCredencialSend (Activity context, String tokenOld, String type, String operationID, CredentialDTO credencialDto) {
        this.activityOrigin = context;
        this.token          = tokenOld;
        this.type           = type;
        this.operationID    = operationID;
        this.credencialDto  = credencialDto;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(
                activityOrigin,
                activityOrigin.getString(R.string.message_send_data_credential));
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

            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
            while (System.currentTimeMillis() < endTime) {
                //espera hasta que pasen los 2 segundos en caso de que halla terminado muy rapido el hilo
            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());

            String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,
                    SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));

            JSONObject jsonData = new JSONObject();

            try {
                jsonData.put("apeMat"   , credencialDto.getApeMat());
                jsonData.put("apePat"   , credencialDto.getApePat());
                jsonData.put("call"     , credencialDto.getCall());
                jsonData.put("clavElec" , credencialDto.getClavElec());
                jsonData.put("col"      , credencialDto.getCol());
                jsonData.put("cp"       , credencialDto.getCp());

                jsonData.put("dist"     , credencialDto.getDist());
                jsonData.put("esta"     , credencialDto.getEsta());
                jsonData.put("foli"     , credencialDto.getFoli());
                jsonData.put("loca"     , credencialDto.getLoca());
                jsonData.put("mrz"      , credencialDto.getMrz());
                jsonData.put("muni"     , credencialDto.getMuni());

                jsonData.put("noExt"    , credencialDto.getNoExt());
                jsonData.put("noInt"    , credencialDto.getNoInt());
                jsonData.put("nomb"     , credencialDto.getNomb());
                jsonData.put("ocr"      , credencialDto.getOcr());
                jsonData.put("secc"     , credencialDto.getSecc());
                jsonData.put("user"     , credencialDto.getUser());
                jsonData.put("vige"     , credencialDto.getVige());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.v("token             :", "token        :" + token);
            Log.v("END POINT         :", "endpoint     :" + endPoint);
            Log.v("Id Operation      :", "operationID  :" + operationID);
            Log.v("Type              :", "Type         :" + type);
            Log.v("Json              :","Json          :" + jsonData.toString());

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<ResponseServicesBID> call = api.enrollmentCredentialUpdate(token, type, operationID, credencialDto);

            call.enqueue(new Callback<ResponseServicesBID>() {

                @Override
                public void onResponse(Call<ResponseServicesBID> call, Response<ResponseServicesBID> response) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, response.code()+"");

                    responseStatus = response.code();

                    if (responseStatus >= 200 && responseStatus < 300) {

                        responseLocal = response.body();

                        //SharedPreferencesUtils.cleanSharedPreferencesOperation(activityOrigin);

                        if (responseLocal.isResultOK()) {

                            Log.i(CLASS_NAME, "onResponse: " + responseLocal.getErrorMessage());
                            AlertDialog dialogoAlert;
                            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), "Se guardo correctamente la información", ApiConstants.ACTION_GO_NEXT);
                            dialogoAlert.setCancelable(false);
                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialogoAlert.show();

                        } else {
                            Log.i(CLASS_NAME, "onResponse: " + responseLocal.getErrorMessage());
                            AlertDialog dialogoAlert;
                            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), "Error al actualizar información", ApiConstants.ACTION_GO_NEXT);
                            dialogoAlert.setCancelable(false);
                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialogoAlert.show();
                        }

                    } else {
                        if (responseStatus >= 300 && responseStatus < 400) {
                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);
                        } else if (responseStatus >= 400 && responseStatus < 500) {
                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);
                        } else if (responseStatus >= 500 && responseStatus < 600) {
                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);
                        }

                        Log.i(CLASS_NAME, "onResponse: " + errorMessage);
                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_GO_NEXT);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseServicesBID> call, Throwable t) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME,"onFailure "+ activityOrigin.getString(R.string.message_ws_response_500));

                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice),
                            activityOrigin.getString(R.string.message_ws_response_500), ApiConstants.ACTION_GO_NEXT);
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
