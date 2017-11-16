package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.dialogs.AlertCurpDialog;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.CredentialResumeDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.response.ResponseServicesBID;
import com.teknei.bid.services.BIDEndPointServices;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;
import com.teknei.bid.ws.ServerConnection;
import com.teknei.bid.ws.ServerConnectionListImages;

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

public class FaceFileSend extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = getClass().getSimpleName();

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

    private ResponseServicesBID responseFace;
    private String              msgError;

    public FaceFileSend(Activity context, String tokenOld, String jsonString, List<File> imageFile) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.jsonS = jsonString;
        this.imageF = imageFile;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(
                activityOrigin,
                activityOrigin.getString(R.string.message_face_scan_check));
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
                ServerConnectionListImages serverConnection = new ServerConnectionListImages();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(activityOrigin, jsonS, endPoint + ApiConstants.METHOD_FACE, token, ServerConnection.METHOD_POST, imageF, "");
                if (arrayResponse[1] != null) {
                    manageResponse(arrayResponse);
                } else {
                    errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_petition_fail);
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_petition_fail);
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
            Log.v("NUM FILES         :", "NUM FILES    :" + imageF.size());
            Log.v("json SEND NO File :", "json no file :" + jsonS);
            Log.v("END POINT         :", "endpoint     :" + endPoint);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            MultipartBody.Part jsonBody =
                    MultipartBody.Part.createFormData("json", imageF.get(0).getName(),
                            RequestBody.create(MediaType.parse("application/json"), imageF.get(0)));

            MultipartBody.Part jsonFront =
                    MultipartBody.Part.createFormData("file", imageF.get(1).getName(),
                            RequestBody.create (MediaType.parse("image/jpg"), imageF.get(1)));

            Call<ResponseServicesBID> call = api.enrollmentFacialFace(token, jsonBody, jsonFront);

            call.enqueue(new Callback<ResponseServicesBID>() {

                @Override
                public void onResponse(Call<ResponseServicesBID> call, Response<ResponseServicesBID> response) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, response.code() + " ");

                    responseStatus = response.code();

                    if (response.body() != null) {
                        responseFace = response.body();
                        responseOk   = responseFace.isResultOK();
                        errorMessage = responseFace.getErrorMessage();
                    }

                    if (responseStatus >= 200 && responseStatus < 300) {

                        if (responseOk) {

                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FACE_OPERATION, "ok");

                            AlertDialog dialogoAlert;
                            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_GO_NEXT);
                            dialogoAlert.setCancelable(false);
                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialogoAlert.show();

                        } else {
                            Log.i(CLASS_NAME, "Face: " + errorMessage);
                            AlertDialog dialogoAlert;
                            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), responseStatus+" "+errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                            dialogoAlert.setCancelable(false);
                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialogoAlert.show();
                        }

                    } else {

                        if (responseStatus >= 300 && responseStatus < 400) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                        } else if (responseStatus >= 400 && responseStatus < 500) {

                            if (responseStatus == 422) {

                                errorMessage = responseStatus + " - " + response.message();

                            } else {

                                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);

                            }
                        } else if (responseStatus >= 500 && responseStatus < 600) {

                            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);

                        }

                        Log.i(CLASS_NAME, "Face: " + errorMessage);
                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
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
        responseJSONObject = (JSONObject) arrayResponse[0];
        responseStatus = (Integer) arrayResponse[1];
        boolean dataExist = false;
        String msgError = "";
        if (responseStatus >= 200 && responseStatus < 300) {
            try {
                dataExist = responseJSONObject.getBoolean("resultOK"); //obtiene los datos del json de respuesta
                msgError  = responseJSONObject.getString ("errorMessage");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (dataExist) {

                responseOk = true;

            } else {

                if (msgError.length() > 5) {
                    errorMessage = msgError;
                } else {
                    errorMessage = ApiConstants.managerErrorServices(Integer.parseInt(msgError), activityOrigin);
                }

            }
        } else if (responseStatus >= 300 && responseStatus < 400) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);
        } else if (responseStatus >= 400 && responseStatus < 500) {

            if (responseStatus == 422) {
                try {
                    dataExist = responseJSONObject.getBoolean("resultOK"); //obtiene los datos del json de respuesta
                    msgError  = responseJSONObject.getString ("errorMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (msgError.length() > 5) {
                    errorMessage = msgError;
                } else {
                    errorMessage = ApiConstants.managerErrorServices(Integer.parseInt(msgError), activityOrigin);
                }

            } else {
                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);
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
                String messageResp = "";
                try {
                    messageResp = responseJSONObject.getString("errorMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FACE_OPERATION, "ok");

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
            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        */
    }

}
