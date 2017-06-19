package mx.teknei.enrolamiento.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import mx.teknei.enrolamiento.R;

public class IdScanActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton idFrontButton;
    ImageButton idPosteriorButton;
    Button continueButton;

    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL = 661;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR = 662;

    private String encodedStringFrontal = null;
    private String encodedStringPosterior = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_scan);

        idFrontButton = (ImageButton) findViewById(R.id.ib_frontal_id_scan);
        idPosteriorButton = (ImageButton) findViewById(R.id.ib_posterior_id_scan);
        continueButton = (Button) findViewById(R.id.b_continue_id_scan);
        idFrontButton.setOnClickListener(this);
        idPosteriorButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        encodedStringFrontal = null;
        encodedStringPosterior = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_frontal_id_scan:
                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL);
                break;
            case R.id.ib_posterior_id_scan:
                dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR);
                break;
            case R.id.b_continue_id_scan:
//                if (validatePictureEncoded()){
                    Intent i = new Intent(this,FaceScanActivity.class);
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
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FRONTAL:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    String encodedString = encodeTobase64(bmp);
                    encodedStringFrontal = encodedString;
//                    bmp.recycle();
                    idFrontButton.setImageBitmap(bmp);
                }
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_POSTERIOR:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    String encodedString = encodeTobase64(bmp);
                    encodedStringPosterior = encodedString;
                    idPosteriorButton.setImageBitmap(bmp);
                }
                break;
        }
    }

    public boolean validatePictureEncoded(){
        if(encodedStringFrontal == null){
            Toast.makeText(this,"Debes tomar una fotografía de la identificación por la parte Frontal",Toast.LENGTH_SHORT).show();
        }else if (encodedStringPosterior == null){
            Toast.makeText(this,"Debes tomar una fotografía de la identificación por la parte Posterior",Toast.LENGTH_SHORT).show();
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
}
