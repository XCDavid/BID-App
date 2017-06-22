package com.mobbeel.mobbscan.simple.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobbeel.mobbscan.simple.R;

public class DocumentScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_scan);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.document_scan_activity_name));
            invalidateOptionsMenu();
        }
    }
}
