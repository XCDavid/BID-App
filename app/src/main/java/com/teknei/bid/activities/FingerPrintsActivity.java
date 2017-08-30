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
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.FingerScanDialog;
import com.teknei.bid.mso.MSOConnection;
import com.teknei.bid.mso.MSOShower;
import com.morpho.android.usb.USBManager;
import com.teknei.bid.tools.TKN_MSO_ERROR;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.MorphoDevice;
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

public class FingerPrintsActivity extends BaseActivity implements View.OnClickListener, MSOShower {
    MorphoDevice morphoDevice;
    MSOConnection msoConnection;

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

    File fileJson;
    List<File> fileList;

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
        fileList = new ArrayList<File>();

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
//        if (!permission) {
//            try {
//                onBackPressed();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_continue_fingerprints:
                if (validateIndexFingers()) {
                    sendPetition();
                }
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
                dialogScan = new FingerScanDialog(this, "Esperando escaneo", "Coloca el dedo seleccionado en el escaner biométrico mientras esta la luz roja.");
                dialogScan.setCancelable(false);
                dialogScan.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogScan.show();
                mso1300(view);
                break;
        }
    }

    public boolean validateIndexFingers() {
        boolean bitMapTake;
        if (bIndexLeft.getDrawable() instanceof BitmapDrawable && bIndexRight.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(FingerPrintsActivity.this, "Capture minimo los dos dedos índice para continuar", Toast.LENGTH_SHORT).show();
        }
        return bitMapTake;
    }

    public void mso1300(View v) {
        imgFP = ((ImageButton) v);
        try {
            MSOConnection.getInstance().tkn_mso_capture(FingerPrintsActivity.this);
        } catch (TKN_MSO_ERROR e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
    }

    public void setImageToRightFinger() {
        if (imgFP != null) {
            Bitmap msoBitMap = MSOConnection.getInstance().getBitMap();
            photoBuffer = imgFPBuff;
            imgFP.setImageBitmap(msoBitMap);

            String operationID = SharedPreferencesUtils.readFromPreferencesString(FingerPrintsActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
            String finger = "";
            int fingerSelect = 0;
            switch (imgFP.getId()) {
                case R.id.b_pinky_left_arm:
                    finger = "I5";
                    fingerSelect = 10;
                    base64PinkyLeft = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_ring_left_arm:
                    finger = "I4";
                    fingerSelect = 9;
                    base64RingLeft = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_middle_left_arm:
                    finger = "I3";
                    fingerSelect = 8;
                    base64MiddleLeft = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_index_left_arm:
                    finger = "I2";
                    fingerSelect = 7;
                    base64IndexLeft = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_thumb_left_arm:
                    finger = "I1";
                    fingerSelect = 6;
                    base64ThumbLeft = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_pinky_right_arm:
                    finger = "D5";
                    fingerSelect = 5;
                    base64PinkyRight = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_ring_riht_arm:
                    finger = "D4";
                    fingerSelect = 4;
                    base64RingRight = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_middle_right_arm:
                    finger = "D3";
                    fingerSelect = 3;
                    base64MiddleRight = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_index_right_arm:
                    finger = "D2";
                    fingerSelect = 2;
                    base64IndexRight = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
                case R.id.b_thumb_right_arm:
                    finger = "D1";
                    fingerSelect = 1;
                    base64ThumbRight = com.teknei.bid.tools.Base64.encode(imgFPBuff);
                    break;
            }
            //Guarda nueva imagen del dedo
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + "_" + operationID + ".jpg");
            if (f.exists()) {
                f.delete();
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + "_" + operationID + ".jpg");
            }
            try {
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
            }
        }
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String fingerOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.FINGERS_OPERATION, "");
        if (fingerOperation.equals("")) {
            fileList.clear();
            String localTime = PhoneSimUtils.getLocalDateAndTime();
//            SharedPreferencesUtils.saveToPreferencesString(FingerPrintsActivity.this, SharedPreferencesUtils.TIMESTAMP_FINGERPRINTS, localTime);

            String jsonString = buildJSON();
            fileList.add(fileJson);
            if (imageFileIndexLeft != null) {
                fileList.add(imageFileIndexLeft);
            }
            if (imageFileIndexRight != null) {
                fileList.add(imageFileIndexRight);
            }
            Log.d("ArrayList Files", "Files:" + fileList.size());
            new FingersSend(FingerPrintsActivity.this, token, jsonString, fileList).execute();
        } else {
            goNext();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(FingerPrintsActivity.this, FakeINEActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID = SharedPreferencesUtils.readFromPreferencesString(FingerPrintsActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("contentType", "image/jpeg");
            jsonObject = addBase64Fingers(jsonObject);
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
            progressBar.setProgress(level);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void updateSensorMessage(String sensorMessage) {
        TextView txtMensaje2 = (TextView) dialogScan.findViewById(R.id.tv_message_move_finger);
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
            dialogoAlert = new AlertDialog(FingerPrintsActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(FingerPrintsActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
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
