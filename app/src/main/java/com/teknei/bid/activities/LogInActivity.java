package com.teknei.bid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.LogIn;
import com.teknei.bid.utils.SharedPreferencesUtils;

public class LogInActivity extends BaseActivity implements View.OnClickListener {
    Button bLogIn;
    EditText etUser;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_log_in);
        etUser = (EditText) findViewById(R.id.et_user_log_in);
        bLogIn = (Button) findViewById(R.id.b_login);
        bLogIn.setOnClickListener(this);

        saveSharedPreferenceByDefault();

        //Borrar
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.OPERATION_ID);
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
        if (!user.equals("")) {
            return true;
        } else {
            Toast.makeText(LogInActivity.this, "Ingresa un usuario para poder continuar", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void sendPetition() {
//        super.sendPetition();
        new LogIn(LogInActivity.this, "", "").execute();
    }

    @Override
    public void goNext() {
//        super.goNext();
        Intent i = new Intent(LogInActivity.this, FormActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void saveSharedPreferenceByDefault(){
        String urlIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_ID_SCAN, getString(R.string.default_url_id_scan));
        String licenseIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.LICENSE_ID_SCAN, getString(R.string.default_license_id_scan));
        String urlTeknei = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_TEKNEI, getString(R.string.default_url_teknei));
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this,SharedPreferencesUtils.URL_ID_SCAN,urlIdScan);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this,SharedPreferencesUtils.LICENSE_ID_SCAN,licenseIdScan);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this,SharedPreferencesUtils.URL_TEKNEI,urlTeknei);

    }

}
