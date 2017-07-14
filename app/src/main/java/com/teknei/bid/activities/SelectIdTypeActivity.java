package com.teknei.bid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mobbeel.mobbscan.api.MobbScanDocumentType;
import com.teknei.bid.R;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.utils.ApiConstants;

public class SelectIdTypeActivity extends BaseActivity implements View.OnClickListener {
    Button ifeCButton;
    Button ifeDButton;
    Button ineButton;
    Button passportButton;
    Button licenseButton;

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

        ifeCButton.setOnClickListener(this);
        ifeDButton.setOnClickListener(this);
        ineButton.setOnClickListener(this);
        passportButton.setOnClickListener(this);
        licenseButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, IdScanActivity.class);
        //Create the bundle
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.b_ife_c_select_id:
                //Add your data from getFactualResults method to bundle
                bundle.putString("id_type", MobbScanDocumentType.MEXIDCardC.toString());
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_ife_d_select_id:
                //Add your data from getFactualResults method to bundle
                bundle.putString("id_type", MobbScanDocumentType.MEXIDCardD.toString());
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_ine_select_id:
                //Add your data from getFactualResults method to bundle
                bundle.putString("id_type", MobbScanDocumentType.MEXIDCardE.toString());
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_passport_select_id:
                //Add your data from getFactualResults method to bundle
                bundle.putString("id_type", MobbScanDocumentType.Passport_TD3.toString());
                //Add the bundle to the intent
                i.putExtras(bundle);
                break;
            case R.id.b_licence_select_id:
                //Add your data from getFactualResults method to bundle
//                bundle.putString("id_type", MobbScanDocumentType.MEXIDCardD.toString());
                //Add the bundle to the intent
//                i.putExtras(bundle);
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
        /*if (id == R.id.main_options_action) {
            Toast.makeText(this, "Trabajando...opciones", Toast.LENGTH_SHORT).show();
        }*/
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void logOut() {
//        super.logOut();
//    }

    @Override
    public void cancelOperation() {
//        String operationID = SharedPreferencesUtils.readFromPreferencesString(SelectIdTypeActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
//        String token = SharedPreferencesUtils.readFromPreferencesString(SelectIdTypeActivity.this, SharedPreferencesUtils.TOKEN_APP, "");
//        if (!operationID.equals("")) {
//            //new AsynckTask para cancelar la operacion
//            new CancelOp(SelectIdTypeActivity.this, operationID, token).execute();
//            return;
//        }
//        if (operationID.equals("")) {
//            Intent end = new Intent(SelectIdTypeActivity.this, FormActivity.class);
//            end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(end);
//            finish();
//        }
        super.cancelOperation();
    }
}
