package com.teknei.bid1.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.teknei.bid1.R;
import com.teknei.bid1.activities.BaseActivity;
import com.teknei.bid1.utils.ApiConstants;

public class AlertDialog extends Dialog implements View.OnClickListener {
    Button okButton;
    Button cancelButton;
    TextView txvTitle;
    TextView txvMessage;
    Activity activityOrigin;

    String titleIn;
    String menssageIn;
    int actionIn;
    int flowStep;

    public AlertDialog(Activity context, String title, String message, int action) {
        super(context);
        activityOrigin = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.alert_dialog);

        this.titleIn = title;
        this.menssageIn = message;
        this.actionIn = action;

        txvTitle = (TextView) findViewById(R.id.alert_title);
        txvMessage = (TextView) findViewById(R.id.alert_message);
        okButton = (Button) findViewById(R.id.ok_buttom);
        cancelButton = (Button) findViewById(R.id.cancel_buttom);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        txvTitle.setText(titleIn);
        txvMessage.setText(menssageIn);
        if (actionIn == ApiConstants.ACTION_TRY_AGAIN || actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL)
            okButton.setText(activityOrigin.getString(R.string.message_ws_tray_again));
        if (actionIn == ApiConstants.ACTION_LOG_OUT || actionIn == ApiConstants.ACTION_CANCEL_OPERATION || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL)
            cancelButton.setVisibility(View.VISIBLE);
    }

    public AlertDialog(Activity context, String title, String message, int action, int flowStep) {
        super(context);
        activityOrigin = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.alert_dialog);

        this.titleIn = title;
        this.menssageIn = message;
        this.actionIn = action;
        this.flowStep = flowStep;

        txvTitle = (TextView) findViewById(R.id.alert_title);
        txvMessage = (TextView) findViewById(R.id.alert_message);
        okButton = (Button) findViewById(R.id.ok_buttom);
        cancelButton = (Button) findViewById(R.id.cancel_buttom);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        txvTitle.setText(titleIn);
        txvMessage.setText(menssageIn);
        if (actionIn == ApiConstants.ACTION_TRY_AGAIN || actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL)
            okButton.setText(activityOrigin.getString(R.string.message_ws_tray_again));
        if (actionIn == ApiConstants.ACTION_LOG_OUT || actionIn == ApiConstants.ACTION_CANCEL_OPERATION || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL)
            cancelButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_buttom:
                dismiss();
                if (actionIn == ApiConstants.ACTION_LOG_OUT || actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION) {
                    ((BaseActivity) activityOrigin).logOut();
                }
                if (actionIn == ApiConstants.ACTION_TRY_AGAIN || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL) {
                    //BORRAR
//                    ((BaseActivity) activityOrigin).goNext();
                    //DES - COMENTAR
                    ((BaseActivity) activityOrigin).sendPetition();
                }
                if (actionIn == ApiConstants.ACTION_CANCEL_OPERATION) {
                    ((BaseActivity) activityOrigin).cancelOperation();
                }
                if (actionIn == ApiConstants.ACTION_GO_NEXT) {
                    ((BaseActivity) activityOrigin).goNext();
                }
                if (actionIn == ApiConstants.ACTION_GO_STEP) {
                    ((BaseActivity) activityOrigin).goStep(flowStep);
                }
                break;
            case R.id.cancel_buttom:
                dismiss();
                break;
        }
    }
}
