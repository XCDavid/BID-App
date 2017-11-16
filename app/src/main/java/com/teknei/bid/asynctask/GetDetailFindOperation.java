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
import com.teknei.bid.response.ResponseDetail;
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

public class GetDetailFindOperation extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = getClass().getSimpleName();

    private String  token;
    private int     stepOperation;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;
    private String curp;

    private ResponseDetail responseDetail;

    public GetDetailFindOperation(Activity context, String tokenOld, int step,String curp) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.stepOperation = step;
        this.curp = curp;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog (activityOrigin, activityOrigin.getString(R.string.message_get_detail_operation));
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
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("curp", curp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ServerConnection serverConnection = new ServerConnection();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(activityOrigin, jsonObject.toString(), endPoint + ApiConstants.METHOD_GET_PENDING_OPERATION, token, ServerConnection.METHOD_POST,null,"");

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

            SearchDTO searchDTO = new SearchDTO();

            searchDTO.setCurp(curp);

            BIDEndPointServices api = RetrofitSingleton.getInstance().build(endPoint).create(BIDEndPointServices.class);

            Call<ResponseDetail> call = api.enrollmentClientDetail(token, searchDTO);

            call.enqueue(new Callback<ResponseDetail>() {

                @Override
                public void onResponse(Call<ResponseDetail> call, Response<ResponseDetail> response) {
                    progressDialog.dismiss();

                    Log.d(CLASS_NAME, response.code() + " ");

                    responseStatus = response.code();

                    if (responseStatus >= 200 && responseStatus < 300) {
                            try {
                                responseDetail    = response.body();
                                String jsonString = SharedPreferencesUtils.readFromPreferencesString
                                                    (activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, "{}");

                                try {

                                    JSONObject jsonData = new JSONObject(jsonString);
                                    jsonData.put("name" , responseDetail.getName());
                                    jsonData.put("appat", responseDetail.getFatherLastName());
                                    jsonData.put("apmat", responseDetail.getMotherLastName());
                                    if (!curp.equals(""))
                                        jsonData.put("curp", responseDetail.getCapturedCurp());
                                    jsonData.put("mrz", responseDetail.getMrz());
                                    jsonData.put("ocr", responseDetail.getOcr());
                                    jsonData.put("address", responseDetail.getAddress());
                                    jsonData.put("validity", responseDetail.getVig());
                                    SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, jsonData.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                switch (stepOperation){
                                    case 1:
                                        //Del paso 2 en adelante hay que almacenar
                                        // 1.- el curp                                          - Ok Paso 1 ( Formulario ok -> continuo en paso 2 'Id Fotos' )
                                        // 2.- el dato SCAN_SAVE_ID de SharedPreferences        - Ok Paso 2 ( Id fotos   ok -> continuo en paso 3 'Foto face' )
                                        // 3.- el dato FACE_OPERATION de SharedPreferences      - Ok Paso 3 ( Foto face  ok -> continuo en paso 4 'Foto doc' )
                                        // 4.- el dato DOCUMENT_OPERATION de SharedPreferences  - Ok Paso 4 ( Foto doc   ok -> continuo en paso 5 'Fingerprints' )
                                        // 5.- el dato FINGERS_OPERATION de SharedPreferences   - Ok Paso 4 (Fingerprints ok -> continuo en paso 5.1 'Foto doc' )
                                        // 5.1- el dato PAY_OPERATION de SharedPreferences      - ( Pantalla Fake INE ) Es parte del paso 5 Fingerprints -> continuo en paso 6 'Ok y Firma' al terminar muestra la pantalla de operación exitosa y firma de contrato
                                        // No hay mas datos almacenados, solo hay que firmar contrato y enviarlo al server para terminar.

                                        // 1.
                                        JSONObject jsonObject2 = new JSONObject();
                                        try {
                                            jsonObject2.put("curp", curp);
                                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject2.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case 2:
                                        // 1.
                                        JSONObject jsonObject3 = new JSONObject();
                                        try {
                                            jsonObject3.put("curp", curp);
                                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject3.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        // 2.
                                        String scanAUX3 = "okCredentials";
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX3);
                                        break;
                                    case 3:
                                        // 1.
                                        JSONObject jsonObject4 = new JSONObject();
                                        try {
                                            jsonObject4.put("curp", curp);
                                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject4.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        // 2.
                                        String scanAUX4 = "okCredentials";
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX4);
                                        // 3.
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FACE_OPERATION, "ok");
                                        break;
                                    case 4:
                                        // 1.
                                        JSONObject jsonObject5 = new JSONObject();
                                        try {
                                            jsonObject5.put("curp", curp);
                                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject5.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        // 2.
                                        String scanAUX5 = "okCredentials";
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX5);
                                        // 3.
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FACE_OPERATION, "ok");
                                        // 4.
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.DOCUMENT_OPERATION, "ok");
                                        break;
                                    case 5:
                                        // 1.
                                        JSONObject jsonObject6 = new JSONObject();
                                        try {
                                            jsonObject6.put("curp", curp);
                                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject6.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        // 2.
                                        String scanAUX6 = "okCredentials";
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX6);
                                        // 3.
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FACE_OPERATION, "ok");
                                        // 4.
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.DOCUMENT_OPERATION, "ok");
                                        // 5.
                                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FINGERS_OPERATION, "ok");
                                        break;
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (stepOperation>0) { //La operacion se recupero -> continua en el paso necesario

                                String messageResp = "La operación se recupero correctamente, seras posicionado en el último paso que quedo pendiente.";
                                AlertDialog dialogoAlert;
                                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), messageResp, ApiConstants.ACTION_GO_STEP,stepOperation);
                                dialogoAlert.setCancelable(false);
                                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                dialogoAlert.show();

                            } else {
                                ((BaseActivity) activityOrigin).sendPetition();
                            }

                        } else {

                            if (responseStatus >= 300 && responseStatus < 400) {

                                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);

                            } else if (responseStatus >= 400 && responseStatus < 500) {

                                String errorResponse = "";
                                errorResponse = activityOrigin.getString(R.string.message_ws_response_400);

                            if (responseStatus == 422){

                                errorResponse   = response.code() + response.message();

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
                public void onFailure(Call<ResponseDetail> call, Throwable t) {
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
//        responseJSONObject = null;
//        responseStatus = 200;
        boolean dataExist = false;
        if (responseStatus >= 200 && responseStatus < 300) {
            try {
                String name = responseJSONObject.getString("nombre");
                String apPat = responseJSONObject.getString("apellidoPaterno");
                String apMat = responseJSONObject.getString("apellidoMaterno");
                String curp = responseJSONObject.getString("curpCapturado");
                String curpI = responseJSONObject.getString("curpIdentificado");
                String address = responseJSONObject.getString("direccion");
                String scanId = responseJSONObject.getString("scanId");
                String documentManagerId = responseJSONObject.getString("documentManagerId");

                String mrz = responseJSONObject.getString("mrz");
                String ocr = responseJSONObject.getString("ocr");
                String validity = responseJSONObject.getString("vig");
                dataExist = true;

//                i.	{“nombre” : “Jorge” , “apellidoPaterno” : “amaro” , “apellidoMaterno” : “coria” , “curpCapturado”  :”abc” ,
//                “curpIdentificado” : “AACJ…”, “dirección” : “aabbcc”, “scanId” : “aahh-sjj”, “documentManagerId” : “00990090”}
                String jsonString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, "{}");
                try {
                    JSONObject jsonData = new JSONObject(jsonString);
                    jsonData.put("name", name);
                    jsonData.put("appat", apPat);
                    jsonData.put("apmat", apMat);
                    if (!curp.equals(""))
                        jsonData.put("curp", curp);
                    jsonData.put("mrz", mrz);
                    jsonData.put("ocr", ocr);
                    jsonData.put("address", address);
                    jsonData.put("validity", validity);
                    SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, jsonData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                switch (stepOperation){
                    case 1:
                        //Del paso 2 en adelante hay que almacenar
                        // 1.- el curp                                          - Ok Paso 1 ( Formulario ok -> continuo en paso 2 'Id Fotos' )
                        // 2.- el dato SCAN_SAVE_ID de SharedPreferences        - Ok Paso 2 ( Id fotos   ok -> continuo en paso 3 'Foto face' )
                        // 3.- el dato FACE_OPERATION de SharedPreferences      - Ok Paso 3 ( Foto face  ok -> continuo en paso 4 'Foto doc' )
                        // 4.- el dato DOCUMENT_OPERATION de SharedPreferences  - Ok Paso 4 ( Foto doc   ok -> continuo en paso 5 'Fingerprints' )
                        // 5.- el dato FINGERS_OPERATION de SharedPreferences   - Ok Paso 4 (Fingerprints ok -> continuo en paso 5.1 'Foto doc' )
                        // 5.1- el dato PAY_OPERATION de SharedPreferences      - ( Pantalla Fake INE ) Es parte del paso 5 Fingerprints -> continuo en paso 6 'Ok y Firma' al terminar muestra la pantalla de operación exitosa y firma de contrato
                        // No hay mas datos almacenados, solo hay que firmar contrato y enviarlo al server para terminar.

                        // 1.
                        JSONObject jsonObject2 = new JSONObject();
                        try {
                            jsonObject2.put("curp", curp);
                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject2.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        // 1.
                        JSONObject jsonObject3 = new JSONObject();
                        try {
                            jsonObject3.put("curp", curp);
                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject3.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 2.
                        String scanAUX3 = "okCredentials";
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX3);
                        break;
                    case 3:
                        // 1.
                        JSONObject jsonObject4 = new JSONObject();
                        try {
                            jsonObject4.put("curp", curp);
                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject4.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 2.
                        String scanAUX4 = "okCredentials";
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX4);
                        // 3.
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FACE_OPERATION, "ok");
                        break;
                    case 4:
                        // 1.
                        JSONObject jsonObject5 = new JSONObject();
                        try {
                            jsonObject5.put("curp", curp);
                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject5.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 2.
                        String scanAUX5 = "okCredentials";
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX5);
                        // 3.
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FACE_OPERATION, "ok");
                        // 4.
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.DOCUMENT_OPERATION, "ok");
                        break;
                    case 5:
                        // 1.
                        JSONObject jsonObject6 = new JSONObject();
                        try {
                            jsonObject6.put("curp", curp);
                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject6.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // 2.
                        String scanAUX6 = "okCredentials";
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX6);
                        // 3.
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FACE_OPERATION, "ok");
                        // 4.
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.DOCUMENT_OPERATION, "ok");
                        // 5.
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.FINGERS_OPERATION, "ok");
                        break;
                }


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
        /*
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {

                if (stepOperation>0) { //La operacion se recupero -> continua en el paso necesario
                    String messageResp = "La operación se recupero correctamente, seras posicionado en el último paso que quedo pendiente.";
                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), messageResp, ApiConstants.ACTION_GO_STEP,stepOperation);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();
                }else{
                    ((BaseActivity) activityOrigin).sendPetition();
                }
            } else {
                Log.i(CLASS_NAME, "logout: " + errorMessage);
                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        } else {
            Log.i(CLASS_NAME, "logout: " + errorMessage);
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }*/
    }
}
