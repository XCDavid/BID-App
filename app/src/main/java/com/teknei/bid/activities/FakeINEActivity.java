package com.teknei.bid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.ConfirmPayOperation;
import com.teknei.bid.dialogs.INEResumeDialog;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class FakeINEActivity extends BaseActivity implements View.OnClickListener {
    TextView tvName;
    TextView tvLastName;
    TextView tvMotherLastNAme;
    TextView tvCurp;
    TextView tvOCR;
    TextView tvValidity;
    TextView tvAddress;
    TextView tvCoincidencePoints;
    Button continueFake;
    Button resumeFake;

    INEResumeDialog resumeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_ine);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.fake_ine_activity_name));
            invalidateOptionsMenu();
        }

        String jsonString = SharedPreferencesUtils.readFromPreferencesString(FakeINEActivity.this, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, "{}");
        String name = "";
        String apPat = "";
        String apMat = "";
        String curp = "";
        String ocr = "";
        String validity = "";
        String address = "";
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((1500 - 1400) + 1) + 1400;
        String coincidence = randomNum + ""; //Nyumeros aleatorios de coinicidencia entre 1400 y 1500
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            try {
                name = jsonObject.getString("name");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                apPat = jsonObject.getString("appat");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                apMat = jsonObject.getString("apmat");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                curp = jsonObject.optString("curp");
                if (curp.equals("")) {
                    String jsonFormString = SharedPreferencesUtils.readFromPreferencesString(FakeINEActivity.this, SharedPreferencesUtils.JSON_INIT_FORM, "{}");
                    JSONObject jsonFormObject = new JSONObject(jsonFormString);
                    try {
                        curp = jsonFormObject.getString("curp");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ocr = jsonObject.getString("ocr");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String mrz = jsonObject.getString("mrz");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                validity = jsonObject.getString("validity");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                address = jsonObject.getString("address");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvName = (TextView) findViewById(R.id.tv_name_fake_ine);
        tvLastName = (TextView) findViewById(R.id.tv_first_ap_fake_ine);
        tvMotherLastNAme = (TextView) findViewById(R.id.tv_second_ap_fake_ine);
        tvCurp = (TextView) findViewById(R.id.tv_curp_fake_ine);
        tvOCR = (TextView) findViewById(R.id.tv_ocr_fake_ine);
        tvValidity = (TextView) findViewById(R.id.tv_validity_fake_ine);
        tvAddress = (TextView) findViewById(R.id.tv_address_fake_ine);
        tvCoincidencePoints = (TextView) findViewById(R.id.tv_coincidence_points_fake_ine);
        continueFake = (Button) findViewById(R.id.b_continue_fake_ine);
        resumeFake = (Button) findViewById(R.id.b_resume_fake_ine);

        continueFake.setOnClickListener(this);
        resumeFake.setOnClickListener(this);

        tvName.setText(name);
        tvLastName.setText(apPat);
        tvMotherLastNAme.setText(apMat);
        tvCurp.setText(curp);
        tvOCR.setText(ocr);
        tvValidity.setText(validity);
        tvAddress.setText(address);
        tvCoincidencePoints.setText(coincidence);

        resumeDialog = new INEResumeDialog(FakeINEActivity.this);
//        resumeDialog.setCancelable(false);
        resumeDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_continue_fake_ine:
//                Intent i = new Intent(FakeINEActivity.this, PayConfirmationActivity.class);
//                startActivity(i);
                sendPetition();
                break;
            case R.id.b_resume_fake_ine:
                resumeDialog.show();
                break;
        }
    }

    //LLamada fake a finalizar la operación para no presentar la pantalla de CONFIRMACION DE PAGO
    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String payOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.PAY_OPERATION, "");
        if (payOperation.equals("")) {
            String jsonString = buildJSON();
            new ConfirmPayOperation(FakeINEActivity.this, token, jsonString).execute();
        } else {
            goNext();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(FakeINEActivity.this, ResultOperationActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(FakeINEActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("tipoPago", "efectivo");
            jsonObject.put("referencia", "0");
            jsonObject.put("importe", "0");
            jsonObject.put("resultadoPago", "ok");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
