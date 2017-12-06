package com.teknei.bid.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.integratedbiometrics.ibscanultimate.IBScan;
import com.integratedbiometrics.ibscanultimate.IBScanDevice;
import com.integratedbiometrics.ibscanultimate.IBScanDeviceListener;
import com.integratedbiometrics.ibscanultimate.IBScanException;
import com.integratedbiometrics.ibscanultimate.IBScanListener;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.FingersSend;
import com.teknei.bid.asynctask.LoginFingerSend;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.domain.FingerLoginDTO;
import com.teknei.bid.response.OAuthAccessToken;
import com.teknei.bid.services.OAuthApi;
import com.teknei.bid.utils.ApiConstants;
import com.teknei.bid.utils.PhoneSimUtils;
import com.teknei.bid.utils.SharedPreferencesUtils;
import com.teknei.bid.ws.RetrofitSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import butterknife.ButterKnife;
import mx.com.morpho.watson_mini.AppState;
import mx.com.morpho.watson_mini.PlaySound;
import mx.com.morpho.watson_mini.WatsonMiniData;
import mx.com.morpho.watson_mini.WatsonMiniHelper;
import mx.com.morpho.watson_mini.WatsonMiniListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType.FLAT_SINGLE_FINGER;
import static com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType.FLAT_TWO_FINGERS;
import static mx.com.morpho.watson_mini.AppState.INITIALIZED;
import static mx.com.morpho.watson_mini.AppState.SCANNER_ATTACHED;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITIES_COUNT;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITY_FAIR_COLOR;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITY_GOOD_COLOR;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITY_NOT_PRESENT_COLOR;
import static mx.com.morpho.watson_mini.Constants.FINGER_QUALITY_POOR_COLOR;
import static mx.com.morpho.watson_mini.Constants.INITIALIZING_DEVICE_INDEX;
import static mx.com.morpho.watson_mini.Constants.STOPPING_CAPTURE_DELAY_MILLIS;
import static mx.com.morpho.watson_mini.Keys.EXTRA_ARGUMENTS;
import static mx.com.morpho.watson_mini.WatsonMiniHelper.drawBitmapRollingLine;

public class LoginFingerWatsonActivity extends BaseActivity implements View.OnClickListener, WatsonMiniListener,
        IBScanListener, IBScanDeviceListener {

    private static final String TAG = "LoginFingerWatsonActivity";

    private String status;
    private WatsonMiniListener watsonMiniListener;
    private Bitmap m_BitmapImage;
    private Bitmap m_BitmapKojakRollImage;
    private Boolean deviceIsOpen = false;
    private TextView[] txtFingerQuality = new TextView[FINGER_QUALITIES_COUNT];

    protected String folderName  = ".morpho";
    protected String WSQfileName = "ImageCaptured";

    protected IBScan ibScan; // The scanner manager

    private IBScanDevice ibScanDevice;
    private IBScanDevice.ImageType imageType;

    protected boolean hasPermission; // To handle if we have permission to use a device
    protected WatsonMiniData watsonMiniData = new WatsonMiniData(); // Information retained (a data buffer)
    protected IBScanDevice.ImageData imageData; // Information retained to show view
    protected PlaySound playSound = new PlaySound(); // An object that will play a sound when the image capture has completed
    private IBScanDevice.ImageData lastImage; //Information retained to show view.

    private ImageView    imageViewPreview;
    private Button       buttonCaptureFinger;
    private Button       buttonContinue;

    private String       mode;

    byte[] fingerLogin   = null;

    String base64FingerLogin;

    File imageFileFinger;

    private Bundle activityArguments;

    File fileJson;

    private String idClient;

    private FingerLoginDTO fingerDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_finger_watson);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.lfM_login_fingerprints_activity_name));
            invalidateOptionsMenu();
        }

        imageViewPreview    = (ImageView) findViewById(R.id.lfw_image_view_preview);
        buttonContinue      = (Button)    findViewById(R.id.lfw_b_login_finger);
        buttonCaptureFinger = (Button)    findViewById(R.id.lfw_b_capture_fingerprint);

        buttonContinue.setOnClickListener(this);
        buttonCaptureFinger.setOnClickListener(this);

        initArguments(savedInstanceState);
        bindViews();                        // Bind Views using butterknife

        onPrepareActivity();
    }

    public void onPrepareActivity() {
        ibScan = IBScan.getInstance(this.getApplicationContext());
        ibScan.setScanListener(this);
        transitionToRefresh();
        setWatsonMiniListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lfw_b_login_finger:

                if (validateIndexFingers()) {
                    Log.d(TAG,"SEND PETICION TRUE");

                    closeDevice();

                    sendPetition();
                }

                break;

            case R.id.lfw_b_capture_fingerprint:

                if (watsonMiniData.state == INITIALIZED)
                    startCapture();
                else
                    openDevice();

                break;
        }
    }

    public boolean validateIndexFingers() {
        boolean bitMapTake;

        if ( imageFileFinger==null) {

            bitMapTake = false;
            Toast.makeText(LoginFingerWatsonActivity.this, "Debe capturar almenos una huella", Toast.LENGTH_SHORT).show();

        } else {

            bitMapTake = true;

        }

        return bitMapTake;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mode = "CAPTURE_ONE_FINGER_FLAT";
        Log.i(TAG, "[INFO] onResume: " + mode);
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        fingerDTO    = new FingerLoginDTO();
        idClient     = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_CLIENT, "");

        fingerDTO.setFingerIndex(base64FingerLogin);
        fingerDTO.setId         (idClient);
        fingerDTO.setContentType("image/wsq");

        new LoginFingerSend(LoginFingerWatsonActivity.this, token, fingerDTO).execute();
    }

    public String buildJSON() {
        fingerDTO = new FingerLoginDTO();

        idClient     = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_CLIENT, "");
        String idEnterprice = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_ENTERPRICE, "");
        String customerType = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.CUSTOMER_TYPE, "");

        Log.d ("--------",idClient);

        fingerDTO.setFingerIndex(base64FingerLogin);
        fingerDTO.setId         (idClient);
        fingerDTO.setContentType("image/wsq");

        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", idClient);
            jsonObject.put("emprId", idEnterprice);
            jsonObject.put("customerType", customerType);
            jsonObject.put("contentType", "image/wsq");
            jsonObject.put("ri", base64FingerLogin);

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

            try {
                jsonObject.put("ri", base64FingerLogin);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return jsonObject;
    }

    @Override
    public void goNext() {
        Intent i = new Intent(LoginFingerWatsonActivity.this, FormActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    protected void showToastOnUiThread(final String message, final int duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, duration);
                toast.show();
            }
        });
    }

    protected int getLayoutResID() {
        return R.layout.activity_login_finger_watson;
    }

    protected int menuLayoutResourceId() {
        return 0;
    }

    protected ImageView getImageViewPreview() {
        return imageViewPreview;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected String getSdkVersion() {
        try {
            IBScan.SdkVersion sdkVersion;
            sdkVersion = this.ibScan.getSdkVersion();
            return sdkVersion.file;
        } catch (IBScanException ibse) {
            return "";
        }
    }

    protected void openDevice() {

        switch (watsonMiniData.state) {
            case SCANNER_ATTACHED:
                /* Transition to initializing state. */
                transitionToInitializing(INITIALIZING_DEVICE_INDEX);
                break;

            case NO_SCANNER_ATTACHED:
                watsonMiniListener.onError(getString(mx.com.morpho.watson_mini.R.string.no_scanner_attached));
                break;

            default:
                String error = "Received unexpected open button event in state " + watsonMiniData.state.toString();
                Log.e(TAG, "[ERROR]: " + error);
                watsonMiniListener.onError(error);
                break;
        }
    }

    protected void closeDevice() {
        /* Sanity check.  Make sure we are in a proper state. */
        switch (watsonMiniData.state) {
            case INITIALIZED:
                /* Transition to closing state. */
                transitionToClosing();
                break;
            case NO_SCANNER_ATTACHED:
                watsonMiniListener.onError(getString(mx.com.morpho.watson_mini.R.string.no_scanner_attached));
                break;
            default:
                String error = "Received unexpected open button event in state " + watsonMiniData.state.toString();
                Log.e(TAG, "[ERROR]: " + error);
                watsonMiniListener.onError(error);
                break;
        }
    }

    protected void startCapture() {
        switch (watsonMiniData.state) {
            case INITIALIZED:
                //showToastOnUiThread("[INFO] INITIALIZED", Toast.LENGTH_LONG);
                Log.i(TAG, "[INFO] startCapture: Starting capture");
                /* Transition to capturing state. */
                transitionToStartingCapture();
                break;

            case SCANNER_ATTACHED:
                //showToastOnUiThread("[INFO] SCANNER_ATTACHED", Toast.LENGTH_LONG);
                String error = getString(mx.com.morpho.watson_mini.R.string.device_attached_but_not_initialized);
                Log.i(TAG, "[INFO] startCapture: " + error);
                watsonMiniListener.onError(error);
                break;

            case NO_SCANNER_ATTACHED:
                //showToastOnUiThread("[INFO] NO_SCANNER_ATTACHED", Toast.LENGTH_LONG);
                Log.i(TAG, "[INFO] NO_SCANNER_ATTACHED");
                watsonMiniListener.onError(getString(mx.com.morpho.watson_mini.R.string.no_scanner_attached));
                break;
            default:
                //showToastOnUiThread("[INFO] default", Toast.LENGTH_LONG);
                Log.e(TAG, "Received unexpected open button event in state " + watsonMiniData.state.toString());
                watsonMiniListener.onError(getString(mx.com.morpho.watson_mini.R.string.general_error));
                return;
        }
    }

    protected void stopCapture() {
        switch (watsonMiniData.state) {
            case CAPTURING:
                /* Transition to stopping capture state. */
                transitionToStoppingCapture();
                break;
            default:
                Log.e(TAG, "Received unexpected open button event in state " + watsonMiniData.state.toString());
                watsonMiniListener.onError(getString(mx.com.morpho.watson_mini.R.string.general_error));
                return;
        }
    }

    protected String getStatus() {
        return status;
    }

    protected void setWatsonMiniListener(WatsonMiniListener watsonMiniListener) {
        this.watsonMiniListener = watsonMiniListener;
    }

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

    protected void initArguments(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_ARGUMENTS)) {
            activityArguments = savedInstanceState.getBundle(EXTRA_ARGUMENTS);
        } else if (getIntent().getExtras() != null) {
            activityArguments = getIntent().getExtras().getBundle(EXTRA_ARGUMENTS);
        }
    }

    /**
     * Every object annotated with {@link ButterKnife} its gonna injected trough butterknife
     */
    protected void bindViews() {
        ButterKnife.bind(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// WatsonMiniListener ///////////////////////////////////////////
    @Override
    public void onStatusChanged(final String status) {
        /*
        Log.i(TAG, "[INFO] onStatusChanged: " + status);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastOnUiThread(status, Toast.LENGTH_LONG);
            }
        });
        */
    }

    @Override
    public void onError(String error) {
        showToastOnUiThread(error, Toast.LENGTH_LONG);
    }

    @Override
    public void onDeviceCounterChanged(int devices) {
        // showToastOnUiThread("Devices connected: " + devices, Toast.LENGTH_LONG);
    }

    @Override
    public void onDeviceInitialization(final int deviceIndex, final int progressValue) {
        Log.i(TAG, "[INFO] onDeviceInitialization: " + progressValue + "%");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressValue != 100){
                    //buttonOpenDevice.setEnabled(false);
                }else{
                    //buttonOpenDevice.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onDeviceStateChanged(boolean isOpen) {
        if(isOpen) {
            //buttonOpenDevice.setVisibility(View.GONE);
            //buttonCloseDevice.setVisibility(View.VISIBLE);
            startCapture();
        } else {
            //buttonOpenDevice.setVisibility(View.VISIBLE);
            //buttonCloseDevice.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNFIWScoreCalculated(int nfiqScore) {
        // showToastOnUiThread("NFIQ Score: " + nfiqScore, Toast.LENGTH_SHORT);
    }

    @Override
    public void onFrameTimeChanged(final String frameTime) {
        /* runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastOnUiThread(frameTime, Toast.LENGTH_LONG);
            }
        }); */
    }

    @Override
    public void onCaptureStarts() {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonStartCapture.setVisibility(View.GONE);
                buttonStopCapture.setVisibility(View.VISIBLE);
            }
        });*/
    }

    @Override
    public void onCaptureStop() {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //buttonStartCapture.setVisibility(View.VISIBLE);
                //buttonStopCapture.setVisibility(View.GONE);
            }
        });*/
    }

    @Override
    public void onCaptureFinishedSuccess(byte[] imageBuffer, final Bitmap imageBitmap, IBScanDevice.ImageFormat imageFormat) {

        byte [] bufferImage = null;

        String operationID = SharedPreferencesUtils.readFromPreferencesString(
                LoginFingerWatsonActivity.this, SharedPreferencesUtils.OPERATION_ID, "");

        File fileImage = new File(Environment.getExternalStorageDirectory()+"/"+folderName+"/"+WSQfileName+".wsq");

        try {

            bufferImage = getBytesFile(fileImage);

        } catch (IOException ex ) {

            ex.printStackTrace();

        }

        if (bufferImage != null) {

            fingerLogin = bufferImage;

            base64FingerLogin = com.teknei.bid.tools.Base64.encode(bufferImage);

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_" + operationID + ".jpg");

            if (f.exists()) {
                f.delete();
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger_"  + operationID + ".jpg");
            }

            try {
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bufferImage);

                fo.close();

                imageFileFinger = f;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] getBytesFile(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);;
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    @Override
    public void scanDeviceAttached(final int deviceId) {
        hasPermission = ibScan.hasPermission(deviceId);
        Log.d(TAG, "Estado " + hasPermission);

        /*
         * Check whether we have permission to access this device.  Request permission so it will
		 * appear as an IB scanner.
		 */
        if (!hasPermission) {
            ibScan.requestPermission(deviceId);
        }
    }

    @Override
    public void scanDeviceDetached(final int deviceId) {
        /*
         * A device has been detached.  We should also receive a scanDeviceCountChanged() callback,
		 * whereupon we can refresh the display.  If our device has detached while scanning, we
		 * should receive a deviceCommunicationBreak() callback as well.
		 */
        watsonMiniListener.onDeviceStateChanged(false);
    }

    @Override
    public void scanDevicePermissionGranted(final int deviceId, final boolean granted) {
        if (granted) {
            /*
             * This device should appear as an IB scanner.  We can wait for the scanDeviceCountChanged()
			 * callback to refresh the display.
			 */
            showToastOnUiThread("Permission granted to device " + deviceId, Toast.LENGTH_SHORT);
        } else {
            showToastOnUiThread("Permission denied to device " + deviceId, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void scanDeviceCountChanged(final int deviceCount) {
        final String verb = (deviceCount == 1) ? "is" : "are";
        final String plural = (deviceCount == 1) ? "" : "s";
        //showToastOnUiThread("There " + verb + " now " + deviceCount + " accessible device" + plural, Toast.LENGTH_SHORT);

		/*
         * The number of recognized accessible scanners has changed.  If there are not zero scanners
		 * and we were not already in the SCANNER_ATTACHED state, let's go there.
		 */
        transitionToRefresh();
    }

    @Override
    public void scanDeviceInitProgress(final int deviceIndex, final int progressValue) {
        setStatus(getString(mx.com.morpho.watson_mini.R.string.initialization) + " " + progressValue + "%");
        watsonMiniListener.onDeviceInitialization(deviceIndex, progressValue);
    }

    @Override
    public void scanDeviceOpenComplete(final int deviceIndex, final IBScanDevice device,
                                       final IBScanException exception) {
        if (device != null) {
            /*
             * The device has now finished initializing.  We can start capturing an image.
			 */
            //showToastOnUiThread("Device " + deviceIndex + " is now initialized", Toast.LENGTH_SHORT);
            transitionToInitialized(device);
        } else {
            /*
             * Initialization failed.  Let's report the error, clean up, and refresh.
			 */
            String error = (exception == null) ? "(unknown)" : exception.getType().toString();
            //showToastOnUiThread("Device " + deviceIndex + " could not be initialized with error " + error, Toast.LENGTH_SHORT);
            transitionToClosing();
        }
    }

    @Override
    public void deviceCommunicationBroken(IBScanDevice ibScanDevice) {
        /*
         * A communication break occurred with a scanner during capture.  Let's cleanup after the
		 * break and then refresh.
		 */
        //showToastOnUiThread("Communication break with device", Toast.LENGTH_SHORT);
        transitionToCommunicationBreak();
    }

    @Override
    public void deviceImagePreviewAvailable(IBScanDevice device, final IBScanDevice.ImageData image) {
        try {
            /*
             * Preserve aspect ratio of image while resizing.
			 */
            //			final String deviceName = SimpleScanActivity.this.m_ibScanDevice.getProperty(PropertyId.PRODUCT_ID);
            final String deviceName = watsonMiniData.deviceName;
            int dstWidth = getImageViewPreview().getWidth();
            int dstHeight = getImageViewPreview().getHeight();
            int dstHeightTemp = (dstWidth * image.height) / image.width;
            if (dstHeightTemp > dstHeight) {
                dstWidth = (dstHeight * image.width) / image.height;
            }else {
                dstHeight = dstHeightTemp;
            }

			/*
             * Get scaled image, perhaps with rolling lines displayed.
			 */
            //final Bitmap bitmapScaled;

            if (imageType.equals(IBScanDevice.ImageType.ROLL_SINGLE_FINGER)) {
                IBScanDevice.RollingData rollingData;
                try {
                    rollingData = ibScanDevice.getRollingInfo();
                } catch (IBScanException ibse) {
                    rollingData = null;
                    Log.e("Simple Scan", "failure getting rolling line " + ibse.getType().toString());
                }
                if (rollingData != null) {
                    int rollingLineWidth = 4;
                    if (deviceName.equals("KOJAK") || deviceName.equals("FIVE-0")) {
                        ibScanDevice.createBmpEx(image.buffer, m_BitmapKojakRollImage);
                        drawBitmapRollingLine(m_BitmapKojakRollImage, dstWidth, dstHeight, image.width, image.height, rollingData.rollingState, rollingData.rollingLineX, rollingLineWidth);
                    } else {
                        ibScanDevice.createBmpEx(image.buffer, m_BitmapImage);
                        drawBitmapRollingLine(m_BitmapImage, dstWidth, dstHeight, image.width, image.height, rollingData.rollingState, rollingData.rollingLineX, rollingLineWidth);
                    }
                } else {
                    if (deviceName.equals("KOJAK") || deviceName.equals("FIVE-0")) {
                        ibScanDevice.createBmpEx(image.buffer, m_BitmapKojakRollImage);
                    } else {
                        ibScanDevice.createBmpEx(image.buffer, m_BitmapImage);
                    }
                }
            } else {
                ibScanDevice.createBmpEx(image.buffer, m_BitmapImage);
            }

            if (m_BitmapImage != null || m_BitmapKojakRollImage != null) {
                /* Make sure this occurs on UI thread. */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setFrameTime(String.format("%1$.3f", image.frameTime));
                        if (imageType.equals(IBScanDevice.ImageType.ROLL_SINGLE_FINGER) && (deviceName.equals("KOJAK") || deviceName.equals("FIVE-0"))) {
                            watsonMiniData.imageBitmap = m_BitmapKojakRollImage;
                            getImageViewPreview().setImageBitmap(m_BitmapKojakRollImage);
                        } else {
                            watsonMiniData.imageBitmap = m_BitmapImage;
                            getImageViewPreview().setImageBitmap(m_BitmapImage);
                        }
                        // watsonMiniListener.onBitmapLivePreviewRetrieved(watsonMiniData.imageBitmap);
                    }
                });
            }
        } catch (IllegalArgumentException ae) {
            Log.e("Simple Scan", "failure gettin Exception line ");
        } catch (IBScanException e) {
            Log.e("Simple Scan", e.getMessage());
        }
    }

    @Override
    public void deviceFingerCountChanged(IBScanDevice ibScanDevice, IBScanDevice.FingerCountState fingerCountState) {

    }

    @Override
    public void deviceFingerQualityChanged(IBScanDevice ibScanDevice, final IBScanDevice.FingerQualityState[] fingerQualities) {
        /* Make sure this occurs on the UI thread. */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                watsonMiniData.fingerMarkerTop = false;
                watsonMiniData.fingerMarkerLeft = false;
                watsonMiniData.fingerMarkerRight = false;

				/* Determine colors for each finger in finger qualities array. */
                for (int i = 0; i < fingerQualities.length; i++) {
                    int color;

                    switch (fingerQualities[i]) {
                        case INVALID_AREA_TOP:
                            watsonMiniData.fingerMarkerTop = true;
                            color = FINGER_QUALITY_POOR_COLOR;
                            break;
                        case INVALID_AREA_LEFT:
                            watsonMiniData.fingerMarkerLeft = true;
                            color = FINGER_QUALITY_POOR_COLOR;
                            break;
                        case INVALID_AREA_RIGHT:
                            watsonMiniData.fingerMarkerRight = true;
                            color = FINGER_QUALITY_POOR_COLOR;
                            break;

                        default:
                        case FINGER_NOT_PRESENT:
                            color = FINGER_QUALITY_NOT_PRESENT_COLOR;
                            break;
                        case GOOD:
                            color = FINGER_QUALITY_GOOD_COLOR;
                            break;
                        case FAIR:
                            color = FINGER_QUALITY_FAIR_COLOR;
                            break;
                        case POOR:
                            color = FINGER_QUALITY_POOR_COLOR;
                            break;
                    }
                    /* Sanity check.  Make sure marker for this finger exists. */
                    if (i < txtFingerQuality.length) {
                        watsonMiniData.fingerQualityColors[i] = color;
                        // TODO Check this: txtFingerQuality[i].setBackgroundColor(color);
                    }
                }
                /* If marker exists for more fingers, color then "not present". */
                for (int i = fingerQualities.length; i < txtFingerQuality.length; i++) {
                    watsonMiniData.fingerQualityColors[i] = FINGER_QUALITY_NOT_PRESENT_COLOR;
                    // TODO Check this: txtFingerQuality[i].setBackgroundColor(FINGER_QUALITY_NOT_PRESENT_COLOR);
                }
            }
        });
    }

    @Override
    public void deviceAcquisitionBegun(IBScanDevice ibScanDevice, IBScanDevice.ImageType imageType) {
        if (imageType.equals(IBScanDevice.ImageType.ROLL_SINGLE_FINGER)) {
            showToastOnUiThread("Beginning acquisition...roll finger left", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void deviceAcquisitionCompleted(IBScanDevice ibScanDevice, IBScanDevice.ImageType imageType) {
        if (imageType.equals(IBScanDevice.ImageType.ROLL_SINGLE_FINGER)) {
            showToastOnUiThread("Completed acquisition...roll finger right", Toast.LENGTH_SHORT);
        } else {
            playSound.playSound();
        }
    }

    @Override
    public void deviceImageResultAvailable(IBScanDevice ibScanDevice, IBScanDevice.ImageData imageData, IBScanDevice.ImageType imageType, IBScanDevice.ImageData[] imageDatas) {

    }

    @Override
    public void deviceImageResultExtendedAvailable(IBScanDevice device, IBScanException imageStatus,
                                                   final IBScanDevice.ImageData image,
                                                   final IBScanDevice.ImageType imageType,
                                                   final int detectedFingerCount,
                                                   final IBScanDevice.ImageData[] segmentImageArray,
                                                   final IBScanDevice.SegmentPosition[] segmentPositionArray) {
        /*
         * Preserve aspect ratio of image while resizing.
		 */
        final String deviceName = watsonMiniData.deviceName;
        int dstWidth = getImageViewPreview().getWidth();
        int dstHeight = getImageViewPreview().getHeight();
        int dstHeightTemp = (dstWidth * image.height) / image.width;
        if (dstHeightTemp > dstHeight) {
            dstWidth = (dstHeight * image.width) / image.height;
        } else {
            dstHeight = dstHeightTemp;
        }

        saveWSQImage(image);

		/*
         * Display image result.
		 */
        try {
            if (imageType.equals(IBScanDevice.ImageType.ROLL_SINGLE_FINGER)) {
                if (deviceName.equals("KOJAK") || deviceName.equals("FIVE-0")) {
                    ibScanDevice.createBmpEx(image.buffer, m_BitmapKojakRollImage);
                } else {
                    ibScanDevice.createBmpEx(image.buffer, m_BitmapImage);
                }
            } else {
                ibScanDevice.createBmpEx(image.buffer, m_BitmapImage);
            }

            if (m_BitmapImage != null || m_BitmapKojakRollImage != null) {
            /* Make sure this occurs on UI thread. */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imageType.equals(IBScanDevice.ImageType.ROLL_SINGLE_FINGER) && (deviceName.equals("KOJAK") || deviceName.equals("FIVE-0"))) {
                            playSound.playSound();
                            watsonMiniData.imageBitmap = m_BitmapKojakRollImage;
                            getImageViewPreview().setImageBitmap(m_BitmapKojakRollImage);
                        } else {
                            watsonMiniData.imageBitmap = m_BitmapImage;
                            getImageViewPreview().setImageBitmap(m_BitmapImage);
                        }
                        setFrameTime(String.format("%1$.3f", image.frameTime));
                    }
                });
            }
        } catch (IllegalArgumentException ae) {
            Log.e("Simple Scan", "failure gettin Exception line ");
        } catch (IBScanException e) {
            Log.e("Simple Scan", e.getMessage());
        }

		/*
         * Finish out the image acquisition and retain result so that the user can view a larger
		 * version of the image later.
		 */
        if (imageStatus != null) {
            /*
             * If an image status is returned, then there was an error during image acquisition.
			 */
            showToastOnUiThread("Image capture ended with error: " + imageStatus.getType().toString(), Toast.LENGTH_SHORT);
        } else {
            showToastOnUiThread("Image result available", Toast.LENGTH_SHORT);

            watsonMiniListener.onCaptureFinishedSuccess(image.buffer, image.toBitmap(), image.format);
            watsonMiniListener.onCaptureStop();
        }
        transitionToImageCaptured(image, imageType, segmentImageArray);
    }

    @Override
    public void devicePlatenStateChanged(IBScanDevice ibScanDevice, IBScanDevice.PlatenState platenState) {
        // TODO: REPORT EVENT
    }

    @Override
    public void deviceWarningReceived(IBScanDevice ibScanDevice, IBScanException warning) {
        showToastOnUiThread("Warning received " + warning.getType().toString(), Toast.LENGTH_SHORT);
    }

    @Override
    public void devicePressedKeyButtons(IBScanDevice ibScanDevice, int i) {
        showToastOnUiThread("PressedKeyButtons ", Toast.LENGTH_SHORT);
    }

    private Handler scannerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message message) {
            final AppState nextState = AppState.values()[message.what];
            switch (nextState) {
                case REFRESH:
                    handleTransitionToRefresh();
                    break;
                case NO_SCANNER_ATTACHED:
                    handleTransitionToNoScannerAttached();
                    break;
                case SCANNER_ATTACHED:
                    String deviceDescription = (String) message.obj;
                    int deviceCounter = message.arg1;
                    handleTransitionToScannerAttached(deviceDescription, deviceCounter);
                    break;
                case INITIALIZING:
                    final int deviceIndex = message.arg1;
                    handleTransitionToInitializing(deviceIndex);
                    break;
                case INITIALIZED:
                    IBScanDevice scanDevice = (IBScanDevice) message.obj;
                    handleTransitionToInitialized(scanDevice);
                    break;
                case COMMUNICATION_BREAK:
                    handleTransitionToCommunicationBreak();
                    break;
                case CLOSING:
                    handleTransitionToClosing();
                    break;
                case STARTING_CAPTURE:
                    handleTransitionToStartingCapture();
                    break;
                case CAPTURING:
                    handleTransitionToCapturing();
                    break;
                case STOPPING_CAPTURE:
                    handleTransitionToStoppingCapture();
                    break;
                case IMAGE_CAPTURED:
                    final Object[] objects = (Object[]) message.obj;
                    final IBScanDevice.ImageData imageData = (IBScanDevice.ImageData) objects[0];
                    final IBScanDevice.ImageType imageType = (IBScanDevice.ImageType) objects[1];
                    final IBScanDevice.ImageData[] splitImageArray = (IBScanDevice.ImageData[]) objects[2];
                    handleTransitionToImageCaptured(imageData, imageType, splitImageArray);
                    break;
            }
            return false;
        }
    });

    /**
     * Set current frame time
     *
     * @param frameTime Frame time string
     */
    private void setFrameTime(String frameTime){
        watsonMiniListener.onFrameTimeChanged(frameTime);
    }

    /**
     * Set current status in status variable
     *
     * @param status Status Message
     */
    private void setStatus(final String status) {
        this.status = status;
        watsonMiniListener.onStatusChanged(status);
        Log.i(TAG, "[INFO] setStatus: " + status);
    }

    /**
     * Used to set device conter
     *
     * @param deviceCounter Value for device counter connected
     */
    private void setDeviceCounter(final int deviceCounter) {
        watsonMiniListener.onDeviceCounterChanged(deviceCounter);
        Log.i(TAG, "[INFO] setDeviceCounter: Device(s) connected: " + deviceCounter);
    }

    /**
     * Calulates NFIQ Image score
     *
     * @param image Image to calculate NFIQ score
     */
    private void calculateNFIQ(IBScanDevice.ImageData image) {
        try {
            int nfiqScore = ibScanDevice.calculateNfiqScore(image);
            //showToastOnUiThread("NFIQ score for print is " + nfiqScore, Toast.LENGTH_SHORT);
            watsonMiniListener.onNFIWScoreCalculated(nfiqScore);
        } catch (IBScanException ibse) {
            showToastOnUiThread("Error calculating NFIQ score " + ibse.getType().toString(), Toast.LENGTH_SHORT);
        }
    }

    /**
     * Save WSQ Image on Device
     *
     * @param image
     */
    private void saveWSQImage(IBScanDevice.ImageData image) {
        try {
            String filename_wsq = Environment.getExternalStorageDirectory().getPath() + "/" + folderName + "/" + WSQfileName + ".wsq";
            int nRc = ibScanDevice.wsqEncodeToFile(filename_wsq, image.buffer, image.width,
                    image.height, image.pitch, image.bitsPerPixel, 500, 0.75, "");
            //showToastOnUiThread("Saved WSQ image", Toast.LENGTH_SHORT);
        } catch (IBScanException ibse) {
            showToastOnUiThread("Error save WSQ " + ibse.getType().toString(), Toast.LENGTH_SHORT);
        }
    }

    //region States Machine

    /**
     * Neutral state (Initial State)
     */
    private void transitionToRefresh() {
        final Message msg = this.scannerHandler.obtainMessage(AppState.REFRESH.ordinal());
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State when scanner is not attached
     */
    private void transitionToNoScannerAttached() {
        final Message msg = this.scannerHandler.obtainMessage(AppState.NO_SCANNER_ATTACHED.ordinal());
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State when device is attached
     *
     * @param deviceDesc  A Device Description
     * @param deviceCount A Device Counter
     */
    private void transitionToScannerAttached(final String deviceDesc, final int deviceCount) {
        final Message msg = this.scannerHandler.obtainMessage(SCANNER_ATTACHED.ordinal(), deviceCount, 0, deviceDesc);
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State for device initialization
     *
     * @param deviceIndex
     */
    private void transitionToInitializing(final int deviceIndex) {
        final Message msg = this.scannerHandler.obtainMessage(AppState.INITIALIZING.ordinal(), deviceIndex, 0);
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State when device is initialized
     *
     * @param device The {@link IBScanDevice} Device
     */
    private void transitionToInitialized(final IBScanDevice device) {
        final Message msg = this.scannerHandler.obtainMessage(INITIALIZED.ordinal(), device);
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State when communication breaks
     */
    private void transitionToCommunicationBreak() {
        final Message msg = this.scannerHandler.obtainMessage(AppState.COMMUNICATION_BREAK.ordinal());
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State when initialization failed
     */
    private void transitionToClosing() {
        final Message msg = scannerHandler.obtainMessage(AppState.CLOSING.ordinal());
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State for starting capture
     */
    private void transitionToStartingCapture() {
        final Message msg = this.scannerHandler.obtainMessage(AppState.STARTING_CAPTURE.ordinal());
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State for capturing image
     */
    private void transitionToCapturing() {
        final Message msg = this.scannerHandler.obtainMessage(AppState.CAPTURING.ordinal());
        this.scannerHandler.sendMessage(msg);
    }

    /**
     * State for stopping capture
     */
    private void transitionToStoppingCapture() {
        final Message msg = this.scannerHandler.obtainMessage(AppState.STOPPING_CAPTURE.ordinal());
        this.scannerHandler.sendMessage(msg);
    }

    private void transitionToStoppingCaptureWithDelay(final int delayMillis) {
        final Message msg = this.scannerHandler.obtainMessage(AppState.STOPPING_CAPTURE.ordinal());
        this.scannerHandler.sendMessageDelayed(msg, delayMillis);
    }

    /**
     * This state is called when image capture was successful
     *
     * @param image           Image captured
     * @param imageType       Image Type
     * @param splitImageArray
     */
    private void transitionToImageCaptured(final IBScanDevice.ImageData image, final IBScanDevice.ImageType imageType,
                                           final IBScanDevice.ImageData[] splitImageArray) {
        final Message msg = this.scannerHandler.obtainMessage(AppState.IMAGE_CAPTURED.ordinal(), 0, 0,
                new Object[]{image, imageType, splitImageArray});
        this.scannerHandler.sendMessage(msg);
    }

    private void handleTransitionToRefresh() {
        /* Sanity check state. */
        switch (this.watsonMiniData.state) {
            case NO_SCANNER_ATTACHED:
            case SCANNER_ATTACHED:
            case CLOSING:
                break;
            case INITIALIZED:
                /*
                 * If the initialized device has been disconnected, transition to closing, then
				 * refresh.
				 */
                if (this.ibScanDevice != null) {
                    try {
                        /* Just a test call. */
                        this.ibScanDevice.isCaptureActive();
                    } catch (IBScanException ibse) {
                        transitionToClosing();
                    }
                }
                return;
            case INITIALIZING:
            case STARTING_CAPTURE:
            case CAPTURING:
            case STOPPING_CAPTURE:
            case IMAGE_CAPTURED:
            case COMMUNICATION_BREAK:
                /*
                 * These transitions is ignored to preserve UI state.  The CLOSING state will transition to
				 * REFRESH.
				 */
                return;
            case REFRESH:
                /*
                 * This transition can occur when multiple events (button presses, device count changed
				 * callbacks occur).  We assume the last execution will transition to the correct state.
				 */
                return;
            default:
                String error = "Received unexpected transition to REFRESH from " + this.watsonMiniData.state.toString();
                watsonMiniListener.onError(error);
                Log.e(TAG, error);
                return;
        }

        // Move to this state
        watsonMiniData.state = AppState.REFRESH;
        setStatus(getString(mx.com.morpho.watson_mini.R.string.refreshing));


        /*
         * Make sure there are no USB devices attached that are IB scanners for which permission has
		 * not been granted.  For any that are found, request permission; we should receive a
		 * callback when permission is granted or denied and then when IBScan recognizes that new
		 * devices are connected, which will result in another refresh.
		 */
        final UsbManager manager = (UsbManager) this.getApplicationContext().getSystemService(Context.USB_SERVICE);
        final HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        final Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            final UsbDevice device = deviceIterator.next();
            final boolean isScanDevice = IBScan.isScanDevice(device);
            if (isScanDevice) {
                final boolean hasPermission = manager.hasPermission(device);
                if (!hasPermission) {
                    this.ibScan.requestPermission(device.getDeviceId());
                }
            }
        }

        /*
         * Determine the next state according to device count.  A state transition always occurs,
		 * either to NO_SCANNER_ATTACHED or SCANNER_ATTACHED.
		 */
        try {
            final int deviceCount = this.ibScan.getDeviceCount();
            if (deviceCount > 0) {
                try {
                    final IBScan.DeviceDesc deviceDesc = this.ibScan.getDeviceDescription(INITIALIZING_DEVICE_INDEX);
                    transitionToScannerAttached(deviceDesc.productName + " - " + deviceDesc.serialNumber, deviceCount);
                } catch (IBScanException ibse) {
                    Log.e(TAG, "Received exception getting device description " + ibse.getType().toString());
                    transitionToNoScannerAttached();
                }
            } else {
                transitionToNoScannerAttached();
            }
        } catch (IBScanException ibse) {
            Log.e(TAG, "Received exception getting device count " + ibse.getType().toString());
            transitionToNoScannerAttached();
        }
    }

    /**
     * Handler when Scanner is not attached
     */
    private void handleTransitionToNoScannerAttached() {
        /* Sanity check state. */
        switch (watsonMiniData.state) {
            case REFRESH:
                break;
            default:
                Log.e(TAG, "Received unexpected transition to NO_SCANNER_ATTACHED from " + watsonMiniData.state.toString());
                return;
        }
        /* Move to this state. */
        watsonMiniData.state = AppState.NO_SCANNER_ATTACHED;
        setStatus(getString(mx.com.morpho.watson_mini.R.string.no_scanners_detected));
    }

    /**
     * Handler when device is attached
     *
     * @param deviceDescription A string with device description
     * @param deviceCounter     A device counter
     */
    private void handleTransitionToScannerAttached(final String deviceDescription,
                                                   final int deviceCounter) {

        /* Sanity check state. */
        switch (this.watsonMiniData.state) {
            case REFRESH:
                break;
            default:
                Log.e(TAG, "Received unexpected transition to SCANNER_ATTACHED from " + this.watsonMiniData.state.toString());
                return;
        }

        /* Move to this state. */
        this.watsonMiniData.state = SCANNER_ATTACHED;

        setStatus(getString(mx.com.morpho.watson_mini.R.string.device_is_attached) + " " + deviceDescription);
        setDeviceCounter(deviceCounter);
    }

    private void handleTransitionToInitializing(final int deviceIndex) {
        /* Sanity check state. */
        if (!watsonMiniData.state.equals(SCANNER_ATTACHED)) {
            watsonMiniListener.onError(getString(mx.com.morpho.watson_mini.R.string.no_scanner_attached));
            return;
        }

        /* Move to this state. */
        this.watsonMiniData.state = AppState.INITIALIZING;

        setStatus(getString(mx.com.morpho.watson_mini.R.string.initializing));

        /* Start device initialization. */
        try {
            /* While the device is being opened, callbacks for initialization progress will be
             * received.  When the device is open, another callback will be received and capture can
			 * begin.  We will stay in this state until capture begins.
			 */
            this.ibScan.openDeviceAsync(deviceIndex);
        } catch (IBScanException ibse) {
            /* Device initialization failed.  Go to closing. */
            showToastOnUiThread("Could not initialize device with exception " + ibse.getType().toString(), Toast.LENGTH_SHORT);
            transitionToClosing();
        }
    }

    private void handleTransitionToInitialized(final IBScanDevice device) {
        /* Sanity check state. */
        switch (this.watsonMiniData.state) {
            case INITIALIZING:
            case STARTING_CAPTURE:
            case STOPPING_CAPTURE:
            case IMAGE_CAPTURED:
                break;
            default:
                String error = "Received unexpected transition to INITIALIZED from " + this.watsonMiniData.state.toString();
                Log.e(TAG, error);
                watsonMiniListener.onError(error);
                return;
        }

        /* Move to this state. */
        this.watsonMiniData.state = INITIALIZED;
        if (watsonMiniData.devicekojak) {
            try {
                watsonMiniData.OnlyRIGHTFOUR = 0;
                watsonMiniData.OnlyLEFTFOUR = 0;
                ibScanDevice.setLEDs(IBScanDevice.LED_NONE);
            } catch (IBScanException e) {
                e.printStackTrace();
            }
        }

        /* If the device is null, we have already passed through this state. */
        if (device != null) {
            /* Enable power save mode. */
            try {
                //setProperty
                device.setProperty(IBScanDevice.PropertyId.ENABLE_POWER_SAVE_MODE, "TRUE");

                this.watsonMiniData.deviceName = device.getProperty(IBScanDevice.PropertyId.PRODUCT_ID);
                final String deviceName = this.watsonMiniData.deviceName;

                //getProperty device Width,Height
                String imageW = device.getProperty(IBScanDevice.PropertyId.IMAGE_WIDTH);
                String imageH = device.getProperty(IBScanDevice.PropertyId.IMAGE_HEIGHT);
                int imageWidth = Integer.parseInt(imageW);
                int imageHeight = Integer.parseInt(imageH);
                this.m_BitmapImage = WatsonMiniHelper.toDrawBitmap(imageWidth, imageHeight);

                if (deviceName.equals("KOJAK") || deviceName.equals("FIVE-0")) {
                    //Kojak_Five-0
                    String kojakRollimageW = device.getProperty(IBScanDevice.PropertyId.ROLLED_IMAGE_WIDTH);
                    String kojakRollimageH = device.getProperty(IBScanDevice.PropertyId.ROLLED_IMAGE_HEIGHT);
                    int imageKojakRollWidth = Integer.parseInt(kojakRollimageW);
                    int imageKojakRollHeight = Integer.parseInt(kojakRollimageH);
                    //draw bitmap.
                    this.m_BitmapKojakRollImage = WatsonMiniHelper.toDrawBitmap(imageKojakRollWidth, imageKojakRollHeight);
                }
            } catch (IBScanException ibse) {
                /*
                 * We could not enable power save mode. This is was non-essential, so we continue on and
				 * see whether we can start capture.
				 */
                Log.e(TAG, "Could not begin enable power save mode " + ibse.getType().toString());
            }

			/* Get list of acceptable capture types. */
            Vector<String> typeVector = new Vector<String>();
            for (IBScanDevice.ImageType imageType : IBScanDevice.ImageType.values()) {
                try {
                    boolean available = device.isCaptureAvailable(imageType, IBScanDevice.ImageResolution.RESOLUTION_500);
                    if (available) {
                        typeVector.add(imageType.toDescription());
                        watsonMiniData.devicekojak = false;
                    }

                    final String deviceName = this.watsonMiniData.deviceName;
                    if (typeVector.size() > 4 && deviceName.equals("KOJAK")) {
                        typeVector.remove(3);
                        typeVector.add("Left Four-finger flat fingerprint");
                        typeVector.add("Right Four-finger flat fingerprint");
                        watsonMiniData.devicekojak = true;
                    }
                } catch (IBScanException ibse) {
                    Log.e(TAG, "Could not check capture availability " + ibse.getType().toString());
                }
            }
            String[] typeArray = new String[0];
            typeArray = typeVector.toArray(typeArray);
            if (typeVector.size() > 1) {
                // setCaptureTypes(typeArray, 1);
            } else {
                // setCaptureTypes(typeArray, 0);
            }

			/* Save device. */
            this.ibScanDevice = device;

            // TODO Handle device is opened or closed
            deviceIsOpen = true;
            watsonMiniListener.onDeviceStateChanged(deviceIsOpen);
        }
    }


    /*
     * Handle transition to communication break.
	 */
    private void handleTransitionToCommunicationBreak() {
        /* Sanity check state. */
        switch (this.watsonMiniData.state) {
            case CAPTURING:
            case STOPPING_CAPTURE:
            case INITIALIZED:
                break;
            default:
                Log.e(TAG, "Received unexpected transition to COMMUNICATION_BREAK from " +
                        this.watsonMiniData.state.toString());
                return;
        }

		/* Move to this state. */
        this.watsonMiniData.state = AppState.COMMUNICATION_BREAK;

		/* Transition to closing, then to refresh. */
        transitionToClosing();
    }

    private void handleTransitionToClosing() {
        /* Sanity check state. */
        switch (this.watsonMiniData.state) {
            case INITIALIZING:
            case INITIALIZED:
            case COMMUNICATION_BREAK:
                break;
            default:
                String error = "Received unexpected transition to CLOSING from " + this.watsonMiniData.state.toString();
                Log.e(TAG, error);
                watsonMiniListener.onError(error);
                return;
        }

		/* Move to this state. */
        this.watsonMiniData.state = AppState.CLOSING;

        /* Close & null device. */
        if (this.ibScanDevice != null) {
            try {
                this.ibScanDevice.close();
                // TODO Handle device is opened or closed
                deviceIsOpen = false;
                watsonMiniListener.onDeviceStateChanged(deviceIsOpen);
            } catch (IBScanException ibse) {
                String error = "Could not close device " + ibse.getType().toString();
                Log.e(TAG, "[ERROR]" + error);
                watsonMiniListener.onError(error);
                showToastOnUiThread(error, Toast.LENGTH_LONG);
            }
            this.ibScanDevice = null;
        }

		/*
         * Refresh the list of devices.
		 */
        transitionToRefresh();
    }

    /**
     * Handle transition to starting capture state.
     */
    private void handleTransitionToStartingCapture() {
		/* Sanity check state. */
        switch (watsonMiniData.state) {
            case INITIALIZED:
                break;
            default:
                Log.e(TAG, "Received unexpected transition to STARTING_CAPTURE from " + watsonMiniData.state.toString());
                return;
        }

		/* Move to this state. */
        watsonMiniData.state = AppState.STARTING_CAPTURE;

        try {
            IBScanDevice.ImageType imageType = getCaptureMode();

            //Bitmap reSize
			/*
			 * Begin capturing an image.  While the image is being captured, we will receive
			 * preview images through callbacks.  At the end of the capture, we will recieve a
			 * final image.
			 */
            if (watsonMiniData.devicekojak) {
                if (imageType == IBScanDevice.ImageType.ROLL_SINGLE_FINGER) {
                    ibScanDevice.setLEDs(IBScanDevice.IBSU_LED_F_PROGRESS_ROLL);
                }
            }

            /*if (watsonMiniData.OnlyLEFTFOUR == 1) {
                //OnlyLEFTFOUR =1;
                //0:Green , 1:Red ,2:Yellow
                //1:LEFT ,2: RIGHT


                this.PlayLed(1, false, 1);
                ibScanDevice.setLEDs(setLeds);
                imageType = IBScanDevice.ImageType.FLAT_FOUR_FINGERS;
            }
            if (watsonMiniData.OnlyRIGHTFOUR == 1) {
                //OnlyRIGHTFOUR =1;
                //0:Green , 1:Red ,2:Yellow
                //1:LEFT ,2: RIGHT


                this.PlayLed(1, false, 2);
                ibScanDevice.setLEDs(setLeds);
                imageType = IBScanDevice.ImageType.FLAT_FOUR_FINGERS;
            }*/

            ibScanDevice.beginCaptureImage(imageType, IBScanDevice.ImageResolution.RESOLUTION_500,
                    IBScanDevice.OPTION_AUTO_CAPTURE | IBScanDevice.OPTION_AUTO_CONTRAST);

			/* Save this device and image type for later use. */
            this.imageType = imageType;
            transitionToCapturing();
        } catch (IBScanException ibse) {
			/* We could not begin capturing.  Go to back to initialized. */
            showToastOnUiThread("Could not begin capturing with error " + ibse.getType().toString(), Toast.LENGTH_SHORT);
            transitionToInitialized(null);
        }
    }

    /*
     * Handle transition to capturing state.
     */
    private void handleTransitionToCapturing() {
		/* Sanity check state. */
        switch (watsonMiniData.state) {
            case STARTING_CAPTURE:
                break;
            default:
                Log.e(TAG, "Received unexpected transition to CAPTURING from " + watsonMiniData.state.toString());
                return;
        }

		/* Move to this state. */
        watsonMiniData.state = AppState.CAPTURING;

		/*
		 * We will start receiving callbacks for preview images and finger count and quality
		 * changes.
		 */
        ibScanDevice.setScanDeviceListener(this);
        // showToastOnUiThread("Now capturing...put a finger on the sensor", Toast.LENGTH_SHORT);
        watsonMiniListener.onCaptureStarts();

		/*
		 * We stay in this state until a good-quality image with the correct number of fingers is
		 * obtained, an error occurs (such as a communication break), or the user presses the "Stop"
		 * button.
		 */
    }

    /*
     * Handle transition to stopping capture state.
     */
    private void handleTransitionToStoppingCapture() {
		/* Sanity check state. */
        switch (watsonMiniData.state) {
            case CAPTURING:
            case STOPPING_CAPTURE:
                break;
            default:
                Log.e(TAG, "Received unexpected transition to STOPPING_CAPTURE from " + watsonMiniData.state.toString());
                return;
        }

		/* Move to this state. */
        watsonMiniData.state = AppState.STOPPING_CAPTURE;

		/* Cancel capture if necessary. */
        boolean done = false;
        try {
            final boolean active = ibScanDevice.isCaptureActive();

            if (!active) {
				/* Capture has already stopped.  Let's transition to the refresh state. */
                showToastOnUiThread("Capture stopped", Toast.LENGTH_SHORT);
                done = true;
            } else {
                try {
					/* Cancel capturing the image. */
                    this.ibScanDevice.cancelCaptureImage();
                    watsonMiniListener.onCaptureStop();
                } catch (IBScanException ibse) {
                    showToastOnUiThread("Could not cancel capturing with error " + ibse.getType().toString(), Toast.LENGTH_SHORT);
                    done = true;
                }
            }
        } catch (IBScanException ibse) {
			/* An error occurred.  Let's try to refresh. */
            showToastOnUiThread("Could not query capture active state " + ibse.getType().toString(), Toast.LENGTH_SHORT);
            done = true;
        }

		/*
		 * On error or capture not active, transition to initialized.
		 */
        if (done) {
            transitionToInitialized(null);
        }
		/*
		 *  We must wait for this to complete, so we will resubmit this transition with a delay.
		 */
        else {
            transitionToStoppingCaptureWithDelay(STOPPING_CAPTURE_DELAY_MILLIS);
        }
    }

    /*
	 * Handle transition to image captured state.
	 */
    private void handleTransitionToImageCaptured(final IBScanDevice.ImageData image,
                                                 final IBScanDevice.ImageType imageType,
                                                 final IBScanDevice.ImageData[] splitImageArray) {
		/* Sanity check state. */
        switch (watsonMiniData.state) {
            case CAPTURING:
                break;
            default:
                showToastOnUiThread("Received unexpected transition to STOPPING_CAPTURE from " +
                        watsonMiniData.state.toString(), Toast.LENGTH_SHORT);
                return;
        }

		/* Move to this state. */
        watsonMiniData.state = AppState.IMAGE_CAPTURED;

		/* Setup UI for state. */
        setStatus(getString(mx.com.morpho.watson_mini.R.string.captured_successfully));

		/*
		 * Save information in case we later show the enlarged image and allow long clicks on the
		 * image view to show that view.
		 */
        this.lastImage = image;
        this.watsonMiniData.imagePreviewImageClickable = true;

		/* Calculate NFIQ score on background thread. */
        Thread t = new Thread() {
            @Override
            public void run() {
                calculateNFIQ(image);
                //saveWSQImage(image);
            }
        };
        t.start();

		/* Move back to initialized state. */
        transitionToInitialized(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
