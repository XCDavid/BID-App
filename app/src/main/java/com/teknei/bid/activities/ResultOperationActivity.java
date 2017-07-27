package com.teknei.bid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.utils.SharedPreferencesUtils;

public class ResultOperationActivity extends AppCompatActivity implements View.OnClickListener{
    Button finishOperation;
    Button tryAgainOperation;
    TextView tvOperationResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_operation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.result_operation_activity_name));
            invalidateOptionsMenu();
        }

        String operationID = SharedPreferencesUtils.readFromPreferencesString(this,SharedPreferencesUtils.OPERATION_ID,"");

        finishOperation = (Button) findViewById(R.id.b_end_result_operation);
        tryAgainOperation = (Button) findViewById(R.id.b_end_result_operation);
        tvOperationResult = (TextView) findViewById(R.id.tv_operation_result);
        finishOperation.setOnClickListener(this);
        tryAgainOperation.setOnClickListener(this);

        tvOperationResult.setText(operationID);
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
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.TIMESTAMP_CREDENTIALS);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.TIMESTAMP_FACE);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.TIMESTAMP_DOCUMENT);
        SharedPreferencesUtils.deleteFromPreferences(this,SharedPreferencesUtils.TIMESTAMP_FINGERPRINTS);
    }
}
