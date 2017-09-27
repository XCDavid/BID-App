package com.teknei.bid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.teknei.bid.R;
import com.teknei.bid.activities.BaseActivity;
import com.teknei.bid.utils.ApiConstants;

/**
 * Created by rgarciav on 18/09/2017.
 */

public class DataValidation extends Dialog implements View.OnClickListener {

    Button okButton;
    Button cancelButton;
    TextView txvTitle;
    TextView txvMessage;
    Activity activityOrigin;

    String titleIn;
    String menssageIn;
    int    actionIn;

    public DataValidation(Activity context, String title, String message) {
        super(context);
        activityOrigin = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.alert_dialog);

        this.titleIn    = title;
        this.menssageIn = message;

        txvTitle     = (TextView) findViewById(R.id.alert_title);
        txvMessage   = (TextView) findViewById(R.id.alert_message);
        okButton     = (Button)   findViewById(R.id.ok_buttom);
        cancelButton = (Button)   findViewById(R.id.cancel_buttom);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        txvTitle.setText(titleIn);
        txvMessage.setText(menssageIn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_buttom:
                dismiss();
            break;

            case R.id.cancel_buttom:
                dismiss();
            break;
        }
    }
}
