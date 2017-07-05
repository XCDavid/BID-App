package com.mobbeel.mobbscan.simple.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.mobbeel.mobbscan.simple.R;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.MorphoDevice;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    Button bLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_log_in);
        bLogIn = (Button) findViewById(R.id.b_login);
        bLogIn.setOnClickListener(this);


        MorphoDevice morphoDevice = new MorphoDevice();
        USBManager.getInstance().initialize(this, "com.morpho.morphosample.USB_ACTION");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_login:
                Intent i = new Intent(LogInActivity.this, FormActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
        }
    }
}
