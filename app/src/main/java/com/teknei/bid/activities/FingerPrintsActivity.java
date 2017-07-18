package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.teknei.bid.R;

import com.teknei.bid.asynctask.DocumentSend;
import com.teknei.bid.asynctask.FingersSend;
import com.teknei.bid.dialogs.FingerScanDialog;
import com.teknei.bid.mso.MSOConnection;
import com.teknei.bid.mso.MSOShower;
import com.morpho.android.usb.USBManager;
import com.teknei.bid.tools.TKN_MSO_ERROR;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FingerPrintsActivity extends BaseActivity implements View.OnClickListener, MSOShower {
    MorphoDevice morphoDevice;
    MSOConnection msoConnection;
//    private DeviceDetectionMode detectionMode = DeviceDetectionMode.SdkDetection;

    Button continueButton;
    //Left Hand
    ImageButton bPinkyLeft;
    ImageButton bRingLeft;
    ImageButton bMiddleLeft;
    ImageButton bIndexLeft;
    ImageButton bThumbLeft;
    //Right Hand
    ImageButton bPinkyRight;
    ImageButton bRingRight;
    ImageButton bMiddleRight;
    ImageButton bIndexRight;
    ImageButton bThumbRight;

    FingerScanDialog dialogScan;

    //Uso para MSOShower
    private byte[] imgFPBuff = null;
    private ImageButton imgFP;
    private List<File> fingersFileArray = null;
//    private TextView txtMensaje;

    private byte[] photoBuffer;
    //Left Hand
    File imageFilePinkyLeft;
    File imageFileRingLeft;
    File imageFileMiddleLeft;
    File imageFileIndexLeft;
    File imageFileThumbLeft;
    //Right Hand
    File imageFilePinkyRight;
    File imageFileRingRight;
    File imageFileMiddleRight;
    File imageFileIndexRight;
    File imageFileThumbRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_prints);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.fingerprints_activity_name));
            invalidateOptionsMenu();
        }
        continueButton = (Button) findViewById(R.id.b_continue_fingerprints);
        continueButton.setOnClickListener(this);
        //Left Arm
        bPinkyLeft = (ImageButton) findViewById(R.id.b_pinky_left_arm);
        bRingLeft = (ImageButton) findViewById(R.id.b_ring_left_arm);
        bMiddleLeft = (ImageButton) findViewById(R.id.b_middle_left_arm);
        bIndexLeft = (ImageButton) findViewById(R.id.b_index_left_arm);
        bThumbLeft = (ImageButton) findViewById(R.id.b_thumb_left_arm);
        //Right Arm
        bPinkyRight = (ImageButton) findViewById(R.id.b_pinky_right_arm);
        bRingRight = (ImageButton) findViewById(R.id.b_ring_riht_arm);
        bMiddleRight = (ImageButton) findViewById(R.id.b_middle_right_arm);
        bIndexRight = (ImageButton) findViewById(R.id.b_index_right_arm);
        bThumbRight = (ImageButton) findViewById(R.id.b_thumb_right_arm);

        bPinkyLeft.setOnClickListener(this);
        bRingLeft.setOnClickListener(this);
        bMiddleLeft.setOnClickListener(this);
        bIndexLeft.setOnClickListener(this);
        bThumbLeft.setOnClickListener(this);
        bPinkyRight.setOnClickListener(this);
        bRingRight.setOnClickListener(this);
        bMiddleRight.setOnClickListener(this);
        bIndexRight.setOnClickListener(this);
        bThumbRight.setOnClickListener(this);

        fingersFileArray = new ArrayList<File>();

        morphoDevice = new MorphoDevice();
        // ---------- Aqui se inicia la conexion con el lector(ya no se hace en el metodo mso1300
        USBManager.getInstance().initialize(this, "com.morpho.morphosample.USB_ACTION");
        try {
            MSOConnection.getInstance().tkn_mso_connect();
            Log.i(this.getClass().getName(), "Conexion Realizada");
        } catch (TKN_MSO_ERROR e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
        MSOConnection.getInstance().setMsoShower(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean permission = USBManager.getInstance().isDevicesHasPermission();
        if (!permission) {
            onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_continue_fingerprints:
//                Intent i = new Intent(this, PayConfirmationActivity.class);

//                Intent i = new Intent(this, PayConfirmationActivity.class);
//                startActivity(i);
                sendPetition();
                break;
            case R.id.b_pinky_left_arm:
            case R.id.b_ring_left_arm:
            case R.id.b_middle_left_arm:
            case R.id.b_index_left_arm:
            case R.id.b_thumb_left_arm:
            case R.id.b_pinky_right_arm:
            case R.id.b_ring_riht_arm:
            case R.id.b_middle_right_arm:
            case R.id.b_index_right_arm:
            case R.id.b_thumb_right_arm:
//                imgFP = (ImageButton)view;
                dialogScan = new FingerScanDialog(this, "Esperando escaneo", "Instrucciones");
                dialogScan.setCancelable(false);
                dialogScan.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogScan.show();
                mso1300(view);
                break;
        }
    }

    public void mso1300(View v) {
        imgFP = ((ImageButton) v);

//        int cnt = countDevices();
//        detectionMode = DeviceDetectionMode.SdkDetection;
        try {
//            MSOConnection.getInstance().tkn_mso_connect();
//            MSOConnection.getInstance().setMsoShower(this);
            MSOConnection.getInstance().tkn_mso_capture(FingerPrintsActivity.this);
        } catch (TKN_MSO_ERROR e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
//        /*try {
//
//        }catch (TKN_MSO_ERROR e){
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();
//        }*/
//        ImageView tv1;
//        tv1= (ImageView) findViewById(R.id.b_pinky_left_arm);
        //Old
//        ((ImageButton)v).setImageBitmap( MSOConnection.getInstance().getBitMap() );
    }

//    synchronized int countDevices() {
//        int count = 0;
//        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
//        HashMap<String, UsbDevice> usbDeviceList = usbManager.getDeviceList();
//
//        Iterator<UsbDevice> usbDeviceIterator = usbDeviceList.values().iterator();
//        while (usbDeviceIterator.hasNext()) {
//            UsbDevice usbDevice = usbDeviceIterator.next();
//            if (MorphoTools.isSupported(usbDevice.getVendorId(), usbDevice.getProductId())) {
//                boolean hasPermission = usbManager.hasPermission(usbDevice);
//                if (!hasPermission) {
//                    // Request permission for using the device
//                    usbManager.requestPermission(usbDevice, PendingIntent.getBroadcast(this, 0, new Intent("com.morpho.android.usb.USB_PERMISSION"), 0));
//                } else {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }
        return hasImage;
    }

    public void setImageToRightFinger() {
        if (imgFP != null) {
            Bitmap msoBitMap = MSOConnection.getInstance().getBitMap();
            imgFP.setImageBitmap(msoBitMap);
            photoBuffer = bitmapToByteArray(msoBitMap);

            String operationID = SharedPreferencesUtils.readFromPreferencesString(FingerPrintsActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
            String dir = Environment.getExternalStorageDirectory() + File.separator;
            String finger = "";
            int fingerSelect = 0;
            switch (imgFP.getId()) {
                case R.id.b_pinky_left_arm:
                    finger = "I5";
                    fingerSelect = 10;
                    break;
                case R.id.b_ring_left_arm:
                    finger = "I4";
                    fingerSelect = 9;
                    break;
                case R.id.b_middle_left_arm:
                    finger = "I3";
                    fingerSelect = 8;
                    break;
                case R.id.b_index_left_arm:
                    finger = "I2";
                    fingerSelect = 7;
                    break;
                case R.id.b_thumb_left_arm:
                    finger = "I1";
                    fingerSelect = 6;
                    break;
                case R.id.b_pinky_right_arm:
                    finger = "D5";
                    fingerSelect = 5;
                    break;
                case R.id.b_ring_riht_arm:
                    finger = "D4";
                    fingerSelect = 4;
                    break;
                case R.id.b_middle_right_arm:
                    finger = "D3";
                    fingerSelect = 3;
                    break;
                case R.id.b_index_right_arm:
                    finger = "D2";
                    fingerSelect = 2;
                    break;
                case R.id.b_thumb_right_arm:
                    finger = "D1";
                    fingerSelect = 1;
                    break;
            }
            //Guarda nueva imagen del rostro de la persona
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + "_" + operationID + ".jpg");
            if (f.exists()) {
                f.delete();
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + "_" + operationID + ".jpg");
            }
            try {
                f.createNewFile();
                //write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(photoBuffer);
                // remember close de FileOutput
                fo.close();

                switch (fingerSelect) {
                    case 10:
                        imageFilePinkyLeft = f;
                        break;
                    case 9:
                        imageFileRingLeft = f;
                        break;
                    case 8:
                        imageFileMiddleLeft = f;
                        break;
                    case 7:
                        imageFileIndexLeft = f;
                        break;
                    case 6:
                        imageFileThumbLeft = f;
                        break;
                    case 5:
                        imageFilePinkyRight = f;
                        break;
                    case 4:
                        imageFileRingRight = f;
                        break;
                    case 3:
                        imageFileMiddleRight = f;
                        break;
                    case 2:
                        imageFileIndexRight = f;
                        break;
                    case 1:
                        imageFileThumbRight = f;
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                f = null;
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
        boolean bitMapTake = false;

        if (bIndexLeft.getDrawable() instanceof BitmapDrawable && bIndexRight.getDrawable()instanceof BitmapDrawable) {
            bitMapTake = true;
        } else if (bIndexLeft.getDrawable() instanceof VectorDrawableCompat || bIndexRight.getDrawable() instanceof VectorDrawableCompat){
            bitMapTake = false;
        }
        //DES_COMENTAR
        //if(bitMapTake){
         //BORRAR
        if(true){
            String jsonString = buildJSON();
            Log.d("FingerJSON", "JSON FINGERs:"+jsonString);
            //Esta AsyncTask es de Otro activity
            //Falta crear la ppropia para mandar todos los archivos de las huellas digitales
            new FingersSend(FingerPrintsActivity.this, token, jsonString,fingersFileArray ).execute();

        }else {
//            Toast.makeText(FingerPrintsActivity.this, "Escanea los dedos indices para continuar", Toast.LENGTH_SHORT).show();
            goNext();
        }
    }

    @Override
    public void goNext() {
//        super.goNext();
        Intent i = new Intent(FingerPrintsActivity.this, PayConfirmationActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(FingerPrintsActivity.this,SharedPreferencesUtils.OPERATION_ID,"");
        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operationId", Integer.valueOf(operationID));
//            jsonObject.put("contentType", "image/jpeg");
            if (imageFileThumbLeft != null){
                jsonObject.put("dedo1I", true);
                fingersFileArray.add(imageFileThumbLeft);
            }else{
                jsonObject.put("dedo1I", false);
            }
            if (imageFileThumbRight != null){
                jsonObject.put("dedo1D", true);
                fingersFileArray.add(imageFileThumbRight);
            }else{
                jsonObject.put("dedo1D", false);
            }
            if (imageFileIndexLeft != null){
                jsonObject.put("dedo2I", true);
                fingersFileArray.add(imageFileIndexLeft);
            }else{
                jsonObject.put("dedo2I", false);
            }
            if (imageFileIndexRight != null){
                jsonObject.put("dedo2D", true);
                fingersFileArray.add(imageFileIndexRight);
            }else{
                jsonObject.put("dedo2D", false);
            }
            if (imageFileMiddleLeft != null){
                jsonObject.put("dedo3I", true);
                fingersFileArray.add(imageFileMiddleLeft);
            }else{
                jsonObject.put("dedo3I", false);
            }
            if (imageFileMiddleRight != null){
                jsonObject.put("dedo3D", true);
                fingersFileArray.add(imageFileMiddleRight);
            }else{
                jsonObject.put("dedo3D", false);
            }
            if (imageFileRingLeft != null){
                jsonObject.put("dedo4I", true);
                fingersFileArray.add(imageFileRingLeft);
            }else{
                jsonObject.put("dedo4I", false);
            }
            if (imageFileRingRight != null){
                jsonObject.put("dedo4D", true);
                fingersFileArray.add(imageFileRingRight);
            }else{
                jsonObject.put("dedo4D", false);
            }
            if (imageFilePinkyLeft != null){
                jsonObject.put("dedo5I", true);
                fingersFileArray.add(imageFilePinkyLeft);
            }else{
                jsonObject.put("dedo5I", false);
            }
            if (imageFilePinkyRight != null){
                jsonObject.put("dedo5D", true);
                fingersFileArray.add(imageFilePinkyRight);
            }else{
                jsonObject.put("dedo5D", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    /**
     * @Author: MAESCOBAR
     * Los siguientes metodos son para hacer la actualizacion automatica de la imagen, progressbar,
     * Buffer de la imagen que se extrajo(para envio)
     */
    @Override
    public void updateSensorProgressBar(int level) {
        try {
            ProgressBar progressBar = (ProgressBar) dialogScan.findViewById(R.id.vertical_progressbar);

            final float[] roundedCorners = new float[]{1, 1, 1, 1, 1, 1, 1, 1};
            ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

            int color = Color.GREEN;

            if (level <= 25) {
                color = Color.RED;
            } else if (level <= 50) {
                color = Color.YELLOW;
            }
            pgDrawable.getPaint().setColor(color);
            ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            progressBar.setProgressDrawable(progress);
            //progressBar.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
            progressBar.setProgress(level);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void updateSensorMessage(String sensorMessage) {
        TextView txtMensaje2 = (TextView) dialogScan.findViewById(R.id.tv_message);
        Log.i("updateMessage", "message update");
        try {
            txtMensaje2.setText(sensorMessage);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void updateImage(Bitmap bitmap) {
        ImageView imgFP2 = (ImageView) dialogScan.findViewById(R.id.fingerprint);
        Log.i("updateImage", "image update");
        try {
            imgFP2.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void showAlert(String message) {

    }

    @Override
    public void updateImageView(final byte[] imgeSrc, final int captureError) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (captureError == ErrorCodes.MORPHO_OK) {
                    imgFPBuff = imgeSrc;
                    Log.w("update OK Image", "image  OK update");
                    setImageToRightFinger();
                    if (dialogScan != null && dialogScan.isShowing()) {
                        dialogScan.dismiss();
                    }
                }
            }
        });
    }
}
