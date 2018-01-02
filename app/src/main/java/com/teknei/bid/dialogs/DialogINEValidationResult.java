package com.teknei.bid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.response.ResponseVerifyCecoban;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class DialogINEValidationResult extends Dialog implements View.OnClickListener {

    private TextView txtDateRequest;
    private TextView txtResponseDescription;
    private TextView txtCustomerId;
    private TextView txtSimilitud7;
    private TextView txtResponseCodeMinusia;
    private TextView txtSimilitud2;
    private TextView txtCodeData;
    private TextView txtRegistrationYear;
    private TextView txtElectorKey;
    private TextView txtSurname;
    private TextView txtYearEmission;
    private TextView txtNumberEmission;
    private TextView txtCIC;
    private TextView txtName;
    private TextView txtcurp;
    private TextView txtOcr;
    private TextView txtLastSurname;

    private Button   btnContinue;

    public DialogINEValidationResult(Activity context, ResponseVerifyCecoban responseValue) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_ine_validation_result);

        txtDateRequest              =  (TextView) findViewById(R.id.divr_et_date_request);
        txtResponseDescription      =  (TextView) findViewById(R.id.divr_et_response_description);
        txtCustomerId               =  (TextView) findViewById(R.id.divr_et_customer_id);
        txtSimilitud7               =  (TextView) findViewById(R.id.divr_et_similitud_7);
        txtResponseCodeMinusia      =  (TextView) findViewById(R.id.divr_et_response_code_minusia);
        txtSimilitud2               =  (TextView) findViewById(R.id.divr_et_similitud_2);

        txtCodeData                 =  (TextView) findViewById(R.id.divr_et_response_code_data);
        txtRegistrationYear         =  (TextView) findViewById(R.id.divr_et_registration_year);
        txtElectorKey               =  (TextView) findViewById(R.id.divr_et_elector_key);
        txtSurname                  =  (TextView) findViewById(R.id.divr_et_surname);
        txtYearEmission             =  (TextView) findViewById(R.id.divr_et_year_emission);
        txtNumberEmission           =  (TextView) findViewById(R.id.divr_et_number_emission);
        txtCIC                      =  (TextView) findViewById(R.id.divr_et_cic);
        txtName                     =  (TextView) findViewById(R.id.divr_et_name);
        txtcurp                     =  (TextView) findViewById(R.id.divr_et_curp);
        txtOcr                      =  (TextView) findViewById(R.id.divr_et_ocr);
        txtLastSurname              =  (TextView) findViewById(R.id.divr_et_last_surname);

        btnContinue                 = (Button) findViewById(R.id.divr_btn_continue);
        btnContinue.setOnClickListener(this);

        txtDateRequest.setText          (responseValue.getResponse().getFechaHoraPeticion());
        txtResponseDescription.setText  (responseValue.getResponse().getDescripcionRespuesta());
        txtCustomerId.setText           (responseValue.getResponse().getFolioCliente()+"");
        txtSimilitud7.setText           (responseValue.getResponse().getMinutiaeResponse().getSimilitud7().toString());
        txtResponseCodeMinusia.setText  (responseValue.getResponse().getMinutiaeResponse().getCodigoRespuestaMinucia().toString());
        txtSimilitud2.setText           (responseValue.getResponse().getMinutiaeResponse().getSimilitud2().toString());
        txtCodeData.setText             (responseValue.getResponse().getDataResponse().getCodigoRespuestaDatos().toString());

        txtRegistrationYear.setText     (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getAnioRegistro()));
        txtElectorKey.setText           (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getClaveElector()));
        txtSurname.setText              (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getApellidoPaterno()));
        txtYearEmission.setText         (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getAnioEmision()));
        txtNumberEmission.setText       (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getNumeroEmisionCredencial()));
        txtCIC.setText                  (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getCic()));
        txtName.setText                 (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getNombre()));
        txtcurp.setText                 (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getCurp()));
        txtOcr.setText                  (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getOcr()));
        txtLastSurname.setText          (validateValue(responseValue.getResponse().getDataResponse().getRespuestaComparacion().getApellidoMaterno()));
    }

    public String validateValue (boolean value) {

        if (value) {
            return "Verdadero";
        }
        return "Falso";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.divr_btn_continue:

                dismiss();

                break;
        }
    }
}
