package com.teknei.bid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teknei.bid.R;
import com.teknei.bid.response.OAuthAccessToken;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.services.OAuthApi;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import android.provider.Settings.Secure;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends BaseActivity implements View.OnClickListener {
    Button bLogIn;
    EditText etUser;
    EditText etPass;
    String user;
    String pass;

    private OAuthApi api;
    private OAuthAccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String uuid = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        Log.d("LogInActivity--",uuid);

        setContentView(R.layout.activity_log_in);
        etUser = (EditText) findViewById(R.id.et_user_log_in);
        etPass = (EditText) findViewById(R.id.et_pass_log_in);
        bLogIn = (Button) findViewById(R.id.b_login);
        bLogIn.setOnClickListener(this);

        SharedPreferencesUtils.cleanSharedPreferencesOperation(this);
        saveSharedPreferenceByDefault();

        SharedPreferencesUtils.saveToPreferencesString(this,SharedPreferencesUtils.ID_ENTERPRICE,"1");
        SharedPreferencesUtils.saveToPreferencesString(this,SharedPreferencesUtils.CUSTOMER_TYPE,"2");
        SharedPreferencesUtils.saveToPreferencesString(this,SharedPreferencesUtils.ID_DEVICE,uuid);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_login:
                if (validateData()) {
                    sendPetition();
                }
                break;
        }
    }

    public boolean validateData() {
        user = etUser.getText().toString();
        pass = etPass.getText().toString();
        if (!user.equals("")) {
            if (!pass.equals("")) {
                return true;
            } else {
                Toast.makeText(LogInActivity.this, "Ingresa una contraseña para poder continuar", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(LogInActivity.this, "Ingresa un usuario para poder continuar", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void sendPetition() {
        String authorization = Base64.encodeToString(new String(user + ":" + pass).getBytes(), Base64.DEFAULT);
        if (user.equals("admin")) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }else{
            //new LogIn(LogInActivity.this, user, pass, "", authorization).execute();

            String urlAuthAccess   = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_AUTHACCESS, getString(R.string.default_url_oauthaccess));

            Log.d("................",urlAuthAccess);

            api = RetrofitSingleton.getInstance().build(urlAuthAccess).create(OAuthApi.class);

            Call<OAuthAccessToken> call = api.getAccessTokenByPassword(user, pass);

            call.enqueue(new Callback<OAuthAccessToken>() {

                @Override
                public void onResponse(Call<OAuthAccessToken> call, Response<OAuthAccessToken> response) {

                    Log.d("................", "onResponse");

                    if (response.isSuccessful()) {

                        accessToken = response.body();

                        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this,SharedPreferencesUtils.TOKEN_APP,"bearer "+accessToken.getAccessToken());
                        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this,SharedPreferencesUtils.USERNAME,user);

                        goNext();
                    } else {

                        String errorMessage = "Error al autenticar verifique datos de usuario y contraseña";

                        AlertDialog dialogoAlert;
                        dialogoAlert = new AlertDialog(LogInActivity.this, getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                        dialogoAlert.setCancelable(false);
                        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogoAlert.show();

                    }
                }

                @Override
                public void onFailure(Call<OAuthAccessToken> call, Throwable t) {

                    Log.d("................", "onFailure");

                    t.printStackTrace();

                    String errorMessage = "Error al conectarse con servidor, verifique conexión";

                    AlertDialog dialogoAlert;
                    dialogoAlert = new AlertDialog(LogInActivity.this, getString(R.string.message_ws_notice), errorMessage, ApiConstants.ACTION_TRY_AGAIN_CANCEL);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();
                }
            });
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(LogInActivity.this, FormActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void saveSharedPreferenceByDefault() {
        String urlIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_ID_SCAN, getString(R.string.default_url_id_scan));
        String licenseIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.LICENSE_ID_SCAN, getString(R.string.default_license_id_scan));
        String urlTeknei = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_TEKNEI, getString(R.string.default_url_teknei));
        String urlMobbSign = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_MOBBSIGN, getString(R.string.default_url_mobbsign));
        String licenseMobbSign = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.MOBBSIGN_LICENSE, getString(R.string.default_license_mobbsign));
        String urlAuthAccess   = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_AUTHACCESS, getString(R.string.default_url_oauthaccess));
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_ID_SCAN, urlIdScan);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.LICENSE_ID_SCAN, licenseIdScan);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_TEKNEI, urlTeknei);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_MOBBSIGN, urlMobbSign);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.MOBBSIGN_LICENSE, licenseMobbSign);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_AUTHACCESS, urlAuthAccess);
    }

}
