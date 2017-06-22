package com.mobbeel.mobbscan.simple.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobbeel.mobbscan.simple.R;

public class FingerPrintsActivity extends AppCompatActivity implements View.OnClickListener{
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_prints);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.fingerprints_activity_name));
            invalidateOptionsMenu();
        }
        continueButton = (Button) findViewById(R.id.b_continue_fingerprints);
        continueButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_continue_fingerprints:
                Intent i = new Intent(this, FaceScanActivity.class);
                startActivity(i);
                break;
        }
    }
}
