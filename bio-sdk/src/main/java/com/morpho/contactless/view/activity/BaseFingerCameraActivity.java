package com.morpho.contactless.view.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.morpho.common.view.BasicActivity;
import com.morpho.common.data.model.WaitTimeout;
import com.morpho.contactless.domain.model.ContactlessData;
import com.morpho.mph_bio_sdk.android.sdk.BioSdk;
import com.morpho.mph_bio_sdk.android.sdk.common.LogLevel;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.BioMatcherSettings;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBioMatcherHandler;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBioMatcherSettings;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBiometricInfo;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.async.BioMatcherAsyncCallbacks;
import com.morpho.mph_bio_sdk.android.sdk.msc.IBioCaptureHandler;
import com.morpho.mph_bio_sdk.android.sdk.msc.async.MscAsyncCallbacks;
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
import com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureFeedbackListener;
import com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureResultListener;
import com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureTrackingListener;
import com.morpho.mph_bio_sdk.android.sdk.utils.image.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import morpho.urt.msc.mscengine.MSCEngine;
import morpho.urt.msc.mscengine.MorphoSurfaceView;


/**
 * Created by J. Alfredo Hernández Alarcón on 01/06/17.
 * Finger Camera Activity
 */
public abstract class BaseFingerCameraActivity extends FragmentActivity
        implements BasicActivity,
        BioCaptureFeedbackListener,
        BioCaptureResultListener{

    private static final String TAG = "BaseFingerCameraAct";

    // Local Variables
    protected MorphoSurfaceView cameraPreview;
    protected IBioCaptureHandler captureHandler;
    protected IBioMatcherHandler matcherHandler;
    private CountDownTimer timer = null;


    /// Android Lifecycle Methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request window features
        requestWindowFeatures();

        // Set content view layout
        setContentView(getLayoutResID());
        bindViews();
        onPreparePresenter();
        onPrepareActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ICaptureOptions captureOptions = createCaptureOptions();
        switch (captureOptions.getBioCaptureMode()) {
            case FINGERPRINT_LEFT_HAND_AUTHENTICATION:
            case FINGERPRINT_RIGHT_HAND_AUTHENTICATION: {
                // createBioCaptureHandlerForFingerAuthentication(captureOptions);
                break;
            }
            default: {
                createBioCaptureHandler(captureOptions);
            }
        }
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
        super.onDestroy();
        // Destroy surface view
        if (cameraPreview != null) {
            cameraPreview.onDestroy();
            try{
                matcherHandler.destroy();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Create the BioCaptureHandler
     * @param captureOptions Your {@link ICaptureOptions} options
     */
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


    /// Methods to implement or Override

    /**
     * The layout resource used for this activity
     * @return the layout id associated to the layout used in the activity.
     */
    @LayoutRes
    protected abstract int getLayoutResID();

    /**
     * Use this method to select your Surface View
     * @return The surface view Resource (ex: R.id.morphoSurfaceView)
     */
    protected abstract int surfaceViewLayout();


    @Override
    public void onPrepareActivity() {
        // Camera surface view initialization
        try{
            cameraPreview = (MorphoSurfaceView) findViewById(surfaceViewLayout());
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "No Surface View found!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPreparePresenter() {

    }


    /// Abstract methods to implement for OPTIONS
    /**
     * Set the capture mode
     * @return
     */
    private ICaptureOptions createCaptureOptions(){
        ICaptureOptions captureOptions = new CaptureOptions();
        boolean overlayEnabled = true;
        boolean isDebugMode = false;
        Camera camera =  Camera.REAR;
        // Torch torch = Torch.ON;
        LogLevel logLevel = LogLevel.DEBUG;
        WaitTimeout waitTimeout = WaitTimeout.WAIT_30; // Use valueOf when use SharedPreferences

        if(isDebugMode){
            captureOptions.setDumpFileEnable(false);
            captureOptions.setVideoRecordEnable(false);
            captureOptions.setPartialDumpVideoRecordEnable(false);
            /*String folderPath =  getString(R.string.default_debug_folder);
            com.morpho.mph_bio_sdk.android.sdk.utils.file.FileUtils.createFolder(Environment.getExternalStorageDirectory().toString(), folderPath);
            folderPath = Environment.getExternalStorageDirectory().toString()+ "/"+folderPath;
            captureOptions.setDumpFileFolder(folderPath);
            captureOptions.setVideoRecordFolder(folderPath);
            captureOptions.setPartialDumpVideoRecordFolder(folderPath);
            */
        }
        captureOptions.setBioCaptureMode(captureMode());
        captureOptions.setCamera(camera);
        captureOptions.setTorch(getCameraTorch());
        captureOptions.setOverlay(overlayEnabled ? Overlay.ON : Overlay.OFF);
        captureOptions.setCaptureTimeout(waitTimeout.getIntegerValue());
        captureOptions.setLogLevel(logLevel);
        return captureOptions;
    }

    /**
     * Selecciona si se enciende el flash o no
     * @return {@link Torch} .On o .Off
     */
    protected abstract Torch getCameraTorch();

    /**
     * Use this method to select the capture mode
     * @return A capture mode {@link BioCaptureMode}
     */
    public abstract BioCaptureMode captureMode();


    // Abtract methods to implement When capture Success

    /**
     * After the capture process is done, this method is called and you can retrieve the MoprohoImage
     * list, and a global image quality.
     * @param imageList The image List
     * @param globalImageQuality The Global Quality
     */
    protected abstract void onBiometricCaptureSuccess(List<MorphoImage> imageList, int globalImageQuality);

    /**
     * This method is called when the camera initialization process were successfully
     */
    protected abstract void onInitializationSuccess();


    /// Private methods used by this class

    /**
     * Shows the activity without TITLE and FULL SCREEN
     */
    private void requestWindowFeatures(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /// Private methods for finger capture

    /**
     * This method is called when the capture starts
     * @param iBioCaptureHandler
     */
    protected void onBioCaptureInitialized(IBioCaptureHandler iBioCaptureHandler){
        // TODO....
        Log.i(TAG, "MSC version: "+ MSCEngine.getMSCVersion().getVersionName());
        captureHandler = iBioCaptureHandler;
        captureHandler.setBioCaptureResultListener(this);
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


    /// Listeners implemented

    /**
     * See {@link com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureResultListener}
     * @param imageList List of images captured
     */
    @Override
    public void onCaptureSuccess(List<MorphoImage> imageList) {
        if(captureHandler!=null) {
            try{
                captureHandler.stopCapture();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        int imageQuality=0;
        int numberOfSamples=0;
        BiometricModality biometricModality = BiometricModality.UNKNOWN;

        // Save fingers to buffer

        for (MorphoImage image: imageList){
            // Get WSQ image
            byte [] data = getWSQImage(image);

            // Get JPG image
            byte [] dataImage = null;
            try{
                dataImage = image.getJPEGImage();
            }catch (Exception e){
                e.printStackTrace();
            }


            if(ContactlessData.getInstance().isLeftHand()){
                switch (image.getBiometricLocation()){
                    case FINGER_LEFT_INDEX:
                    case FINGER_RIGHT_INDEX:
                        Log.e(TAG, "onCaptureSuccess: FINGER_LEFT_INDEX");
                        ContactlessData.getInstance().getLeftHand().setIndex(data);
                        ContactlessData.getInstance().getLeftHand().setIndexJPG(dataImage);
                        break;
                    case FINGER_LEFT_MIDDLE:
                    case FINGER_RIGHT_MIDDLE:
                        Log.e(TAG, "onCaptureSuccess: FINGER_LEFT_MIDDLE");
                        ContactlessData.getInstance().getLeftHand().setMiddle(data);
                        ContactlessData.getInstance().getLeftHand().setMiddleJPG(dataImage);
                        break;
                    case FINGER_LEFT_RING:
                    case FINGER_RIGHT_RING:
                        Log.e(TAG, "onCaptureSuccess: FINGER_LEFT_RING");
                        ContactlessData.getInstance().getLeftHand().setRing(data);
                        ContactlessData.getInstance().getLeftHand().setRingJPG(dataImage);
                        break;
                    case FINGER_LEFT_LITTLE:
                    case FINGER_RIGHT_LITTLE:
                        Log.e(TAG, "onCaptureSuccess: FINGER_LEFT_LITTLE");
                        ContactlessData.getInstance().getLeftHand().setLittle(data);
                        ContactlessData.getInstance().getLeftHand().setLittleJPG(dataImage);
                        break;
                }
            }else{
                switch (image.getBiometricLocation()){
                    case FINGER_RIGHT_INDEX:
                    case FINGER_LEFT_INDEX:
                        Log.e(TAG, "onCaptureSuccess: FINGER_RIGHT_INDEX");
                        ContactlessData.getInstance().getRightHand().setIndex(data);
                        ContactlessData.getInstance().getRightHand().setIndexJPG(dataImage);
                        break;
                    case FINGER_RIGHT_MIDDLE:
                    case FINGER_LEFT_MIDDLE:
                        Log.e(TAG, "onCaptureSuccess: FINGER_RIGHT_MIDDLE");
                        ContactlessData.getInstance().getRightHand().setMiddle(data);
                        ContactlessData.getInstance().getRightHand().setMiddleJPG(dataImage);
                        break;
                    case FINGER_RIGHT_RING:
                    case FINGER_LEFT_RING:
                        Log.e(TAG, "onCaptureSuccess: FINGER_RIGHT_RING");
                        ContactlessData.getInstance().getRightHand().setRing(data);
                        ContactlessData.getInstance().getRightHand().setRingJPG(dataImage);
                        break;
                    case FINGER_RIGHT_LITTLE:
                    case FINGER_LEFT_LITTLE:
                        Log.e(TAG, "onCaptureSuccess: FINGER_RIGHT_LITTLE");
                        ContactlessData.getInstance().getRightHand().setLittle(data);
                        ContactlessData.getInstance().getRightHand().setLittleJPG(dataImage);
                        break;
                }
            }

            /*switch (image.getBiometricLocation()){
                case FINGER_LEFT_INDEX:
                    Log.e(TAG, "onCaptureSuccess: FINGER_LEFT_INDEX");
                    ContactlessData.getInstance().getLeftHand().setIndex(data);
                    ContactlessData.getInstance().getLeftHand().setIndexJPG(dataImage);
                    break;
                case FINGER_LEFT_MIDDLE:
                    Log.e(TAG, "onCaptureSuccess: FINGER_LEFT_MIDDLE");
                    ContactlessData.getInstance().getLeftHand().setMiddle(data);
                    ContactlessData.getInstance().getLeftHand().setMiddleJPG(dataImage);
                    break;
                case FINGER_LEFT_RING:
                    Log.e(TAG, "onCaptureSuccess: FINGER_LEFT_RING");
                    ContactlessData.getInstance().getLeftHand().setRing(data);
                    ContactlessData.getInstance().getLeftHand().setRingJPG(dataImage);
                    break;
                case FINGER_LEFT_LITTLE:
                    Log.e(TAG, "onCaptureSuccess: FINGER_LEFT_LITTLE");
                    ContactlessData.getInstance().getLeftHand().setLittle(data);
                    ContactlessData.getInstance().getLeftHand().setLittleJPG(dataImage);
                    break;
                case FINGER_RIGHT_INDEX:
                    Log.e(TAG, "onCaptureSuccess: FINGER_RIGHT_INDEX");
                    ContactlessData.getInstance().getRightHand().setIndex(data);
                    ContactlessData.getInstance().getRightHand().setIndexJPG(dataImage);
                    break;
                case FINGER_RIGHT_MIDDLE:
                    Log.e(TAG, "onCaptureSuccess: FINGER_RIGHT_MIDDLE");
                    ContactlessData.getInstance().getRightHand().setMiddle(data);
                    ContactlessData.getInstance().getRightHand().setMiddleJPG(dataImage);
                    break;
                case FINGER_RIGHT_RING:
                    Log.e(TAG, "onCaptureSuccess: FINGER_RIGHT_RING");
                    ContactlessData.getInstance().getRightHand().setRing(data);
                    ContactlessData.getInstance().getRightHand().setRingJPG(dataImage);
                    break;
                case FINGER_RIGHT_LITTLE:
                    Log.e(TAG, "onCaptureSuccess: FINGER_RIGHT_LITTLE");
                    ContactlessData.getInstance().getRightHand().setLittle(data);
                    ContactlessData.getInstance().getRightHand().setLittleJPG(dataImage);
                    break;
            }*/
        }

        try {
            Iterator<MorphoImage> morphoImageIterator = imageList.iterator();
            while (morphoImageIterator.hasNext()){
                MorphoImage image = morphoImageIterator.next();
                //addCapturedImage(image);
                switch (image.getBiometricLocation()) {
                    // If capture is not a finger...
                    case HAND_UNKNOWN:
                    case HAND_RIGHT:
                    case HAND_LEFT: {
                        break;
                    }
                    default:{
                        numberOfSamples++;
                        biometricModality = image.getBiometricModality();
                        Log.i(TAG, "Image quality: "+image.getImageQuality());
                        Log.i(TAG, "onCaptureSuccess: Biometric Modality" + biometricModality);
                        imageQuality += image.getImageQuality();
                        break;
                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG, "", e);
        }

        ICaptureOptions captureOptions = captureHandler.getCaptureOptions();

        switch (captureOptions.getBioCaptureMode()){
            case FINGERPRINT_RIGHT_HAND_AUTHENTICATION:
            case FINGERPRINT_LEFT_HAND_AUTHENTICATION:{
                //Don't do anything
                break;
            }
            default:{
                int averageQuality;
                try{
                    averageQuality = imageQuality/numberOfSamples; // Average for all fingers captured
                }catch (Exception e){
                    averageQuality=-1;
                }
                // Call onBiometricCaptureSuccess method
                onBiometricCaptureSuccess(imageList, averageQuality);
                break;
            }
        }
    }

    /**
     * Use this method to handle capture errors
     * @param captureError Error
     * @param iBiometricInfo Info
     * @param bundle A bundle
     */
    @Override
    public void onCaptureFailure(CaptureError captureError, IBiometricInfo iBiometricInfo, Bundle bundle) {
        switch (captureError){
            case BAD_CAPTURE_FINGERS:
                break;
            case BAD_CAPTURE_HAND:
                break;
            default:{
                if(captureHandler!=null) {
                    try {
                        captureHandler.startCapture();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }


    /**
     * TODO
     * @return Sections for BioMatcher
     */
    public IBioMatcherSettings createMatcherOptions(){
        IBioMatcherSettings bioMatcherSettings = new BioMatcherSettings();

        boolean isDebugMode = false;
        if(isDebugMode){
            LogLevel logLevel = LogLevel.DISABLE;
            bioMatcherSettings.setLogLevel(logLevel);
            bioMatcherSettings.setDumpFileEnable(false);
            //String folderPath =  getString(R.string.default_debug_folder);
            //com.morpho.mph_bio_sdk.android.sdk.utils.file.FileUtils.createFolder(Environment.getExternalStorageDirectory().toString(), folderPath);
            //folderPath = Environment.getExternalStorageDirectory().toString() + "/" + folderPath;
            //bioMatcherSettings.setDumpFileFolder(folderPath);
        }else{
            bioMatcherSettings.setLogLevel(LogLevel.DISABLE);
            bioMatcherSettings.setDumpFileEnable(false);
            bioMatcherSettings.setDumpFileFolder(null);
        }

        return bioMatcherSettings;
    }

    protected void onBioMatcherInitialized(IBioMatcherHandler iBioMatcherHandler){
        matcherHandler =  iBioMatcherHandler;
        onInitializationSuccess();
    }

    /**
     * This method is called when an Error happen
     * @param e An Exception
     */
    protected abstract void onErrorHandling(Exception e);


    /// Methods used onCreate

    /**
     * Every object annotated with {@link ButterKnife} its gonna injected trough butterknife
     */
    protected void bindViews(){
        ButterKnife.bind(this);
    }

    /// Basic methods

    /**
     * Start a new activity
     * @param intent An Intent
     * @param finish If true, this activity will be closed
     */
    protected void startNextActivity(Intent intent, boolean finish) {
        startActivity(intent);
        if (finish) finish();
    }


    protected byte [] getWSQImage(MorphoImage image){
        /// GET WSQ IMAGE
        IImage out = image;
        try{
            IImage wsqImage = ImageUtils.toWSQ(out ,15,(byte)0,(byte)0xff);
            byte [] bufferWSQ = wsqImage.getBuffer();
            Log.d(TAG, "onCaptureSuccess: WSQ Saved");
            return bufferWSQ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    protected void saveWSQImage( String name, MorphoImage image){
        /// GET WSQ IMAGE
        IImage out = image;
        try{
            IImage wsqImage = ImageUtils.toWSQ(out ,15,(byte)0,(byte)0xff);
            byte [] bufferWSQ = wsqImage.getBuffer();
            // Save IMAGE
            new SavePhotoTask(name , "wsq").execute(bufferWSQ);
            Log.d(TAG, "onCaptureSuccess: WSQ Saved");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    protected void saveJPGImage(String name, byte [] image){
        new SavePhotoTask(name, "jpg").execute(image);
    }

}


class SavePhotoTask extends AsyncTask<byte[], String, String> {

    private String format;
    private String name;

    public SavePhotoTask(String name, String format) {
        this.name = name;
        this.format = format;
    }
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    @Override
    protected String doInBackground(byte[]... image) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File photo=new File(Environment.getExternalStorageDirectory(), name + "_"+"finger_"+ sdf.format(timestamp) +"." + format);

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