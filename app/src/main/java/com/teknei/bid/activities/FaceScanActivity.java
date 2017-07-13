package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import com.teknei.bid.R;

public class FaceScanActivity extends AppCompatActivity implements View.OnClickListener/*, SurfaceHolder.Callback, CompoundButton.OnCheckedChangeListener*/{
    ImageButton ibFacePictureButton;
    Button continueFaceScan;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE = 663;
    String encodedStringFace;

    private byte[] photoBuffer;

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
                    Intent i = new Intent(this,FingerPrintsActivity.class);
                    startActivity(i);
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
        imagex.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
