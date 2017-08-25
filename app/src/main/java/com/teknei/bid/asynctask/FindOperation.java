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
                activityOrigin.getString(R.string.message_start_operation));
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
//                Object arrayResponse[] = serverConnection.connection(activityOrigin, null, endPoint + ApiConstants.METHOD_START_OPERATION, token, ServerConnection.METHOD_POST,null,"");
//                if (arrayResponse[1] != null) {
                    manageResponse(null);
//                } else {
//                    errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
//                }
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
//        responseJSONObject = (JSONObject) arrayResponse[0];
//        responseStatus = (Integer) arrayResponse[1];
        responseJSONObject = null;
        responseStatus = 200;
        boolean dataExist = false;
        String resultString = "";
        int operationLevel = 0;
        String operationId = "";
        if (responseStatus >= 200 && responseStatus < 300) {
            try {
//                dataExist = responseJSONObject.getBoolean("resultOK"); //Descomentar
//                operationLevel = responseJSONObject.getInt("operationLevel");//Descomentar
//                operationId = responseJSONObject.getString("operationId");//Descomentar
                dataExist = true;
                operationLevel = Integer.valueOf(curp);
                operationId="123";
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.OPERATION_ID, operationId);
                stepOperation = operationLevel;
                switch (stepOperation){
                    case 1:
                        //AQUI VA SE VAN A RECIORRER LAS OPCIONES EN CASO DE QUE ME ENVIE SU PROPIA NUMERACION Y EMPIEZE A CONTAR DESDE EL UNO COMO EL PASO PARA TOMAR LAS FOTOS DE LA CREDENCIAL

//                        //Datos a almacenar:
//                        JSONObject jsonObject = new JSONObject();
//                        try {
////                            jsonObject.put("deviceId", phoneID);
////                            jsonObject.put("employee", employee);
//                            jsonObject.put("curp", curp);
////                            jsonObject.put("email", mail);
////                            jsonObject.put("nombre", name);
////                            jsonObject.put("primerApellido", app1);
////                            jsonObject.put("segundoApellido", app2);
////                            jsonObject.put("telefono", phone);
////                            jsonObject.put("refContrato", numContract);
//                            //***Almacena Json con los datos del formulario
//
//                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject.toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        break;
                    case 2:
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
                    case 3:
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
                    case 4:
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
                    case 5:
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
                    case 6:
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
                errorMessage = activityOrigin.getString(R.string.message_ws_response_fail);
            }
        } else if (responseStatus >= 300 && responseStatus < 400) {
            errorMessage = activityOrigin.getString(R.string.message_ws_response_300);
        } else if (responseStatus >= 400 && responseStatus < 500) {
//            errorMessage = activityOrigin.getString(R.string.message_ws_response_400);
//            resultString = responseJSONObject.optString("resultOK");
            String errorResponse = "";
//            if (resultString.equals("false")) {
//                errorResponse = responseJSONObject.optString("errorMessage");
//            }
            if (responseStatus == 422){
                errorResponse = responseJSONObject.optString("errorMessage");
            }

            errorMessage = responseStatus + " - " + errorResponse;
        } else if (responseStatus >= 500 && responseStatus < 600) {
            errorMessage = activityOrigin.getString(R.string.message_ws_response_500);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {

                if (stepOperation>1) { //La operacion se recupero -> continua en el paso necesario
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
    }

}
