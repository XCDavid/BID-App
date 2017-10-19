package com.teknei.bid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentResumeDialog extends Dialog implements View.OnClickListener {

    Button continueButton;
    EditText txvName;
    EditText txvApPat;
    EditText txvApMat;

    EditText txvStreet;
    EditText txvSuburb;
    EditText txvPostcode;
    EditText txvLocality;
    EditText txvState;

    Activity activityOrigin;

    public DocumentResumeDialog (Activity context) {
        super(context);
        activityOrigin = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.document_resume_dialog);
        txvName = (EditText) findViewById(R.id.tv_name_document_result_resume);
        txvApPat = (EditText) findViewById(R.id.tv_appat_document_result_resume);
        txvApMat = (EditText) findViewById(R.id.tv_apmat_document_result_resume);

        txvStreet   = (EditText) findViewById(R.id.tv_street_document_result_resume);
        txvSuburb   = (EditText) findViewById(R.id.tv_suburb_document_result_resume);
        txvPostcode = (EditText) findViewById(R.id.tv_postcode_document_result_resume);
        txvLocality = (EditText) findViewById(R.id.tv_locality_document_result_resume);
        txvState    = (EditText) findViewById(R.id.tv_state_document_result_resume);

        continueButton = (Button) findViewById(R.id.b_continue_document_result_resume);
        continueButton.setOnClickListener(this);

        String jsonString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE, "{}");
        String name = "";
        String apPat = "";
        String apMat = "";
        String street   = "";
        String suburb   = "";
        String postCode = "";
        String locality = "";
        String state    = "";

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            name = jsonObject.optString("name");
            apPat = jsonObject.optString("appat");
            apMat = jsonObject.optString("apmat");

            street   = jsonObject.optString("street");      // Calle
            suburb   = jsonObject.optString("suburb");      // Colonia
            postCode = jsonObject.optString("postCode");    // Codigo Postal
            locality = jsonObject.optString("locality");    // Localidad
            state    = jsonObject.optString("state");       // Estado

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txvName.setText(name);
        txvApPat.setText(apPat);
        txvApMat.setText(apMat);

        txvStreet.setText(street);
        txvSuburb.setText(suburb);
        txvPostcode.setText(postCode);
        txvLocality.setText(locality);
        txvState.setText(state);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == continueButton) {
            ((BaseActivity) activityOrigin).goNext();
        }
    }
}