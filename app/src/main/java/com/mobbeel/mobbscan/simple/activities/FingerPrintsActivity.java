package com.mobbeel.mobbscan.simple.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.annotation.NonNull;
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

import com.mobbeel.mobbscan.simple.R;

import java.util.HashMap;
import java.util.Iterator;

import com.mobbeel.mobbscan.simple.dialogs.FingerScanDialog;
import com.mobbeel.mobbscan.simple.mso.MSOConnection;
import com.mobbeel.mobbscan.simple.mso.MSOShower;
import com.mobbeel.mobbscan.simple.tools.DeviceDetectionMode;
import com.mobbeel.mobbscan.simple.tools.MorphoTools;
import com.morpho.android.usb.USBManager;
import com.mobbeel.mobbscan.simple.tools.TKN_MSO_ERROR;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.MorphoDevice;

public class FingerPrintsActivity extends AppCompatActivity implements View.OnClickListener, MSOShower {
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
//    private TextView txtMensaje;

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



        morphoDevice = new MorphoDevice();
        // ---------- Aqui se inicia la conexion con el lector(ya no se hace en el metodo mso1300
        USBManager.getInstance().initialize(this, "com.morpho.morphosample.USB_ACTION");
        try {
            MSOConnection.getInstance().tkn_mso_connect();
            Log.i(this.getClass().getName(), "Conexion Realizada");
        } catch (TKN_MSO_ERROR e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
        MSOConnection.getInstance().setMsoShower(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean permission = USBManager.getInstance().isDevicesHasPermission();
        if (!permission){
            onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.b_continue_fingerprints:
                Intent i = new Intent(this, DocumentScanActivity.class);

//                Intent i = new Intent(this, PayConfirmationActivity.class);
                startActivity(i);
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
                dialogScan = new FingerScanDialog(this,"Esperando escaneo","Instrucciones");
                dialogScan.setCancelable(false);
                dialogScan.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogScan.show();
                mso1300(view);
                break;
        }
    }

    public void mso1300(View v) {
        imgFP = ((ImageButton)v);

//        int cnt = countDevices();
//        detectionMode = DeviceDetectionMode.SdkDetection;
        try {
//            MSOConnection.getInstance().tkn_mso_connect();
//            MSOConnection.getInstance().setMsoShower(this);
            MSOConnection.getInstance().tkn_mso_capture(FingerPrintsActivity.this);
        } catch (TKN_MSO_ERROR e){
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
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }

    public void setImageToRightFinger(){
        if(imgFP != null) {
            imgFP.setImageBitmap(MSOConnection.getInstance().getBitMap());
        }
    }

    /**
     * @Author: MAESCOBAR
     * Los siguientes metodos son para hacer la actualizacion automatica de la imagen, progressbar,
     * Buffer de la imagen que se extrajo(para envio)
     */
    @Override
    public void updateSensorProgressBar(int level) {
        try
        {
            ProgressBar progressBar = (ProgressBar) dialogScan.findViewById(R.id.vertical_progressbar);

            final float[] roundedCorners = new float[] { 1, 1, 1, 1, 1, 1, 1, 1 };
            ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

            int color = Color.GREEN;

            if (level <= 25)
            {
                color = Color.RED;
            }
            else if (level <= 50)
            {
                color = Color.YELLOW;
            }
            pgDrawable.getPaint().setColor(color);
            ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            progressBar.setProgressDrawable(progress);
            //progressBar.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
            progressBar.setProgress(level);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    @Override
    public void updateSensorMessage(String sensorMessage) {
        TextView txtMensaje2 = (TextView) dialogScan.findViewById(R.id.tv_message);
        Log.i("updateMessage","message update");
        try{
            txtMensaje2.setText(sensorMessage);
        }
        catch (Exception e){
            e.getMessage();
        }
    }

    @Override
    public void updateImage(Bitmap bitmap) {
        ImageView imgFP2 = (ImageView) dialogScan.findViewById(R.id.fingerprint);
        Log.i("updateImage","image update");
        try
        {
            imgFP2.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
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
                if(captureError == ErrorCodes.MORPHO_OK){
                    imgFPBuff = imgeSrc;
                    Log.w("update OK Image","image  OK update");
                    setImageToRightFinger();
                    if(dialogScan != null  && dialogScan.isShowing()){
                        dialogScan.dismiss();
                    }
                }
            }
        });
    }
}
