package com.teknei.bid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.CheckRegisterAccount;
import com.teknei.bid.asynctask.FindOperation;
import com.teknei.bid.asynctask.RegisterUserCompany;
import com.teknei.bid.asynctask.StartOperation;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.DataValidation;
import com.teknei.bid.domain.AccountDTO;
import com.teknei.bid.domain.UserCompanyDTO;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;

import static android.R.id.edit;

public class AccountRegistrationActivity extends BaseActivity implements View.OnClickListener {

    private EditText        edtUser;
    private EditText        edtPassword;
    private EditText        edtConfirm;
    private Button          btnContinue;
    private AccountDTO      accountDTO;
    private UserCompanyDTO  userComDTO;

    private String token;
    private String companyID;
    private String deviceID;
    private String operationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_registration);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.ar_tv_title_activity));
            invalidateOptionsMenu();
        }

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        token       = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        companyID   = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_ENTERPRICE, "");
        deviceID    = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_DEVICE, "");
        operationID = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");

        edtUser     = (EditText) findViewById(R.id.ar_et_user);
        edtPassword = (EditText) findViewById(R.id.ar_et_password);
        edtConfirm  = (EditText) findViewById(R.id.ar_et_confirm);
        btnContinue = (Button) findViewById(R.id.ar_btn_continue);

        btnContinue.setOnClickListener(this);

        edtUser.setFilters    (new InputFilter[]{filter, new InputFilter.LengthFilter(20)});
        edtPassword.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10)});
        edtConfirm.setFilters (new InputFilter[]{filter, new InputFilter.LengthFilter(10)});
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ar_btn_continue:
                if (validateDataForm()) {
                    sendPetition();
                }
                break;
        }
    }

    private boolean validateDataForm() {

        if (edtUser.getText().toString().equals("")) {

            DataValidation dataValidation;
            dataValidation = new DataValidation(AccountRegistrationActivity.this, getString(R.string.message_data_validation),
                            getString(R.string.ar_message_obligatory_field_user));
            dataValidation.setCancelable(false);
            dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dataValidation.show();

            edtUser.clearFocus();
            if (edtUser.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtUser, InputMethodManager.SHOW_IMPLICIT);
            }

        } else if (edtPassword.getText().toString().equals("")) {

            DataValidation dataValidation;
            dataValidation = new DataValidation(AccountRegistrationActivity.this, getString(R.string.message_data_validation),
                    getString(R.string.ar_message_obligatory_field_password));
            dataValidation.setCancelable(false);
            dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dataValidation.show();

            edtPassword.clearFocus();
            if (edtPassword.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtPassword, InputMethodManager.SHOW_IMPLICIT);
            }

        } if (edtConfirm.getText().toString().equals("")) {

            DataValidation dataValidation;
            dataValidation = new DataValidation(AccountRegistrationActivity.this, getString(R.string.message_data_validation),
                    getString(R.string.ar_message_obligatory_field_confirm));
            dataValidation.setCancelable(false);
            dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dataValidation.show();

            edtConfirm.clearFocus();
            if (edtConfirm.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtConfirm, InputMethodManager.SHOW_IMPLICIT);
            }

        } else if (!edtPassword.getText().toString().equals(edtConfirm.getText().toString())){

            DataValidation dataValidation;
            dataValidation = new DataValidation(AccountRegistrationActivity.this, getString(R.string.message_data_validation),
                    getString(R.string.ar_message_error_confirm));
            dataValidation.setCancelable(false);
            dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dataValidation.show();

        } else {

            return true;

        }

        return false;
    }

    public void sendRegisterUserCompany(AccountDTO accountDTO) {

        this.accountDTO = accountDTO;

        userComDTO = new UserCompanyDTO(Long.parseLong(companyID),accountDTO.getIdOperation(),Long.parseLong(operationID));

        new RegisterUserCompany(this,token,userComDTO).execute();
    }

    @Override
    public void sendPetition() {

        accountDTO = new AccountDTO(Long.parseLong(operationID), edtPassword.getText().toString(), edtUser.getText().toString());

        new CheckRegisterAccount(this, token, accountDTO).execute();

    }

    @Override
    public void goNext() {
        Intent i = new Intent(AccountRegistrationActivity.this, ResultOperationActivity.class);
        startActivity(i);
    }

    //menu actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.i_close_operation_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(AccountRegistrationActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(AccountRegistrationActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() { }

}
