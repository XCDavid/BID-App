package com.teknei.bid.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.integratedbiometrics.ibscanultimate.IBScanDevice;
import com.teknei.bid.BaseAction;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.FingersSend;
import com.teknei.bid.asynctask.LogOut;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.utils.ApiConstants;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import mx.com.morpho.watson_mini.AppState;
import mx.com.morpho.watson_mini.WatsonMiniActivity;
import mx.com.morpho.watson_mini.WatsonMiniListener;

import static com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType.FLAT_SINGLE_FINGER;
import static com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType.FLAT_TWO_FINGERS;

public class FingerWatsonActivity extends WatsonMiniActivity implements BaseAction, View.OnClickListener, WatsonMiniListener {

    private static final String TAG = "FingerWatsonActivity";
    private Context myContext = FingerWatsonActivity.this;
    private String  mode;
    private byte[]  imgCapture;

    private LinearLayout lnTakeFingerWatson;
    private ImageView imageViewPreview;

    private Button buttonOpenDevice;
    private Button  buttonCloseDevice;
    private Button  buttonStartCapture;
    private Button  buttonStopCapture;

    private Button  buttonContinue;

    //  UI Variables //
    private RadioButton cbLeftThumb;
    private RadioButton cbLeftIndex;
    private RadioButton cbLeftMiddle;
    private RadioButton cbLeftRing;
    private RadioButton cbLeftLittle;
    private RadioButton cbRightThumb;
    private RadioButton cbRightIndex;
    private RadioButton cbRightMiddle;
    private RadioButton cbRightRing;
    private RadioButton cbRightLittle;

    private Switch swChangeBiosdk;

    byte[] left_thumb   = null;
    byte[] left_index   = null;
    byte[] left_middle  = null;
    byte[] left_ring    = null;
    byte[] left_little  = null;
    byte[] right_thumb  = null;
    byte[] right_index  = null;
    byte[] right_middle = null;
    byte[] right_ring   = null;
    byte[] right_little = null;

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
    //Left Hand
    String base64PinkyLeft;
    String base64RingLeft;
    String base64MiddleLeft;
    String base64IndexLeft;
    String base64ThumbLeft;
    //Right Hand
    String base64PinkyRight;
    String base64RingRight;
    String base64MiddleRight;
    String base64IndexRight;
    String base64ThumbRight;

    //Uso para MSOShower
    private List<File> fingersFileArray = null;

    File fileJson;
    List<File> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_watson);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.fingerprints_activity_name));
            invalidateOptionsMenu();
        }

        cbLeftThumb     = (RadioButton) findViewById(R.id.fin1);
        cbLeftIndex     = (RadioButton) findViewById(R.id.fin2);
        cbLeftMiddle    = (RadioButton) findViewById(R.id.fin3);
        cbLeftRing      = (RadioButton) findViewById(R.id.fin4);
        cbLeftLittle    = (RadioButton) findViewById(R.id.fin5);
        cbRightThumb    = (RadioButton) findViewById(R.id.fin6);
        cbRightIndex    = (RadioButton) findViewById(R.id.fin7);
        cbRightMiddle   = (RadioButton) findViewById(R.id.fin8);
        cbRightRing     = (RadioButton) findViewById(R.id.fin9);
        cbRightLittle   = (RadioButton) findViewById(R.id.fin10);

        imageViewPreview     = (ImageView) findViewById (R.id.image_view_preview);
        lnTakeFingerWatson   = (LinearLayout) findViewById(R.id.ln_take_finger_watson);

        buttonOpenDevice   = (Button) findViewById(R.id.button_open_device);
        buttonCloseDevice  = (Button) findViewById(R.id.button_close_device);
        buttonStartCapture = (Button) findViewById(R.id.button_start_capture);
        buttonStopCapture  = (Button) findViewById(R.id.button_stop_capture);
        buttonContinue     = (Button) findViewById(R.id.b_continue_bio_watson);

        buttonOpenDevice.setOnClickListener  (this);
        buttonCloseDevice.setOnClickListener (this);
        buttonStartCapture.setOnClickListener(this);
        buttonStopCapture.setOnClickListener (this);
        buttonContinue.setOnClickListener    (this);

        fingersFileArray = new ArrayList<File>();
        fileList = new ArrayList<File>();

        swChangeBiosdk = (Switch) findViewById(R.id.sw_watson_change_biosdk);

        swChangeBiosdk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (swChangeBiosdk.isChecked()){

                    Intent i = new Intent(FingerWatsonActivity.this, FingerBioSdkActivity.class);
                    startActivity(i);

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_open_device:
                openDevice();
                break;

            case R.id.button_close_device:
                closeDevice();
                break;

            case R.id.button_start_capture:

                if(!cbLeftThumb.isChecked()   && !cbLeftIndex.isChecked()   && !cbLeftMiddle.isChecked()  &&
                        !cbLeftRing.isChecked()    && !cbLeftLittle.isChecked()  && !cbRightThumb.isChecked()  &&
                        !cbRightIndex.isChecked()  && !cbRightMiddle.isChecked() && !cbRightRing.isChecked()   &&
                        !cbRightLittle.isChecked()) {

                    showToastOnUiThread("Debe seleccionar una opción", Toast.LENGTH_LONG);

                } else {

                    startCapture();

                }
                break;

            case R.id.button_stop_capture:
                stopCapture();
                break;

            case R.id.b_continue_bio_watson:

                if (validateIndexFingers()) {
                    Log.d(TAG,"SEND PETICION TRUE");
                    sendPetition();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        if (token.equals("")) {
             Intent end = new Intent(getApplicationContext(), LogInActivity.class);
             end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(end);
             finish();
        }

        mode = "CAPTURE_ONE_FINGER_FLAT";
        Log.i(TAG, "[INFO] onResume: " + mode);
    }

    @Override
    public void onPrepareActivity() {
        super.onPrepareActivity();
        setWatsonMiniListener(this);
        Log.i(TAG, "[INFO] onPrepareActivity: " + getSdkVersion());
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_finger_watson;
    }

    @Override
    protected int menuLayoutResourceId() {
        return 0;
    }

    @Override
    protected ImageView getImageViewPreview() {
        return imageViewPreview;
    }

    @Override
    protected IBScanDevice.ImageType getCaptureMode() {
        switch (mode){
            case "CAPTURE_ONE_FINGER_FLAT":
                return FLAT_SINGLE_FINGER;
            case "CAPTURE_TWO_FINGERS_FLAT":
                return FLAT_TWO_FINGERS;
            default:
                return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// WatsonMiniListener ///////////////////////////////////////////
    @Override
    public void onStatusChanged(final String status) {
        Log.i(TAG, "[INFO] onStatusChanged: " + status);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastOnUiThread(status, Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onError(String error) {
        showToastOnUiThread(error, Toast.LENGTH_LONG);
    }

    @Override
    public void onDeviceCounterChanged(int devices) {
        //showToastOnUiThread("Devices connected: " + devices, Toast.LENGTH_SHORT);
    }

    @Override
    public void onDeviceInitialization(final int deviceIndex, final int progressValue) {
        Log.i(TAG, "[INFO] onDeviceInitialization: " + progressValue + "%");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressValue != 100){
                    buttonOpenDevice.setEnabled(false);
                }else{
                    buttonOpenDevice.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onDeviceStateChanged(boolean isOpen) {
        if(isOpen) {
            buttonOpenDevice.setVisibility(View.GONE);
            buttonCloseDevice.setVisibility(View.VISIBLE);
        } else {
            buttonOpenDevice.setVisibility(View.VISIBLE);
            buttonCloseDevice.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNFIWScoreCalculated(int nfiqScore) {
        // showToastOnUiThread("NFIQ Score: " + nfiqScore, Toast.LENGTH_SHORT);
    }

    @Override
    public void onFrameTimeChanged(final String frameTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastOnUiThread(frameTime, Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onCaptureStarts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonStartCapture.setVisibility(View.GONE);
                buttonStopCapture.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCaptureStop() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonStartCapture.setVisibility(View.VISIBLE);
                buttonStopCapture.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Called when image capture finish with success
     *
     * @param imageBuffer Image buffer
     * @param imageBitmap Image Bitmap
     * @param imageFormat Image Format
     */
    @Override
    public void onCaptureFinishedSuccess(byte[] imageBuffer, final Bitmap imageBitmap, IBScanDevice.ImageFormat imageFormat) {

        String operationID = SharedPreferencesUtils.readFromPreferencesString(
                                FingerWatsonActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
        String finger = "";
        int fingerSelect = 0;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        imageBitmap.compress (Bitmap.CompressFormat.PNG, 100, baos);

        imageBuffer = baos.toByteArray();

        if (imageBuffer != null) {

            if(cbLeftThumb.isChecked()) {
                left_thumb = imageBuffer;
                finger = "I1";
                fingerSelect = 6;
                base64ThumbLeft = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbLeftIndex.isChecked()) {
                left_index = imageBuffer;
                finger = "I2";
                fingerSelect = 7;
                base64IndexLeft = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbLeftMiddle.isChecked()) {
                left_middle = imageBuffer;
                finger = "I3";
                fingerSelect = 8;
                base64MiddleLeft = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbLeftRing.isChecked()) {
                left_ring = imageBuffer;
                finger = "I4";
                fingerSelect = 9;
                base64RingLeft = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbLeftLittle.isChecked()) {
                left_little = imageBuffer;
                finger = "I5";
                fingerSelect = 10;
                base64PinkyLeft = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbRightThumb.isChecked()) {
                right_thumb = imageBuffer;
                finger = "D1";
                fingerSelect = 1;
                base64ThumbRight = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbRightIndex.isChecked()) {
                right_index = imageBuffer;
                finger = "D2";
                fingerSelect = 2;
                base64IndexRight = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbRightMiddle.isChecked()) {
                right_middle = imageBuffer;
                finger = "D3";
                fingerSelect = 3;
                base64MiddleRight = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbRightRing.isChecked()) {
                right_ring = imageBuffer;
                finger = "D4";
                fingerSelect = 4;
                base64RingRight = com.teknei.bid.tools.Base64.encode(imageBuffer);

            }else if(cbRightLittle.isChecked()) {
                right_little = imageBuffer;
                finger = "D5";
                fingerSelect = 5;
                base64PinkyRight = com.teknei.bid.tools.Base64.encode(imageBuffer);
            }

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + "_" + operationID + ".jpg");
            if (f.exists()) {
                f.delete();
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + "_" + operationID + ".jpg");
            }

            try {
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(imageBuffer);

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
            }
        }
    }

    @Override
    public void logOut() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        if (!operationID.equals("")) {
//            new CancelOp(this, operationID, token, ApiConstants.ACTION_BLOCK_CANCEL_OPERATION).execute();
            SharedPreferencesUtils.cleanSharedPreferencesOperation(this);
//            return;
        }
        if (!token.equals("")) {
            new LogOut(this, token).execute();
            return;
        }
        if (token.equals("") && operationID.equals("")) {
            finish();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(FingerWatsonActivity.this, FakeINEActivity.class);
        startActivity(i);
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String fingerOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.FINGERS_OPERATION, "");

        if (fingerOperation.equals("")) {
            fileList.clear();
            String localTime = PhoneSimUtils.getLocalDateAndTime();

            String jsonString = buildJSON();

            fileList.add(fileJson);

            if (imageFileIndexLeft != null) {
                fileList.add(imageFileIndexLeft);
            }

            if (imageFileIndexRight != null) {
                fileList.add(imageFileIndexRight);
            }

            Log.d("ArrayList Files", "Files:" + fileList.size());
            new FingersSend(FingerWatsonActivity.this, token, jsonString, fileList, ApiConstants.TYPE_ACT_LOCAL).execute();

        } else {
            goNext();
        }
    }

    public void cancelOperation() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");
        String token       = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");

        if (!operationID.equals("")) {
            SharedPreferencesUtils.cleanSharedPreferencesOperation(this);
        }

        String operationIDAux = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");
        if (operationIDAux.equals("")) {
            Intent end = new Intent(this, FormActivity.class);
            end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(end);
            finish();
        }
    }

    @Override
    public void goStep(int flowStep) {

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
            dialogoAlert = new AlertDialog(FingerWatsonActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION_LOCAL);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(FingerWatsonActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT_LOCAL);
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

    public boolean validateIndexFingers() {
        boolean bitMapTake;
        if (imageFilePinkyLeft == null && imageFileRingLeft == null && imageFileMiddleLeft == null &&
                imageFileIndexLeft == null && imageFileThumbLeft == null && imageFilePinkyRight == null &&
                    imageFileRingRight == null && imageFileMiddleRight == null && imageFileIndexRight == null &&
                        imageFileThumbRight == null) {
            bitMapTake = false;
            Toast.makeText(FingerWatsonActivity.this, "Capture minimo un dedo para continuar", Toast.LENGTH_SHORT).show();
        } else {
            bitMapTake = true;
        }
        return bitMapTake;
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(FingerWatsonActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
        String idEnterprice = SharedPreferencesUtils.readFromPreferencesString(FingerWatsonActivity.this, SharedPreferencesUtils.ID_ENTERPRICE, "");
        String customerType = SharedPreferencesUtils.readFromPreferencesString(FingerWatsonActivity.this, SharedPreferencesUtils.CUSTOMER_TYPE, "");

        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emprId", idEnterprice);
            jsonObject.put("customerType", customerType);
            jsonObject.put("operationId", operationID+"");
            jsonObject.put("contentType", "image/jpeg");

            Log.i("....-.......,,.,",jsonObject.toString());

            if (imageFileThumbLeft != null) {
                jsonObject.put("dedo1I", true);
                fingersFileArray.add(imageFileThumbLeft);
            } else {
                jsonObject.put("dedo1I", false);
            }

            if (imageFileThumbRight != null) {
                jsonObject.put("dedo1D", true);
                fingersFileArray.add(imageFileThumbRight);
            } else {
                jsonObject.put("dedo1D", false);
            }

            if (imageFileIndexLeft != null) {
                jsonObject.put("dedo2I", true);
                fingersFileArray.add(imageFileIndexLeft);
            } else {
                jsonObject.put("dedo2I", false);
            }

            if (imageFileIndexRight != null) {
                jsonObject.put("dedo2D", true);
                fingersFileArray.add(imageFileIndexRight);
            } else {
                jsonObject.put("dedo2D", false);
            }

            if (imageFileMiddleLeft != null) {
                jsonObject.put("dedo3I", true);
                fingersFileArray.add(imageFileMiddleLeft);
            } else {
                jsonObject.put("dedo3I", false);
            }

            if (imageFileMiddleRight != null) {
                jsonObject.put("dedo3D", true);
                fingersFileArray.add(imageFileMiddleRight);
            } else {
                jsonObject.put("dedo3D", false);
            }

            if (imageFileRingLeft != null) {
                jsonObject.put("dedo4I", true);
                fingersFileArray.add(imageFileRingLeft);
            } else {
                jsonObject.put("dedo4I", false);
            }

            if (imageFileRingRight != null) {
                jsonObject.put("dedo4D", true);
                fingersFileArray.add(imageFileRingRight);
            } else {
                jsonObject.put("dedo4D", false);
            }

            if (imageFilePinkyLeft != null) {
                jsonObject.put("dedo5I", true);
                fingersFileArray.add(imageFilePinkyLeft);
            } else {
                jsonObject.put("dedo5I", false);
            }

            if (imageFilePinkyRight != null) {
                jsonObject.put("dedo5D", true);
                fingersFileArray.add(imageFilePinkyRight);
            } else {
                jsonObject.put("dedo5D", false);
            }

            jsonObject = addBase64Fingers(jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Writer output;
            fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "fingers" + ".json");
            if (fileJson.exists()) {
                fileJson.delete();
                fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "fingers" + ".json");
            }
            Log.d ("FINGER WATSON ",jsonObject.toString());
            output = new BufferedWriter(new FileWriter(fileJson));
            output.write(jsonObject.toString());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private JSONObject addBase64Fingers(JSONObject jsonObject) {

        if (base64PinkyLeft != null && !base64PinkyLeft.equals("")) {
            try {
                jsonObject.put("ll", base64PinkyLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64RingLeft != null && !base64RingLeft.equals("")) {
            try {
                jsonObject.put("lr", base64RingLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64MiddleLeft != null && !base64MiddleLeft.equals("")) {
            try {
                jsonObject.put("lm", base64MiddleLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64IndexLeft != null && !base64IndexLeft.equals("")) {
            try {
                jsonObject.put("li", base64IndexLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64ThumbLeft != null && !base64ThumbLeft.equals("")) {
            try {
                jsonObject.put("lt", base64ThumbLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Right arm
        if (base64PinkyRight != null && !base64PinkyRight.equals("")) {
            try {
                jsonObject.put("rl", base64PinkyRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64RingRight != null && !base64RingRight.equals("")) {
            try {
                jsonObject.put("rr", base64RingRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64MiddleRight != null && !base64MiddleRight.equals("")) {
            try {
                jsonObject.put("rm", base64MiddleRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64IndexRight != null && !base64IndexRight.equals("")) {
            try {
                jsonObject.put("ri", base64IndexRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64ThumbRight != null && !base64ThumbRight.equals("")) {
            try {
                jsonObject.put("rt", base64ThumbRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
}
