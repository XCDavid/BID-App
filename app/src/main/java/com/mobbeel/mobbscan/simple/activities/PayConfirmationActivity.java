package com.mobbeel.mobbscan.simple.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import com.mobbeel.mobbscan.simple.R;
import com.mobbeel.mobbscan.simple.utils.MoneyMask;

public class PayConfirmationActivity extends AppCompatActivity {
    EditText etContractreference;
    EditText etPayAmount;

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

        etContractreference.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(20)});
        etPayAmount.addTextChangedListener(new MoneyMask(etPayAmount,"$"));
    }
}
