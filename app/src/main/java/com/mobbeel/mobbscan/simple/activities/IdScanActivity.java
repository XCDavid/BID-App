package com.mobbeel.mobbscan.simple.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobbeel.mobbscan.api.MobbScanAPI;
import com.mobbeel.mobbscan.api.MobbScanAPIError;
import com.mobbeel.mobbscan.api.MobbScanDocumentSide;
import com.mobbeel.mobbscan.api.MobbScanDocumentType;
import com.mobbeel.mobbscan.api.MobbScanOperationMode;
import com.mobbeel.mobbscan.api.listener.IDDocumentDetectionListener;
import com.mobbeel.mobbscan.api.listener.IDDocumentScanListener;
import com.mobbeel.mobbscan.api.listener.LicenseStatusListener;
import com.mobbeel.mobbscan.api.listener.ScanStartListener;
import com.mobbeel.mobbscan.api.result.MobbScanDetectionResult;
import com.mobbeel.mobbscan.api.result.MobbScanDetectionResultData;
import com.mobbeel.mobbscan.api.result.MobbScanLicenseResult;
import com.mobbeel.mobbscan.api.result.MobbScanScanResult;
import com.mobbeel.mobbscan.api.result.MobbScanScanResultData;
import com.mobbeel.mobbscan.api.result.MobbScanStartScanResult;
import com.mobbeel.mobbscan.document.IDDocument;

import java.util.Date;

import com.mobbeel.mobbscan.simple.R;
import com.mobbeel.mobbscan.simple.dialogs.ProgressDialog;
import com.mobbeel.mobbscan.simple.utils.SharedPreferencesUtils;

public class IdScanActivity extends AppCompatActivity implements View.OnClickListener, IDDocumentScanListener, IDDocumentDetectionListener {
    ImageButton idFrontButton;
    ImageButton idPosteriorButton;
    Button continueButton;
    LinearLayout buttonShowHideResultData;
    LinearLayout sectionResultData;
    ImageView indicatorResultShow;

    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL = 661;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR = 662;

    private String encodedStringFrontal = null;
    private String encodedStringPosterior = null;

    ProgressDialog progressDialog;
    ProgressDialog progressDialogToScan;

    String scandIdOperation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_scan);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.id_scan_activity_name));
            invalidateOptionsMenu();
        }
        idFrontButton = (ImageButton) findViewById(R.id.ib_frontal_id_scan);
        idPosteriorButton = (ImageButton) findViewById(R.id.ib_posterior_id_scan);
        continueButton = (Button) findViewById(R.id.b_continue_id_scan);
        buttonShowHideResultData = (LinearLayout) findViewById(R.id.ly_button_reult_data_id_scan);
        sectionResultData = (LinearLayout) findViewById(R.id.ly_text_reult_data_id_scan);
        indicatorResultShow = (ImageView) findViewById(R.id.iv_idicator_result_show_id_scan);
        idFrontButton.setOnClickListener(this);
        idPosteriorButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        buttonShowHideResultData.setOnClickListener(this);

        encodedStringFrontal = null;
        encodedStringPosterior = null;

        scandIdOperation = SharedPreferencesUtils.readFromPreferencesString(this,SharedPreferencesUtils.ID_SCAN,null);

        MobbScanAPI.getInstance().setBaseUrl("https://mobbscan-pre.com.mobbeel.com/");
//        MobbScanAPI.getInstance().setApiMode(MobbScanAPI.MobbScanAPIMode.OFFLINE);
//        MobbScanAPI.getInstance().setBaseUrl("https://201.99.106.95:28443/mobsscan-wrapper/solr/");
        MobbScanAPI.getInstance().initAPI("a64a304e-b13f-4f69-a0f9-512cc6c85cad", this, new LicenseStatusListener() {
            @Override
            public void onLicenseStatusChecked(MobbScanLicenseResult licenseResult, Date licenseValidTo) {
                if (licenseResult != MobbScanLicenseResult.VALID) {
                    Toast.makeText(IdScanActivity.this, "There was a problem with the license", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(IdScanActivity.this, "VALID License" + licenseResult.toString() + ", Date:" + licenseValidTo, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_frontal_id_scan:
//                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL);
                scanFront();
                break;
            case R.id.ib_posterior_id_scan:
//                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR);
                break;
            case R.id.ly_button_reult_data_id_scan:
                showHideResultData();
                break;
            case R.id.b_continue_id_scan:
//                if (validatePictureEncoded()){
                Intent i = new Intent(this, FaceScanActivity.class);
                startActivity(i);
//                }
                break;
        }
    }

    public void showHideResultData(){
        if(sectionResultData.getVisibility() == View.VISIBLE){
            sectionResultData.setVisibility(View.GONE);
            indicatorResultShow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_black_48dp));
        }else{
            sectionResultData.setVisibility(View.VISIBLE);
            indicatorResultShow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_up_black_48dp));
        }

    }

//    private void dispatchTakePictureIntent(int REQUEST_CODE) {
//        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, REQUEST_CODE);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL:
//                if (resultCode == RESULT_OK && data != null) {
//                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
//                    String encodedString = encodeTobase64(bmp);
//                    encodedStringFrontal = encodedString;
////                    bmp.recycle();
//                    idFrontButton.setImageBitmap(bmp);
//                }
//                break;
//            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR:
//                if (resultCode == RESULT_OK && data != null) {
//                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
//                    String encodedString = encodeTobase64(bmp);
//                    encodedStringPosterior = encodedString;
//                    idPosteriorButton.setImageBitmap(bmp);
//                }
//                break;
//        }
//    }

    public boolean validatePictureEncoded() {
        if (encodedStringFrontal == null) {
            Toast.makeText(this, "Debes tomar una fotografía de la identificación por la parte Frontal", Toast.LENGTH_SHORT).show();
        } else if (encodedStringPosterior == null) {
            Toast.makeText(this, "Debes tomar una fotografía de la identificación por la parte Posterior", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    //    public static String encodeTobase64(Bitmap image) {
//        Bitmap imagex = image;
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        imagex.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        byte[] b = outputStream.toByteArray();
//        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
//        return imageEncoded;
//    }
//
//    public static Bitmap decodeBase64(String input) {
//        byte[] decodedByte = Base64.decode(input, 0);
//        return BitmapFactory.decodeByteArray(decodedByte, 0,      decodedByte.length);
//    }
    public void scanFront() {
        scan(MobbScanOperationMode.SCAN_ONLY_FRONT);
    }

    public void scanBack() {
        scan(MobbScanOperationMode.SCAN_ONLY_BACK);
    }

    public void scanBoth() {
        scan(MobbScanOperationMode.SCAN_BOTH_SIDES);
    }

    private void scan(final MobbScanOperationMode operationMode) {
        refreshUI(null);
        progressDialogToScan = new ProgressDialog(this, getString(R.string.start_scan_process_id_scan));
        progressDialogToScan.setCancelable(false);
        progressDialogToScan.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialogToScan.show();

        MobbScanAPI.getInstance().startScan(MobbScanDocumentType.MEXIDCardD, operationMode, new ScanStartListener() {
            @Override
            public void onScanStarted(MobbScanStartScanResult result, String scanId, MobbScanAPIError error) {
                if (result == MobbScanStartScanResult.OK) {
                    if (scandIdOperation == null && !scandIdOperation.equals("")){
                        scandIdOperation = scanId;
                        SharedPreferencesUtils.saveToPreferencesString(IdScanActivity.this,SharedPreferencesUtils.ID_SCAN,scandIdOperation);
                    }
                    if (operationMode == MobbScanOperationMode.SCAN_ONLY_FRONT) {
                        MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.FRONT, scanId, IdScanActivity.this, IdScanActivity.this);
                    } else if (operationMode == MobbScanOperationMode.SCAN_ONLY_BACK) {
                        MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.BACK, scanId, IdScanActivity.this, IdScanActivity.this);
                    } else if (operationMode == MobbScanOperationMode.SCAN_BOTH_SIDES) {
                        MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.FRONT, scanId, IdScanActivity.this, IdScanActivity.this);
                        MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.BACK, scanId, IdScanActivity.this, IdScanActivity.this);
                    }
                } else {
                    progressDialogToScan.hide();
                    Toast.makeText(IdScanActivity.this, error.toString() + ": The scan process could not be started. Please, contact with Mobbeel", Toast.LENGTH_LONG).show();
                    MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.FRONT, scanId, IdScanActivity.this, IdScanActivity.this);
                }
            }
        });
    }

    private void refreshUI(IDDocument document) {
        if (document != null) {
            ((TextView) findViewById(R.id.tvPeronalNumber)).setText(document.getPersonalNumber());
            ((TextView) findViewById(R.id.tvDocumentNumber)).setText(document.getDocumentNumber());
            ((TextView) findViewById(R.id.tvNameAndSurname)).setText(document.getName() + " " + document.getSurname());
            ((TextView) findViewById(R.id.tvDateOfBirth)).setText(document.getDateOfBirth());
            ((TextView) findViewById(R.id.tvGender)).setText(document.getGender());
            ((TextView) findViewById(R.id.tvValidTo)).setText(document.getDateOfExpiry());
            ((TextView) findViewById(R.id.tvNationality)).setText(document.getNationality());
        } else {
            ((TextView) findViewById(R.id.tvPeronalNumber)).setText("");
            ((TextView) findViewById(R.id.tvDocumentNumber)).setText("");
            ((TextView) findViewById(R.id.tvNameAndSurname)).setText("");
            ((TextView) findViewById(R.id.tvDateOfBirth)).setText("");
            ((TextView) findViewById(R.id.tvGender)).setText("");
            ((TextView) findViewById(R.id.tvValidTo)).setText("");
            ((TextView) findViewById(R.id.tvNationality)).setText("");
        }

    }

    @Override
    public void onIDDocumentScanned(MobbScanScanResult result, MobbScanScanResultData resultData, MobbScanAPIError error) {
        if (result == MobbScanScanResult.COMPLETED) {
            refreshUI(resultData.getIdDocument());
        } else if (result == MobbScanScanResult.ERROR) {
            Toast.makeText(IdScanActivity.this, "There was an error during scan process = " + error.toString(), Toast.LENGTH_LONG).show();
        }
        if (result != MobbScanScanResult.PENDING_OTHER_SIDE && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onIDDocumentDetected(MobbScanDetectionResult result, MobbScanDetectionResultData resultData, MobbScanAPIError error) {
//        progressDialog.setMessage("Extracting document information...");
        progressDialog = new ProgressDialog(this, getString(R.string.get_info_process_id_scan));
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
        if (result == MobbScanDetectionResult.OK) {
            // free allocated memory if you don't use the bitmap result
            if (resultData.getImage() != null && !resultData.getImage().isRecycled()) {
//                resultData.getImage().recycle();
                if (resultData.getDocumentSide() == MobbScanDocumentSide.FRONT) {
                    Bitmap bmp = resultData.getImage();
                    idFrontButton.setImageBitmap(bmp);
                }
                if (resultData.getDocumentSide() == MobbScanDocumentSide.FRONT) {
                    Bitmap bmp = resultData.getImage();
                    idFrontButton.setImageBitmap(bmp);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobbScanAPI.getInstance().release();
    }
}
