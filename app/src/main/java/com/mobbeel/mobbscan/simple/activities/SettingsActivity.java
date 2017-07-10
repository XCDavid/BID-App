package com.mobbeel.mobbscan.simple.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.mobbeel.mobbscan.simple.R;
import com.mobbeel.mobbscan.simple.utils.SharedPreferencesUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etServerMobbScan;
    EditText etLicenseMobbScan;
    EditText etServerTeknei;

    Button updateButton;

    String urlIdScan;
    String licenseIdScan;
    String urlTeknei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_activity_name));
            invalidateOptionsMenu();
        }

        urlIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_ID_SCAN, getString(R.string.default_url_id_scan));
        licenseIdScan = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.LICENSE_ID_SCAN, getString(R.string.default_license_id_scan));
        urlTeknei = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_TEKNEI, getString(R.string.default_url_teknei));

        etServerMobbScan = (EditText) findViewById(R.id.et_settings_url_id_scan);
        etLicenseMobbScan = (EditText) findViewById(R.id.et_settings_license_id_scan);
        etServerTeknei = (EditText) findViewById(R.id.et_settings_url_teknei);

        updateButton = (Button) findViewById(R.id.b_update_project_settings);

        etServerMobbScan.setText(urlIdScan);
        etLicenseMobbScan.setText(licenseIdScan);
        etServerTeknei.setText(urlTeknei);

        updateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_update_project_settings:
                urlIdScan = etServerMobbScan.getText().toString();
                licenseIdScan = etLicenseMobbScan.getText().toString();
                urlTeknei = etServerTeknei.getText().toString();

                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.URL_ID_SCAN,urlIdScan);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.LICENSE_ID_SCAN,licenseIdScan);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.URL_TEKNEI,urlTeknei);

                Toast.makeText(SettingsActivity.this, "Ajustes actualizados !", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
