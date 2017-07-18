package com.teknei.bid.activities;

import android.app.Activity;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.teknei.bid.R;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.teknei.bid.asynctask.DocumentSend;
import com.teknei.bid.asynctask.FaceFileSend;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DocumentScanActivity extends BaseActivity implements View.OnClickListener{
    private static final int REQUEST_CODE = 99;
    ImageButton takeDocumentPicture;
    Button bContinue;

    private byte[] photoBuffer;
    File imageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_scan);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.document_scan_activity_name));
            invalidateOptionsMenu();
        }

        takeDocumentPicture = (ImageButton) findViewById(R.id.ib_document_scan);
        bContinue = (Button) findViewById(R.id.b_continue_document_scan);
        takeDocumentPicture.setOnClickListener(this);
        bContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_document_scan:
                startScan(ScanConstants.OPEN_CAMERA);
                break;
            case R.id.b_continue_document_scan:
//                Intent i = new Intent(this, FingerPrintsActivity.class);
//                startActivity(i);
                sendPetition();
                break;
        }
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
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                takeDocumentPicture.setImageBitmap(bitmap);
                photoBuffer = bitmapToByteArray(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Guarda nueva imagen del rostro de la persona
            String operationID = SharedPreferencesUtils.readFromPreferencesString(DocumentScanActivity.this,SharedPreferencesUtils.OPERATION_ID,"");
            String dir = Environment.getExternalStorageDirectory()+ File.separator;
            File f = new File(Environment.getExternalStorageDirectory()+ File.separator + "document_"+operationID+".jpg");
            if(f.exists()){
                f.delete();
                f = new File(Environment.getExternalStorageDirectory()+ File.separator + "document_"+operationID+".jpg");
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
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); //Tipo de imagen
        return stream.toByteArray();
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        boolean bitMapTake = false;
        Bitmap bitmap;

        if (takeDocumentPicture.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
            bitmap = ((BitmapDrawable)takeDocumentPicture.getDrawable()).getBitmap();
        } else if (takeDocumentPicture.getDrawable() instanceof VectorDrawableCompat){
            bitMapTake = false;
        }
        if(bitMapTake){
            String jsonString = buildJSON();
            new DocumentSend(DocumentScanActivity.this, token, jsonString,imageFile ).execute();
        }else {
            Toast.makeText(DocumentScanActivity.this, "Toma una foto para poder continuar", Toast.LENGTH_SHORT).show();
//            goNext();
        }
    }

    @Override
    public void goNext() {
//        super.goNext();
        Intent i = new Intent(DocumentScanActivity.this, FingerPrintsActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(DocumentScanActivity.this,SharedPreferencesUtils.OPERATION_ID,"");
        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("contentType", "image/jpeg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
