package com.morpho.mrz.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.morpho.common.view.BasicActivity;
import com.morpho.mph_bio_sdk.android.sdk.BioSdk;
import com.morpho.mph_bio_sdk.android.sdk.msc.async.MscAsyncCallbacks;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.IDocumentCaptureHandler;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.data.DocCaptureInfo;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.data.DocumentCaptureError;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.data.IDocumentCaptureOptions;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.data.mrz.IMRZLine;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.data.mrz.IMrzRecord;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.data.results.DocumentImage;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.data.results.MorphoDocumentRegion;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.listeners.DocumentCaptureFeedbackListener;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.listeners.DocumentCaptureListener;
import com.morpho.mph_bio_sdk.android.sdk.msc.document.listeners.DocumentCaptureTrackingListener;
import com.morpho.mph_bio_sdk.android.sdk.msc.error.BioCaptureHandlerError;
import com.morpho.mrz.R;
import com.morpho.mrz.data.model.Point;

import java.util.List;

import butterknife.ButterKnife;
import morpho.urt.msc.mscengine.MSCEngine;
import morpho.urt.msc.mscengine.MorphoSurfaceView;

/**
 * Created by J. Alfredo Hernández Alarcón on 01/06/17.
 * AbstractDocumentActivity
 */

public abstract class AbstractDocumentActivity extends Activity
        implements BasicActivity,
        DocumentCaptureFeedbackListener,
        DocumentCaptureListener,
        DocumentCaptureTrackingListener{
    // TAG Name
    private final static String TAG = AbstractDocumentActivity.class.getSimpleName();

    protected MorphoSurfaceView cameraPreview;
    protected IDocumentCaptureHandler captureHandler;

    // Points
    protected Point p1 = new Point(), p2 = new Point();


    // Basic Activity
    /**
     * The layout resource used for this activity
     * @return the layout id associated to the layout used in the activity.
     */
    @LayoutRes
    protected abstract int getLayoutResID();

    /**
     * Every object annotated with {@link butterknife.BindView} its gonna injected trough butterknife
     */
    private void bindViews() {
        ButterKnife.bind(this);
    }

    @Override
    public void onPrepareActivity() {
        cameraPreview = (MorphoSurfaceView) findViewById(morphoSurfaceViewID());
        cameraPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(captureHandler!=null){
                    //Force a document capture
                    captureHandler.forceCapture();
                }
            }
        });

    }

    @Override
    public void onPreparePresenter() {

    }

    /**
     * The Morpho surface view id
     * @return
     */
    protected abstract int morphoSurfaceViewID();

    // ANDROID LIFECYCLE
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        bindViews();
        onPrepareActivity();
        onPreparePresenter();
    }

    @Override
    protected void onDestroy() {
        // Destroy surface view
        if (cameraPreview != null) {
            cameraPreview.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        IDocumentCaptureOptions captureOptions = configureCapture();
        BioSdk.createDocumentCaptureHandler(this, captureOptions, new MscAsyncCallbacks<IDocumentCaptureHandler>() {
            @Override
            public void onPreExecute() {
                Toast.makeText(AbstractDocumentActivity.this, getText(R.string.loading_capture), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(IDocumentCaptureHandler result) {
                onDocumentCaptureInitialized(result);
            }

            @Override
            public void onError(BioCaptureHandlerError e) {
                onErrorHandling(new IllegalStateException(e.name()));
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            if (captureHandler != null) {
                captureHandler.destroy();
                captureHandler = null;
            }
        }catch (Exception e){
            Log.e(TAG, "", e);
        }
        super.onPause();
    }

    /**
     * Error handling
     * @param e Error
     */
    protected void onErrorHandling(Exception e){
        Log.e(TAG, "", e);
        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * OnDocumentCaptureInitialized
     * @param result
     */
    private void onDocumentCaptureInitialized(IDocumentCaptureHandler result){
        Log.d(TAG, "MSC version: "+ MSCEngine.getMSCVersion().getVersionName());
        captureHandler = result;
        captureHandler.setDocCaptureFeedbackListener(this);
        captureHandler.setDocumentCaptureListener(this);
        captureHandler.setDocumentTrackingListener(this);
        captureHandler.startPreview();
        captureHandler.startCapture();
    }


    // setDocCaptureFeedbackListener methods...

    /**
     * Override this method to get a feedback when you make a capture
     * @param docCaptureInfo
     */
    @Override
    public void onCaptureInfo(DocCaptureInfo docCaptureInfo) {

    }

    // setDocCaptureFeedbackListener methods...

    @Override
    public void onMRZDocumentRead(List<IMRZLine> list, IMrzRecord iMrzRecord) {

    }

    /**
     * What to do when capture the image
     * @param documentImage
     */
    @Override
    public void onCaptureImageDocument(DocumentImage documentImage) {

    }

    /**
     * What to do when capture fails
     * @param documentImage Document image
     * @param s A message
     */
    @Override
    public void onCaptureFieldImageDocument(DocumentImage documentImage, String s) {

    }

    /**
     * What to do when capture fails
     * @param documentCaptureError Error
     */
    @Override
    public void onDocumentCaptureFailure(DocumentCaptureError documentCaptureError) {

    }

    /**
     * On capture finish
     */
    @Override
    public void onCaptureFinish() {

    }

    // Document capture tracking
    /**
     * This method make a document tracking.
     * @param trackingInfo
     */
    @Override
    public void onTracking(List<MorphoDocumentRegion> trackingInfo) {
        if(trackingInfo==null||trackingInfo.isEmpty()){
            return;
        }
        MorphoDocumentRegion morphoDocumentTracking = trackingInfo.get(0);
        Log.d(TAG, "Document Location: "+morphoDocumentTracking.getDocumentLocation());
        Log.d(TAG, "Point1: "+morphoDocumentTracking.getPoint1().x+", "+morphoDocumentTracking.getPoint1().y);
        Log.d(TAG, "Point2: "+morphoDocumentTracking.getPoint2().x+", "+morphoDocumentTracking.getPoint2().y);
        Log.d(TAG, "Point3: "+morphoDocumentTracking.getPoint3().x+", "+morphoDocumentTracking.getPoint3().y);
        Log.d(TAG, "Point4: "+morphoDocumentTracking.getPoint4().x+", "+morphoDocumentTracking.getPoint4().y);

        // Points
        try{
            p1.setX(morphoDocumentTracking.getPoint1().x);
            p1.setY(morphoDocumentTracking.getPoint1().y);
            p2.setX(morphoDocumentTracking.getPoint3().x);
            p2.setY(morphoDocumentTracking.getPoint3().y);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }


    // Capture handler configuration

    /**
     * Use this method to configure you capture handler
     * @return IDocumentCaptureOptions Options
     */
    protected abstract IDocumentCaptureOptions configureCapture();

}
