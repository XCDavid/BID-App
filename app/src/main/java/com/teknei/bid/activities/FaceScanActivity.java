package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.teknei.bid.R;
import com.teknei.bid.asynctask.CredentialsCaptured;
import com.teknei.bid.asynctask.FaceFileSend;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class FaceScanActivity extends BaseActivity implements View.OnClickListener/*, SurfaceHolder.Callback, CompoundButton.OnCheckedChangeListener*/{
    ImageButton ibFacePictureButton;
    Button continueFaceScan;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE = 663;
    String encodedStringFace;

    private byte[] photoBuffer;

    File imageFile;

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
        ibFacePictureButton.setOnClickListener(this);
        continueFaceScan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_face_scan:
                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE);
                break;
            case R.id.b_continue_face_scan:
//                if (validatePictureEncoded()){
                    /*Intent i = new Intent(this,FingerPrintsActivity.class);
                    startActivity(i);*/
                    sendPetition();
//                }
                break;
        }
    }
    private void dispatchTakePictureIntent(int REQUEST_CODE) {
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    photoBuffer = bitmapToByteArray(bmp);
                    String encodedString = encodeTobase64(bmp);
                    encodedStringFace = encodedString;
                    ibFacePictureButton.setImageBitmap(bmp);

                    //Guarda nueva imagen del rostro de la persona
                    String operationID = SharedPreferencesUtils.readFromPreferencesString(FaceScanActivity.this,SharedPreferencesUtils.OPERATION_ID,"");
                    String dir = Environment.getExternalStorageDirectory()+ File.separator;
                    File f = new File(Environment.getExternalStorageDirectory()
                            + File.separator + "face_"+operationID+".jpg");
                    if(f.exists()){
                        f.delete();
                        f = new File(Environment.getExternalStorageDirectory()+File.separator + "face_"+operationID+".jpg");
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
        }
    }

    public boolean validatePictureEncoded(){
        if(encodedStringFace == null){
            Toast.makeText(this,"Debes tomar una fotografía del rostro del usuario para continuar.",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Ok",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap imagex = image;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imagex.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); //Tipo de imagen
        byte[] b = outputStream.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0,      decodedByte.length);
    }

    public Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

//        Bitmap mapAux = ibFacePictureButton.getDrawable().getBitMap();
        boolean bitMapTake = false;
        Bitmap bitmap;

        if (ibFacePictureButton.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
            bitmap = ((BitmapDrawable)ibFacePictureButton.getDrawable()).getBitmap();
        } else if (ibFacePictureButton.getDrawable() instanceof VectorDrawableCompat ){
            bitMapTake = false;
        }

//        if (faceOperation.equals("")) {
            //Des comentar
//        if(bitMapTake){
            //BORRAR
            if (true) {
//            goNext();
                String jsonString = buildJSON();
                new FaceFileSend(FaceScanActivity.this, token, jsonString, imageFile).execute();
            } else {
                Toast.makeText(FaceScanActivity.this, "Toma una foto para poder continuar", Toast.LENGTH_SHORT).show();
//            goNext();
            }
//        }else{
//            goNext();
//        }
    }

    @Override
    public void goNext() {
//        super.goNext();
        Intent i = new Intent(FaceScanActivity.this, DocumentScanActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(FaceScanActivity.this,SharedPreferencesUtils.OPERATION_ID,"");
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
