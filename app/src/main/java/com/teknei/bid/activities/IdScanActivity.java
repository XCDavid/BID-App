package com.teknei.bid.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.CredentialsCaptured;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.ProgressDialog;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.PermissionsUtils;
import com.teknei.bid.utils.PhoneSimUtils;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class IdScanActivity extends BaseActivity implements View.OnClickListener, IDDocumentScanListener, IDDocumentDetectionListener {
    ImageButton idFrontButton;
    ImageButton idPosteriorButton;
    Button continueButton;
    //LinearLayout buttonShowHideResultData;
    //LinearLayout sectionResultData;
    //ImageView indicatorResultShow;
    ConstraintLayout posteriorLayout;

    MobbScanDocumentType idTypeSelected;

    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL = 661;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR = 662;

    private String encodedStringFrontal = null;
    private String encodedStringPosterior = null;

    ProgressDialog progressDialog;
    ProgressDialog progressDialogToScan;
    ProgressDialog progressDialogStartLoad;

    String scandIdOperation = null;

    String stringCredentialType = "";

    File fileJson;
    List<File> fileList;

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
        //buttonShowHideResultData = (LinearLayout) findViewById(R.id.ly_button_reult_data_id_scan);
        //sectionResultData = (LinearLayout) findViewById(R.id.ly_text_reult_data_id_scan);
        //indicatorResultShow = (ImageView) findViewById(R.id.iv_idicator_result_show_id_scan);
        posteriorLayout = (ConstraintLayout) findViewById(R.id.cl_posterior);
        idFrontButton.setOnClickListener(this);
        idPosteriorButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        //buttonShowHideResultData.setOnClickListener(this);

        progressDialog = new ProgressDialog(this, getString(R.string.get_info_process_id_scan));
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        progressDialogToScan = new ProgressDialog(this, getString(R.string.start_scan_process_id_scan));
        progressDialogToScan.setCancelable(false);
        progressDialogToScan.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        progressDialogStartLoad = new ProgressDialog(this, getString(R.string.load_id_scan));
        progressDialogStartLoad.setCancelable(false);
        progressDialogStartLoad.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialogStartLoad.show();

        encodedStringFrontal = null;
        encodedStringPosterior = null;

        fileList = new ArrayList<File>();

        Bundle bundle = getIntent().getExtras();
        //Extract the data…
        String idType = bundle.getString("id_type");
        Log.w("Option selected",idType);
        idTypeSelected = MobbScanDocumentType.getMobbScanDocumentType(idType);
        modifyLayoutByIdSelected(idTypeSelected);

        scandIdOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_SCAN, null);

        //Check Permissions For Android 6.0 up
        PermissionsUtils.checkPermissionCamera(this);

        String urlMobbScan = SharedPreferencesUtils.readFromPreferencesString(IdScanActivity.this, SharedPreferencesUtils.URL_ID_SCAN, "");
        String licenceMobbScan = SharedPreferencesUtils.readFromPreferencesString(IdScanActivity.this, SharedPreferencesUtils.LICENSE_ID_SCAN, "");

//        MobbScanAPI.getInstance().setBaseUrl("https://mobbscan-pre.mobbeel.com/");
        MobbScanAPI.getInstance().setBaseUrl(urlMobbScan);//directa
//        MobbScanAPI.getInstance().setBaseUrl("https://200.95.167.84:38080/");//publica TEK
//        MobbScanAPI.getInstance().setApiMode(MobbScanAPI.MobbScanAPIMode.OFFLINE);
//        MobbScanAPI.getInstance().setBaseUrl("https://201.99.106.95:28443/mobsscan-wrapper/solr/");
//        MobbScanAPI.getInstance().initAPI("a64a304e-b13f-4f69-a0f9-512cc6c85cad", this, new LicenseStatusListener() {
//            @Override
//            public void onLicenseStatusChecked(MobbScanLicenseResult licenseResult, Date licenseValidTo) {
        MobbScanAPI.getInstance().initAPI(licenceMobbScan, this, new LicenseStatusListener() { // PRUEBAS
            //        MobbScanAPI.getInstance().initAPI("0b3237e6-76c5-40d7-b895-0b7c74ccda5a", this, new LicenseStatusListener() {
            @Override
            public void onLicenseStatusChecked(MobbScanLicenseResult licenseResult, Date licenseValidTo) {  // com.teknei.bid
                if (licenseResult != MobbScanLicenseResult.VALID) {
                    Toast.makeText(IdScanActivity.this, "Ocurrio un problema con la licencia", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(IdScanActivity.this, "Licencia Valida" + licenseResult.toString() + ", Fecha :" + licenseValidTo, Toast.LENGTH_LONG).show();
                }
                if (progressDialogStartLoad != null && progressDialogStartLoad.isShowing()) {
                    progressDialogStartLoad.dismiss();
                }
            }
        });
    }

    private void modifyLayoutByIdSelected(MobbScanDocumentType idType) {
        if (idType == MobbScanDocumentType.Passport_TD3) {
        posteriorLayout.setVisibility(View.GONE);
        stringCredentialType = ApiConstants.STRING_PASSPORT;
    } else if (idType == MobbScanDocumentType.MEXIDCardE) {
        posteriorLayout.setVisibility(View.VISIBLE);
        stringCredentialType = ApiConstants.STRING_INE;
    } else {
        posteriorLayout.setVisibility(View.VISIBLE);
        stringCredentialType = ApiConstants.STRING_IFE;
    }

}


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_frontal_id_scan:
//                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL);
//                scanFront();
                if (idTypeSelected == MobbScanDocumentType.Passport_TD3) {
                    scanFront();
                } else {
                    scanBoth();
                }
                break;
            case R.id.ib_posterior_id_scan:
//                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR);
//                scanBack();
                scanBoth();
                break;
            /*case R.id.ly_button_reult_data_id_scan:
                showHideResultData();
                break;*/
            case R.id.b_continue_id_scan:
//                if (validatePictureTake()){
//                Intent i = new Intent(this, FaceScanActivity.class);
//                startActivity(i);
                sendPetition();
//                }
                break;
        }
    }

    public void showHideResultData() {
        /*
        if (sectionResultData.getVisibility() == View.VISIBLE) {
            sectionResultData.setVisibility(View.GONE);
            indicatorResultShow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_black_48dp));
        } else {
            sectionResultData.setVisibility(View.VISIBLE);
            indicatorResultShow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_up_black_48dp));
        }
        */
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
//
    public boolean validatePictureEncoded() {
        if (encodedStringFrontal == null) {
            Toast.makeText(this, "Debes tomar una fotografía de la identificación por la parte Frontal", Toast.LENGTH_SHORT).show();
        } else if (encodedStringPosterior == null) {
            Toast.makeText(this, "Debes tomar una fotografía de la identificación por la parte Posterior", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
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

        progressDialogToScan.show();

        MobbScanAPI.getInstance().startScan(idTypeSelected, operationMode, new ScanStartListener() {
            @Override
            public void onScanStarted(MobbScanStartScanResult result, String scanId, MobbScanAPIError error) {
                progressDialogToScan.hide();
                if (result == MobbScanStartScanResult.OK) {
                    scandIdOperation = scanId;
                    SharedPreferencesUtils.saveToPreferencesString(IdScanActivity.this, SharedPreferencesUtils.ID_SCAN, scandIdOperation);
                    if (operationMode == MobbScanOperationMode.SCAN_ONLY_FRONT) {
                        MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.FRONT, scanId, IdScanActivity.this, IdScanActivity.this);
                    } else if (operationMode == MobbScanOperationMode.SCAN_ONLY_BACK) {
                        MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.BACK, scanId, IdScanActivity.this, IdScanActivity.this);
                    } else if (operationMode == MobbScanOperationMode.SCAN_BOTH_SIDES) {
                        MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.FRONT, scanId, IdScanActivity.this, IdScanActivity.this);
                        MobbScanAPI.getInstance().scanDocument(MobbScanDocumentSide.BACK, scanId, IdScanActivity.this, IdScanActivity.this);
                    }
                } else {
                    Toast.makeText(IdScanActivity.this, error.toString() + ": EL proceso de escaneo no puede ser iniciado. Contacta con Mobbeel porfavor", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void refreshUI(IDDocument document) {
        if (document != null) {
            String nameS = document.getName();
            String apPatS = document.getFirstSurname();
            String apMatS = document.getSecondSurname();
//            String curpS = document.get();
//            String section =  document.getSec;
//            String documentNumber =  document.getDocumentNumber();
            String psersonalNumber =  document.getPersonalNumber();
            String OCR = psersonalNumber;
            String validity = document.getDateOfExpiry();
//            String address = document.getAddress();

            //***Contruye el json con datos que no obtiene MobbScan
//            String jsonString = SharedPreferencesUtils.readFromPreferencesString(IdScanActivity.this,SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE,"{}");
//
//            try {
//                JSONObject jsonData = new JSONObject(jsonString);
//                jsonData.put("name", nameS+"");
//                jsonData.put("appat", apPatS+"");
//                jsonData.put("apmat", apMatS+"");
////                jsonData.put("curp", curpS);
//                jsonData.put("ocr", OCR+"");
//                jsonData.put("validity", validity+"");
//                jsonData.put("address", "");
//                SharedPreferencesUtils.saveToPreferencesString(IdScanActivity.this,SharedPreferencesUtils.JSON_CREDENTIALS_RESPONSE,jsonData.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            /*((TextView) findViewById(R.id.tvPeronalNumber)).setText(document.getPersonalNumber());
            ((TextView) findViewById(R.id.tvDocumentNumber)).setText(document.getDocumentNumber());
            ((TextView) findViewById(R.id.tvNameAndSurname)).setText(document.getName() + " " + document.getSurname());
            ((TextView) findViewById(R.id.tvDateOfBirth)).setText(document.getDateOfBirth());
            ((TextView) findViewById(R.id.tvGender)).setText(document.getGender());
            ((TextView) findViewById(R.id.tvValidTo)).setText(document.getDateOfExpiry());
            ((TextView) findViewById(R.id.tvNationality)).setText(document.getNationality());*/
        } else {
            /*((TextView) findViewById(R.id.tvPeronalNumber)).setText("");
            ((TextView) findViewById(R.id.tvDocumentNumber)).setText("");
            ((TextView) findViewById(R.id.tvNameAndSurname)).setText("");
            ((TextView) findViewById(R.id.tvDateOfBirth)).setText("");
            ((TextView) findViewById(R.id.tvGender)).setText("");
            ((TextView) findViewById(R.id.tvValidTo)).setText("");
            ((TextView) findViewById(R.id.tvNationality)).setText("");*/
        }

    }

    @Override
    public void onIDDocumentScanned(MobbScanScanResult result, MobbScanScanResultData resultData, MobbScanAPIError error) {
        if (result == MobbScanScanResult.COMPLETED) {
            refreshUI(resultData.getIdDocument());
        } else if (result == MobbScanScanResult.ERROR) {
            Toast.makeText(IdScanActivity.this, "Error durante el escaneo = " + error.toString(), Toast.LENGTH_LONG).show();
        }
        if (result != MobbScanScanResult.PENDING_OTHER_SIDE && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onIDDocumentDetected(MobbScanDetectionResult result, MobbScanDetectionResultData resultData, MobbScanAPIError error) {
//        progressDialog.setMessage("Extracting document information...");

        progressDialog.show();
        if (result == MobbScanDetectionResult.OK) {
            // free allocated memory if you don't use the bitmap result
            if (resultData.getImage() != null && !resultData.getImage().isRecycled()) {
//                resultData.getImage().recycle();
                if (resultData.getDocumentSide() == MobbScanDocumentSide.FRONT) {
                    Bitmap bmp = resultData.getImage();
                    idFrontButton.setImageBitmap(bmp);
                    showHideResultData();
                }
                if (resultData.getDocumentSide() == MobbScanDocumentSide.BACK) {
                    Bitmap bmp = resultData.getImage();
                    idPosteriorButton.setImageBitmap(bmp);
                    showHideResultData();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobbScanAPI.getInstance().release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsUtils.CAMERA_REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    IdScanActivity.this.onBackPressed();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");

        String scanSave = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.SCAN_SAVE_ID, "");
//        String scanActual = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_SCAN, "");

//        if (!scanSave.equals("")) {}
        //BORRAR
        if (false) {
            goNext();
        } else {
            String localTime = PhoneSimUtils.getLocalDateAndTime();
//            SharedPreferencesUtils.saveToPreferencesString(IdScanActivity.this,SharedPreferencesUtils.TIMESTAMP_CREDENTIALS,localTime);

            String jsonString = buildJSON();
            fileList.add(fileJson);
            new CredentialsCaptured(IdScanActivity.this, token, jsonString, fileList,stringCredentialType).execute();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(IdScanActivity.this, FaceScanActivity.class);
        startActivity(i);
    }

    public String buildJSON() {

        String operationID  = SharedPreferencesUtils.readFromPreferencesString(IdScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "666");
        String idEnterprice = SharedPreferencesUtils.readFromPreferencesString(IdScanActivity.this, SharedPreferencesUtils.ID_ENTERPRICE, "default");
        String customerType = SharedPreferencesUtils.readFromPreferencesString(IdScanActivity.this, SharedPreferencesUtils.CUSTOMER_TYPE, "default");


        String scanID = scandIdOperation;
        //BORRAR
//        String scanID = "c670040a-a13e-4ad5-81ae-a49bd9c7c6a3";
        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emprId", idEnterprice);
            jsonObject.put("customerType", customerType);
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("scanId", scanID);
            jsonObject.put("credentialType", stringCredentialType);
//            jsonObject.put("imageResolution", 560);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String sendJSON = jsonObject.toString();
            sendJSON = sendJSON.replaceAll("\\\\", "");

            Writer output = null;
            fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "json" + ".json");
            if (fileJson.exists()) {
                fileJson.delete();
                fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "json" + ".json");
            }
            output = new BufferedWriter(new FileWriter(fileJson));
            output.write(sendJSON);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //menu actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.i_close_operation_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(IdScanActivity.this, getString(R.string.message_cancel_operation_title), getString(R.string.message_cancel_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(IdScanActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
