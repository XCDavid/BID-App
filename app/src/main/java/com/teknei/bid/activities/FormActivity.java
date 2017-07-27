package com.teknei.bid.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.StartOperation;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.PermissionsUtils;
import com.teknei.bid.utils.PhoneSimUtils;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormActivity extends BaseActivity implements View.OnClickListener {
    EditText etName;
    EditText etLastName;
    EditText etMotherLastName;
    EditText etCurp;
    EditText etMail;
    EditText etPhone;
    EditText etRefContract;

    Button buttonContinue;

    String phoneID;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.form_activity_name));
            invalidateOptionsMenu();
        }
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[a-zA-ZÑñáéíóúÁÉÍÓÚ ]+")) {
                    return source;
                }
                return "";
            }
        };
        InputFilter filterCURP = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[A-Z0-9ÑÁÉÍÓÚ]+")) {
                    return source;
                }
                return "";
            }
        };
        InputFilter filterMail = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[a-zA-Z0-9@.!#$%&'*+/=?^_`{|}~-]+")) {
                    return source;
                }
                return "";
            }
        };
        InputFilter filterBasic = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[a-zA-Z0-9]+")) {
                    return source;
                }
                return "";
            }
        };

        etName = (EditText) findViewById(R.id.et_name_form);
        etLastName = (EditText) findViewById(R.id.et_last_name_form);
        etMotherLastName = (EditText) findViewById(R.id.et_mother_last_name_form);
        etCurp = (EditText) findViewById(R.id.et_curp_form);
        etMail = (EditText) findViewById(R.id.et_mail_form);
        etPhone = (EditText) findViewById(R.id.et_phone_form);
        etRefContract = (EditText) findViewById(R.id.et_contract_ref_form);
        buttonContinue = (Button) findViewById(R.id.b_continue_form);

        buttonContinue.setOnClickListener(this);
        etName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});
        etLastName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});
        etMotherLastName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});
        etCurp.setFilters(new InputFilter[]{filterCURP, new InputFilter.LengthFilter(18)});
        etMail.setFilters(new InputFilter[]{filterMail, new InputFilter.LengthFilter(40)});
        etRefContract.setFilters(new InputFilter[]{filterBasic, new InputFilter.LengthFilter(20)});

        //Check Permissions For Android 6.0 up
        PermissionsUtils.checkPermissionPhoneState(this);

//        phoneID = PhoneSimUtils.getImei(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_continue_form:
//                if (validateDataForm()){

                //Borrar
//                SharedPreferencesUtils.saveToPreferencesString(FormActivity.this,SharedPreferencesUtils.OPERATION_ID,"666");

//                Intent i = new Intent(this, SelectIdTypeActivity.class);
//                startActivity(i);


                sendPetition();
//                }
                break;
        }
    }

    private boolean validateDataForm() {
        if (etName.getText().toString().equals("")) {
            Toast.makeText(this, "El campo ( Nombre ) es obligatorio", Toast.LENGTH_SHORT).show();
            etName.clearFocus();
            if (etName.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (etLastName.getText().toString().equals("")) {
            Toast.makeText(this, "El campo ( Apellido paterno ) es obligatorio", Toast.LENGTH_SHORT).show();
            etLastName.clearFocus();
            if (etLastName.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etLastName, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (etMotherLastName.getText().toString().equals("")) {
            Toast.makeText(this, "El campo ( Apellido materno ) es obligatorio", Toast.LENGTH_SHORT).show();
            etMotherLastName.clearFocus();
            if (etMotherLastName.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etMotherLastName, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (etCurp.getText().toString().equals("")) {
            Toast.makeText(this, "El campo ( CURP ) es obligatorio", Toast.LENGTH_SHORT).show();
            etCurp.clearFocus();
            if (etCurp.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etCurp, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (etCurp.getText().toString().length() < 18) {
            Toast.makeText(this, "El campo ( CURP ) debe tener 18 digitos", Toast.LENGTH_SHORT).show();
            etCurp.clearFocus();
            if (etCurp.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etCurp, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (etPhone.getText().toString().equals("")) {
            Toast.makeText(this, "El campo ( Teléfono ) es obligatorio", Toast.LENGTH_SHORT).show();
            etPhone.clearFocus();
            if (etPhone.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etPhone, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (etPhone.getText().toString().length() < 8) {
            Toast.makeText(this, "El campo ( Teléfono ) debe tener minimo 8 caracteres", Toast.LENGTH_SHORT).show();
            etPhone.clearFocus();
            if (etPhone.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etPhone, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (!etMail.getText().toString().equals("")) {
            if (!validate(etMail.getText().toString())) {
                Toast.makeText(this, "El campo ( Correo ) tiene un formato erroneo", Toast.LENGTH_SHORT).show();
                etMail.clearFocus();
                if (etMail.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etMail, InputMethodManager.SHOW_IMPLICIT);
                }
            } else {
//                Toast.makeText(this, "Super OK !!!", Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
//            Toast.makeText(this, "Super OK !!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    //menu actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.i_search_menu) {
            Intent i = new Intent(this, ConsultActivity.class);
            startActivity(i);
        }
        if (id == R.id.i_settings_menu) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(FormActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialogoAlert;
        dialogoAlert = new AlertDialog(FormActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
        dialogoAlert.setCancelable(false);
        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogoAlert.show();
    }

    public String buildJSON() {
        String name = etName.getText().toString();
        String app1 = etLastName.getText().toString();
        String app2 = etMotherLastName.getText().toString();
        String curp = etCurp.getText().toString();
        String mail = etMail.getText().toString();
        String phone = etPhone.getText().toString();
        String numContract = etRefContract.getText().toString();

        //***Contruye el json con datos que no obtiene MobbScan Falta comprobar Icar
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("curp", curp);
            SharedPreferencesUtils.saveToPreferencesString(FormActivity.this,SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE,jsonData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //***

        String employee = SharedPreferencesUtils.readFromPreferencesString(FormActivity.this,SharedPreferencesUtils.USERNAME,"default");
        phoneID = PhoneSimUtils.getImei(this);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String operationID = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");
        if(operationID.equals("")){
            String jsonString = buildJSON();
            new StartOperation(FormActivity.this, token, jsonString).execute();
        }else {
            goNext();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(FormActivity.this, SelectIdTypeActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsUtils.WRITE_READ_EXTERNAL_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    //Check AGAIN Permissions For Android 6.0 up
                    PermissionsUtils.checkPermissionPhoneState(FormActivity.this);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
