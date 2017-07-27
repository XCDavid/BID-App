package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IdRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.ConfirmPayOperation;
import com.teknei.bid.asynctask.DocumentSend;
import com.teknei.bid.asynctask.StartOperation;
import com.teknei.bid.utils.MoneyMask;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PayConfirmationActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    EditText etContractreference;
    EditText etPayAmount;
    Button buttonContinue;

    RadioGroup radioGroup;
    RadioGroup radioGroupOk;
    ToggleButton cashToggle;
    ToggleButton cardToggle;
    ToggleButton okToggle;
    ToggleButton errorToggle;
    ToggleButton mBtnCurrentToggle;
    ToggleButton mBtnCurrentToggleOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirmation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.pay_confirm_activity_name));
            invalidateOptionsMenu();
        }
        InputFilter filter = new InputFilter() {
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
        InputFilter filterDecimalNumber = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("")) { // for backspace
                    return source;
                }
                if (source.toString().matches("[0-9.]+")) {
                    return source;
                }
                return "";
            }
        };
        etContractreference = (EditText) findViewById(R.id.et_contract_reference_pay_confirm);
        etPayAmount = (EditText) findViewById(R.id.et_pay_amount_pay_confirm);
        buttonContinue = (Button) findViewById(R.id.b_continue_pay_confirm);
        radioGroup = ((RadioGroup) findViewById(R.id.toggleGroup));
        cashToggle = ((ToggleButton) findViewById(R.id.tgg_btn_cash));
        cardToggle = ((ToggleButton) findViewById(R.id.tgg_btn_card));
        radioGroupOk = ((RadioGroup) findViewById(R.id.toggleGroupPaymentOk));
        okToggle = ((ToggleButton) findViewById(R.id.tgg_btn_pay_ok));
        errorToggle = ((ToggleButton) findViewById(R.id.tgg_btn_pay_error));


        mBtnCurrentToggle = cashToggle;
        mBtnCurrentToggleOK = okToggle;

        buttonContinue.setOnClickListener(this);
        cashToggle.setOnClickListener(this);
        cardToggle.setOnClickListener(this);
        okToggle.setOnClickListener(this);
        errorToggle.setOnClickListener(this);
        etContractreference.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)});
        etPayAmount.addTextChangedListener(new MoneyMask(etPayAmount, "$"));

        radioGroup.setOnCheckedChangeListener(this);
        radioGroupOk.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_continue_pay_confirm:
//                if (validateDataForm()) {
//                    Intent i = new Intent(this, ResultOperationActivity.class);
//                    startActivity(i);}
                sendPetition();
//                }
                break;
            case R.id.tgg_btn_cash:
            case R.id.tgg_btn_card:
                onToggle(view);
                break;
            case R.id.tgg_btn_pay_ok:
            case R.id.tgg_btn_pay_error:
                onToggleOk(view);
                break;
        }
    }

    private boolean validateDataForm() {
        if (etContractreference.getText().toString().equals("")) {
            Toast.makeText(this, "El campo ( Número de referencia ) es obligatorio", Toast.LENGTH_SHORT).show();
            etContractreference.clearFocus();
            if (etContractreference.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etContractreference, InputMethodManager.SHOW_IMPLICIT);
            }
        } else if (etPayAmount.getText().toString().equals("")) {
            Toast.makeText(this, "El campo ( Importe ) es obligatorio", Toast.LENGTH_SHORT).show();
            etPayAmount.clearFocus();
            if (etPayAmount.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(etPayAmount, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
//            Toast.makeText(this, "Super OK !!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String payOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.PAY_OPERATION, "");
        if (payOperation.equals("")) {
            String jsonString = buildJSON();
            new ConfirmPayOperation(PayConfirmationActivity.this, token, jsonString).execute();
        } else {
            goNext();
        }
    }

    @Override
    public void goNext() {
//        super.goNext();
        Intent i = new Intent(PayConfirmationActivity.this, ResultOperationActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(PayConfirmationActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
        String reference = etContractreference.getText().toString();
        String amount = etPayAmount.getText().toString();

        int idPayType =  mBtnCurrentToggle.getId();
        int idOkPay = mBtnCurrentToggleOK.getId();
        String payType = "";
        switch (idPayType){
            case R.id.tgg_btn_cash:
                payType = "efectivo";
                break;
            case R.id.tgg_btn_card:
                payType = "tarjeta";
                break;
        }
        String okPay = "";
        switch (idOkPay){
            case R.id.tgg_btn_pay_ok:
                okPay = "ok";
                break;
            case R.id.tgg_btn_pay_error:
                okPay = "error";
                break;
        }

        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("tipoPago", payType);
            jsonObject.put("referencia", reference);
            jsonObject.put("importe", amount);
            jsonObject.put("resultadoPago", okPay);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        for (int j = 0; j < radioGroup.getChildCount(); j++) {
            final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
            view.setChecked(view.getId() == i);
        }
    }

    public void onToggle(View view) {
//        ((RadioGroup)view.getParent()).check(view.getId());

        final ToggleButton mBtnToggle = (ToggleButton) view;

        // select only one toggle button at any given time
        if (mBtnCurrentToggle != null) {
            mBtnCurrentToggle.setChecked(false);
        }
        mBtnToggle.setChecked(true);
        mBtnCurrentToggle = mBtnToggle;
        // app specific stuff ..
    }

    public void onToggleOk(View view) {
//        ((RadioGroup)view.getParent()).check(view.getId());

        final ToggleButton mBtnToggle = (ToggleButton) view;

        // select only one toggle button at any given time
        if (mBtnCurrentToggleOK != null) {
            mBtnCurrentToggleOK.setChecked(false);
        }
        mBtnToggle.setChecked(true);
        mBtnCurrentToggleOK = mBtnToggle;
        // app specific stuff ..
    }
}
