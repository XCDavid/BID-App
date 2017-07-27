package com.teknei.bid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.mobbeel.mobbscan.api.MobbScanDocumentType;
import com.teknei.bid.R;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.utils.ApiConstants;

public class SelectIdTypeActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Button ifeCButton;
    Button ifeDButton;
    Button ineButton;
    Button passportButton;
    Button licenseButton;
    Button icarButton;

    Switch mySwitch;

    boolean credentialProvider = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_id_type);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.select_id_scan_activity_name));
            invalidateOptionsMenu();
        }
        ifeCButton = (Button) findViewById(R.id.b_ife_c_select_id);
        ifeDButton = (Button) findViewById(R.id.b_ife_d_select_id);
        ineButton = (Button) findViewById(R.id.b_ine_select_id);
        passportButton = (Button) findViewById(R.id.b_passport_select_id);
        licenseButton = (Button) findViewById(R.id.b_licence_select_id);
        icarButton = (Button) findViewById(R.id.b_icar_select_id);

        mySwitch = (Switch) findViewById(R.id.sw_id_provider);

        ifeCButton.setOnClickListener(this);
        ifeDButton.setOnClickListener(this);
        ineButton.setOnClickListener(this);
        passportButton.setOnClickListener(this);
        licenseButton.setOnClickListener(this);
        icarButton.setOnClickListener(this);

        //set the switch to ON
        mySwitch.setChecked(true);
        mySwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        if (credentialProvider) {
            i = new Intent(this, IcarScanActivity.class);
        } else {
            i = new Intent(this, IdScanActivity.class);
        }
        //Create the bundle
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.b_ife_c_select_id:
                if (credentialProvider) {
                    bundle.putString("id_type", "IFE");
                } else {
                    bundle.putString("id_type", MobbScanDocumentType.MEXIDCardC.toString());
                }
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_ife_d_select_id:
                if (credentialProvider) {
                    bundle.putString("id_type", "IFE");
                } else {
                    bundle.putString("id_type", MobbScanDocumentType.MEXIDCardD.toString());
                }
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_ine_select_id:
                if (credentialProvider) {
                    bundle.putString("id_type", "INE");
                } else {
                    bundle.putString("id_type", MobbScanDocumentType.MEXIDCardE.toString());
                }
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_passport_select_id:
                if (credentialProvider) {
                    bundle.putString("id_type", "PASAPORTE");
                } else {
                    bundle.putString("id_type", MobbScanDocumentType.Passport_TD3.toString());
                }
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_licence_select_id:
//                bundle.putString("id_type", MobbScanDocumentType.MEXIDCardD.toString());
                //Add the bundle to the intent
//                i.putExtras(bundle);
                break;
            case R.id.b_icar_select_id:
                //Add the bundle to the intent
//                i.putExtras(bundle);
//                i = new Intent(this, IcarScanActivity.class);
                break;
        }
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
            dialogoAlert = new AlertDialog(SelectIdTypeActivity.this, getString(R.string.message_cancel_operation_title), getString(R.string.message_cancel_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(SelectIdTypeActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            credentialProvider = true;
        } else {
            credentialProvider = false;
        }
    }
}
