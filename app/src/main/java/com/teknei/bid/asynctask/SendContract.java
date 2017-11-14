package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.io.Files;
import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.response.ResponseServicesBID;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;
import com.teknei.bid.ws.ServerConnection;
import com.teknei.bid.ws.ServerConnectionDownloadFile;
import com.teknei.bid.ws.ServerConnectionUploadFileBytes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendContract extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = getClass().getSimpleName();

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

    private ResponseServicesBID responseLocal;

    public SendContract(Activity context, String tokenOld, int idOperationIn, List<byte []> filesIn) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.idOperation = idOperationIn;
        this.filesList = filesIn;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activityOrigin,activityOrigin.getString(R.string.message_load_send_contract));
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
            /*
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
            */

            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
            while (System.currentTimeMillis() < endTime) {
                //espera hasta que pasen los 2 segundos en caso de que halla terminado muy rapido el hilo
            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());

            String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,
                    SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));

            Log.v("token             :", "token        :" + token);
            Log.v("NUM FILES         :", "NUM FILES    :" + filesList.size());
            Log.v("Id Operation      :", "id ope       :" + idOperation);
            Log.v("END POINT         :", "endpoint     :" + endPoint);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            MultipartBody.Part jsonBody =
                    MultipartBody.Part.createFormData("String", "id",
                            RequestBody.create(MediaType.parse("text/plain"), idOperation+""));

            MultipartBody.Part jsonFront =
                    MultipartBody.Part.createFormData("file", "file",
                            RequestBody.create (MediaType.parse("image/jpg"), filesList.get(0)));

            Call<ResponseServicesBID> call = api.enrollmentContractAdd(jsonBody, jsonFront);

            call.enqueue(new Callback<ResponseServicesBID>() {

                @Override
                public void onResponse(Call<ResponseServicesBID> call, Response<ResponseServicesBID> response) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, "complete:" + response.code());

                    responseStatus = response.code();

                    if(response.body() != null) {
                        responseLocal = response.body();
                    }


                    if (responseStatus >= 200 && responseStatus < 300) {

                        if (responseLocal.isResultOK()) {

                            ((BaseActivity) activityOrigin).goNext();

                        } else {
                            ((BaseActivity) activityOrigin).goNext();
                            AlertDialog dialogoAlert;
                            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), responseLocal.getErrorMessage(), ApiConstants.ACTION_TRY_AGAIN_CANCEL);
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

                        errorMessage = activityOrigin.getString(R.string.message_ws_no_internet);
                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN);
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
        /*progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {
                ((BaseActivity) activityOrigin).goNext();
            } else {
                //BORRAR
                ((BaseActivity) activityOrigin).goNext();
                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
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
        }*/
    }
}
