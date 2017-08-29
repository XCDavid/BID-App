package com.teknei.bid.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.teknei.bid.R;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.teknei.bid.asynctask.DocumentSend;
import com.teknei.bid.asynctask.FaceFileSend;
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


public class DocumentScanActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 99;
    ImageButton takeDocumentPicture;
    Button bContinue;

    private byte[] photoBuffer;
    File imageFile;
    File fileJson;

    List<File> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_scan);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.document_scan_activity_name));
            invalidateOptionsMenu();
        }

        fileList = new ArrayList<File>();

        takeDocumentPicture = (ImageButton) findViewById(R.id.ib_document_scan);
        bContinue = (Button) findViewById(R.id.b_continue_document_scan);
        takeDocumentPicture.setOnClickListener(this);
        bContinue.setOnClickListener(this);

        //Check Permissions For Android 6.0 up
        PermissionsUtils.checkPermissionReadWriteExternalStorage(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_document_scan:
                startScan(ScanConstants.OPEN_CAMERA);
                break;
            case R.id.b_continue_document_scan:
                if (validatePictureTake()) {
                    sendPetition();
                }
                break;
        }
    }

    public boolean validatePictureTake() {
        boolean bitMapTake = false;
        if (takeDocumentPicture.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(this, "Debes tomar una fotografía del documento para continuar.", Toast.LENGTH_SHORT).show();
        }
        return bitMapTake;
    }

    protected void startScan(int preference) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                takeDocumentPicture.setImageBitmap(bitmap);
                photoBuffer = bitmapToByteArray(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Guarda nueva imagen del documento: comprobante de domicilio
            String operationID = SharedPreferencesUtils.readFromPreferencesString(DocumentScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "document_" + operationID + ".jpg");
            if (f.exists()) {
                f.delete();
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "document_" + operationID + ".jpg");
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
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); //Tipo de imagen
        return stream.toByteArray();
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String documentOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.DOCUMENT_OPERATION, "");
        if (documentOperation.equals("")) {
            String localTime = PhoneSimUtils.getLocalDateAndTime();
            SharedPreferencesUtils.saveToPreferencesString(DocumentScanActivity.this, SharedPreferencesUtils.TIMESTAMP_DOCUMENT, localTime);

            String jsonString = buildJSON();
            fileList.add(fileJson);
            fileList.add(imageFile);
            new DocumentSend(DocumentScanActivity.this, token, jsonString, fileList).execute();
        } else {
            goNext();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(DocumentScanActivity.this, FingerPrintsActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(DocumentScanActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("contentType", "image/jpeg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Writer output = null;
            fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "document" + ".json");
            if (fileJson.exists()) {
                fileJson.delete();
                fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "document" + ".json");
            }
            output = new BufferedWriter(new FileWriter(fileJson));
            output.write(jsonObject.toString());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsUtils.WRITE_READ_EXTERNAL_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    //Check AGAIN Permissions For Android 6.0 up
                    PermissionsUtils.checkPermissionWriteExternalStorage(DocumentScanActivity.this);
                }

                if (grantResults.length > 1
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    //Check AGAIN Permissions For Android 6.0 up
                    PermissionsUtils.checkPermissionReadExternalStorage(DocumentScanActivity.this);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
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
            dialogoAlert = new AlertDialog(DocumentScanActivity.this, getString(R.string.message_cancel_operation_title), getString(R.string.message_cancel_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(DocumentScanActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
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
