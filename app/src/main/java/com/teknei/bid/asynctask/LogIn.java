package com.teknei.bid.asynctask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.activities.LogInActivity;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.response.OAuthAccessToken;
import com.teknei.bid.response.ResponseDetailMe;
import com.teknei.bid.services.OAuthApi;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;
import com.teknei.bid.ws.ServerConnection;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogIn extends AsyncTask<String, Void, Void> {

    private final String CLASS_NAME = getClass().getSimpleName();

    private String newToken;
    private String token;
    private String authorization;

    private String userToCheck;
    private String passToCheck;

    private Activity activityOrigin;
    private JSONObject responseJSONObject;
    private String errorMessage;
    private boolean responseOk = false;
    private ProgressDialog progressDialog;

    private boolean hasConecction = false;
    private Integer responseStatus = 0;

    private long endTime;

    private OAuthApi api;
    private OAuthAccessToken accessToken;

    public LogIn(Activity context, String userString,String passString, String tokenOld, String autho) {
        this.activityOrigin = context;
        this.userToCheck    = userString;
        this.passToCheck    = passString;
        this.token          = tokenOld;
        this.authorization  = autho;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(
                activityOrigin,
                activityOrigin.getString(R.string.get_user_log_in));
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
        endTime = System.currentTimeMillis() + 1000;
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
                String endPoint = SharedPreferencesUtils.readFromPreferencesString(activityOrigin,SharedPreferencesUtils.URL_TEKNEI, activityOrigin.getString(R.string.default_url_teknei));
                Object arrayResponse[] = serverConnection.connection(activityOrigin, null, endPoint + ApiConstants.LOG_IN_USER , token, ServerConnection.METHOD_GET,null,authorization);
                if (arrayResponse[1] != null) {
                    manageResponse(arrayResponse);
                } else {
                    errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = activityOrigin.getString(R.string.message_ws_petition_fail);
            }*/

            Log.i("Wait", "timer after DO: " + System.currentTimeMillis());
            while (System.currentTimeMillis() < endTime) {
                //espera hasta que pasen 1 segundo en caso de que halla terminado muy rapido el hilo
            }
            Log.i("Wait", "timer finish : " + System.currentTimeMillis());

            String urlAuthAccess   = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_AUTHACCESS, activityOrigin.getString(R.string.default_url_oauthaccess));

            Log.v("END POINT         :", "endpoint     :" + urlAuthAccess);

            api = RetrofitSingleton.getInstance().build(urlAuthAccess).create(OAuthApi.class);

            Call<OAuthAccessToken> call = api.getAccessTokenByPassword(userToCheck, passToCheck);

            call.enqueue(new Callback<OAuthAccessToken>() {

                @Override
                public void onResponse(Call<OAuthAccessToken> call, Response<OAuthAccessToken> response) {

                    if (response.isSuccessful()) {

                        accessToken = response.body();

                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TOKEN_APP,"bearer "+accessToken.getAccessToken());
                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.USERNAME,userToCheck);

                        obteinDataCustomer();

                    } else {
                        progressDialog.dismiss();

                        String errorMessage = "Error al autenticar verifique datos de usuario y contraseña";

                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();

                    }
                }

                @Override
                public void onFailure(Call<OAuthAccessToken> call, Throwable t) {

                    progressDialog.dismiss();

                    t.printStackTrace();

                    String errorMessage = "Error al conectarse con servidor, verifique conexión";

                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();
                }
            });
        }
        return null;
    }

    private void manageResponse(Object arrayResponse[]) {
        /*
        responseJSONObject = (JSONObject) arrayResponse[0];
        responseStatus = (Integer) arrayResponse[1];
        String tokenGet = null;
        if (responseStatus >= 200 && responseStatus < 300) {
            try {
                tokenGet = responseJSONObject.getString("token"); //obtiene los datos del json de respuesta
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (tokenGet != null && !tokenGet.equals("")) {
                responseOk = true;
            } else {
                errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_petition_fail);
            }
        } else if (responseStatus >= 300 && responseStatus < 400) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_300);
        } else if (responseStatus >= 400 && responseStatus < 500) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_400);
        } else if (responseStatus >= 500 && responseStatus < 600) {
            errorMessage = responseStatus + " - " + activityOrigin.getString(R.string.message_ws_response_500);
        }
        */
    }

    public void obteinDataCustomer () {
        String urlAuthAccess   = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.URL_AUTHACCESS, activityOrigin.getString(R.string.default_url_oauthaccess));
        String token           = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TOKEN_APP, "");

        Log.v("END POINT         :", "endpoint     :" + urlAuthAccess);

        api = RetrofitSingleton.getInstance().build(urlAuthAccess).create(OAuthApi.class);

        Call<ResponseDetailMe> call = api.getOwnerInfo(token);

        call.enqueue(new Callback<ResponseDetailMe>() {

            @Override
            public void onResponse(Call<ResponseDetailMe> call, Response<ResponseDetailMe> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {

                    ResponseDetailMe responseCustomer = response.body();

                    if (responseCustomer.isAuthenticated()) {

                        Log.d(CLASS_NAME,responseCustomer.getClientId()+"");

                        String idClient =  responseCustomer.getClientId()+"";

                        SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.ID_CLIENT, idClient);

                        ((BaseActivity) activityOrigin).goNext();

                    } else {

                        String errorMessage = "Error al obtener datos de usuario";

                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice),
                                errorMessage, ApiConstants.ACTION_CANCEL_OPERATION);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();

                    }

                } else {

                    String errorMessage = "Error al obtener datos de usuario";

                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage,
                            ApiConstants.ACTION_CANCEL_OPERATION);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();

                }
            }

            @Override
            public void onFailure(Call<ResponseDetailMe> call, Throwable t) {
                progressDialog.dismiss();

                t.printStackTrace();

                String errorMessage = "Error al conectarse con servidor, verifique conexión";

                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        });
    }

    @Override
    protected void onPostExecute(Void result) {
        /*
        progressDialog.dismiss();
        if (hasConecction) {
            if (responseOk) {
                String tokenGet = "";
                try {
                    tokenGet = responseJSONObject.getString("token");
                    SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.TOKEN_APP,tokenGet);
                    SharedPreferencesUtils.saveToPreferencesString(activityOrigin,SharedPreferencesUtils.USERNAME,userToCheck);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((BaseActivity) activityOrigin).goNext();
            } else {
                AlertDialog dialogoAlert;
                dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                dialogoAlert.setCancelable(false);
                dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogoAlert.show();
            }
        } else {
            errorMessage = activityOrigin.getString(R.string.message_ws_no_internet);
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(activityOrigin, activityOrigin.getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        */
    }
}
