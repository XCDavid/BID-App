package com.mobbeel.mobbscan.simple.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobbeel.mobbscan.api.MobbScanDocumentType;
import com.mobbeel.mobbscan.simple.R;

public class SelectIdTypeActivity extends AppCompatActivity implements View.OnClickListener {
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
        Intent i = new Intent(this,IdScanActivity.class);
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
}
