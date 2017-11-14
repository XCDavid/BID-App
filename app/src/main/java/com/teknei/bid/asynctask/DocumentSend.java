package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.DocumentResumeDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.response.ResponseDocument;
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

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentSend extends AsyncTask<String, Void, Void> {

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

    boolean resolution = false;

    private long endTime;

    public DocumentSend(Activity context, String tokenOld, String jsonString, List<File> imageFile) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.jsonS = jsonString;
        this.imageF = imageFile;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog ( activityOrigin, activityOrigin.getString(R.string.message_face_scan_check));
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
        File jsonFile, documentFile;
        if (hasConecction) {
            /*
            try {
                ServerConnectionListImages serverConnection = new ServerConnectionListImages();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(activityOrigin, jsonS, endPoint + ApiConstants.METHOD_DOCUMENT, token, ServerConnection.METHOD_POST, imageF, "");
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
            Log.v("NUM FILES         :", "NUM FILES    :" + imageF.size());
            Log.v("json SEND NO File :", "json no file :" + jsonS);
            Log.v("END POINT         :", "endpoint     :" + endPoint);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            MultipartBody.Part jsonBody =
                    MultipartBody.Part.createFormData("json", imageF.get(0).getName(),
                            RequestBody.create(MediaType.parse("application/json"), imageF.get(0)));

            MultipartBody.Part jsonDoc =
                    MultipartBody.Part.createFormData("file", imageF.get(1).getName(),
                            RequestBody.create (MediaType.parse("image/jpg"), imageF.get(1)));

            Call<ResponseDocument> call = api.enrollmentAddressComprobanteParsed(jsonBody, jsonDoc);

            call.enqueue(new Callback<ResponseDocument>() {

                @Override
                public void onResponse(Call<ResponseDocument> call, Response<ResponseDocument> response) {
                    progressDialog.dismiss();

                    Log.d("Response Message", "complete:" + response.code());

                    if (response.isSuccessful() && response.body()!=null) {

                        ResponseDocument responseDoc = response.body();

                        if (responseDoc.isResultOK()) {

                            String messageResp = "";
                            String messageComplete = "";
                            String jsonResult = "";

                            try {

                                messageComplete = responseDoc.getErrorMessage();
                                String messageSplit[] = messageComplete.split("\\|");
                                messageResp = messageSplit[0];
                                jsonResult  = messageSplit[1];

                                Log.d("Response Message", "complete:" + messageComplete);
                                Log.d("Response Message", "message:" + messageResp);

                                if (messageSplit.length > 1) {
                                    Log.d("Response Message", "json:" + jsonResult);
                                    String name = "";
                                    String apPat = "";
                                    String apMat = "";
                                    String address = "";
                                    String street = "";
                                    String suburb = "";
                                    String zipCode = "";
                                    String locality = "";
                                    String state = "";

                                    JSONObject dataObjectJSON = new JSONObject(jsonResult);

                                    try {
                                        street = dataObjectJSON.getString("street");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        suburb = dataObjectJSON.getString("suburb");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        zipCode = dataObjectJSON.getString("zipCode");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        locality = dataObjectJSON.getString("locality");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        state = dataObjectJSON.getString("state");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (!street.equals("")) {
                                        resolution = true;
                                    }

                                    if (resolution) {
                                        String jsonString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, "{}");
                                        try {
                                            JSONObject jsonData = new JSONObject(jsonString);
                                            jsonData.put("name"  , name);
                                            jsonData.put("appat" , apPat);
                                            jsonData.put("apmat" , apMat);

                                            jsonData.put("street"  , street);
                                            jsonData.put("suburb"  , suburb);
                                            jsonData.put("zipCode" , zipCode);
                                            jsonData.put("locality", locality);
                                            jsonData.put("state"   , state);

                                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.DOCUMENT_OPERATION, jsonData.toString());

                                            Log.d("Response Message", "json:" + jsonData.toString());

                                            DocumentResumeDialog dialogoAlert;
                                            dialogoAlert = new DocumentResumeDialog(activityOrigin);
                                            dialogoAlert.setCancelable(false);
                                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                            dialogoAlert.show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        errorMessage = responseStatus + " - " + "Error al obtener información de la imagen";
                                        AlertDialog dialogoAlert;
                                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                                        dialogoAlert.setCancelable(false);
                                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialogoAlert.show();
                                    }
                                }

                                else {
                                    errorMessage = responseStatus + " - " + messageResp;
                                    AlertDialog dialogoAlert;
                                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                                    dialogoAlert.setCancelable(false);
                                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialogoAlert.show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

                            Log.d("Response Message", "complete:" + response.message().toString());
                            Log.d("Response Message", "complete:" + response.code());

                            AlertDialog dialogoAlert;
                                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), response.message().toString(), ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                                dialogoAlert.setCancelable(false);
                                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialogoAlert.show();
                        }

                    } else {

                        Log.d("Response Message", "complete:" + response.message().toString());
                        Log.d("Response Message", "complete:" + response.code());

                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice),response.message().toString(), ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseDocument> call, Throwable t) {
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

            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
            while (System.currentTimeMillis() < endTime) {
                //espera hasta que pasen los 2 segundos en caso de que halla terminado muy rapido el hilo
            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());

        }
        return null;
    }


    private void manageResponse(Object arrayResponse[]) {
        /*
        responseJSONObject = (JSONObject) arrayResponse[0];
        responseStatus = (Integer) arrayResponse[1];
        boolean dataExist = false;
        String resultString = "";
        int     msgError  = 0;

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
            if (responseStatus == 422) {
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
            resultString = responseJSONObject.optString("resultOK");
            String errorResponse = "";
            if (resultString.equals("false")) {
                errorResponse = responseJSONObject.optString("errorMessage");
            }
            errorMessage = responseStatus + " - " + errorResponse;
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d("RESPUESTA WEB SERVICES", "-----onPostExecute-----");
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {
                String messageResp = "";
                String messageComplete = "";
                String jsonResult = "";

                try {
                    messageComplete = responseJSONObject.getString("errorMessage");
                    String messageSplit[] = messageComplete.split("\\|");
                    messageResp = messageSplit[0];
                    Log.d("Response Message", "complete:" + messageComplete);
                    Log.d("Response Message", "message:" + messageResp);

                    if (messageSplit.length > 1) {
                        Log.d("Response Message", "json:" + jsonResult);
                        String name = "";
                        String apPat = "";
                        String apMat = "";
                        String address = "";
                        String street   = "";
                        String suburb   = "";
                        String zipCode = "";
                        String locality = "";
                        String state    = "";

                            JSONObject respJSON = new JSONObject(jsonResult);
                            JSONObject dataObjectJSON = respJSON.getJSONObject("document");

                            try {
                                name = dataObjectJSON.getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                apPat = dataObjectJSON.getString("firstSurname");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                apMat = dataObjectJSON.getString("secondSurname");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                address = dataObjectJSON.getString("address");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                address = dataObjectJSON.getString("street");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                address = dataObjectJSON.getString("suburb");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                address = dataObjectJSON.getString("zipCode");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                address = dataObjectJSON.getString("locality");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                address = dataObjectJSON.getString("state");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        if (!name.equals("") && !street.equals("")) {
                            resolution = true;
                        }

                        if (resolution) {
                            String jsonString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, "{}");
                            try {
                                JSONObject jsonData = new JSONObject(jsonString);
                                jsonData.put("name", name);
                                jsonData.put("appat", apPat);
                                jsonData.put("apmat", apMat);
                                jsonData.put("address", address);

                                jsonData.put("street", street);
                                jsonData.put("suburb", suburb);
                                jsonData.put("zipCode", zipCode);
                                jsonData.put("locality", locality);
                                jsonData.put("state", state);

                                SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.DOCUMENT_OPERATION, jsonData.toString());

                                DocumentResumeDialog dialogoAlert;
                                dialogoAlert = new DocumentResumeDialog (activityOrigin);
                                dialogoAlert.setCancelable(false);
                                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialogoAlert.show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            errorMessage = responseStatus + " - " + "Error al obtener información de la imagen";
                            AlertDialog dialogoAlert;
                            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                            dialogoAlert.setCancelable(false);
                            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialogoAlert.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("Message Document Send", "Send: " + errorMessage);
                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CONTINUE);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        } else {
            Log.i("Message Document Send", "Send: " + errorMessage);
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
    */
    }

}
