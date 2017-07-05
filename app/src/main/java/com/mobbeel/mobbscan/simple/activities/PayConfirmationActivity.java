package com.mobbeel.mobbscan.simple.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.mobbeel.mobbscan.simple.R;
import com.mobbeel.mobbscan.simple.utils.MoneyMask;

public class PayConfirmationActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etContractreference;
    EditText etPayAmount;
    Button buttonContinue;

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

        buttonContinue.setOnClickListener(this);
        etContractreference.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)});
        etPayAmount.addTextChangedListener(new MoneyMask(etPayAmount,"$"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_continue_pay_confirm:
                Intent i = new Intent(this,ResultOperationActivity.class);
                startActivity(i);
                break;
        }
    }
}
