package com.teknei.bid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.morpho.common.view.BasicActivity;
import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.activities.ContractFingerSignatureBioSmart;
import com.teknei.bid.activities.ContractFingerSignatureMSO;
import com.teknei.bid.activities.ContractFingerSignatureWatson;
import com.teknei.bid.activities.ResultOperationActivity;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.SharedPreferencesUtils;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class FinishSingContractDialog extends Dialog implements View.OnClickListener{
    private Button btnOK;
    private Button btnRetry;
    private Button btnCancel;

    private TextView txvTitle;
    private TextView txvMessage;
    private Activity activityOrigin;

    private String titleIn;
    private String menssageIn;
    private String opcionReader;

    private int    actionIn;
    private int    actionFragment;

    public FinishSingContractDialog(Activity context, String title, String message, int action, int actionFragment, String opcionFingerprintReader) {
        super(context);

        activityOrigin = context;

        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.finish_sing_contract_dialog);

        this.titleIn        = title;
        this.menssageIn     = message;
        this.actionIn       = action;
        this.actionFragment = actionFragment;
        this.opcionReader   = opcionFingerprintReader;

        txvTitle   = (TextView) findViewById(R.id.fscd_txt_title);
        txvMessage = (TextView) findViewById(R.id.fscd_txt_message);

        btnOK     = (Button) findViewById(R.id.fscd_btn_ok);
        btnRetry  = (Button) findViewById(R.id.fscd_btn_retry);
        btnCancel = (Button) findViewById(R.id.fscd_btn_cancel);

        btnOK.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        txvTitle.setText(titleIn);
        txvMessage.setText(menssageIn);


        switch (action){
            case ApiConstants.ACTION_FINISH_SING_CONTRACT_GO_NEXT:
                btnOK.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                break;

            case ApiConstants.ACTION_FINISH_SING_CONTRACT_TRY_AGAIN:
                btnOK.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                break;

            case ApiConstants.ACTION_FINISH_SING_CONTRACT_CANCEL:
                btnOK.setVisibility(View.GONE);
                btnRetry.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                break;

            case ApiConstants.ACTION_FINISH_SING_CONTRACT_TRY_AGAIN_CANCEL:
                btnOK.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                break;

            case ApiConstants.ACTION_FINISH_SING_CONTRACT_TRY_AGAIN_CONTINUE:
                btnOK.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fscd_btn_ok:
                dismiss();

                if (actionFragment == ApiConstants.ACTION_SEND_FINGER_CONTRACT) {

                    if (opcionReader.equals("watson")){

                        ((ContractFingerSignatureWatson) activityOrigin).confirmContract();

                    } else if (opcionReader.equals("biosmart")) {

                        ((ContractFingerSignatureBioSmart) activityOrigin).confirmContract();

                    } else {

                        ((ContractFingerSignatureMSO) activityOrigin).confirmContract();
                    }

                } else {

                        ((BaseActivity) activityOrigin).goNext();

                }
                break;

            case R.id.fscd_btn_retry:
                dismiss();

                if (actionFragment != ApiConstants.ACTION_SEND_FINGER_CONTRACT) {

                    if (opcionReader.equals("watson")){

                        ((ContractFingerSignatureWatson) activityOrigin).confirmContract();

                    } else if (opcionReader.equals("biosmart")) {

                        ((ContractFingerSignatureBioSmart) activityOrigin).confirmContract();

                    } else {

                        ((ContractFingerSignatureMSO) activityOrigin).confirmContract();
                    }

                } else {

                    ((BaseActivity) activityOrigin).sendPetition();

                }

                break;

            case R.id.fscd_btn_cancel:
                dismiss();

                if (actionFragment == ApiConstants.ACTION_CONFIRM_FINGER_CONTRACT) {

                    ((BaseActivity) activityOrigin).goNext();

                }
                break;
        }

    }
}
