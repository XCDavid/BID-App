package com.teknei.bid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.teknei.bid.R;
import com.teknei.bid.utils.SharedPreferencesUtils;

public class ResultOperationActivity extends AppCompatActivity implements View.OnClickListener{
    Button finishOperation;
    Button tryAgainOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_operation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.result_operation_activity_name));
            invalidateOptionsMenu();
        }

        finishOperation = (Button) findViewById(R.id.b_end_result_operation);
        tryAgainOperation = (Button) findViewById(R.id.b_end_result_operation);
        finishOperation.setOnClickListener(this);
        tryAgainOperation.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_end_result_operation:
                cleanSharedPreferences();

                Intent end = new Intent(ResultOperationActivity.this, FormActivity.class);
                end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(end);
                finish();
                break;
            case R.id.b_try_again_result_operation:
                break;
        }
    }

    private void cleanSharedPreferences() {
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.OPERATION_ID);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.ID_SCAN);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.SCAN_SAVE_ID);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.FACE_OPERATION);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.DOCUMENT_OPERATION);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.FINGERS_OPERATION);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.PAY_OPERATION);
    }
}
