package com.teknei.bid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.teknei.bid.R;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;

public class PersonSelectionActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnCustomer;
    private ImageButton btnOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_selection);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.sp_tv_title_activity));
            invalidateOptionsMenu();
        }

        btnCustomer = (ImageButton) findViewById(R.id.ps_ib_customer);
        btnOperator = (ImageButton) findViewById(R.id.ps_ib_operator);

        btnOperator.setOnClickListener(this);
        btnCustomer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String typePerson;
        switch (view.getId()) {
            case R.id.ps_ib_customer:
                typePerson = getResources().getString(R.string.type_customer);
                SharedPreferencesUtils.saveToPreferencesString
                        (PersonSelectionActivity.this,SharedPreferencesUtils.TYPE_PERSON,typePerson);
                break;

            case R.id.ps_ib_operator:
                typePerson = getResources().getString(R.string.type_operator);
                SharedPreferencesUtils.saveToPreferencesString
                        (PersonSelectionActivity.this,SharedPreferencesUtils.TYPE_PERSON,typePerson);
                break;
        }
        goNext();
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
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(PersonSelectionActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialogoAlert;
        dialogoAlert = new AlertDialog(PersonSelectionActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
        dialogoAlert.setCancelable(false);
        dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogoAlert.show();
    }

    @Override
    public void goNext() {
        Intent i = new Intent(PersonSelectionActivity.this, FormActivity.class);
        startActivity(i);
    }
}
