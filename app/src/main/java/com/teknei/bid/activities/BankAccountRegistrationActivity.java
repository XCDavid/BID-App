package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.FindOperation;
import com.teknei.bid.asynctask.GetCreditInstitution;
import com.teknei.bid.asynctask.SendBankAccount;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.DataValidation;
import com.teknei.bid.domain.BankAccountDTO;
import com.teknei.bid.domain.BankingInstitutionDTO;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class BankAccountRegistrationActivity extends BaseActivity
                              implements View.OnClickListener , AdapterView.OnItemSelectedListener {

    private Spinner  spBanks;
    private Button   btnContinue;
    private EditText edClabe;
    private EditText edAlias;
    private EditText edAmmount;

    private String  token;
    private String  idOperation   = "";
    private String  idDispositivo = "";
    private Integer idCreditInst  = 0;
    private BankAccountDTO valueDTO;
    private List<BankingInstitutionDTO> listBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bank_account_registration);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.bar_activity_name));
            invalidateOptionsMenu();
        }

        listBank = new ArrayList<BankingInstitutionDTO>();

        edClabe     = (EditText) findViewById(R.id.bar_clabe);
        edAlias     = (EditText) findViewById(R.id.bar_alias);
        edAmmount   = (EditText) findViewById(R.id.bar_ammount);

        spBanks     = (Spinner)  findViewById(R.id.sp_option_bank);

        btnContinue = (Button) findViewById(R.id.bar_btn_continue);
        btnContinue.setOnClickListener(this);

        token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        idOperation   = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");
        idDispositivo = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");

        new GetCreditInstitution( this, token).execute();

    }

    public void showSpinnerBank (List<BankingInstitutionDTO> listBank) {

        this.listBank = listBank;

        ArrayAdapter<CharSequence> adapterZone = new ArrayAdapter<CharSequence> (this,
                android.R.layout.simple_spinner_item) {
            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        adapterZone.add("* Seleccione un banco");

        for (BankingInstitutionDTO value : listBank) {

            adapterZone.add(value.getNomLarg());

        }

        adapterZone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBanks.setAdapter(adapterZone);
        spBanks.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_btn_continue:
                if (validateDataForm()) {

                    sendPetition();

                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == R.id.sp_option_bank) {

            String bankSelect = ((String) adapterView.getItemAtPosition(i));

            for (BankingInstitutionDTO value : listBank) {
                if (value.getNomLarg().compareTo(bankSelect) == 0) {
                    idCreditInst = value.getIdInstCred();
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

        DataValidation dataValidation;
        dataValidation = new DataValidation(BankAccountRegistrationActivity.this,
                getString(R.string.message_data_validation), getString(R.string.bar_message_obligatory_field_bank));
        dataValidation.setCancelable(false);
        dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dataValidation.show();

    }

    @Override
    public void goNext() {
        Intent i = new Intent(BankAccountRegistrationActivity.this, ResultOperationActivity.class);
        startActivity(i);
    }

    @Override
    public void sendPetition() {

        Double  valueTemp    = (Double.parseDouble(edAmmount.getText().toString())*100);
        Integer valueAmmount = valueTemp.intValue();

        valueDTO = new BankAccountDTO(true, edAlias.getText().toString(),valueAmmount,
                edClabe.getText().toString(),Integer.parseInt(idOperation),idCreditInst,
                true, idDispositivo);

        new SendBankAccount(this, token, valueDTO).execute();
    }

    @Override
    public void onBackPressed() { }

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
            dialogoAlert = new AlertDialog(BankAccountRegistrationActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(BankAccountRegistrationActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateDataForm() {

        if (edClabe.getText().toString().equals("")) {

            DataValidation dataValidation;
            dataValidation = new DataValidation(BankAccountRegistrationActivity.this,
                    getString(R.string.message_data_validation), getString(R.string.bar_message_obligatory_field_clabe));
            dataValidation.setCancelable(false);
            dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dataValidation.show();

            edClabe.clearFocus();
            if (edClabe.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(edClabe, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (edAlias.getText().toString().equals("")) {

            DataValidation dataValidation;
            dataValidation = new DataValidation(BankAccountRegistrationActivity.this,
                    getString(R.string.message_data_validation), getString(R.string.bar_message_obligatory_field_alias));
            dataValidation.setCancelable(false);
            dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dataValidation.show();

            edAlias.clearFocus();
            if (edAlias.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(edAlias, InputMethodManager.SHOW_IMPLICIT);
            }

        }else if (edAmmount.getText().toString().equals("") || Double.parseDouble(edAmmount.getText().toString()) <= 0.0) {

            DataValidation dataValidation;
            dataValidation = new DataValidation(BankAccountRegistrationActivity.this,
                    getString(R.string.message_data_validation), getString(R.string.bar_message_obligatory_field_ammount));
            dataValidation.setCancelable(false);
            dataValidation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dataValidation.show();

            edAmmount.clearFocus();
            if (edAmmount.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(edAmmount, InputMethodManager.SHOW_IMPLICIT);
            }

        } else {
            return true;
        }
        return false;
    }

}
