package com.teknei.bid.activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobbeel.mobblicense.IOUtils;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.GetContract;
import com.teknei.bid.mobbsign.MobbSignActivity;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ResultOperationActivity extends BaseActivity implements View.OnClickListener{
    Button finishOperation;
    Button tryAgainOperation;
    Button contractGenerate;
    Button contractSing;
    TextView tvOperationResult;

    private String operationID = "";
    private int    typePerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_operation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.result_operation_activity_name));
            invalidateOptionsMenu();
        }

        operationID = SharedPreferencesUtils.readFromPreferencesString(this,SharedPreferencesUtils.OPERATION_ID,"");
        typePerson  = Integer.parseInt(SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TYPE_PERSON, ""));

        finishOperation   = (Button) findViewById(R.id.b_end_result_operation);
        tryAgainOperation = (Button) findViewById(R.id.b_end_result_operation);
        contractGenerate  = (Button) findViewById(R.id.b_contract_generate_result_operation);
        contractSing      = (Button) findViewById(R.id.b_contract_sign_result_operation);

        tvOperationResult = (TextView) findViewById(R.id.tv_operation_result);

        finishOperation.setOnClickListener(this);
        tryAgainOperation.setOnClickListener(this);
        contractGenerate.setOnClickListener(this);
        contractSing.setOnClickListener(this);

        tvOperationResult.setText(operationID);

        if (typePerson == ApiConstants.TYPE_OPERATOR) {
            contractGenerate.setVisibility(View.GONE);
            contractSing.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_end_result_operation:
                SharedPreferencesUtils.cleanSharedPreferencesOperation(ResultOperationActivity.this);

                Intent end = new Intent(ResultOperationActivity.this, PersonSelectionActivity.class);
                end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(end);
                finish();
                break;

            case R.id.b_try_again_result_operation:
                break;

            case R.id.b_contract_generate_result_operation:
                //Add the bundle to the intent
                String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
                operationID = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");
                new GetContract(ResultOperationActivity.this, token, Integer.valueOf(operationID)).execute();
                break;

            case R.id.b_contract_sign_result_operation:

                String opcionFingerprintReader = SharedPreferencesUtils.readFromPreferencesString
                                                (ResultOperationActivity.this, SharedPreferencesUtils.FINGERPRINT_READER, "");

                if (opcionFingerprintReader.equals("watson")){

                    Intent i = new Intent(ResultOperationActivity.this, ContractFingerSignatureWatson.class);
                    startActivity(i);

                } else if (opcionFingerprintReader.equals("biosmart")) {

                    Intent i = new Intent(ResultOperationActivity.this, ContractFingerSignatureBioSmart.class);
                    startActivity(i);

                } else {

                    Intent i = new Intent(ResultOperationActivity.this, ContractFingerSignatureMSO.class);
                    startActivity(i);
                }

                break;
        }
    }

    @Override
    public void goNext() {
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "contract_" + operationID + ".pdf");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(intent);

//        InputStream inputStream;
        String DOC_ID = "0000013ee31c54e348a98eaed7d221858a40";
        int RC_MOBBSIGN = 1;
        Intent intent = new Intent(ResultOperationActivity.this, MobbSignActivity.class);
        try {
            InputStream targetStream = new FileInputStream(file);
            intent.putExtra(MobbSignActivity.EXTRA_DOCUMENT, IOUtils.toByteArray(targetStream));
            intent.putExtra(MobbSignActivity.EXTRA_DOC_ID, DOC_ID);
            intent.putExtra("id_operation", operationID);
            targetStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, RC_MOBBSIGN);
    }

    @Override
    public void onBackPressed() { }
}
