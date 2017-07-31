package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.IdScanActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.ServerConnection;
import com.teknei.bid.ws.ServerConnectionListImages;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class CredentialsCaptured extends AsyncTask<String, Void, Void> {
    //    private String newToken;
    private String token;
    private String jsonS;
    private List<File> jsonF;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    public CredentialsCaptured(Activity context, String tokenOld, String jsonString, List<File> jsonFile) {
        this.activityOrigin = context;
        this.token = tokenOld;
        this.jsonS = jsonString;
        this.jsonF = jsonFile;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(
                activityOrigin,
                activityOrigin.getString(R.string.message_credentials_captured));
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
            try {
                ServerConnectionListImages serverConnection = new ServerConnectionListImages();
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(activityOrigin, jsonS, endPoint + ApiConstants.METHOD_CREDENTIALS, token, ServerConnection.METHOD_POST, jsonF, "");
                if (arrayResponse[1] != null) {
                    manageResponse(arrayResponse);
                } else {
                    errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
                }
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
        responseJSONObject = (JSONObject) arrayResponse[0];
        responseStatus = (Integer) arrayResponse[1];
        boolean dataExist = false;
        if (responseStatus >= 200 && responseStatus < 300) {
            try {
                dataExist = responseJSONObject.getBoolean("resultOK"); //obtiene los datos del json de respuesta
            } catch (JSONException e) {
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
            errorMessage = activityOrigin.getString(R.string.message_ws_response_400);
        } else if (responseStatus >= 500 && responseStatus < 600) {
            errorMessage = activityOrigin.getString(R.string.message_ws_response_500);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.dismiss();
        //BORRAR
        String scanAUX1 = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.ID_SCAN, "666");
        SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX1);

        if (hasConecction) {
            if (responseOk) {
                String messageComplete = "";
                String messageResp = "";
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
                        String mrz = "";
                        String ocr = "";
                        String validity = "";
                        String curp = "";
                        jsonResult = messageSplit[1];
                        if (jsonF.size() == 1) {
                            //MOBBSCAN Unicamente INE *************************************************************************
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
                                mrz = dataObjectJSON.getString("mrz");
                                Log.w("MRZ","MRZ : "+mrz);
//                                String mrz = "IDMEX1587903166<<4499068496638\\n8512246M2712310MEX<02<<12416<4\\nHERNANDEZ<ERAZO<<MONICA<<<<<<<";
                                String uno = "\\\n";
                                String dos = "\\n";
                                String tres = "\n";
                                String cuatro = "\\\\n";
                                String mrzSplit1[] = mrz.split(uno);
                                String mrzSplit2[] = mrz.split(dos);
                                String mrzSplit3 ="";
                                if (mrz.length()>31) {
                                    mrzSplit3 = mrz.substring(0, 30);
                                }

                                String mrzSplit4[] = mrz.split(cuatro);
                                Log.w("MRZ split","MRZ split 1: "+mrzSplit1.toString());
                                Log.w("MRZ split","MRZ split 2: "+mrzSplit2.toString());
                                Log.w("MRZ split","MRZ split 3: "+mrzSplit3);
                                Log.w("MRZ split","MRZ split 4: "+mrzSplit4.toString());

                                String firstLine = mrzSplit4[0];
                                String firstSplit[] = firstLine.split("\\<\\<");
                                String firstSplit2[] = mrzSplit3.split("\\<\\<");
//                String ocr;
                                if (firstSplit.length > 1) {
//                                    String ocr = firstSplit[1];
                                    Log.w("MRZ OCR","OCR: "+ocr);
                                }
                                if (firstSplit2.length > 1) {
                                    ocr = firstSplit2[1];
                                    Log.w("MRZ OCR 2","OCR 2: "+ocr);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                validity = dataObjectJSON.getString("dateOfExpiry");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            try {
                                //Muchas veces el curp es incorrecto
                                //curp = dataObjectJSON.getString("curp");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        }
                        if (jsonF.size() == 3) {
                            //ICAR Unicamente INE *************************************************************************
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
                                mrz = dataObjectJSON.getString("MRZ");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                validity = dataObjectJSON.getString("vigencia");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                curp = dataObjectJSON.getString("curp");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //***Contruye el json con datos que no obtiene MobbScan Falta comprobar Icar
                        String jsonString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, "{}");

                        try {
                            JSONObject jsonData = new JSONObject(jsonString);
                            jsonData.put("name", name );
                            jsonData.put("appat", apPat);
                            jsonData.put("apmat", apMat );
                            if (!curp.equals(""))
                                jsonData.put("curp", curp);
                            jsonData.put("mrz", mrz);
                            jsonData.put("ocr", ocr);
                            jsonData.put("address", address);
                            jsonData.put("validity", validity );
                            SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, jsonData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String scanAUX = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.ID_SCAN, "");
                SharedPreferencesUtils.saveToPreferencesString(activityOrigin, SharedPreferencesUtils.SCAN_SAVE_ID, scanAUX);

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
    }

}