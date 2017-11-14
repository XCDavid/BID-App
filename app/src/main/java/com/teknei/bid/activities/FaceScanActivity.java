package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.CredentialsCaptured;
import com.teknei.bid.asynctask.FaceFileSend;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.PhoneSimUtils;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class FaceScanActivity extends BaseActivity implements View.OnClickListener/*, SurfaceHolder.Callback, CompoundButton.OnCheckedChangeListener*/ {
    ImageButton ibFacePictureButton;
    ImageView faceDefaultImgV;
    Button continueFaceScan;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE = 663;

    private byte[] photoBuffer;
    File imageFile;
    File fileJson;
    List<File> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.face_scan_activity_name));
            invalidateOptionsMenu();
        }

        ibFacePictureButton = (ImageButton) findViewById(R.id.ib_face_scan);
        continueFaceScan = (Button) findViewById(R.id.b_continue_face_scan);
        faceDefaultImgV = (ImageView) findViewById(R.id.imageViewFace);
        ibFacePictureButton.setOnClickListener(this);
        continueFaceScan.setOnClickListener(this);

        fileList = new ArrayList<File>();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_face_scan:
                startScan(ScanConstants.OPEN_CAMERA, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE);
                break;
            case R.id.b_continue_face_scan:
                if (validatePictureTake()) {
                    sendPetition();
                }
                break;
        }
    }

    protected void startScan(int preference, int REQUEST_CODE) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        getContentResolver().delete(uri, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    photoBuffer = bitmapToByteArray(bmp);
                    ibFacePictureButton.setImageBitmap(bmp);

                    faceDefaultImgV.setVisibility(View.INVISIBLE);
                    //Guarda nueva imagen del rostro de la persona
                    String operationID = SharedPreferencesUtils.readFromPreferencesString(FaceScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "face_" + operationID + ".jpg");
                    if (f.exists()) {
                        f.delete();
                        f = new File(Environment.getExternalStorageDirectory() + File.separator + "face_" + operationID + ".jpg");
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
        }
    }

    public boolean validatePictureTake() {
        boolean bitMapTake = false;
        if (ibFacePictureButton.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(this, "Debes tomar una fotografía del rostro del usuario para continuar.", Toast.LENGTH_SHORT).show();
        }
        return bitMapTake;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); //Tipo de imagen
        return stream.toByteArray();
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String faceOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.FACE_OPERATION, "");
        if (faceOperation.equals("")) {
            fileList.clear();
            String localTime = PhoneSimUtils.getLocalDateAndTime();
//            SharedPreferencesUtils.saveToPreferencesString(FaceScanActivity.this, SharedPreferencesUtils.TIMESTAMP_FACE, localTime);

            String jsonString = buildJSON();
            fileList.add(fileJson);
            fileList.add(imageFile);
            new FaceFileSend(FaceScanActivity.this, token, jsonString, fileList).execute();
        } else {
            goNext();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(FaceScanActivity.this, DocumentScanActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID  = SharedPreferencesUtils.readFromPreferencesString(FaceScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "23");
        String idEnterprice = SharedPreferencesUtils.readFromPreferencesString(FaceScanActivity.this, SharedPreferencesUtils.ID_ENTERPRICE, "default");
        String customerType = SharedPreferencesUtils.readFromPreferencesString(FaceScanActivity.this, SharedPreferencesUtils.CUSTOMER_TYPE, "default");

        Log.d("FaceScanActivity"," Operation  id " + operationID);
        Log.d("FaceScanActivity"," Enterprice id " + idEnterprice);
        Log.d("FaceScanActivity"," Customer Type " + customerType);

        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emprId", idEnterprice);
            jsonObject.put("customerType", customerType);
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("contentType", "image/jpeg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Writer output;
            fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "rostro" + ".json");
            if (fileJson.exists()) {
                fileJson.delete();
                fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "rostro" + ".json");
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
            dialogoAlert = new AlertDialog(FaceScanActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(FaceScanActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
