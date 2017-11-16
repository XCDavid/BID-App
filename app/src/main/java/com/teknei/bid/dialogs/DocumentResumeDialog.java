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
import com.teknei.bid.asynctask.DataCredencialSend;
import com.teknei.bid.asynctask.DataDocumentSend;
import com.teknei.bid.domain.AddressDTO;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentResumeDialog extends Dialog implements View.OnClickListener {

    Button continueButton;

    EditText txvStreet;
    EditText txvSuburb;
    EditText txvZipCode;
    EditText txvLocality;
    EditText txvState;

    Activity activityOrigin;

    private AddressDTO valueDto;

    public DocumentResumeDialog (Activity context) {
        super(context);
        activityOrigin = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.document_resume_dialog);

        txvStreet   = (EditText) findViewById(R.id.tv_street_document_result_resume);
        txvSuburb   = (EditText) findViewById(R.id.tv_suburb_document_result_resume);
        txvZipCode  = (EditText) findViewById(R.id.tv_zipcode_document_result_resume);
        txvLocality = (EditText) findViewById(R.id.tv_locality_document_result_resume);
        txvState    = (EditText) findViewById(R.id.tv_state_document_result_resume);

        continueButton = (Button) findViewById(R.id.b_continue_document_result_resume);
        continueButton.setOnClickListener(this);

        String jsonString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.DOCUMENT_OPERATION, "{}");
        String street   = "";
        String suburb   = "";
        String zipCode  = "";
        String locality = "";
        String state    = "";

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            street   = jsonObject.optString("street");      // Calle
            suburb   = jsonObject.optString("suburb");      // Colonia
            zipCode  = jsonObject.optString("zipCode");    // Codigo Postal
            locality = jsonObject.optString("locality");    // Localidad
            state    = jsonObject.optString("state");       // Estado

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

            valueDto = new AddressDTO();

            valueDto.setCountry   ("");
            //valueDto.setExtNumber (0);
            //valueDto.setIntNumber (0);
            valueDto.setLocality  (txvLocality.getText().toString());
            valueDto.setMunicipio (txvLocality.getText().toString());
            valueDto.setState     (txvState.getText().toString());
            valueDto.setStreet    (txvStreet.getText().toString());
            valueDto.setSuburb    (txvSuburb.getText().toString());
            valueDto.setZipCode   (txvZipCode.getText().toString());

            String operationID = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.OPERATION_ID, "");
            String token       = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TOKEN_APP, "");

            new DataDocumentSend(activityOrigin, token, ApiConstants.TYPE_PASSPORT+"", operationID, valueDto).execute();
        }
    }
}