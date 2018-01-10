package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.morpho.common.data.LicenseService;
import com.morpho.common.data.model.WaitTimeout;
import com.morpho.contactless.interactor.FingersInteractor;
import com.morpho.contactless.presenter.FingersPresenter;
import com.morpho.license.interactor.LicenseInteractor;
import com.morpho.license.presenter.LicensePresenter;
import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsException;
import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsInvalidLicenseException;
import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsNoLicenseAvailableException;
import com.morpho.lkms.android.sdk.lkms_core.license.ILkmsLicense;
import com.morpho.mph_bio_sdk.android.sdk.BioSdk;
import com.morpho.mph_bio_sdk.android.sdk.common.LogLevel;
import com.morpho.mph_bio_sdk.android.sdk.licence.ILicenseManager;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.BioMatcherSettings;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBioMatcherHandler;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBioMatcherSettings;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBiometricInfo;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.async.BioMatcherAsyncCallbacks;
import com.morpho.mph_bio_sdk.android.sdk.msc.IBioCaptureHandler;
import com.morpho.mph_bio_sdk.android.sdk.msc.async.MscAsyncCallbacks;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.BioCaptureInfo;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.BioCaptureMode;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.BiometricModality;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.Camera;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.CaptureError;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.CaptureOptions;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.ICaptureOptions;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.Overlay;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.Torch;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.results.IImage;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.results.MorphoBioTraking;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.results.MorphoImage;
import com.morpho.mph_bio_sdk.android.sdk.msc.error.BioCaptureHandlerError;
import com.morpho.mph_bio_sdk.android.sdk.msc.error.exceptions.MSCException;
import com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureFeedbackListener;
import com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureResultListener;
import com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureTrackingListener;
import com.morpho.mph_bio_sdk.android.sdk.utils.image.ImageUtils;
import com.teknei.bid.R;
import com.teknei.bid.asynctask.FingersSend;
import com.teknei.bid.dialogs.AlertDialog;
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
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import morpho.urt.msc.mscengine.MorphoSurfaceView;

public class FingerBioSdkActivity extends BaseActivity implements FingersPresenter.View, LicensePresenter.Ui,
        BioCaptureResultListener, BioCaptureFeedbackListener, View.OnClickListener {

    // Local Variables
    protected MorphoSurfaceView cameraPreview;
    protected IBioCaptureHandler captureHandler;
    protected IBioMatcherHandler matcherHandler;
    private CountDownTimer timer = null;

    private static final String TAG = "FingerBioSdkActivity";
    private LicensePresenter licensePresenter;
    private FingersPresenter fingersPresenter;

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
    //Uso para MSOShower
    private List<File> fingersFileArray = null;

    File fileJson;
    List<File> fileList;

    RelativeLayout takeFingerLinearLayout;
    ImageView      takeImage;
    TextView       takeSideText;
    Switch         sdkBioSideHand;
    Button         sdkBioContinue;
    boolean        isTakeLeft;
    boolean        isTakeRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_bio_sdk);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.bio_sdk_activity_name));
            invalidateOptionsMenu();
        }

        isTakeRight = false;

        isTakeLeft  = false;

        fingersFileArray = new ArrayList<File>();
        fileList         = new ArrayList<File>();

        takeFingerLinearLayout = (RelativeLayout) findViewById(R.id.ln_take_finger);

        takeSideText           = (TextView) findViewById(R.id.tv_hand_bio_sdk);

        takeImage              = (ImageView) findViewById(R.id.i_take_image_bio_sdk);

        sdkBioContinue         = (Button)    findViewById(R.id.b_continue_bio_sdk);

        sdkBioSideHand         = (Switch)    findViewById(R.id.sw_id_hand_side);

        sdkBioContinue.setOnClickListener(FingerBioSdkActivity.this);
        takeImage.setOnClickListener(FingerBioSdkActivity.this);

        sdkBioSideHand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sdkBioSideHand.isChecked()){
                    takeSideText.setText(R.string.bio_sdk_message_left);
                } else {
                    takeSideText.setText(R.string.bio_sdk_message_right);
                }
            }
        });

        licensePresenter = new LicensePresenter(new LicenseInteractor( new LicenseService(this,
                "http://201.99.117.119:8081/ServiceProviderLicense/LicenseRequest",
                "https://service-intg.dictao.com/lkms-server-app")));
        licensePresenter.setUi(FingerBioSdkActivity.this);

        checkLicense();

        bindViews();

        onPreparePresenter();

        onPrepareActivity();
    }

    private void checkLicense() {
        ILicenseManager licenseManager= BioSdk.createLicenseManager(getApplicationContext());
        try {
            ILkmsLicense license = licenseManager.retrieveLicense(getApplicationContext());

            onLicenseRetrieved(license);

            Toast.makeText(this, "License Activated", Toast.LENGTH_LONG).show();

        } catch (LkmsNoLicenseAvailableException e) {

            Toast.makeText(getApplicationContext(), "Creando licencia. Por favor espere...", Toast.LENGTH_LONG).show();

            licensePresenter.createLicense();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        ICaptureOptions captureOptions = createCaptureOptions();

        createBioCaptureHandler(captureOptions);
    }

    @Override
    protected void onPause() {

        try {
            if (captureHandler != null) {
                captureHandler.destroy();
                captureHandler = null;
            }
            if (matcherHandler != null) {
                matcherHandler.destroy();
                matcherHandler = null;
            }
        }catch (Exception e){
            Log.e(TAG, "", e);
        }

        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (cameraPreview != null) {
            cameraPreview.onDestroy();
        }
        super.onDestroy();
    }

    protected void createBioCaptureHandler(ICaptureOptions captureOptions){
        BioSdk.createBioCaptureHandler(this, captureOptions, new MscAsyncCallbacks<IBioCaptureHandler>() {
            @Override
            public void onPreExecute() {
            }
            @Override
            public void onSuccess(IBioCaptureHandler result) {
                onBioCaptureInitialized(result);
            }

            @Override
            public void onError(BioCaptureHandlerError e) {
                onErrorHandling(new IllegalStateException(e.name()));
            }
        });
    }

    protected int surfaceViewLayout() {
        return R.id.morpho_surface_view_finger;
    }

    public void onPrepareActivity() {
        try{

            cameraPreview = (MorphoSurfaceView) findViewById(surfaceViewLayout());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No Surface View found!", Toast.LENGTH_LONG).show();
        }
    }

    public void onPreparePresenter() {
        fingersPresenter = new FingersPresenter(new FingersInteractor());
        fingersPresenter.setView(this);
    }

    private ICaptureOptions createCaptureOptions() {
        ICaptureOptions captureOptions = new CaptureOptions();
        Camera camera =  Camera.REAR;
        Torch torch = Torch.ON;
        LogLevel logLevel = LogLevel.ERROR;
        WaitTimeout waitTimeout = WaitTimeout.WAIT_30; // Use valueOf when use SharedPreferences

        captureOptions.setTorch   (torch);
        captureOptions.setCamera  (camera);
        captureOptions.setLogLevel(logLevel);
        captureOptions.setOverlay (Overlay.ON);
        captureOptions.setBioCaptureMode(captureMode());
        captureOptions.setCaptureTimeout(waitTimeout.getIntegerValue());

        return captureOptions;
    }

    public BioCaptureMode captureMode() {
        // You can choose here the capture mode
        return BioCaptureMode.FINGERPRINT_LEFT_HAND;
    }

    protected void onBiometricCaptureSuccess(List<MorphoImage> imageList, int globalImageQuality) {
        if(imageList.size() != 1){
            fingersPresenter.getImagesFingers (imageList);
            fingersPresenter.getFingersQuality(globalImageQuality);
        }
        if(matcherHandler == null) {
            return;
        }
    }

    protected void onInitializationSuccess() {
        try {
            captureHandler.startPreview();
        } catch (MSCException e) {
            e.printStackTrace();
        }
    }

    protected void onBioCaptureInitialized(IBioCaptureHandler iBioCaptureHandler) {

        captureHandler = iBioCaptureHandler;
        captureHandler.setBioCaptureResultListener  (this);
        captureHandler.setBioCaptureFeedbackListener(this);
        captureHandler.setBioTrackingListener(new BioCaptureTrackingListener() {
            @Override
            public void onTracking(List<MorphoBioTraking> trackingInfo) {
                if(captureHandler==null){
                    return;
                }
            }
        });

        IBioMatcherSettings bioMatcherSettings = createMatcherOptions();

        BioSdk.createBioMatcherHandler(this, bioMatcherSettings, new BioMatcherAsyncCallbacks<IBioMatcherHandler>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onSuccess(IBioMatcherHandler iBioMatcherHandler) {
                onBioMatcherInitialized(iBioMatcherHandler);
            }

            @Override
            public void onError(Exception e) {
                onErrorHandling(e);
            }
        });
    }

    @Override
    public void onCaptureSuccess(List<MorphoImage> imageList) {
        if(captureHandler!=null) {
            try {
                captureHandler.stopCapture();
            } catch (MSCException e) {
                e.printStackTrace();
            }
        }
        int imageQuality    =0;
        int numberOfSamples =0;
        BiometricModality biometricModality = BiometricModality.UNKNOWN;

        MorphoImage index = imageList.get(0);
        saveWSQImage(index);

        try{
            saveJPGImage(index.getJPEGImage());
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Iterator<MorphoImage> morphoImageIterator = imageList.iterator();
            while (morphoImageIterator.hasNext()){
                MorphoImage image = morphoImageIterator.next();
                numberOfSamples++;
                biometricModality = image.getBiometricModality();
                Log.i(TAG, "Image quality: "+ image.getImageQuality() +" "+ numberOfSamples);
                Log.i(TAG, "onCaptureSuccess: Biometric Modality" + biometricModality);
                imageQuality += image.getImageQuality();
            }
        }catch (Exception e){
            Log.e(TAG, "", e);
        }

        ICaptureOptions captureOptions = captureHandler.getCaptureOptions();

        int averageQuality;
        try{
            averageQuality = imageQuality/numberOfSamples; // Average for all fingers captured
        }catch (Exception e){
            averageQuality=-1;
        }
        onBiometricCaptureSuccess(imageList, averageQuality);
    }

    @Override
    public void onCaptureFailure(CaptureError captureError, IBiometricInfo iBiometricInfo, Bundle bundle) {
        if(captureHandler!=null) {
            try {
                captureHandler.startCapture();
            } catch (MSCException e) {
                e.printStackTrace();
            }
        }
    }

    public IBioMatcherSettings createMatcherOptions(){
        IBioMatcherSettings bioMatcherSettings = new BioMatcherSettings();
        bioMatcherSettings.setLogLevel(LogLevel.DISABLE);
        bioMatcherSettings.setDumpFileEnable(false);
        bioMatcherSettings.setDumpFileFolder(null);
        return bioMatcherSettings;
    }

    protected void onBioMatcherInitialized(IBioMatcherHandler iBioMatcherHandler){
        matcherHandler =  iBioMatcherHandler;
        onInitializationSuccess();
    }

    protected void onErrorHandling(Exception e) {
        Log.e(TAG, "", e);
        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
    }

    protected void bindViews(){
        ButterKnife.bind(this);
    }

    protected void saveWSQImage(MorphoImage image){
        /// GET WSQ IMAGE
        IImage out = image;
        try{
            IImage wsqImage = ImageUtils.toWSQ(out ,15,(byte)0,(byte)0xff);
            byte [] bufferWSQ = wsqImage.getBuffer();
            // Save IMAGE
            new SavePhotoTask("wsq").execute(bufferWSQ);
            Log.d(TAG, "onCaptureSuccess: WSQ Saved");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void saveJPGImage(byte [] image){
        new SavePhotoTask("jpg").execute(image);
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // progressBar.setVisibility(View.GONE);
            }
        });*/
    }

    @Override
    public void showImages(List<byte[]> jpgImages) {

        int numImage     = 0;
        int fingerSelect = 0;
        String operationID  = SharedPreferencesUtils.readFromPreferencesString(FingerBioSdkActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
        String finger = "";

        if (jpgImages.size() > 0) {

            for (byte[] image:jpgImages ) {

                photoBuffer = image;

                switch (numImage) {

                    case 0:
                        if (!sdkBioSideHand.isChecked()) {
                            finger = "D2";
                            fingerSelect = 2;
                            base64IndexRight = com.teknei.bid.tools.Base64.encode(com.teknei.bid.services.CipherFingerServices.cipherFinger(operationID,image));
                        } else {
                            finger = "I2";
                            fingerSelect = 7;
                            base64IndexLeft = com.teknei.bid.tools.Base64.encode(com.teknei.bid.services.CipherFingerServices.cipherFinger(operationID,image));
                        }
                        break;

                    case 1:
                        if (!sdkBioSideHand.isChecked()) {
                            finger = "D3";
                            fingerSelect = 3;
                            base64MiddleRight = com.teknei.bid.tools.Base64.encode(com.teknei.bid.services.CipherFingerServices.cipherFinger(operationID,image));
                        } else {
                            finger = "I3";
                            fingerSelect = 8;
                            base64MiddleLeft = com.teknei.bid.tools.Base64.encode(com.teknei.bid.services.CipherFingerServices.cipherFinger(operationID,image));
                        }
                        break;

                    case 2:
                        if (!sdkBioSideHand.isChecked()) {
                            finger = "D4";
                            fingerSelect = 4;
                            base64RingRight = com.teknei.bid.tools.Base64.encode(com.teknei.bid.services.CipherFingerServices.cipherFinger(operationID,image));
                        } else {
                            finger = "I4";
                            fingerSelect = 9;
                            base64RingLeft = com.teknei.bid.tools.Base64.encode(com.teknei.bid.services.CipherFingerServices.cipherFinger(operationID,image));
                        }
                        break;

                    case 3:
                        if (!sdkBioSideHand.isChecked()) {
                            finger = "D5";
                            fingerSelect = 5;
                            base64PinkyRight = com.teknei.bid.tools.Base64.encode(com.teknei.bid.services.CipherFingerServices.cipherFinger(operationID,image));
                        } else {
                            finger = "I5";
                            fingerSelect = 10;
                            base64PinkyLeft = com.teknei.bid.tools.Base64.encode(com.teknei.bid.services.CipherFingerServices.cipherFinger(operationID,image));
                        }
                        break;
                }

                Log.d(TAG, Environment.getExternalStorageDirectory() + File.separator + "finger_" + finger + "_" + operationID + ".jpg");

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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                numImage++;
            }

            if (!sdkBioSideHand.isChecked() && numImage >= 4) {
                isTakeLeft = true;
            }

            if (sdkBioSideHand.isChecked() && numImage >= 4) {
                isTakeRight = true;
            }

            if (isTakeLeft && isTakeRight) {

                try {
                    captureHandler.stopPreview();
                } catch (MSCException e) {
                    e.printStackTrace();
                }

            } else {
                onPreparePresenter();
                onPrepareActivity();
            }
        }
    }

    @Override
    public void showJPGImageError(Exception e) {
        Log.e(TAG, "showJPGImageError: "+ e.getMessage());
    }

    @Override
    public void showImageQuality(final int quality) {
        Toast.makeText(getApplicationContext(),"Calidad de la Imagen: " +
                quality, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showBadImageQuality(final int quality) {
        Toast.makeText(getApplicationContext(),
                "Tu imagen es de baja calidad\nPuedes realizar otra captura para mejorar la toma de imagen", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showSuccessLicenseRetrievedMessage(ILkmsLicense license) {
        Toast.makeText(this, license.getProfileId() , Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorLicenseRetrievedMessage(LkmsException e) {
        Toast.makeText(this, e.getMessage() , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLicenseRetrieved(ILkmsLicense license) {
        try {
            ILicenseManager licenseManager= BioSdk.createLicenseManager(getApplicationContext());
            licenseManager.activate(getApplicationContext());
        } catch (LkmsInvalidLicenseException | LkmsNoLicenseAvailableException e) {
            Log.e(TAG, "", e);
        }
    }

    @Override
    public void onCaptureInfo(BioCaptureInfo bioCaptureInfo, Bundle bundle) {
        Log.e(TAG, "onCaptureInfo: " + bioCaptureInfo.toString());
    }

    @Override
    public void onCaptureFinish() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.i_take_image_bio_sdk:
                try {
                    captureHandler.startCapture();
                } catch (MSCException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.b_continue_bio_sdk:
                if (validateIndexFingers()) {
                    Log.d(TAG,"SEND PETICION TRUE");
                    sendPetition();
                }
                break;
        }
    }

    public boolean validateIndexFingers() {
        boolean bitMapTake;

        if (isTakeLeft && isTakeRight) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(FingerBioSdkActivity.this, "Debe capturar ambas manos para continuar", Toast.LENGTH_SHORT).show();
        }
        return bitMapTake;
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
            new FingersSend(FingerBioSdkActivity.this, token, jsonString, fileList, ApiConstants.TYPE_ACT_BASIC).execute();
        } else {
            goNext();
        }
    }

    @Override
    public void goNext() {
        Intent i = new Intent(FingerBioSdkActivity.this, FakeINEActivity.class);
        startActivity(i);
    }

    public String buildJSON() {
        String operationID  = SharedPreferencesUtils.readFromPreferencesString(FingerBioSdkActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
        String idEnterprice = SharedPreferencesUtils.readFromPreferencesString(FingerBioSdkActivity.this, SharedPreferencesUtils.ID_ENTERPRICE, "default");
        String customerType = SharedPreferencesUtils.readFromPreferencesString(FingerBioSdkActivity.this, SharedPreferencesUtils.CUSTOMER_TYPE, "default");

        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emprId", idEnterprice);
            jsonObject.put("customerType", customerType);
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("contentType", "image/jpeg");
            jsonObject = addBase64Fingers(jsonObject);

            Log.d(TAG,"---"+jsonObject);

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
                Log.d ("----------------------------","ll" + base64PinkyLeft);
                jsonObject.put("ll", base64PinkyLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64RingLeft != null && !base64RingLeft.equals("")) {
            try {
                Log.d ("----------------------------","lr" + base64RingLeft);
                jsonObject.put("lr", base64RingLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64MiddleLeft != null && !base64MiddleLeft.equals("")) {
            try {
                Log.d ("----------------------------","lm" + base64MiddleLeft);
                jsonObject.put("lm", base64MiddleLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64IndexLeft != null && !base64IndexLeft.equals("")) {
            try {
                Log.d ("----------------------------","li" + base64IndexLeft);
                jsonObject.put("li", base64IndexLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64ThumbLeft != null && !base64ThumbLeft.equals("")) {
            try {
                Log.d ("----------------------------","lt" + base64ThumbLeft);
                jsonObject.put("lt", base64ThumbLeft);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Right arm
        if (base64PinkyRight != null && !base64PinkyRight.equals("")) {
            try {
                Log.d ("----------------------------","rl" + base64PinkyRight);
                jsonObject.put("rl", base64PinkyRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64RingRight != null && !base64RingRight.equals("")) {
            try {
                Log.d ("----------------------------","rr" + base64RingRight);
                jsonObject.put("rr", base64RingRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64MiddleRight != null && !base64MiddleRight.equals("")) {
            try {
                Log.d ("----------------------------","rm" + base64MiddleRight);
                jsonObject.put("rm", base64MiddleRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64IndexRight != null && !base64IndexRight.equals("")) {
            try {
                Log.d ("----------------------------","ri" + base64IndexRight);
                jsonObject.put("ri", base64IndexRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (base64ThumbRight != null && !base64ThumbRight.equals("")) {
            try {
                Log.d ("----------------------------","rt" + base64ThumbRight);
                jsonObject.put("rt", base64ThumbRight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
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
            dialogoAlert = new AlertDialog(FingerBioSdkActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(FingerBioSdkActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
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

class SavePhotoTask extends AsyncTask<byte[], String, String> {

    private String format;

    public SavePhotoTask(String format) {
        this.format = format;
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    @Override
    protected String doInBackground(byte[]... image) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File photo=new File(Environment.getExternalStorageDirectory(), "indexFingerTKN_"+"." + format);

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());

            fos.write(image[0]);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

        return(null);
    }
}