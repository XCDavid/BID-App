package com.teknei.bid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.dialogs.INEResumeDialog;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class FakeINEActivity extends AppCompatActivity implements View.OnClickListener {
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
                curp = jsonObject.getString("curp");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String mrz = jsonObject.getString("mrz");
//                                String mrz = "IDMEX1587903166<<4499068496638\\n8512246M2712310MEX<02<<12416<4\\nHERNANDEZ<ERAZO<<MONICA<<<<<<<";
//                                String uno = "\\\n";
//                                String dos = "\\n";
//                                String tres = "\n";
                String cuatro = "\\\\n";
//                                String mrzSplit1[] = mrz.split(uno);
//                                String mrzSplit2[] = mrz.split(dos);
//                                String mrzSplit3[] = mrz.split(tres);
                String mrzSplit4[] = mrz.split(cuatro);
                String firstLine = mrzSplit4[0];
                String firstSplit[] = firstLine.split("\\<\\<");
//                String ocr;
                if (firstSplit.length > 1)
                    ocr = firstSplit[1];
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
                Intent i = new Intent(FakeINEActivity.this, PayConfirmationActivity.class);
                startActivity(i);
                break;
            case R.id.b_resume_fake_ine:
                resumeDialog.show();
                break;
        }
    }
}
