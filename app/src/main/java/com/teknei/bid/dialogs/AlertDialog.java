package com.teknei.bid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.activities.FingerWatsonActivity;
import com.teknei.bid.utils.ApiConstants;

public class AlertDialog extends Dialog implements View.OnClickListener {

    private final String CLASS_NAME = getClass().getSimpleName();

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

        if (actionIn == ApiConstants.ACTION_TRY_AGAIN || actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION ||
             actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE ||
              actionIn == ApiConstants.ACTION_TRY_AGAIN_LOCAL || actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION_LOCAL ||
               actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL_LOCAL || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_LOCAL
                || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_DOC)
            okButton.setText    (activityOrigin.getString(R.string.message_ws_tray_again));

        if (actionIn == ApiConstants.ACTION_LOG_OUT || actionIn == ApiConstants.ACTION_CANCEL_OPERATION  ||
             actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL || actionIn == ApiConstants.ACTION_LOG_OUT_LOCAL ||
              actionIn == ApiConstants.ACTION_CANCEL_OPERATION_LOCAL  || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL_LOCAL
                || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_DOC) {
            cancelButton.setVisibility(View.VISIBLE);

        } else if (actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_LOCAL
                || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_DOC) {

            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText(activityOrigin.getString(R.string.continue_message_dialog));

        }
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

        if (actionIn == ApiConstants.ACTION_TRY_AGAIN || actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION ||
             actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL || actionIn == ApiConstants.ACTION_TRY_AGAIN_LOCAL ||
              actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION_LOCAL || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL_LOCAL
                || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_DOC)
            okButton.setText(activityOrigin.getString(R.string.message_ws_tray_again));

        if (actionIn == ApiConstants.ACTION_LOG_OUT || actionIn == ApiConstants.ACTION_CANCEL_OPERATION ||
             actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL || actionIn == ApiConstants.ACTION_LOG_OUT_LOCAL ||
              actionIn == ApiConstants.ACTION_CANCEL_OPERATION_LOCAL || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL_LOCAL
                || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_DOC)
            cancelButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ok_buttom:
                dismiss();

                if (actionIn == ApiConstants.ACTION_LOG_OUT || actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION) {
                    ((BaseActivity) activityOrigin).logOut();

                } else if (actionIn == ApiConstants.ACTION_LOG_OUT_LOCAL || actionIn == ApiConstants.ACTION_BLOCK_CANCEL_OPERATION_LOCAL) {
                    ((FingerWatsonActivity) activityOrigin).logOut();

                } else if (actionIn == ApiConstants.ACTION_TRY_AGAIN || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL
                        || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE) {
                    ((BaseActivity) activityOrigin).sendPetition();

                } else if (actionIn == ApiConstants.ACTION_TRY_AGAIN_LOCAL || actionIn == ApiConstants.ACTION_TRY_AGAIN_CANCEL_LOCAL
                        || actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_LOCAL) {
                    ((FingerWatsonActivity) activityOrigin).sendPetition();

                } else if (actionIn == ApiConstants.ACTION_CANCEL_OPERATION) {
                    ((BaseActivity) activityOrigin).cancelOperation();

                } else if (actionIn == ApiConstants.ACTION_CANCEL_OPERATION_LOCAL) {
                    ((FingerWatsonActivity) activityOrigin).cancelOperation();

                } else if (actionIn == ApiConstants.ACTION_GO_NEXT) {
                    ((BaseActivity) activityOrigin).goNext();

                } else if (actionIn == ApiConstants.ACTION_GO_NEXT_LOCAL) {
                    ((FingerWatsonActivity) activityOrigin).goNext();

                } else if (actionIn == ApiConstants.ACTION_GO_STEP) {
                    ((BaseActivity) activityOrigin).goStep(flowStep);

                } else if (actionIn == ApiConstants.ACTION_GO_STEP_LOCAL) {
                    ((FingerWatsonActivity) activityOrigin).goStep(flowStep);

                } else if (actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_DOC) {
                    ((BaseActivity) activityOrigin).sendPetition();

                }
                break;
            case R.id.cancel_buttom:
                dismiss();

                if (actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE) {
                    ((BaseActivity) activityOrigin).goNext();

                } else if (actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_LOCAL) {
                    ((FingerWatsonActivity) activityOrigin).goNext();

                }else if (actionIn == ApiConstants.ACTION_TRY_AGAIN_CONTINUE_DOC) {
                    DocumentResumeDialog dialogoAlert;
                    dialogoAlert = new DocumentResumeDialog(activityOrigin);
                    dialogoAlert.setCancelable(false);
                    dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialogoAlert.show();
                }

                break;
        }
    }
}
