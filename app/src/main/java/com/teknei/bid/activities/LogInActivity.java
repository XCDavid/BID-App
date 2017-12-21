package com.teknei.bid.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.integratedbiometrics.ibscanultimate.IBScan;
import com.integratedbiometrics.ibscanultimate.IBScanDevice;
import com.integratedbiometrics.ibscanultimate.IBScanDeviceListener;
import com.integratedbiometrics.ibscanultimate.IBScanException;
import com.integratedbiometrics.ibscanultimate.IBScanListener;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.LogIn;
import com.teknei.bid.asynctask.StartOperation;
import com.teknei.bid.response.OAuthAccessToken;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.response.ResponseDetailMe;
import com.teknei.bid.services.OAuthApi;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import android.provider.Settings.Secure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import mx.com.morpho.watson_mini.AppState;
import mx.com.morpho.watson_mini.PlaySound;
import mx.com.morpho.watson_mini.WatsonMiniData;
import mx.com.morpho.watson_mini.WatsonMiniHelper;
import mx.com.morpho.watson_mini.WatsonMiniListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType.FLAT_SINGLE_FINGER;
import static com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType.FLAT_TWO_FINGERS;
import static mx.com.morpho.watson_mini.AppState.INITIALIZED;
import static mx.com.morpho.watson_mini.AppState.SCANNER_ATTACHED;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITIES_COUNT;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITY_FAIR_COLOR;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITY_GOOD_COLOR;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITY_NOT_PRESENT_COLOR;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITY_POOR_COLOR;
import static mx.com.morpho.watson_mini.Constants.INITIALIZING_DEVICE_INDEX;
import static mx.com.morpho.watson_mini.Constants.STOPPING_CAPTURE_DELAY_MILLIS;
import static mx.com.morpho.watson_mini.WatsonMiniHelper.drawBitmapRollingLine;

public class LogInActivity extends BaseActivity implements View.OnClickListener {

    Button   bLogIn;
    EditText etUser;
    EditText etPass;
    String   user;
    String   pass;

    private OAuthApi api;
    private OAuthAccessToken accessToken;

    private static final String TAG = "LogInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String uuid = Secure.getString(this.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

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
        }
        else {
            new LogIn(LogInActivity.this, user, pass, "", "").execute();
        }
    }

    @Override
    public void goNext() {
        String opcionFingerprintReader = SharedPreferencesUtils.readFromPreferencesString(LogInActivity.this, SharedPreferencesUtils.FINGERPRINT_READER, "");

        if (opcionFingerprintReader.equals("watson")){

            Intent i = new Intent(LogInActivity.this, LoginFingerWatsonActivity.class);
            startActivity(i);

        } else if (opcionFingerprintReader.equals("biosmart")) {

            Intent i = new Intent(LogInActivity.this, LoginFingerBioSmartActivity.class);
            startActivity(i);

        } else {

            Intent i = new Intent(LogInActivity.this, LoginFingerMSOActivity.class);
            startActivity(i);
        }
    }

    public void saveSharedPreferenceByDefault() {
        String urlIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_ID_SCAN, getString(R.string.default_url_id_scan));
        String licenseIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.LICENSE_ID_SCAN, getString(R.string.default_license_id_scan));
        String urlTeknei = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_TEKNEI, getString(R.string.default_url_teknei));
        String urlMobbSign = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_MOBBSIGN, getString(R.string.default_url_mobbsign));
        String licenseMobbSign = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.MOBBSIGN_LICENSE, getString(R.string.default_license_mobbsign));
        String urlAuthAccess   = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_AUTHACCESS, getString(R.string.default_url_oauthaccess));
        String fingerprintReader = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.FINGERPRINT_READER, getString(R.string.default_fingerprint_reader));

        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_ID_SCAN, urlIdScan);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.LICENSE_ID_SCAN, licenseIdScan);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_TEKNEI, urlTeknei);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_MOBBSIGN, urlMobbSign);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.MOBBSIGN_LICENSE, licenseMobbSign);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_AUTHACCESS, urlAuthAccess);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.FINGERPRINT_READER, fingerprintReader);
    }
}
