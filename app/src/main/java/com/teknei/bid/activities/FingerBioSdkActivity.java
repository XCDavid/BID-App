package com.teknei.bid.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import com.teknei.bid.R;

public class FingerBioSdkActivity extends BaseActivity {
    RelativeLayout takeFingerLinearLayout;
    Switch handSideSwitch;
    ImageView takeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_bio_sdk);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.bio_sdk_activity_name));
            invalidateOptionsMenu();
        }

        takeFingerLinearLayout = (RelativeLayout) findViewById(R.id.ln_take_finger);
        takeImage              = (ImageView) findViewById(R.id.i_take_image_bio_sdk);
    }

}