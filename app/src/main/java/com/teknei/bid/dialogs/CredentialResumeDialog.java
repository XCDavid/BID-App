package com.teknei.bid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class CredentialResumeDialog extends Dialog implements View.OnClickListener {
    Button continueButton;
    TextView txvName;
    TextView txvApPat;
    TextView txvApMat;
    TextView txvCurp;
    TextView txvMRZ;
    TextView txvOCR;
    TextView txvAddress;
    TextView txvValidity;
    Activity activityOrigin;

    public CredentialResumeDialog(Activity context) {
        super(context);
        activityOrigin = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.credential_resume_dialog);
        txvName = (TextView) findViewById(R.id.tv_name_credential_result_resume);
        txvApPat = (TextView) findViewById(R.id.tv_appat_credential_result_resume);
        txvApMat = (TextView) findViewById(R.id.tv_apmat_credential_result_resume);
        txvCurp = (TextView) findViewById(R.id.tv_curp_credential_result_resume);
        txvMRZ = (TextView) findViewById(R.id.tv_mrz_credential_result_resume);
        txvOCR = (TextView) findViewById(R.id.tv_ocr_credential_result_resume);
        txvAddress = (TextView) findViewById(R.id.tv_address_credential_result_resume);
        txvValidity = (TextView) findViewById(R.id.tv_validity_credential_result_resume);

        continueButton = (Button) findViewById(R.id.b_continue_credential_result_resume);
        continueButton.setOnClickListener(this);

        String jsonString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, "{}");
        String name = "";
        String apPat = "";
        String apMat = "";
        String curp = "";
        String mrz = "";
        String ocr = "";
        String validity = "";
        String address = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            name = jsonObject.optString("name");
            apPat = jsonObject.optString("appat");
            apMat = jsonObject.optString("apmat");
            curp = jsonObject.optString("curp");
            ocr = jsonObject.optString("ocr");
            mrz = jsonObject.optString("mrz");
            validity = jsonObject.optString("validity");
            address = jsonObject.optString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txvName.setText(name);
        txvApPat.setText(apPat);
        txvApMat.setText(apMat);
        txvCurp.setText(curp);
        txvMRZ.setText(mrz);
        txvOCR.setText(ocr);
        txvAddress.setText(address);
        txvValidity.setText(validity);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == continueButton) {
            ((BaseActivity) activityOrigin).goNext();
        }
    }
}
