package com.teknei.bid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.teknei.bid.R;
import com.teknei.bid.utils.SharedPreferencesUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText etServerMobbScan;
    EditText etLicenseMobbScan;
    EditText etServerTeknei;
    EditText etServerMobbSign;
    EditText etLicenseMobbSign;
    EditText etServerAuth;
    EditText etIdCompany;

    Spinner spFingerprintReader;

    Button updateButton;

    String urlIdScan;
    String licenseIdScan;
    String urlTeknei;
    String urlMobbsign;
    String licenseMobbsign;
    String urlAuth;
    String fingerprintReader;
    String idCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.settings_activity_name));
            invalidateOptionsMenu();
        }

        urlIdScan         = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_ID_SCAN, getString(R.string.default_url_id_scan));
        licenseIdScan     = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.LICENSE_ID_SCAN, getString(R.string.default_license_id_scan));
        urlTeknei         = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_TEKNEI, getString(R.string.default_url_teknei));
        urlMobbsign       = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_MOBBSIGN, getString(R.string.default_url_mobbsign));
        licenseMobbsign   = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.MOBBSIGN_LICENSE, getString(R.string.default_license_mobbsign));
        urlAuth           = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.URL_AUTHACCESS, getString(R.string.default_url_oauthaccess));
        fingerprintReader = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.FINGERPRINT_READER, getString(R.string.default_fingerprint_reader));
        idCompany         = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_ENTERPRICE, getString(R.string.default_id_company));

        etServerMobbScan  = (EditText) findViewById(R.id.et_settings_url_id_scan);
        etLicenseMobbScan = (EditText) findViewById(R.id.et_settings_license_id_scan);
        etServerTeknei    = (EditText) findViewById(R.id.et_settings_url_teknei);
        etServerMobbSign  = (EditText) findViewById(R.id.et_settings_url_mobbsign);
        etLicenseMobbSign = (EditText) findViewById(R.id.et_settings_licence_mobbsign);
        etServerAuth      = (EditText) findViewById(R.id.et_settings_url_auth);
        etIdCompany       = (EditText) findViewById(R.id.et_settings_id_company);

        updateButton = (Button) findViewById(R.id.b_update_project_settings);

        etServerMobbScan.setText    (urlIdScan);
        etLicenseMobbScan.setText   (licenseIdScan);
        etServerTeknei.setText      (urlTeknei);
        etServerMobbSign.setText    (urlMobbsign);
        etLicenseMobbSign.setText   (licenseMobbsign);
        etServerAuth.setText        (urlAuth);
        etIdCompany.setText         (idCompany);

        spFingerprintReader = (Spinner) findViewById(R.id.sp_option_fingerprint_reader);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                            R.array.fingerprint_reader_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spFingerprintReader.setAdapter(adapter);
        spFingerprintReader.setOnItemSelectedListener(this);

        int posReader = 0;

        for (int i = 0; i < adapter.getCount();i++) {
            String temp = adapter.getItem(i).toString();

            if (temp.equals(fingerprintReader)) {
                posReader = i;
            }

        }

        spFingerprintReader.setSelection(posReader);

        updateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_update_project_settings:
                urlIdScan       = etServerMobbScan.getText().toString();
                licenseIdScan   = etLicenseMobbScan.getText().toString();
                urlTeknei       = etServerTeknei.getText().toString();
                urlMobbsign     = etServerMobbSign.getText().toString();
                licenseMobbsign = etLicenseMobbSign.getText().toString();
                urlAuth         = etServerAuth.getText().toString();
                idCompany       = etIdCompany.getText().toString();

                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.URL_ID_SCAN,urlIdScan);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.LICENSE_ID_SCAN,licenseIdScan);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.URL_TEKNEI,urlTeknei);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.URL_MOBBSIGN,urlMobbsign);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.MOBBSIGN_LICENSE,licenseMobbsign);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.URL_AUTHACCESS,urlAuth);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.FINGERPRINT_READER,fingerprintReader);
                SharedPreferencesUtils.saveToPreferencesString(SettingsActivity.this,SharedPreferencesUtils.ID_ENTERPRICE,idCompany);

                Toast.makeText(SettingsActivity.this, "Ajustes actualizados !", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        fingerprintReader = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
