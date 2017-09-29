package com.teknei.bid1.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.teknei.bid1.R;
import com.teknei.bid1.utils.SharedPreferencesUtils;

public class INEResumeDialog extends Dialog implements View.OnClickListener {
    Button cancelButton;
    TextView txvCredentials;
    TextView txvFace;
    TextView txvDocument;
    TextView txvFingerprints;
    Activity activityOrigin;

    public INEResumeDialog(Activity context) {
        super(context);
        activityOrigin = context;
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */
        setContentView(R.layout.resume_operation_dialog);
        txvCredentials = (TextView) findViewById(R.id.tv_credential_fake_ine);
        txvFace = (TextView) findViewById(R.id.tv_face_fake_ine);
        txvDocument = (TextView) findViewById(R.id.tv_document_fake_ine);
        txvFingerprints = (TextView) findViewById(R.id.tv_fingerprints_fake_ine);
        cancelButton = (Button) findViewById(R.id.b_cancel_dialog_fake_ine);
        cancelButton.setOnClickListener(this);

        String timeCredentialsString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TIMESTAMP_CREDENTIALS, "");
        String timeFaceString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TIMESTAMP_FACE, "");
        String timeDocumentString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TIMESTAMP_DOCUMENT, "");
        String timeFingerprintsString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TIMESTAMP_FINGERPRINTS, "");
        txvCredentials.setText(timeCredentialsString);
        txvFace.setText(timeFaceString);
        txvDocument.setText(timeDocumentString);
        txvFingerprints.setText(timeFingerprintsString);
    }

    @Override
    public void onClick(View v) {
        if (v == cancelButton) {
            dismiss();
        }
    }

    @Override
    public void show() {
        super.show();
        String timeCredentialsString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TIMESTAMP_CREDENTIALS, "");
        String timeFaceString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TIMESTAMP_FACE, "");
        String timeDocumentString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TIMESTAMP_DOCUMENT, "");
        String timeFingerprintsString = SharedPreferencesUtils.readFromPreferencesString(activityOrigin, SharedPreferencesUtils.TIMESTAMP_FINGERPRINTS, "");
        txvCredentials.setText(timeCredentialsString);
        txvFace.setText(timeFaceString);
        txvDocument.setText(timeDocumentString);
        txvFingerprints.setText(timeFingerprintsString);

    }
}
