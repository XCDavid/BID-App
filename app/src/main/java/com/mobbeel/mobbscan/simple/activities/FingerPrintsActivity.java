package com.mobbeel.mobbscan.simple.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobbeel.mobbscan.simple.R;

import java.util.HashMap;
import java.util.Iterator;

import com.mobbeel.mobbscan.simple.mso.MSOConnection;
import com.mobbeel.mobbscan.simple.tools.DeviceDetectionMode;
import com.mobbeel.mobbscan.simple.tools.MorphoTools;
import com.morpho.android.usb.USBManager;
import com.mobbeel.mobbscan.simple.tools.TKN_MSO_ERROR;
import com.morpho.morphosmart.sdk.MorphoDevice;

public class FingerPrintsActivity extends AppCompatActivity implements View.OnClickListener{
    MorphoDevice morphoDevice;
    MSOConnection msoConnection;
    private DeviceDetectionMode detectionMode = DeviceDetectionMode.SdkDetection;

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
        USBManager.getInstance().initialize(this, "com.morpho.morphosample.USB_ACTION");
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
                mso1300(view);
                break;
        }
    }

    public void mso1300(View v) {
        int cnt = countDevices();
        detectionMode = DeviceDetectionMode.SdkDetection;
        try {
            MSOConnection.getInstance().tkn_mso_connect();
        } catch (TKN_MSO_ERROR e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
        try {
            MSOConnection.getInstance().tkn_mso_capture(FingerPrintsActivity.this);
        }catch (TKN_MSO_ERROR e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
//        ImageView tv1;
//        tv1= (ImageView) findViewById(R.id.b_pinky_left_arm);
        ((ImageButton)v).setImageBitmap( MSOConnection.getInstance().getBitMap() );
    }

    public void updateImageFingerView(Bitmap bitmap){
        bPinkyLeft.setImageBitmap( bitmap );
    }

    synchronized int countDevices() {
        int count = 0;
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> usbDeviceList = usbManager.getDeviceList();

        Iterator<UsbDevice> usbDeviceIterator = usbDeviceList.values().iterator();
        while (usbDeviceIterator.hasNext()) {
            UsbDevice usbDevice = usbDeviceIterator.next();
            if (MorphoTools.isSupported(usbDevice.getVendorId(), usbDevice.getProductId())) {
                boolean hasPermission = usbManager.hasPermission(usbDevice);
                if (!hasPermission) {
                    // Request permission for using the device
                    usbManager.requestPermission(usbDevice, PendingIntent.getBroadcast(this, 0, new Intent("com.morpho.android.usb.USB_PERMISSION"), 0));
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }
}
