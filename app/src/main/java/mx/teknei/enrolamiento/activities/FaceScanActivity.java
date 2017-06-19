package mx.teknei.enrolamiento.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.teknei.enrolamiento.R;

public class FaceScanActivity extends AppCompatActivity implements View.OnClickListener/*, SurfaceHolder.Callback, CompoundButton.OnCheckedChangeListener*/{
    ImageButton ibFacePictureButton;
    Button continueFaceScan;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE = 663;
    String encodedStringFace;

//    private Camera camera;
//    private SurfaceView surfaceView;
//    private SurfaceHolder surfaceHolder;
//    boolean preview = false;
//    private Button btnTakePicture;
//    private TextView txtFaceCount;
//    private ImageView ibFacePictureButton;
//    private Button confirm;
//    private Button recapture;
    private byte[] photoBuffer;
//    private View root;
//    private ToggleButton cameraSelect;
//    private int currentCameraId;
//    private FaceListener faceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);
//        faceListener = new FaceListener();

        ibFacePictureButton = (ImageButton) findViewById(R.id.ib_face_scan);
        continueFaceScan = (Button) findViewById(R.id.b_continue_face_scan);
        ibFacePictureButton.setOnClickListener(this);
        continueFaceScan.setOnClickListener(this);

//        txtFaceCount = (TextView) findViewById(R.id.tvFaceCount);
//        cameraSelect = (ToggleButton) findViewById(R.id.cameraSelect);
//        cameraSelect.setChecked(true);
//        cameraSelect.setVisibility(View.GONE);
//        cameraSelect.setOnCheckedChangeListener(this);
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

//    private class FaceListener implements Camera.FaceDetectionListener{
//
//        @Override
//        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
//            Log.d(FaceScanActivity.class.getName(),"CARAS: "+faces.length);
//            if (faces.length == 0){
////                btnTakePicture.setVisibility(View.GONE);
//                //Toast.makeText(getActivity().getApplicationContext(), "Cara no detectada..!", Toast.LENGTH_SHORT).show();
//                txtFaceCount.setText("Cara NO detectada!");
//            }else if(faces.length > 1){
////                btnTakePicture.setVisibility(View.GONE);
//                txtFaceCount.setText("Sólo se permite tomar foto de una sóla cara, caras detectadas:" + " " +String.valueOf(faces.length));
//            }else if(faces.length == 1){
//                txtFaceCount.setText("Cara detectada!");
////                btnTakePicture.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
//    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback(){
//        @Override
//        public void onShutter() {
//
//        }};
//
//    Camera.PictureCallback mRawPictureCallback = new Camera.PictureCallback(){
//
//        @Override
//        public void onPictureTaken(byte[] data, Camera arg1) {
//
//            Bitmap bmp;
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inMutable = true;
//            bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//
//            if(bmp != null){
//
//                bmp = RotateBitmap(bmp,180);
//
//                photoBuffer = bitmapToByteArray(bmp);
////                ibFacePictureButton.setVisibility(View.VISIBLE);
//                ibFacePictureButton.setImageBitmap(bmp);
////                confirm.setVisibility(View.VISIBLE);
//            }else{
//                txtFaceCount.setText("Error al momento de tomar foto");
//            }
//
////            btnTakePicture.setVisibility(View.GONE);
////            recapture.setVisibility(View.VISIBLE);
////            surfaceView.setVisibility(View.GONE);
//
//        }};
//
//    Camera.PictureCallback mJPGPictureCallback = new Camera.PictureCallback(){
//
//        @Override
//        public void onPictureTaken(byte[] data, Camera arg1) {
//
//        }};
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        // TODO Auto-generated method stub
//        if(preview){
//            camera.stopFaceDetection();
//            camera.stopPreview();
//            preview = false;
//        }
//
//        if (camera != null){
//            try {
//                camera.setPreviewDisplay(surfaceHolder);
//                camera.setFaceDetectionListener(faceListener);
//                camera.startPreview();
//                camera.startFaceDetection();
//                preview = true;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        // TODO Auto-generated method stub
//        if (Camera.getNumberOfCameras() >= 2) {
//
//            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
//            //if you want to open front facing camera use this line
//            camera = Camera.open(currentCameraId);
//
//            //if you want to use the back facing camera
//            //camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//
//            android.hardware.Camera.CameraInfo info =
//                    new android.hardware.Camera.CameraInfo();
//
//
//            int result;
//            result = (info.orientation + 180) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//            camera.setDisplayOrientation(result);
//        }
//        camera.setFaceDetectionListener(faceListener);
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        // TODO Auto-generated method stub
//        camera.stopFaceDetection();
//        camera.stopPreview();
//        camera.release();
//        camera = null;
//        preview = false;
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_FACE);
//        if(camera != null){
//            if (preview) {
//                camera.stopPreview();
//            }
//            //NB: if you don't release the current camera before switching, you app will crash
//            camera.release();
//
//            //swap the id of the camera to be used
//            if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
//                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
//            } else {
//                currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//            }
//            camera = Camera.open(currentCameraId);
//            if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
//                camera.setFaceDetectionListener(faceListener);
//            } else {
//                camera.setFaceDetectionListener(faceListener);
//            }
//
//            //Code snippet for this method from somewhere on android developers, i forget where
//            try {
//                //this step is critical or preview on new camera will no know where to render to
//                camera.setPreviewDisplay(surfaceHolder);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            camera.startPreview();
//        }
//    }

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
