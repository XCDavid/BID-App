package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Environment;
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

import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.ConfirmSignContract;
import com.teknei.bid.asynctask.SendFingerSignContract;
import com.teknei.bid.dialogs.FingerScanDialog;
import com.teknei.bid.domain.FingerSingDTO;
import com.teknei.bid.mso.MSOConnection;
import com.teknei.bid.mso.MSOShower;
import com.teknei.bid.tools.TKN_MSO_ERROR;
import com.teknei.bid.utils.SharedPreferencesUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ContractFingerSignatureMSO extends BaseActivity implements View.OnClickListener, MSOShower {

    MorphoDevice morphoDevice;
    MSOConnection msoConnection;

    Button      btnContinue;
    ImageButton btnCaptureFinger;

    FingerScanDialog dialogScan;

    private byte[] imgFPBuff = null;
    private ImageButton imgFP;

    private byte[] photoBuffer;

    File imageFileFingerPrint;
    String base64FingerPrint="";

    private String idOperation;
    private FingerSingDTO fingerDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_finger_signature_mso);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.cfs_mso_form_activity_name));
            invalidateOptionsMenu();
        }

        btnContinue = (Button)      findViewById(R.id.cfs_mso_b_continue);
        btnCaptureFinger = (ImageButton) findViewById(R.id.cfs_mso_btn_finger_print);

        btnContinue.setOnClickListener(this);
        btnCaptureFinger.setOnClickListener(this);

        morphoDevice = new MorphoDevice();
        // ---------- Aqui se inicia la conexion con el lector(ya no se hace en el metodo mso1300
        USBManager.getInstance().initialize(this, "com.morpho.morphosample.USB_ACTION");

        try {

            MSOConnection.getInstance().tkn_mso_connect();
            Log.i(this.getClass().getName(), "Conexion Realizada");

            MSOConnection.getInstance().setMsoShower(this);

        } catch (TKN_MSO_ERROR e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();

            MSOConnection.getInstance().setMsoShower(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean permission = USBManager.getInstance().isDevicesHasPermission();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cfs_mso_b_continue:
                if (validateIndexFingers()) {
                    sendPetition();
                }
                break;
            case R.id.cfs_mso_btn_finger_print:

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
        if (btnCaptureFinger.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(ContractFingerSignatureMSO.this, getResources().getString(R.string.cfs_mso_message_obligatory), Toast.LENGTH_SHORT).show();
        }
        return bitMapTake;
    }

    public void mso1300(View v) {
        imgFP = ((ImageButton) v);
        try {
            MSOConnection.getInstance().tkn_mso_capture(ContractFingerSignatureMSO.this);
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

            String finger = "";
            int fingerSelect = 0;

            finger = "ri";
            fingerSelect = 10;
            base64FingerPrint = com.teknei.bid.tools.Base64.encode(imgFPBuff);

            //Guarda nueva imagen del dedo
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + ".jpg");
            if (f.exists()) {
                f.delete();
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + ".jpg");
            }
            try {
                //write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(photoBuffer);
                // remember close de FileOutput
                fo.close();

                imageFileFingerPrint = f;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");

        idOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.OPERATION_ID, "");

        fingerDTO    = new FingerSingDTO(base64FingerPrint,Long.parseLong(idOperation));

        new SendFingerSignContract(ContractFingerSignatureMSO.this, token, fingerDTO).execute();
    }

    public void confirmContract() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        new ConfirmSignContract(ContractFingerSignatureMSO.this, token, fingerDTO).execute();
    }

    @Override
    public void goNext() {
        Intent i = new Intent(ContractFingerSignatureMSO.this, ResultOperationActivity.class);
        startActivity(i);
    }

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
}
