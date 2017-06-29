package com.mobbeel.mobbscan.simple.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mobbeel.mobbscan.simple.R;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etName;
    EditText etLastName;
    EditText etMotherLastName;
    EditText etCurp;
    EditText etMail;
    EditText etPhone;

    Button buttonContinue;

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

        etName = (EditText) findViewById(R.id.et_name_form);
        etLastName = (EditText) findViewById(R.id.et_last_name_form);
        etMotherLastName = (EditText) findViewById(R.id.et_mother_last_name_form);
        etCurp = (EditText) findViewById(R.id.et_curp_form);
        etMail = (EditText) findViewById(R.id.et_mail_form);
        etPhone = (EditText) findViewById(R.id.et_phone_form);
        buttonContinue = (Button) findViewById(R.id.b_continue_form);

        buttonContinue.setOnClickListener(this);
        etName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});
        etLastName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});
        etMotherLastName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(30)});
        etCurp.setFilters(new InputFilter[]{filterCURP, new InputFilter.LengthFilter(18)});
        etMail.setFilters(new InputFilter[]{filterMail, new InputFilter.LengthFilter(40)});

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_continue_form:
//                if (validateDataForm()){
                    Intent i = new Intent(this,SelectIdTypeActivity.class);
                    startActivity(i);
//                }
                break;
        }
    }

    private boolean validateDataForm() {
        if (etName.getText().toString().equals("")){
            Toast.makeText(this,"El campo ( Nombre ) es obligatorio",Toast.LENGTH_SHORT).show();
            etName.clearFocus();
            if(etName.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
            }
        }else if (etLastName.getText().toString().equals("")){
            Toast.makeText(this,"El campo ( Apellido paterno ) es obligatorio",Toast.LENGTH_SHORT).show();
            etLastName.clearFocus();
            if(etLastName.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etLastName, InputMethodManager.SHOW_IMPLICIT);
            }
        }else if (etMotherLastName.getText().toString().equals("")){
            Toast.makeText(this,"El campo ( Apellido materno ) es obligatorio",Toast.LENGTH_SHORT).show();
            etMotherLastName.clearFocus();
            if(etMotherLastName.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etMotherLastName, InputMethodManager.SHOW_IMPLICIT);
            }
        }else if (etCurp.getText().toString().equals("")){
            Toast.makeText(this,"El campo ( CURP ) es obligatorio",Toast.LENGTH_SHORT).show();
            etCurp.clearFocus();
            if(etCurp.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etCurp, InputMethodManager.SHOW_IMPLICIT);
            }
        }else if (etCurp.getText().toString().length() < 18){
            Toast.makeText(this,"El campo ( CURP ) debe tener 18 digitos",Toast.LENGTH_SHORT).show();
            etCurp.clearFocus();
            if(etCurp.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etCurp, InputMethodManager.SHOW_IMPLICIT);
            }
        }else if (etPhone.getText().toString().equals("")){
            Toast.makeText(this,"El campo ( Teléfono ) es obligatorio",Toast.LENGTH_SHORT).show();
            etPhone.clearFocus();
            if(etPhone.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etPhone, InputMethodManager.SHOW_IMPLICIT);
            }
        }else if (etPhone.getText().toString().length() < 8){
            Toast.makeText(this,"El campo ( Teléfono ) debe tener minimo 8 caracteres",Toast.LENGTH_SHORT).show();
            etPhone.clearFocus();
            if(etPhone.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etPhone, InputMethodManager.SHOW_IMPLICIT);
            }
        }else if (!etMail.getText().toString().equals("")){
            if (!validate(etMail.getText().toString())){
                Toast.makeText(this,"El campo ( Correo ) tiene un formato erroneo",Toast.LENGTH_SHORT).show();
                etMail.clearFocus();
                if(etMail.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etMail, InputMethodManager.SHOW_IMPLICIT);
                }
            }else {
                Toast.makeText(this,"Super OK !!!",Toast.LENGTH_SHORT).show();
                return true;
            }
        }else{
            Toast.makeText(this,"Super OK !!!",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
