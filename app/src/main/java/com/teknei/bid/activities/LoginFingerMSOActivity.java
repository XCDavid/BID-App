package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.teknei.bid.asynctask.FingersSend;
import com.teknei.bid.asynctask.LoginFingerSend;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.dialogs.FingerScanDialog;
import com.teknei.bid.domain.FingerLoginDTO;
import com.teknei.bid.mso.MSOConnection;
import com.teknei.bid.mso.MSOShower;
import com.teknei.bid.services.CipherFingerServices;
import com.teknei.bid.tools.TKN_MSO_ERROR;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.PhoneSimUtils;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class LoginFingerMSOActivity extends BaseActivity implements View.OnClickListener, MSOShower {

    MorphoDevice morphoDevice;
    MSOConnection msoConnection;

    Button      fingerPrintLogin;
    ImageButton fingerPrint;

    FingerScanDialog dialogScan;

    private byte[] imgFPBuff = null;
    private ImageButton imgFP;

    private byte[] photoBuffer;

    File imageFileFingerPrint;
    String base64FingerPrint;

    private String idClient;
    private FingerLoginDTO fingerDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_finger_mso);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        idClient     = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_CLIENT, "");

        fingerPrintLogin = (Button)      findViewById(R.id.lfm_b_login_finger);
        fingerPrint      = (ImageButton) findViewById(R.id.lfm_b_finger_print);

        fingerPrintLogin.setOnClickListener(this);
        fingerPrint.setOnClickListener(this);

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
            case R.id.lfm_b_login_finger:
                if (validateIndexFingers()) {
                    sendPetition();
                }
                break;
            case R.id.lfm_b_finger_print:

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
        if (fingerPrint.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(LoginFingerMSOActivity.this, "Capture huella del dedo índice", Toast.LENGTH_SHORT).show();
        }
        return bitMapTake;
    }

    public void mso1300(View v) {
        imgFP = ((ImageButton) v);
        try {
            MSOConnection.getInstance().tkn_mso_capture(LoginFingerMSOActivity.this);
        } catch (TKN_MSO_ERROR e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
    }

    public void setImageToRightFinger() {
        byte[] ciphered;

        if (imgFP != null) {
            Bitmap msoBitMap = MSOConnection.getInstance().getBitMap();
            photoBuffer = imgFPBuff;
            imgFP.setImageBitmap(msoBitMap);

            String finger = "";
            int fingerSelect = 0;

            ciphered = com.teknei.bid.services.CipherFingerServices.cipherFinger(idClient, imgFPBuff);

            finger = "ri";
            fingerSelect = 10;
            base64FingerPrint = com.teknei.bid.tools.Base64.encode(ciphered);

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

    public String buildJSON() {
        File fileJson;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", idClient);
            jsonObject.put("contentType", "image/wsq");
            if (base64FingerPrint != null && !base64FingerPrint.equals("")) {
                try {
                    jsonObject.put("rm", base64FingerPrint);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Writer output;
            fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "login" + ".json");
            if (fileJson.exists()) {
                fileJson.delete();
                fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "login" + ".json");
            }
            output = new BufferedWriter(new FileWriter(fileJson));
            output.write(jsonObject.toString());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        fingerDTO    = new FingerLoginDTO();

        fingerDTO.setFingerIndex(base64FingerPrint);
        fingerDTO.setId         (idClient);
        fingerDTO.setContentType("image/wsq");

        Log.d("LoginFingerMSOACT", buildJSON());

        new LoginFingerSend(LoginFingerMSOActivity.this, token, fingerDTO).execute();
    }

    @Override
    public void goNext() {
        Intent i = new Intent(LoginFingerMSOActivity.this, PersonSelectionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
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


    @Override
    public void onBackPressed() {
        SharedPreferencesUtils.deleteFromPreferences(LoginFingerMSOActivity.this, SharedPreferencesUtils.TOKEN_APP);
        SharedPreferencesUtils.deleteFromPreferences(LoginFingerMSOActivity.this, SharedPreferencesUtils.USERNAME);
        SharedPreferencesUtils.cleanSharedPreferencesOperation(LoginFingerMSOActivity.this);

        Intent i = new Intent(LoginFingerMSOActivity.this, LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
