package com.teknei.bid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.FindOperation;
import com.teknei.bid.asynctask.SendConfirmOTPV;
import com.teknei.bid.asynctask.StartOperation;
import com.teknei.bid.dialogs.DataValidation;
import com.teknei.bid.domain.ValidateOtpDTO;
import com.teknei.bid.utils.SharedPreferencesUtils;

public class OTPValidationMailActivity extends BaseActivity implements View.OnClickListener {

    EditText etCode;
    Button   btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation_mail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.otpv_mail_form_activity_name));
            invalidateOptionsMenu();
        }

        etCode      = (EditText) findViewById(R.id.otpv_mail_edt_codigo);
        btnContinue = (Button) findViewById(R.id.otpv_mail_btn_continue);

        btnContinue.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.otpv_mail_btn_continue:
                if (validateDataForm()) {
                    sendPetition();
                }
                break;
        }
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String operationID = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");

        new SendConfirmOTPV (OTPValidationMailActivity.this, token, Integer.parseInt(operationID), etCode.getText().toString()).execute();
    }

    @Override
    public void goNext() {
        Intent i = new Intent(OTPValidationMailActivity.this, SelectIdTypeActivity.class);
        startActivity(i);
    }

    private boolean validateDataForm() {

        if (etCode.getText().toString().equals("")) {

            DataValidation dataValidation;
            dataValidation = new DataValidation(OTPValidationMailActivity.this,
                    getString(R.string.message_data_validation), getString(R.string.otpv_mail_message_obligatory_field_code));
            dataValidation.setCancelable(false);
            dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dataValidation.show();

            etCode.clearFocus();
            if (etCode.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etCode, InputMethodManager.SHOW_IMPLICIT);
            }

        } else {

            return true;

        }
        return false;
    }
}
