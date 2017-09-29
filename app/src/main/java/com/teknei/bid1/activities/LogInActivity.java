package com.teknei.bid1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teknei.bid1.R;
import com.teknei.bid1.asynctask.LogIn;
import com.teknei.bid1.asynctask.StartOperation;
import com.teknei.bid1.utils.PhoneSimUtils;
import com.teknei.bid1.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class LogInActivity extends BaseActivity implements View.OnClickListener {
    Button bLogIn;
    EditText etUser;
    EditText etPass;
    String user;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_log_in);
        etUser = (EditText) findViewById(R.id.et_user_log_in);
        etPass = (EditText) findViewById(R.id.et_pass_log_in);
        bLogIn = (Button) findViewById(R.id.b_login);
        bLogIn.setOnClickListener(this);

        SharedPreferencesUtils.cleanSharedPreferencesOperation(this);
        saveSharedPreferenceByDefault();
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
            new LogIn(LogInActivity.this, user, pass, "", authorization).execute();
        }
    }

    @Override
    public void goNext() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String operationID = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");
        if (operationID.equals("")) {
            String jsonString = buildJSON();
            new StartOperation(LogInActivity.this, token, jsonString).execute();
//            new FindOperation(FormActivity.this, token, etCurp.getText().toString()).execute();
        }
    }

    public void saveSharedPreferenceByDefault() {
        String urlIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_ID_SCAN, getString(R.string.default_url_id_scan));
        String licenseIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.LICENSE_ID_SCAN, getString(R.string.default_license_id_scan));
        String urlTeknei = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_TEKNEI, getString(R.string.default_url_teknei));
        String urlMobbSign = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_MOBBSIGN, getString(R.string.default_url_mobbsign));
        String licenseMobbSign = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.MOBBSIGN_LICENSE, getString(R.string.default_license_mobbsign));
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_ID_SCAN, urlIdScan);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.LICENSE_ID_SCAN, licenseIdScan);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_TEKNEI, urlTeknei);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.URL_MOBBSIGN, urlMobbSign);
        SharedPreferencesUtils.saveToPreferencesString(LogInActivity.this, SharedPreferencesUtils.MOBBSIGN_LICENSE, licenseMobbSign);
    }

    public String buildJSON() {
        String name  = "NOMBRE";
        String app1  = "AP1";
        String app2  = "AP2";
        String curp  = randomCURP() + "";
        String mail  = "MAIL";
        String phone = "PHONE";
        String numContract = "123";
        String phoneID = PhoneSimUtils.getImei(this);

        String employee = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.USERNAME, "default");

        //Construimos el JSON con los datos del formulario

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("deviceId", phoneID);
            jsonObject.put("employee", employee);
            jsonObject.put("curp", curp);
            jsonObject.put("email", mail);
            jsonObject.put("nombre", name);
            jsonObject.put("primerApellido", app1);
            jsonObject.put("segundoApellido", app2);
            jsonObject.put("telefono", phone);
            jsonObject.put("refContrato", numContract);
            //***Almacena Json con los datos del formulario
            SharedPreferencesUtils.saveToPreferencesString(this, SharedPreferencesUtils.JSON_INIT_FORM, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public int randomCURP () {
        int dig7 = new Random().nextInt(9000000)+1000000;
        return dig7;
    }

    public void selectionActivity () {
        Intent i = new Intent(LogInActivity.this, SelectIdTypeActivity.class);
        startActivity(i);
    }

}
