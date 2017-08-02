package com.teknei.bid.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mobbeel.mobblicense.IOUtils;
import com.mobbeel.mobbscan.api.MobbScanDocumentType;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.GetContract;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.mobbsign.MobbSignActivity;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SelectIdTypeActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Button ifeCButton;
    Button ifeDButton;
    Button ineButton;
    Button passportButton;
    Button licenseButton;
    Button icarButton;

    Switch mySwitch;

    boolean credentialProvider = true;

    String operationID = "";

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

        if (credentialProvider){
            ifeDButton.setVisibility(View.GONE);
            ifeCButton.setText(getString(R.string.ife_select_id_type));
        }else{
            ifeDButton.setVisibility(View.VISIBLE);
            ifeCButton.setText(getString(R.string.ife_c_select_id_type));
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;
        if (credentialProvider) {
            i = new Intent(this, IcarScanActivity.class);
        } else {
            i = new Intent(this, IdScanActivity.class);
        }
        //Borrar
        boolean flag = true;
        //Create the bundle
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.b_ife_c_select_id:
                if (credentialProvider) {
                    bundle.putString("id_type", ApiConstants.STRING_IFE);
                } else {
                    bundle.putString("id_type", MobbScanDocumentType.MEXIDCardC.toString());
                }
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_ife_d_select_id:
                if (credentialProvider) {
                    bundle.putString("id_type", ApiConstants.STRING_IFE);
                } else {
                    bundle.putString("id_type", MobbScanDocumentType.MEXIDCardD.toString());
                }
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_ine_select_id:
                if (credentialProvider) {
                    bundle.putString("id_type", ApiConstants.STRING_INE);
                } else {
                    bundle.putString("id_type", MobbScanDocumentType.MEXIDCardE.toString());
                }
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_passport_select_id:
                if (credentialProvider) {
                    bundle.putString("id_type", ApiConstants.STRING_PASSPORT);
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
                String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
                operationID = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");
                flag = false;
                new GetContract(SelectIdTypeActivity.this, token, Integer.valueOf(operationID)).execute();
                break;
        }
        if (flag)
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
            ifeDButton.setVisibility(View.GONE);
            ifeCButton.setText(getString(R.string.ife_select_id_type));
        } else {
            credentialProvider = false;
            ifeDButton.setVisibility(View.VISIBLE);
            ifeCButton.setText(getString(R.string.ife_c_select_id_type));
        }
    }

    @Override
    public void goNext() {
//        super.goNext();
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "contract_" + operationID + ".pdf");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(intent);

//        InputStream inputStream;
        String DOC_ID = "0000013ee31c54e348a98eaed7d221858a40";
        int RC_MOBBSIGN = 1;
        Intent intent = new Intent(SelectIdTypeActivity.this, MobbSignActivity.class);
        try {
            InputStream targetStream = new FileInputStream(file);
            intent.putExtra(MobbSignActivity.EXTRA_DOCUMENT, IOUtils.toByteArray(targetStream));
            intent.putExtra(MobbSignActivity.EXTRA_DOC_ID, DOC_ID);
            intent.putExtra("id_operation", operationID);
            targetStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, RC_MOBBSIGN);
    }
}
