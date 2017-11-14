package com.teknei.bid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.asynctask.DataCredencialSend;
import com.teknei.bid.asynctask.GetDetailFindOperation;
import com.teknei.bid.domain.CredentialDTO;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class CredentialResumeDialog extends Dialog implements View.OnClickListener {
    Button continueButton;
    EditText txvName;
    EditText txvApPat;
    EditText txvApMat;
    EditText txvCurp;
    EditText txvMRZ;
    EditText txvOCR;
    EditText txvAddress;
    EditText txvValidity;

    EditText txvStreet;
    EditText txvSuburb;
    EditText txvZipCode;
    EditText txvLocality;
    EditText txvState;

    Activity activityOrigin;

    CredentialDTO   credentialDTO;

    public CredentialResumeDialog(Activity context) {
        super(context);
        activityOrigin = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.credential_resume_dialog);
        txvName = (EditText) findViewById(R.id.tv_name_credential_result_resume);
        txvApPat = (EditText) findViewById(R.id.tv_appat_credential_result_resume);
        txvApMat = (EditText) findViewById(R.id.tv_apmat_credential_result_resume);
        txvCurp = (EditText) findViewById(R.id.tv_curp_credential_result_resume);
        txvMRZ = (EditText) findViewById(R.id.tv_mrz_credential_result_resume);
        txvOCR = (EditText) findViewById(R.id.tv_ocr_credential_result_resume);
        txvValidity = (EditText) findViewById(R.id.tv_validity_credential_result_resume);

        txvStreet   = (EditText) findViewById(R.id.tv_street_credential_result_resume);
        txvSuburb   = (EditText) findViewById(R.id.tv_suburb_credential_result_resume);
        txvZipCode = (EditText) findViewById(R.id.tv_zipcode_credential_result_resume);
        txvLocality = (EditText) findViewById(R.id.tv_locality_credential_result_resume);
        txvState    = (EditText) findViewById(R.id.tv_state_credential_result_resume);

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
        String address  = "";
        String street   = "";
        String suburb   = "";
        String zipCode = "";
        String locality = "";
        String state    = "";

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

            street   = jsonObject.optString("street");      // Calle
            suburb   = jsonObject.optString("suburb");      // Colonia
            zipCode = jsonObject.optString("zipCode");     // Codigo Postal
            locality = jsonObject.optString("locality");    // Localidad
            state    = jsonObject.optString("state");       // Estado

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txvName.setText(name);
        txvApPat.setText(apPat);
        txvApMat.setText(apMat);
        txvCurp.setText(curp);
        txvMRZ.setText(mrz);
        txvOCR.setText(ocr);
        //txvAddress.setText(address);
        txvValidity.setText(validity);

        txvStreet.setText(street);
        txvSuburb.setText(suburb);
        txvZipCode.setText(zipCode);
        txvLocality.setText(locality);
        txvState.setText(state);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == continueButton) {

            credentialDTO = new CredentialDTO();

            credentialDTO.setNomb  (txvName.getText().toString());

            credentialDTO.setApeMat(txvApPat.getText().toString());

            credentialDTO.setApePat(txvApMat.getText().toString());

            credentialDTO.setCall  (txvStreet.getText().toString());

            credentialDTO.setClavElec("");

            credentialDTO.setCol(txvSuburb.getText().toString());

            credentialDTO.setCp(txvZipCode.getText().toString());

            credentialDTO.setDist("");

            credentialDTO.setEsta(txvState.getText().toString());

            credentialDTO.setFoli("");

            credentialDTO.setLoca("");

            credentialDTO.setMrz(txvMRZ.getText().toString());

            credentialDTO.setMuni(txvLocality.getText().toString());

            credentialDTO.setNoExt("");

            credentialDTO.setNoInt("");

            credentialDTO.setOcr(txvOCR.getText().toString()) ;

            credentialDTO.setSecc("");

            credentialDTO.setUser("");

            credentialDTO.setVige(txvValidity.getText().toString());

            //txvCurp.setText(curp);

            String operationID = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.OPERATION_ID, "");
            String token       = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TOKEN_APP, "");

            new DataCredencialSend(activityOrigin, token, ApiConstants.TYPE_INE+"", operationID, credentialDTO).execute();

        }
    }
}
