package com.teknei.bid.activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.teknei.bid.asynctask.LoginFingerSend;
import com.teknei.bid.dialogs.FingerScanDialog;
import com.teknei.bid.domain.FingerLoginDTO;
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
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import morpho.urt.msc.mscengine.MorphoSurfaceView;

public class LoginFingerBioSmartActivity extends BaseActivity implements FingersPresenter.View, LicensePresenter.Ui,
        BioCaptureResultListener, BioCaptureFeedbackListener, View.OnClickListener {

    private static final String TAG = "LoginFingerBioSmartActivity";

    // Local Variables
    protected MorphoSurfaceView  cameraPreview;
    protected IBioCaptureHandler captureHandler;
    protected IBioMatcherHandler matcherHandler;
    private   CountDownTimer     timer = null;
    private   LicensePresenter   licensePresenter;
    private   FingersPresenter   fingersPresenter;

    private byte[] photoBuffer;

    ImageView      btnCaptureFinger;
    Button         btnLoginFinger;

    private File   imageFingerLogin;
    private String base64FingerLogin;

    private String idClient;
    private FingerLoginDTO fingerDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_finger_bio_smart);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.lfM_login_fingerprints_activity_name));
            invalidateOptionsMenu();
        }

        btnCaptureFinger   = (ImageView) findViewById(R.id.lfb_i_take_image_bio_sdk);
        btnLoginFinger     = (Button)    findViewById(R.id.lfb_b_login_finger);

        btnCaptureFinger.setOnClickListener(LoginFingerBioSmartActivity.this);
        btnLoginFinger.setOnClickListener  (LoginFingerBioSmartActivity.this);

        licensePresenter = new LicensePresenter(new LicenseInteractor( new LicenseService(this,
                "http://201.99.117.119:8081/ServiceProviderLicense/LicenseRequest",
                "https://service-intg.dictao.com/lkms-server-app")));
        licensePresenter.setUi(LoginFingerBioSmartActivity.this);

        checkLicense();

        bindViews();

        onPreparePresenter();

        onPrepareActivity();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lfb_b_login_finger:
                if (validateIndexFingers()) {
                    sendPetition();
                }
                break;
            case R.id.lfb_i_take_image_bio_sdk:
                try {
                    captureHandler.startCapture();
                } catch (MSCException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferencesUtils.deleteFromPreferences(LoginFingerBioSmartActivity.this, SharedPreferencesUtils.TOKEN_APP);
        SharedPreferencesUtils.deleteFromPreferences(LoginFingerBioSmartActivity.this, SharedPreferencesUtils.USERNAME);
        SharedPreferencesUtils.cleanSharedPreferencesOperation(LoginFingerBioSmartActivity.this);

        Intent i = new Intent(LoginFingerBioSmartActivity.this, LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public boolean validateIndexFingers() {
        boolean bitMapTake;
        if (base64FingerLogin.length()>0) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(LoginFingerBioSmartActivity.this, "Capture huella del dedo índice", Toast.LENGTH_SHORT).show();
        }
        return bitMapTake;
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

    protected void bindViews(){
        ButterKnife.bind(this);
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

    protected int surfaceViewLayout() {
        return R.id.morpho_surface_view_finger;
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

    protected void onErrorHandling(Exception e) {
        Log.e(TAG, "", e);
        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
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

    protected void onInitializationSuccess() {
        try {
            captureHandler.startPreview();
        } catch (MSCException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoader() {

    }

    @Override
    public void hideLoader() {

    }

    @Override
    public void showImages(List<byte[]> jpgImages) {

        int    fingerSelect = 0;
        String operationID  =
                SharedPreferencesUtils.readFromPreferencesString
                        (LoginFingerBioSmartActivity.this, SharedPreferencesUtils.OPERATION_ID, "");

        if (jpgImages.size() > 0) {

            photoBuffer = jpgImages.get(0);

            base64FingerLogin = com.teknei.bid.tools.Base64.encode(photoBuffer);

            Log.d(TAG, Environment.getExternalStorageDirectory() + File.separator + "finger.jpg");

            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger.jpg");
            if (f.exists()) {
                f.delete();
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger.jpg");
            }

            try {
                //write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(photoBuffer);
                // remember close de FileOutput
                fo.close();

                imageFingerLogin = f;

            } catch (IOException e) {
                e.printStackTrace();
            }
             //   captureHandler.stopPreview();
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
                "Tu imagen es de baja calidad\nPuedes realizar otra captura para\nmejorar la toma de imagen", Toast.LENGTH_LONG).show();
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

    @Override
    public void onCaptureFinish() {

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

    protected void onBiometricCaptureSuccess(List<MorphoImage> imageList, int globalImageQuality) {
        if(imageList.size() != 1){
            fingersPresenter.getImagesFingers (imageList);
            fingersPresenter.getFingersQuality(globalImageQuality);
        }
        if(matcherHandler == null) {
            return;
        }
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");

        fingerDTO    = new FingerLoginDTO();
        idClient     = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.ID_CLIENT, "");

        fingerDTO.setFingerIndex(base64FingerLogin);
        fingerDTO.setId         (idClient);
        fingerDTO.setContentType("image/wsq");

        new LoginFingerSend(LoginFingerBioSmartActivity.this, token, fingerDTO).execute();
    }

    @Override
    public void goNext() {
        try {
            captureHandler.stopPreview();
        } catch (MSCException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(LoginFingerBioSmartActivity.this, FormActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
