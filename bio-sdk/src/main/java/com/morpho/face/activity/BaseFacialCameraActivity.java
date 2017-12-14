package com.morpho.face.activity;

import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.morpho.common.view.BaseMorphoActivity;
import com.morpho.common.view.BasicActivity;
import com.morpho.face.activity.ui.FacialCameraUi;
import com.morpho.mph_bio_sdk.android.sdk.BioSdk;
import com.morpho.mph_bio_sdk.android.sdk.common.DataKeyValues;
import com.morpho.mph_bio_sdk.android.sdk.common.LogLevel;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.BioMatcherSettings;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBioMatcherHandler;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBioMatcherSettings;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.IBiometricInfo;
import com.morpho.mph_bio_sdk.android.sdk.morpholite.async.BioMatcherAsyncCallbacks;
import com.morpho.mph_bio_sdk.android.sdk.msc.FaceCaptureHandler;
import com.morpho.mph_bio_sdk.android.sdk.msc.IBioCaptureHandler;
import com.morpho.mph_bio_sdk.android.sdk.msc.IFaceCaptureHandler;
import com.morpho.mph_bio_sdk.android.sdk.msc.IFaceCaptureOptions;
import com.morpho.mph_bio_sdk.android.sdk.msc.async.MscAsyncCallbacks;

import com.morpho.mph_bio_sdk.android.sdk.msc.data.BioCaptureInfo;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.BioCaptureMode;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.Camera;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.CaptureError;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.FaceCaptureOptions;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.ICaptureOptions;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.Torch;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.results.MorphoImage;
import com.morpho.mph_bio_sdk.android.sdk.msc.error.BioCaptureHandlerError;
import com.morpho.mph_bio_sdk.android.sdk.msc.error.exceptions.MSCException;
import com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureFeedbackListener;
import com.morpho.mph_bio_sdk.android.sdk.msc.listeners.BioCaptureResultListener;

import java.util.List;
import com.morpho.mrz.R;
import butterknife.ButterKnife;
import morpho.urt.msc.mscengine.MSCEngine;
import morpho.urt.msc.mscengine.MorphoSurfaceView;

/**
 * Base Facial Camera Activity
 *
 * Extends from this class when you need a Facial Camera Activity for your projects.
 *
 * @author Jesús Alfredo Hernández Alarcón <jesus.alarcon@morpho.com>
 */
public abstract class BaseFacialCameraActivity extends BaseMorphoActivity
        implements BasicActivity,
        BioCaptureResultListener,
        BioCaptureFeedbackListener,
        FacialCameraUi{

    //region Variables
    private static final String TAG = "B. FacialCameraActivity";
    private Toolbar toolbar;

    // Handlers
    protected IFaceCaptureHandler captureHandler;
    protected IBioMatcherHandler matcherHandler;
    protected MorphoSurfaceView morphoSurfaceViewPreview;

    protected int currentStep = 0;

    //endregion

    //region ANDROID LIFECYCLE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResID());   // Set content view layout
        initSupportActionBar();             // Init support for action bar
        bindViews();                        // Bind Views using butterknife
        onPreparePresenter();               // Prepare your presenters
        onPrepareActivity();                // Prepare your activity
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
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (captureHandler != null) {
                captureHandler.destroy();
                captureHandler = null;
            }
            if (matcherHandler != null) {
                matcherHandler.destroy();
                matcherHandler = null;
            }
            morphoSurfaceViewPreview.onDestroy();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }
        super.onDestroy();
    }

    //endregion

    //region BASE ANDROID
    /**
     * In this method you prepare you activity. You can configure the UI elements
     * or whatever you need.
     */
    @Override public void onPrepareActivity() {
        setupWindow();
        setMorphoSurfacePreview();
    }

    /**
     * In this method you can configure your presenters (if you are using MVP)
     */
    @Override public void onPreparePresenter() {

    }

    /**
     * Toolbar will be configured like a {@link ActionBar} if exists in the layout
     * if it doesn't exist will be ignored
     */
    private void initSupportActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            onSetupSupportActionBar(getSupportActionBar());
        }
    }

    /**
     * Called just after bindViews.
     * Override this method to configure your {@link ActionBar}
     */
    protected void onSetupSupportActionBar(ActionBar actionBar) {

    }
    //endregion

    //region METHODS TO IMPLEMENT OR OVERRIDE
    /**
     * The layout resource used for this activity
     * @return the layout id associated to the layout used in the activity.
     */
    @LayoutRes
    protected abstract int getLayoutResID();

    /**
     * Every object annotated with {@link ButterKnife} its gonna injected trough butterknife
     */
    protected void bindViews(){
        ButterKnife.bind(this);
    }

    /**
     * The morpho layout resource use to show a live preview
     * @return A Morpho Surface Preview
     */
    protected abstract int surfaceViewLayout();
    //endregion

    //region BASE FACIAL CAMERA ACTIVITY METHODS TO IMPLEMENT IN YOUR ACTIVITY
    /**
     * Called after onBioCaptureHandlerCreated > onCaptureSuccess method
     * @param imageList The image list captured
     */
    protected abstract void onBioCaptureSuccess(List<MorphoImage> imageList);

    /**
     * Called after onBioMatcherCreationSuccess method.
     */
    protected void onInitializationSuccess(){
        startPreview();
        startCapture();
    }

    /**
     * Called after onBioCaptureError method
     */
    protected abstract void onInitializationFailure();
    //endregion

    //region CAMERA OPTIONS
    /**
     * Set the camera mode
     * @return {@link Camera} Camera mode
     */
    protected abstract Camera camera();

    /**
     * Used to set timeout
     * @return Timeout in seconds
     */
    protected abstract int timeout();

    /**
     * Set if torch is ON or OFF
     * @return {@link Torch} Torch
     */
    protected abstract Torch torch();

    /**
     * Set the bio capture mode. You can use this modes:
     * - TRACK_FACE_CHALLENGE_VERY_LOW
     * - TRACK_FACE_CHALLENGE_LOW
     * - TRACK_FACE_CHALLENGE_MEDIUM
     * - TRACK_FACE_CHALLENGE_HIGH
     * - TRACK_FACE_CHALLENGE_VERY_HIGH
     *
     * @return {@link BioCaptureMode} BioCapture Mode
     */
    protected abstract BioCaptureMode bioCaptureMode();

    /**
     * Challenge delay. This is a delay between challenges.
     * @return Delay in seconds
     */
    protected abstract int challengeDelay();
    //endregion

    //region PRIVATE METHODS
    private void setupWindow(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setMorphoSurfacePreview(){
        try{
            morphoSurfaceViewPreview = (MorphoSurfaceView) findViewById(surfaceViewLayout());
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "No Surface View found!", Toast.LENGTH_LONG).show();
        }
    }

    //endregion

    //region FACIAL CAMERA ACTIVITY BASIC FUNCTIONALITY

    /**
     * This method should be used before your capture starts
     */
    public void initializeCaptureHandler(){
        BioSdk.createBioCaptureHandler(this, getCameraOptions(), new MscAsyncCallbacks<IBioCaptureHandler>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onSuccess(IBioCaptureHandler result) {
                onBioCaptureHandlerCreated(result);
            }

            @Override
            public void onError(BioCaptureHandlerError e) {
                onBioCaptureError(e);
            }
        });
    }

    protected void onBioCaptureError(BioCaptureHandlerError e){
        Toast.makeText(this, e.name(), Toast.LENGTH_SHORT).show();
        onInitializationFailure();
    }

    protected void onBioMatcherCreationSuccess(IBioMatcherHandler result){
        matcherHandler =  result;
        onInitializationSuccess();
    }

    protected void onBioMatcherError(Exception e){
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        onInitializationFailure();
    }

    protected void startPreview(){
        if(captureHandler!=null){
            try {
                captureHandler.startPreview();
            }catch (MSCException e) {
                onErrorHandling(e);
                return;
            }
        }
    }

    protected void startCapture(){
        if(captureHandler!=null){
            try {
                captureHandler.startCapture();
            }catch (MSCException e) {
                onErrorHandling(e);
                return;
            }
        }
    }

    protected void onErrorHandling(Exception e){
        Log.e(TAG, "", e);
        /*
        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
        finish();
        */
    }


    protected void onBioCaptureHandlerCreated(IBioCaptureHandler result){
        Log.d(TAG, "MSC version: "+ MSCEngine.getMSCVersion().getVersionName());
        captureHandler = (FaceCaptureHandler) result;
        captureHandler.setBioCaptureResultListener(this);
        captureHandler.setBioCaptureFeedbackListener(this);

        IBioMatcherSettings bioMatcherSettings = createMatcherOptions();

        BioSdk.createBioMatcherHandler(this, bioMatcherSettings, new BioMatcherAsyncCallbacks<IBioMatcherHandler>() {
            @Override
            public void onPreExecute() {
                //Toast.makeText(FaceCameraActivity.this, getText(R.string.loading_matcher), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(IBioMatcherHandler iBioMatcherHandler) {
                onBioMatcherCreationSuccess(iBioMatcherHandler);
            }

            @Override
            public void onError(Exception e) {
                onBioMatcherError(e);
            }
        });
    }

    /**
     * Capture options. In this method is used to set capture options
     * @return {@link ICaptureOptions} Capture Options
     */
    private ICaptureOptions getCameraOptions() {
        IFaceCaptureOptions captureOptions = new FaceCaptureOptions();
        captureOptions.setBioCaptureMode(bioCaptureMode());
        captureOptions.setCamera(camera());
        captureOptions.setCaptureTimeout(timeout());
        captureOptions.setTorch(torch());
        captureOptions.setChallengeInterDelay(challengeDelay());
        return captureOptions;
    }

    /**
     * TODO: Customize like Capture options...
     * Match Handler options. Used to set MatchHandler Options.
     * @return {@link IBioMatcherSettings} Settings
     */
    public IBioMatcherSettings createMatcherOptions(){
        IBioMatcherSettings bioMatcherSettings = new BioMatcherSettings();
        boolean isDebugMode = false;
        if(isDebugMode){
            bioMatcherSettings.setLogLevel(LogLevel.DEBUG);
            bioMatcherSettings.setDumpFileEnable(true);
            String folderPath = "SmartBioSdk/debug";
            com.morpho.mph_bio_sdk.android.sdk.utils.file.FileUtils.createFolder(Environment.getExternalStorageDirectory().toString(), folderPath);
            folderPath = Environment.getExternalStorageDirectory().toString() + "/" + folderPath;
            bioMatcherSettings.setDumpFileFolder(folderPath);
        }else{
            bioMatcherSettings.setLogLevel(LogLevel.DISABLE);
            bioMatcherSettings.setDumpFileEnable(false);
            bioMatcherSettings.setDumpFileFolder(null);
        }
        return bioMatcherSettings;
    }
    //endregion

    //region FEEDBACK LISTENER IMPLEMENTATION
    @Override
    public void onCaptureInfo(BioCaptureInfo bioCaptureInfo, Bundle extraInfo) {
        String message = "";
        switch (bioCaptureInfo){
            case FACE_INFO_TURN_LEFT:
                message = getString(R.string.info_face_turn_left);
                break;
            case FACE_INFO_TURN_RIGHT:
                message = getString(R.string.info_face_turn_right);
                break;
            case FACE_INFO_TURN_DOWN:
                message = getString(R.string.info_turn_head_down);
                break;
            case FACE_INFO_TURN_LEFTRIGHT:
                message = getString(R.string.info_left_right_movement);
                break;
            case FACE_INFO_COME_BACK_FIELD:
                message =  getString(R.string.info_face_come_back_field);
                break;
            case FACE_INFO_GET_OUT_FIELD:
                message = getString(R.string.info_face_get_out_of_field);
                break;
            case FACE_INFO_CENTER_LOOK_FRONT_OF_CAMERA:
                message = getString(R.string.info_face_center_look_front_of_camera);
                break;
            case INFO_UNDEFINED:{
                if(extraInfo.containsKey(DataKeyValues.FACE_CHALLENGE_COUNTER_TOTAL) &&
                        extraInfo.containsKey(DataKeyValues.FACE_CHALLENGE_COUNTER_CURRENT)) {
                    int total = extraInfo.getInt(DataKeyValues.FACE_CHALLENGE_COUNTER_TOTAL);
                    int current = extraInfo.getInt(DataKeyValues.FACE_CHALLENGE_COUNTER_CURRENT);
                    updateStepProgress(current);
                    totalSteps(total);
                    currentStep = current;
                    Log.i(TAG, "TOTAL : "+total+", CURRENT :"+current);
                }
                break;
            }
            default:{
                // message = bioCaptureInfo.name();
                break;
            }
        }
        // Show feedback message
        showFeedback(message);
    }
    //endregion

    //region BIO CAPTURE RESULT LISTENER
    @Override
    public void onCaptureSuccess(List<MorphoImage> imageList) {
        try {
            captureHandler.stopCapture();
            captureHandler.stopPreview();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }
        onBioCaptureSuccess(imageList);
        onCaptureFinishedWithoutErrors();
    }

    @Override
    public void onCaptureFailure(CaptureError captureError, IBiometricInfo iBiometricInfo, Bundle extraInfo) {
        // Toast.makeText(getApplicationContext(), captureError.toString() + " " + iBiometricInfo.getBiometricLocation(), Toast.LENGTH_LONG).show();
        switch (captureError){
            case CAPTURE_DELAYED:
                Log.e(TAG, "onCaptureFailure: CAPTURE_DELAYED");
                break;
            case LIVENESS_CHECK:
                Log.e(TAG, "onCaptureFailure: LIVENESS_CHECK");
                break;
            case CAPTURE_TIMEOUT:
                Log.e(TAG, "onCaptureFailure: CAPTURE_TIMEOUT");
                break;
            case BAD_CAPTURE_FACE:
                Log.e(TAG, "onCaptureFailure: BAD_CAPTURE_FACE");
                break;
            default:{
                if(captureHandler!=null) {
                    Log.i(TAG, "Challenge error step/max: "+currentStep+"/"+captureHandler.getTotalNumberOfCapturesBeforeDelay());
                }
                break;
            }
        }
        if(captureHandler!=null) {
            try {
                captureHandler.stopCapture();
                captureHandler.stopPreview();
            }catch(MSCException e){
                onErrorHandling(e);
                return;
            }
        }
        onCaptureError(captureError);
    }

    @Override
    public void onCaptureFinish() {
        onCaptureFinished();
    }
    //endregion
}
