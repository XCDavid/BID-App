package com.teknei.bid.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.morpho.common.data.LicenseService;
import com.morpho.common.view.custom.AlertDialogTool;
import com.morpho.common.view.custom.DialogUses;
import com.morpho.face.activity.ui.FacialCameraUi;
import com.morpho.license.interactor.LicenseInteractor;
import com.morpho.license.presenter.LicensePresenter;
import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsException;
import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsInvalidLicenseException;
import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsNoLicenseAvailableException;
import com.morpho.lkms.android.sdk.lkms_core.license.ILkmsLicense;
import com.morpho.mph_bio_sdk.android.sdk.BioSdk;
import com.morpho.mph_bio_sdk.android.sdk.common.DataKeyValues;
import com.morpho.mph_bio_sdk.android.sdk.common.LogLevel;
import com.morpho.mph_bio_sdk.android.sdk.licence.ILicenseManager;
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
import com.teknei.bid.R;
import com.teknei.bid.asynctask.FaceFileSend;
import com.teknei.bid.dialogs.AlertDialog;
import com.teknei.bid.tools.Base64;
import com.teknei.bid.tools.BitmapHelper;
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
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.jvm.internal.Intrinsics;
import morpho.urt.msc.mscengine.MSCEngine;
import morpho.urt.msc.mscengine.MorphoSurfaceView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FaceEnrollActivity extends BaseActivity implements View.OnClickListener, LicensePresenter.Ui, BioCaptureResultListener,
        BioCaptureFeedbackListener, FacialCameraUi {

    //region Variables
    private static final String TAG = "B. FacialCameraActivity";
    private Toolbar toolbar;

    private TextView textViewFeedback;
    private MorphoSurfaceView morphoSurfaceView;
    private CircleProgressView circleProgressViewChallenge;
    private CircleImageView circleImageViewCaptured;

    private Button buttonTakeAgain;
    private Button buttonContinue;

    private String fontName = "Raleway-Regular.ttf";
    protected SharedPreferences sharedPreferences;

    private LicensePresenter licensePresenter;

    private byte[] photoBuffer;
    private File imageFile;
    private File fileJson;
    private List<File> fileList;
    //endregion

    //region Handlers
    protected IFaceCaptureHandler captureHandler;
    protected IBioMatcherHandler matcherHandler;
    protected MorphoSurfaceView morphoSurfaceViewPreview;

    protected int currentStep = 0;
    private int totalSteps;
    //endregion

    //region ANDROID LIFECYCLE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_enroll);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.face_scan_activity_name));
            invalidateOptionsMenu();
        }

        // Prepare License Presenter
        licensePresenter = new LicensePresenter(new LicenseInteractor( new LicenseService
                (this, getString(R.string.url_activation_server_default), getString(R.string.url_lkms_server_default))));

        licensePresenter.setUi(this);

        hideBars();         // Hide Bars

        checkLicense();

        fileList = new ArrayList<File>();

        //loadFont();
        loadSharedPreferences();

        textViewFeedback            = (TextView) findViewById(R.id.text_view_feedback);
        circleImageViewCaptured     = (CircleImageView) findViewById(R.id.circle_image_view_captured);
        circleProgressViewChallenge = (CircleProgressView) findViewById(R.id.circle_progress_view_challenge);
        morphoSurfaceView           = (MorphoSurfaceView) findViewById(R.id.morpho_surface_view_face);
        buttonTakeAgain             = (Button) findViewById(R.id.fe_btn_take_again);
        buttonContinue              = (Button) findViewById(R.id.fe_btn_continue);

        try{
            morphoSurfaceViewPreview = (MorphoSurfaceView) findViewById(R.id.morpho_surface_view_face);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "No Surface View found!", Toast.LENGTH_LONG).show();
        }

        circleImageViewCaptured.setOnClickListener(this);
        buttonTakeAgain.setOnClickListener(this);
        buttonContinue.setOnClickListener (this);

        initSupportActionBar ();             // Init support for action bar
        onPrepareActivity    ();             // Prepare your activity
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fe_btn_take_again:
                FaceEnrollActivity.this.recreate();
                break;

            case R.id.circle_image_view_captured:
                FaceEnrollActivity.this.recreate();
                break;

            case R.id.fe_btn_continue:
                if (validatePictureTake()) {
                    sendPetition();
                }
                break;
        }

    }
    //endregion

    //region BASE ANDROID
    /**
     * In this method you prepare you activity. You can configure the UI elements
     * or whatever you need.
     */
    public void onPrepareActivity() {
        initUi();
        startCountdownTimer();
        setupWindow();
        setMorphoSurfacePreview();
    }

    public boolean validatePictureTake() {
        boolean bitMapTake = false;
        if (circleImageViewCaptured.getDrawable() instanceof BitmapDrawable) {
            bitMapTake = true;
        } else {
            bitMapTake = false;
            Toast.makeText(this, "Debes tomar una fotografía del rostro del usuario para continuar.", Toast.LENGTH_SHORT).show();
        }
        return bitMapTake;
    }

    @Override
    public void sendPetition() {
        String token = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.TOKEN_APP, "");
        String faceOperation = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.FACE_OPERATION, "");
        if (faceOperation.equals("")) {
            fileList.clear();
            String localTime = PhoneSimUtils.getLocalDateAndTime();
//            SharedPreferencesUtils.saveToPreferencesString(FaceScanActivity.this, SharedPreferencesUtils.TIMESTAMP_FACE, localTime);

            String jsonString = buildJSON();
            fileList.add(fileJson);
            fileList.add(imageFile);
            new FaceFileSend(FaceEnrollActivity.this, token, jsonString, fileList).execute();
        } else {
            goNext();
        }
    }

    @Override
    public void goNext() {
        //Intent i = new Intent(FaceEnrollActivity.this, DocumentScanActivity.class);
        //startActivity(i);

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

        String opcionFingerprintReader = SharedPreferencesUtils.readFromPreferencesString(FaceEnrollActivity.this, SharedPreferencesUtils.FINGERPRINT_READER, "");

        if (opcionFingerprintReader.equals("watson")){

            Intent i = new Intent(FaceEnrollActivity.this, FingerWatsonActivity.class);
            startActivity(i);

        } else if (opcionFingerprintReader.equals("biosmart")) {

            Intent i = new Intent(FaceEnrollActivity.this, FingerBioSdkActivity.class);
            startActivity(i);

        } else {

            Intent i = new Intent(FaceEnrollActivity.this, FingerPrintsActivity.class);
            startActivity(i);
        }

    }

    public String buildJSON() {
        String operationID  = SharedPreferencesUtils.readFromPreferencesString(FaceEnrollActivity.this, SharedPreferencesUtils.OPERATION_ID, "23");
        String idEnterprice = SharedPreferencesUtils.readFromPreferencesString(FaceEnrollActivity.this, SharedPreferencesUtils.ID_ENTERPRICE, "default");
        String customerType = SharedPreferencesUtils.readFromPreferencesString(FaceEnrollActivity.this, SharedPreferencesUtils.CUSTOMER_TYPE, "default");

        Log.d("FaceScanActivity"," Operation  id " + operationID);
        Log.d("FaceScanActivity"," Enterprice id " + idEnterprice);
        Log.d("FaceScanActivity"," Customer Type " + customerType);

        //Construimos el JSON
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("emprId", idEnterprice);
            jsonObject.put("customerType", customerType);
            jsonObject.put("operationId", Integer.valueOf(operationID));
            jsonObject.put("contentType", "image/jpeg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Writer output;
            fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "rostro" + ".json");
            if (fileJson.exists()) {
                fileJson.delete();
                fileJson = new File(Environment.getExternalStorageDirectory() + File.separator + "rostro" + ".json");
            }
            output = new BufferedWriter(new FileWriter(fileJson));
            output.write(jsonObject.toString());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
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
            dialogoAlert = new AlertDialog(FaceEnrollActivity.this, getString(R.string.message_close_operation_title), getString(R.string.message_close_operation_alert), ApiConstants.ACTION_CANCEL_OPERATION);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        if (id == R.id.i_log_out_menu) {
            AlertDialog dialogoAlert;
            dialogoAlert = new AlertDialog(FaceEnrollActivity.this, getString(R.string.message_title_logout), getString(R.string.message_message_logout), ApiConstants.ACTION_LOG_OUT);
            dialogoAlert.setCancelable(false);
            dialogoAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogoAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    /**
     * Toolbar will be configured like a {@link ActionBar} if exists in the layout
     * if it doesn't exist will be ignored
     */
    private void initSupportActionBar() {
        toolbar = (Toolbar) findViewById(com.morpho.mrz.R.id.toolbar);
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

    //region BASE FACIAL CAMERA ACTIVITY METHODS TO IMPLEMENT IN YOUR ACTIVITY

    /**
     * Called after onBioMatcherCreationSuccess method.
     */
    protected void onInitializationSuccess(){
        startPreview();
        startCapture();
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
        Toast.makeText(getApplicationContext(),
                "Error al inicializar la captura facial",
                Toast.LENGTH_LONG).show();
        buttonTakeAgain.setVisibility(View.VISIBLE);
    }

    protected void onBioMatcherCreationSuccess(IBioMatcherHandler result){
        matcherHandler =  result;
        onInitializationSuccess();
    }

    protected void onBioMatcherError(Exception e){
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),
                "Error al inicializar la captura facial",
                Toast.LENGTH_LONG).show();
        buttonTakeAgain.setVisibility(View.VISIBLE);
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
        captureOptions.setBioCaptureMode(BioCaptureMode.TRACK_FACE_CHALLENGE_LOW);
        captureOptions.setCamera(Camera.REAR);
        captureOptions.setCaptureTimeout(45);
        captureOptions.setTorch(Torch.OFF);
        captureOptions.setChallengeInterDelay(5);
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
                message = getString(com.morpho.mrz.R.string.info_face_turn_left);
                break;
            case FACE_INFO_TURN_RIGHT:
                message = getString(com.morpho.mrz.R.string.info_face_turn_right);
                break;
            case FACE_INFO_TURN_DOWN:
                message = getString(com.morpho.mrz.R.string.info_turn_head_down);
                break;
            case FACE_INFO_TURN_LEFTRIGHT:
                message = getString(com.morpho.mrz.R.string.info_left_right_movement);
                break;
            case FACE_INFO_COME_BACK_FIELD:
                message =  getString(com.morpho.mrz.R.string.info_face_come_back_field);
                break;
            case FACE_INFO_GET_OUT_FIELD:
                message = getString(com.morpho.mrz.R.string.info_face_get_out_of_field);
                break;
            case FACE_INFO_CENTER_LOOK_FRONT_OF_CAMERA:
                message = getString(com.morpho.mrz.R.string.info_face_center_look_front_of_camera);
                break;
            case INFO_UNDEFINED: {
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

        buttonTakeAgain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCaptureFinish() {
        onCaptureFinished();
    }

    @Override
    public void updateStepProgress(int current) {
        // Obtiene el challenge actual que se está realizando
        Log.d(TAG, "Realizando el challenge número: " + current);
        circleProgressViewChallenge.setValueAnimated((float)current-1);
    }

    @Override
    public void totalSteps(int totalSteps) {
        // Aqui se obtiene el numero total de challenges (en caso de que se eliga algun modo challenge)
        Log.d(TAG, "Total numero de challenges por realizar: " + totalSteps);
        this.totalSteps = totalSteps;
        circleProgressViewChallenge.setMaxValue(totalSteps);
    }

    @Override
    public void showFeedback(String message) {
        // Muestra un mensaje de texto con información para el usuario sobre los moviemientos que
        // debe realizar en la captura
        textViewFeedback.setText(message);
    }

    @Override
    public void onCaptureError(CaptureError captureError) {
        // Metodo que se llama cuando existe un error en la captura
        Toast.makeText(getApplicationContext(),
                "Error en la captura facial: " + captureError.name(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCaptureFinished() {
        // LLamado cuando la captura termina
        morphoSurfaceViewPreview.setVisibility(View.INVISIBLE);
        circleImageViewCaptured.setVisibility (View.VISIBLE);
        buttonTakeAgain.setVisibility         (View.VISIBLE);
    }

    @Override
    public void onCaptureFinishedWithoutErrors() {
        circleProgressViewChallenge.setValueAnimated(this.totalSteps);
        textViewFeedback.setText("Captura Facial Finalizada");
    }
    //endregion

    //region PRIVATE METHODS
    private void startCountdownTimer(){
        String time = "5";
        int timeSeconds = Integer.valueOf(time);
        final long start = timeSeconds * 1000;
        new CountDownTimer(start, 100) {
            public void onTick(long millisUntilFinished) {
                performTick(millisUntilFinished);
            }
            public void onFinish() {
                Log.d(TAG, "onTick: Start camera process!");
                initializeCaptureHandler(); // Start Capture...
            }
            void performTick(long millisUntilFinished) {
                textViewFeedback.setText( getString(R.string.time_before_start, String.valueOf(Math.round(millisUntilFinished * 0.001f))) );
            }
        }.start();
    }


    private void initUi(){
        setOrientationByWindowSize();
        textViewFeedback.setText("");
        circleImageViewCaptured.setVisibility(View.GONE);
        buttonTakeAgain.setVisibility(View.GONE);
    }

    private void setupWindow(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setMorphoSurfacePreview(){
        try{
            morphoSurfaceViewPreview = (MorphoSurfaceView) findViewById(R.id.morpho_surface_view_face);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "No Surface View found!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called after onBioCaptureHandlerCreated > onCaptureSuccess method
     *
     * @param imageList The image list captured
     */
    protected void onBioCaptureSuccess(List<MorphoImage> imageList) {

        Toast.makeText(getApplicationContext(), "Captura facial finalizada correctamente", Toast.LENGTH_SHORT).show();

        try{

            circleImageViewCaptured.setImageBitmap(BitmapHelper.byteArrayToBitmap(imageList.get(0).getJPEGImage()));

            photoBuffer = imageList.get(0).getJPEGImage();

            String operationID = SharedPreferencesUtils.readFromPreferencesString(FaceEnrollActivity.this, SharedPreferencesUtils.OPERATION_ID, "");
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "face_" + operationID + ".jpg");
            if (f.exists()) {
                f.delete();
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "face_" + operationID + ".jpg");
            }

            try {
                //write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(photoBuffer);
                // remember close de FileOutput
                fo.close();
                imageFile = f;
            } catch (IOException e) {
                e.printStackTrace();
                imageFile = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /*
    private void loadFont(){
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/"+ fontName)
                .setFontAttrId(com.morpho.mrz.R.attr.fontPath)
                .build()
        );
    }
    */

    public void setOrientationByWindowSize(){
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            //Toast.makeText(this, "Large screen",Toast.LENGTH_LONG).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            //Toast.makeText(this, "Normal sized screen" , Toast.LENGTH_LONG).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            //Toast.makeText(this, "Small sized screen" , Toast.LENGTH_LONG).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            //Toast.makeText(this, "Screen size is neither large, normal or small" , Toast.LENGTH_LONG).show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    protected void loadSharedPreferences(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    //endregion

    //region ALERTS and Toasts
    public void showAlert(final Context context, final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogTool.showAlertDialog(title,
                        message,
                        getString(com.morpho.mrz.R.string.accept), null, null, context, new DialogUses() {
                            @Override
                            public void cancelButtonAction() {
                            }

                            @Override
                            public void acceptButtonAction() {
                            }
                        });
            }
        });

    }

    public void showAlert(final Context context, final String title, final String message, final TextView input) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogTool.showAlertDialog(title,
                        message,
                        getString(com.morpho.mrz.R.string.accept), null, input, context, new DialogUses() {
                            @Override
                            public void cancelButtonAction() {
                            }

                            @Override
                            public void acceptButtonAction() {
                            }
                        });
            }
        });

    }

    public void showAlert(final Context context, final String title, final String message, final DialogUses uses) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogTool.showAlertDialog(title,
                        message,
                        getString(com.morpho.mrz.R.string.accept), null, null, context, uses);
            }
        });

    }

    public void showAlert(final Context context, final String title, final String message, final String positve, final String negative,
                          final DialogUses uses) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialogTool.showAlertDialog(title,
                        message,
                        positve, negative, null, context, uses);
            }
        });

    }

    /**
     * Display a simple toast with LONG duration
     * @param context The context
     * @param message Your message
     */
    protected void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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

    }

    @Override
    public void showErrorLicenseRetrievedMessage(LkmsException e) {

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

    private void checkLicense(){
        ILicenseManager licenseManager= BioSdk.createLicenseManager(getApplicationContext());
        try {
            ILkmsLicense license = licenseManager.retrieveLicense(getApplicationContext());
            onLicenseRetrieved(license);
            Log.d(TAG, "checkLicense: License Activated");
        } catch (LkmsNoLicenseAvailableException e) {
            Toast.makeText(getApplicationContext(),"Realizando peticion de licencia. Por favor espere...", Toast.LENGTH_LONG).show();
            licensePresenter.createLicense();
        }
    }

    private boolean isConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    protected void hideBars() {
        this.getWindow().setFlags(1024, 1024);
        Window var10000 = this.getWindow();
        Intrinsics.checkExpressionValueIsNotNull(var10000, "window");
        View decorView = var10000.getDecorView();
        int uiOptions = 6;
        Intrinsics.checkExpressionValueIsNotNull(decorView, "decorView");
        decorView.setSystemUiVisibility(uiOptions);
    }
}
