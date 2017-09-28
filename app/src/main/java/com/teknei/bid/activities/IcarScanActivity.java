package com.teknei.bid.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.CredentialsCaptured;
import com.teknei.bid.dialogs.AlertDialog;
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
import java.util.ArrayList;
import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.error.CameraErrorCallback;
import io.fotoapparat.hardware.CameraException;
import io.fotoapparat.parameter.LensPosition;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.photo.BitmapPhoto;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static io.fotoapparat.log.Loggers.fileLogger;
import static io.fotoapparat.log.Loggers.logcat;
import static io.fotoapparat.log.Loggers.loggers;
import static io.fotoapparat.parameter.selector.AspectRatioSelectors.standardRatio;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoRedEye;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.torch;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.autoFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.continuousFocus;
import static io.fotoapparat.parameter.selector.FocusModeSelectors.fixed;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.lensPosition;
import static io.fotoapparat.parameter.selector.Selectors.firstAvailable;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;
import static io.fotoapparat.parameter.selector.SizeSelectors.smallestSize;
import static io.fotoapparat.result.transformer.SizeTransformers.scaled;

public class IcarScanActivity extends BaseActivity implements View.OnClickListener {

    ImageButton idFrontButton;
    ImageButton idPosteriorButton;
    Button continueButton;
    LinearLayout buttonShowHideResultData;
    LinearLayout sectionResultData;
    ImageView indicatorResultShow;
    ConstraintLayout posteriorLayout;
    TextView instructionsTV;
    LinearLayout resultLayout;

    TextView photoSideTV;

    LinearLayout        optionLayout;
    LinearLayout        camaraLayout;
    CameraView          cameraView;
    Fotoapparat         backFotoapparat;

    ImageView           takeImage;

    Switch simpleSwitch;

    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL = 661;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR = 662;

    String scandIdOperation = null;

    String stringCredentialType = "";

    private byte[] photoBuffer;
    private byte[] photoBufferBack;
    File imageFile;
    File imageFileBack;

    File fileJson;
    List<File> fileList;

    int resolution = 0;
    int code_image = 0;

    boolean onePicture = false;
    boolean hasCameraPermission;

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
        instructionsTV = (TextView) findViewById(R.id.tv_id_scan_instructions);
        resultLayout = (LinearLayout) findViewById(R.id.ly_button_reult_data_id_scan);
        idFrontButton.setOnClickListener(this);
        idPosteriorButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        buttonShowHideResultData.setOnClickListener(this);
        resultLayout.setVisibility(View.GONE);

        photoSideTV  = (TextView)     findViewById(R.id.tv_id_scan_id_photo_side);
        camaraLayout = (LinearLayout) findViewById(R.id.ly_section_camara);
        optionLayout = (LinearLayout) findViewById(R.id.ly_section_option);
        cameraView   = (CameraView)   findViewById(R.id.camera_view);

        takeImage = (ImageView) findViewById(R.id.i_take_image);
        takeImage.setOnClickListener(this);

        // initiate a Switch
        simpleSwitch = (Switch) findViewById(R.id.sw_id_type_photo);

        optionLayout.setVisibility(View.VISIBLE);
        camaraLayout.setVisibility(View.INVISIBLE);

        fileList = new ArrayList<File>();
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        stringCredentialType = bundle.getString("id_type");
        Log.w("Option selected", stringCredentialType);

        //stringCredentialType = "INE";
        modifyLayoutByIdSelected(stringCredentialType);

        //scandIdOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_SCAN, null);

        //Check Permissions For Android 6.0 up
        PermissionsUtils.checkPermissionCamera(this);
        PermissionsUtils.checkPermissionReadWriteExternalStorage(this);

        hasCameraPermission = PermissionsUtils.hasCameraPermission(this);

        setupFotoapparat();
        focusOnLongClick();
    }

    private void modifyLayoutByIdSelected(String idType) {
        if (idType.equals("PASAPORTE")) {
            onePicture = true;
            posteriorLayout.setVisibility(View.GONE);
            instructionsTV.setText(getString(R.string.id_scan_instructions_one_side));
        } else {
            posteriorLayout.setVisibility(View.VISIBLE);
            instructionsTV.setText(getString(R.string.id_scan_instructions_both_sides));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_frontal_id_scan:
                photoSideTV.setText(R.string.id_scan_photo_side_front);
                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL);
                break;

            case R.id.ib_posterior_id_scan:
                photoSideTV.setText(R.string.id_scan_photo_side_back);
                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR);
                break;

            case R.id.ly_button_reult_data_id_scan:
                showHideResultData();
                break;

            case R.id.b_continue_id_scan:
                if (validatePictureTake()) {
                    sendPetition();
                }
                break;

            case R.id.i_take_image:

                takePicture();

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

        Boolean switchState = simpleSwitch.isChecked();

        if (!switchState) {

            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
            startActivityForResult(intent, REQUEST_CODE);

        } else {

            code_image = REQUEST_CODE;

            if (hasCameraPermission) {
                camaraLayout.setVisibility(View.VISIBLE);
                optionLayout.setVisibility(View.INVISIBLE);
                cameraView.setVisibility(View.VISIBLE);
                backFotoapparat.start();
            }
        }
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
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    Bitmap bitmap;
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
                    //Guarda nueva imagen del rostro de la persona
                    String operationID = SharedPreferencesUtils.readFromPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_front" + operationID + ".jpg");
                    if (f.exists()) {
                        f.delete();
                        f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_front" + operationID + ".jpg");
                    }
                    try {
                        //write the bytes in file
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(photoBuffer);
                        // remember close de FileOutput
                        fo.close();
                        imageFile = f;
                    } catch (IOException e) {
                        e.printStackTrace();
                        imageFile = null;
                    }
                }
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        getContentResolver().delete(uri, null, null);
                        idPosteriorButton.setImageBitmap(bitmap);
                        photoBufferBack = bitmapToByteArray(bitmap);
                        Log.d("REsolution", "rResolution BACK: w->" + bitmap.getWidth() + " , h->" + bitmap.getHeight());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Guarda nueva imagen del rostro de la persona
                    String operationID = SharedPreferencesUtils.readFromPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
                    String dir = Environment.getExternalStorageDirectory() + File.separator;
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_back" + operationID + ".jpg");
                    if (f.exists()) {
                        f.delete();
                        f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_back" + operationID + ".jpg");
                    }
                    try {
                        //write the bytes in file
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(photoBufferBack);
                        // remember close de FileOutput
                        fo.close();
                        imageFileBack = f;
                    } catch (IOException e) {
                        e.printStackTrace();
                        imageFileBack = null;
                    }
                }
                break;
        }
    }

    public boolean validatePictureTake() {
        boolean bitMapTake = false;
        if (idFrontButton.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(this, "Debes tomar una fotografía de la identificación por la parte Frontal", Toast.LENGTH_SHORT).show();
        }
        if (!onePicture) {
            if (idPosteriorButton.getDrawable() instanceof BitmapDrawable) {
                bitMapTake = true;
            } else {
                bitMapTake = false;
                Toast.makeText(this, "Debes tomar una fotografía de la identificación por la parte Posterior", Toast.LENGTH_SHORT).show();
            }
        }
        return bitMapTake;
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
                    backFotoapparat.start();
                    cameraView.setVisibility(View.VISIBLE);

                } else {
                    IcarScanActivity.this.onBackPressed();

                    View noPermissionView = findViewById(R.id.tv_id_scan_permission);
                    noPermissionView.setVisibility(View.GONE);
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
        String scanSave = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.SCAN_SAVE_ID, "");
        if (scanSave.equals("")) {
            fileList.clear();
            String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
            String localTime = PhoneSimUtils.getLocalDateAndTime();
//            SharedPreferencesUtils.saveToPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.TIMESTAMP_CREDENTIALS, localTime);

            String jsonString = buildJSON();
            fileList.add(fileJson);
            fileList.add(imageFile);
            if (!stringCredentialType.equals(ApiConstants.STRING_PASSPORT)) {
                fileList.add(imageFileBack);
            }
            new CredentialsCaptured(IcarScanActivity.this, token, jsonString, fileList, stringCredentialType).execute();
        } else {
            goNext();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(IcarScanActivity.this, FaceScanActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "23");
        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("credentialType", stringCredentialType);
            jsonObject.put("imageResolution", 560);
            jsonObject.put("contentType", "image/jpeg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String sendJSON = jsonObject.toString();
            sendJSON = sendJSON.replaceAll("\\\\", "");

            Writer output;
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
            AlertDialog dialogAlert;
            dialogAlert = new AlertDialog(IcarScanActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogAlert.setCancelable(false);
            dialogAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogAlert;
            dialogAlert = new AlertDialog(IcarScanActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogAlert.setCancelable(false);
            dialogAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupFotoapparat() {
        backFotoapparat     = createFotoapparat(LensPosition.BACK);
    }

    private void focusOnLongClick() {

        cameraView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                backFotoapparat.autoFocus();
                return true;
            }
        });

    }

    private Fotoapparat createFotoapparat(LensPosition position) {
        Log.d ("ICARScanActivity","createFotoapparat");
        return Fotoapparat.with(this).into(cameraView).previewScaleType(ScaleType.CENTER_CROP)
                .photoSize(standardRatio(biggestSize()))
                .lensPosition(lensPosition(position))
                .focusMode(firstAvailable(continuousFocus(), autoFocus(), fixed()))
                .flash(firstAvailable(autoRedEye(), autoFlash(), torch(), off()))
                .frameProcessor(new SampleFrameProcessor())
                .logger(loggers(logcat(), fileLogger(this)))
                .cameraErrorCallback(new CameraErrorCallback() {
                    @Override
                    public void onError(CameraException e) {
                        Toast.makeText(IcarScanActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .build();
    }

    private void takePicture() {
        backFotoapparat.focus();

        PhotoResult photoResult = backFotoapparat.takePicture();

        photoResult.toBitmap(scaled(0.25f)).whenAvailable(new PendingResult.Callback<BitmapPhoto>() {
                    @Override
                    public void onResult(BitmapPhoto result) {

                        Bitmap bitmap;
                        String operationID;
                        File f;

                        switch (code_image){

                            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL:
                                ImageView imageView = (ImageView) findViewById(R.id.imageViewCameraId1);
                                imageView.setVisibility(View.INVISIBLE);

                                bitmap = result.bitmap;
                                idFrontButton.setImageBitmap(bitmap);
                                photoBuffer = bitmapToByteArray(bitmap);
                                Log.d("REsolution", "rResolution: w->" + bitmap.getWidth() + " , h->" + bitmap.getHeight());
                                resolution = bitmap.getWidth() * bitmap.getHeight();
                                Log.d("REsolution", "rResolution: total->" + resolution);

                                operationID = SharedPreferencesUtils.readFromPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
                                f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_front" + operationID + ".jpg");
                                if (f.exists()) {
                                    f.delete();
                                    f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_front" + operationID + ".jpg");
                                }
                                try {
                                    //write the bytes in file
                                    FileOutputStream fo = new FileOutputStream(f);
                                    fo.write(photoBuffer);
                                    // remember close de FileOutput
                                    fo.close();
                                    imageFile = f;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    imageFile = null;
                                }

                                break;

                            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR:
                                ImageView imageView1 = (ImageView) findViewById(R.id.imageViewCameraId2);
                                imageView1.setVisibility(View.INVISIBLE);

                                bitmap = result.bitmap;
                                idPosteriorButton.setImageBitmap(bitmap);
                                photoBufferBack = bitmapToByteArray(bitmap);
                                Log.d("REsolution", "rResolution BACK: w->" + bitmap.getWidth() + " , h->" + bitmap.getHeight());

                                //Guarda nueva imagen del rostro de la persona
                                operationID = SharedPreferencesUtils.readFromPreferencesString(IcarScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
                                String dir = Environment.getExternalStorageDirectory() + File.separator;
                                f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_back" + operationID + ".jpg");
                                if (f.exists()) {
                                    f.delete();
                                    f = new File(Environment.getExternalStorageDirectory() + File.separator + "icar_back" + operationID + ".jpg");
                                }
                                try {
                                    //write the bytes in file
                                    FileOutputStream fo = new FileOutputStream(f);
                                    fo.write(photoBufferBack);
                                    // remember close de FileOutput
                                    fo.close();
                                    imageFileBack = f;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    imageFileBack = null;
                                }
                                break;
                        }

                        backFotoapparat.stop();

                        camaraLayout.setVisibility(View.INVISIBLE);

                        optionLayout.setVisibility(View.VISIBLE);

                    }



                });
    }

    private class SampleFrameProcessor implements FrameProcessor {
        @Override
        public void processFrame(Frame frame) {
            // Perform frame processing, if needed
        }
    }

    @Override
    public void onBackPressed() {
           if (backFotoapparat.isAvailable() && camaraLayout.getVisibility() == View.VISIBLE) {
               backFotoapparat.stop();
           }
           super.onBackPressed();
    }
}
