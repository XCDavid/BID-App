package com.teknei.bid1.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mobbeel.mobbscan.api.MobbScanDocumentType;
import com.teknei.bid1.R;
import com.teknei.bid1.dialogs.AlertDialog;
import com.teknei.bid1.utils.ApiConstants;
import com.teknei.bid1.utils.PermissionsUtils;

public class SelectIdTypeActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Button ifeCButton;
    Button ifeDButton;
    Button ineButton;
    Button passportButton;
    Button licenseButton;

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

        mySwitch = (Switch) findViewById(R.id.sw_id_provider);

        ifeCButton.setOnClickListener(this);
        ifeDButton.setOnClickListener(this);
        ineButton.setOnClickListener(this);
        passportButton.setOnClickListener(this);
        licenseButton.setOnClickListener(this);

        //set the switch to ON
        mySwitch.setChecked(true);
        mySwitch.setOnCheckedChangeListener(this);

        if (credentialProvider) {
            ifeDButton.setVisibility(View.GONE);
            ifeCButton.setText(getString(R.string.ife_select_id_type));
        } else {
            ifeDButton.setVisibility(View.VISIBLE);
            ifeCButton.setText(getString(R.string.ife_c_select_id_type));
        }
        //Check Permissions For Android 6.0 up
        PermissionsUtils.checkPermissionCamera(this);
        PermissionsUtils.checkPermissionReadWriteExternalStorage(this);
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
            dialogoAlert = new AlertDialog(SelectIdTypeActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsUtils.CAMERA_REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    SelectIdTypeActivity.this.onBackPressed();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PermissionsUtils.WRITE_READ_EXTERNAL_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    //Check AGAIN Permissions For Android 6.0 up
                    PermissionsUtils.checkPermissionWriteExternalStorage(SelectIdTypeActivity.this);
                }

                if (grantResults.length > 1
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    //Check AGAIN Permissions For Android 6.0 up
                    PermissionsUtils.checkPermissionReadExternalStorage(SelectIdTypeActivity.this);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
