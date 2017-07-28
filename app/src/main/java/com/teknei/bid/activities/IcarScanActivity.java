package com.teknei.bid.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
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

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
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

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class IcarScanActivity extends BaseActivity implements View.OnClickListener {
    ImageButton idFrontButton;
    ImageButton idPosteriorButton;
    Button continueButton;
    LinearLayout buttonShowHideResultData;
    LinearLayout sectionResultData;
    ImageView indicatorResultShow;
    ConstraintLayout posteriorLayout;

    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL = 661;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR = 662;

    private String encodedStringFrontal = null;
    private String encodedStringPosterior = null;

//    ProgressDialog progressDialog;
//    ProgressDialog progressDialogToScan;
//    ProgressDialog progressDialogStartLoad;

    String scandIdOperation = null;

    String stringCredentialType = "";

    private byte[] photoBuffer;
    private byte[] photoBufferBack;
    File imageFile;
    File imageFileBack;

    File fileJson;
    List<File> fileList;

    int resolution=0;


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
        posteriorLayout = (ConstraintLayout) findViewById(R.id.cl_posterior);
        idFrontButton.setOnClickListener(this);
        idPosteriorButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        buttonShowHideResultData.setOnClickListener(this);

//        progressDialog = new ProgressDialog(this, getString(R.string.get_info_process_id_scan));
//        progressDialog.setCancelable(false);
//        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        progressDialogToScan = new ProgressDialog(this, getString(R.string.start_scan_process_id_scan));
//        progressDialogToScan.setCancelable(false);
//        progressDialogToScan.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        progressDialogStartLoad = new ProgressDialog(this, getString(R.string.load_id_scan));
//        progressDialogStartLoad.setCancelable(false);
//        progressDialogStartLoad.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        progressDialogStartLoad.show();

        encodedStringFrontal = null;
        encodedStringPosterior = null;

        fileList = new ArrayList<File>();
        Bundle bundle = getIntent().getExtras();
        //Extract the data…
        stringCredentialType = bundle.getString("id_type");
//        stringCredentialType = "INE";
        modifyLayoutByIdSelected(stringCredentialType);

        scandIdOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_SCAN, null);

        //Check Permissions For Android 6.0 up
        PermissionsUtils.checkPermissionCamera(this);
        PermissionsUtils.checkPermissionReadWriteExternalStorage(this);

    }

    private void modifyLayoutByIdSelected(String idType) {
        if ( idType.equals( "PASAPORTE" ) ) {
            posteriorLayout.setVisibility(View.GONE);
//            stringCredentialType = "PASAPORTE";
        /*} else if (idType == "INE") {
            posteriorLayout.setVisibility(View.VISIBLE);
//            stringCredentialType = "INE";
        */} else {
            posteriorLayout.setVisibility(View.VISIBLE);
//            stringCredentialType = "IFE";
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_frontal_id_scan:
                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL);
//                scanFront();
//                if (idTypeSelected == MobbScanDocumentType.Passport_TD3) {
//                    scanFront();
//                } else {
//                    scanBoth();
//                }
                break;
            case R.id.ib_posterior_id_scan:
                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR);
//                scanBack();
//                scanBoth();
                break;
            case R.id.ly_button_reult_data_id_scan:
                showHideResultData();
                break;
            case R.id.b_continue_id_scan:
//                if (validatePictureEncoded()){
//                Intent i = new Intent(this, FaceScanActivity.class);
//                startActivity(i);
                sendPetition();
//                }
                break;
        }
    }

    public void showHideResultData() {
        if (sectionResultData.getVisibility() == View.VISIBLE) {
            sectionResultData.setVisibility(View.GONE);
            indicatorResultShow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_down_black_48dp));
        } else {
            sectionResultData.setVisibility(View.VISIBLE);
            indicatorResultShow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_keyboard_arrow_up_black_48dp));
        }

    }

    private void dispatchTakePictureIntent(int REQUEST_CODE) {
//        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, REQUEST_CODE);

        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); //Tipo de imagen
        return stream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL:
                if (resultCode == RESULT_OK && data != null) {
//                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
//                    String encodedString = encodeTobase64(bmp);
//                    encodedStringFrontal = encodedString;
//                    bmp.recycle();
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        getContentResolver().delete(uri, null, null);
                        idFrontButton.setImageBitmap(bitmap);
                        photoBuffer = bitmapToByteArray(bitmap);
                        Log.d("REsolution", "rResolution: w->" + bitmap.getWidth() + " , h->" + bitmap.getHeight());
                        resolution = bitmap.getWidth() * bitmap.getHeight();
                        Log.d("REsolution", "rResolution: total->" + resolution);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    idFrontButton.setImageBitmap(bmp);
                    //Guarda nueva imagen del rostro de la persona
                    String operationID = SharedPreferencesUtils.readFromPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
                    String dir = Environment.getExternalStorageDirectory() + File.separator;
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_front" + operationID + ".jpg");
                    if (f.exists()) {
                        f.delete();
                        f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_front" + operationID + ".jpg");
                    }
                    try {
                        f.createNewFile();
                        //write the bytes in file
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(photoBuffer);
                        // remember close de FileOutput
                        fo.close();
                        imageFile = f;
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        imageFile = null;
                    }
                }
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR:
                if (resultCode == RESULT_OK && data != null) {
//                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
//                    String encodedString = encodeTobase64(bmp);
//                    encodedStringPosterior = encodedString;
//                    idPosteriorButton.setImageBitmap(bmp);
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        getContentResolver().delete(uri, null, null);
                        idPosteriorButton.setImageBitmap(bitmap);
                        photoBufferBack = bitmapToByteArray(bitmap);
                        Log.d("REsolution", "rResolution BACK: w->" + bitmap.getWidth() + " , h->" + bitmap.getHeight());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    idFrontButton.setImageBitmap(bmp);
                    //Guarda nueva imagen del rostro de la persona
                    String operationID = SharedPreferencesUtils.readFromPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
                    String dir = Environment.getExternalStorageDirectory() + File.separator;
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_back" + operationID + ".jpg");
                    if (f.exists()) {
                        f.delete();
                        f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_back" + operationID + ".jpg");
                    }
                    try {
                        f.createNewFile();
                        //write the bytes in file
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(photoBufferBack);
                        // remember close de FileOutput
                        fo.close();
                        imageFileBack = f;
                    } catch (IOException e) {
                        e.printStackTrace();
                        f = null;
                        imageFileBack = null;
                    }
                }
                break;
        }
    }

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

    public static String encodeTobase64(Bitmap image) {
        Bitmap imagex = image;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imagex.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] b = outputStream.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    //
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
//
//    private void refreshUI(IDDocument document) {
//        if (document != null) {
//            ((TextView) findViewById(R.id.tvPeronalNumber)).setText(document.getPersonalNumber());
//            ((TextView) findViewById(R.id.tvDocumentNumber)).setText(document.getDocumentNumber());
//            ((TextView) findViewById(R.id.tvNameAndSurname)).setText(document.getName() + " " + document.getSurname());
//            ((TextView) findViewById(R.id.tvDateOfBirth)).setText(document.getDateOfBirth());
//            ((TextView) findViewById(R.id.tvGender)).setText(document.getGender());
//            ((TextView) findViewById(R.id.tvValidTo)).setText(document.getDateOfExpiry());
//            ((TextView) findViewById(R.id.tvNationality)).setText(document.getNationality());
//        } else {
//            ((TextView) findViewById(R.id.tvPeronalNumber)).setText("");
//            ((TextView) findViewById(R.id.tvDocumentNumber)).setText("");
//            ((TextView) findViewById(R.id.tvNameAndSurname)).setText("");
//            ((TextView) findViewById(R.id.tvDateOfBirth)).setText("");
//            ((TextView) findViewById(R.id.tvGender)).setText("");
//            ((TextView) findViewById(R.id.tvValidTo)).setText("");
//            ((TextView) findViewById(R.id.tvNationality)).setText("");
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MobbScanAPI.getInstance().release();
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
                    IcarScanActivity.this.onBackPressed();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PermissionsUtils.WRITE_READ_EXTERNAL_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    //Check AGAIN Permissions For Android 6.0 up
                    PermissionsUtils.checkPermissionWriteExternalStorage(IcarScanActivity.this);
                }

                if (grantResults.length > 1
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    //Check AGAIN Permissions For Android 6.0 up
                    PermissionsUtils.checkPermissionReadExternalStorage(IcarScanActivity.this);
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

//        if (!scanSave.equals("")) {
        //BORRAR
        if (false) {
            goNext();
        } else {
            String localTime = PhoneSimUtils.getLocalDateAndTime();
            SharedPreferencesUtils.saveToPreferencesString(IcarScanActivity.this,SharedPreferencesUtils.TIMESTAMP_CREDENTIALS,localTime);

            String jsonString = buildJSON();
            fileList.add(fileJson);
            fileList.add(imageFile);
            if ( !stringCredentialType.equals("PASAPORTE") ) {
                fileList.add(imageFileBack);
            }
            new CredentialsCaptured(IcarScanActivity.this, token, jsonString, fileList).execute();
        }
    }

    @Override
    public void goNext() {
//        super.goNext();
        Intent i = new Intent(IcarScanActivity.this, FaceScanActivity.class);
        startActivity(i);
    }

    public String buildJSON() {

        String operationID = SharedPreferencesUtils.readFromPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "23");

//        String scanID = scandIdOperation;
        //BORRAR
//        String scanID = "c670040a-a13e-4ad5-81ae-a49bd9c7c6a3";
        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("operationId", Integer.valueOf(operationID));
//            jsonObject.put("scanId", "");
            jsonObject.put("credentialType", stringCredentialType);
            jsonObject.put("imageResolution", resolution);
            jsonObject.put("contentType", "image/jpeg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Writer output = null;
            fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "json" + ".json");
            if (fileJson.exists()) {
                fileJson.delete();
                fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "json" + ".json");
            }
            output = new BufferedWriter(new FileWriter(fileJson));
            output.write(jsonObject.toString());
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
            dialogoAlert = new AlertDialog(IcarScanActivity.this, getString(R.string.message_cancel_operation_title), getString(R.string.message_cancel_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(IcarScanActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
