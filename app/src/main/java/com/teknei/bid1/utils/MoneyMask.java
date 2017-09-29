package com.teknei.bid1.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

public class MoneyMask implements TextWatcher {
	private EditText mEditText;
	String currencySymbol;

	public MoneyMask(EditText e,String moneda) {
		mEditText = e;
		currencySymbol =moneda;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (!s.toString().matches(
				"^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$")) {
			String userInput = "" + s.toString().replaceAll("[^\\d]", "");
			StringBuilder cashAmountBuilder = new StringBuilder(userInput);

			while (cashAmountBuilder.length() > 3
					&& cashAmountBuilder.charAt(0) == '0') {
				cashAmountBuilder.deleteCharAt(0);
			}
			while (cashAmountBuilder.length() < 3) {
				cashAmountBuilder.insert(0, '0');
			}
			cashAmountBuilder.insert(cashAmountBuilder.length() - 2, '.');
			cashAmountBuilder.insert(0, currencySymbol);

			mEditText.setText(cashAmountBuilder.toString());
			mEditText.setTextKeepState(cashAmountBuilder.toString());
			Selection.setSelection(mEditText.getText(), cashAmountBuilder
					.toString().length());
		}

	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

}