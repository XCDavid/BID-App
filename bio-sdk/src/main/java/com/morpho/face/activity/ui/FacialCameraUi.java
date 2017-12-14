package com.morpho.face.activity.ui;

import com.morpho.mph_bio_sdk.android.sdk.msc.data.CaptureError;

/**
 * Created by alfredohernandez on 15/08/17.
 * Morpho Android
 */

public interface FacialCameraUi  {
    /**
     * You can use this method to update a progress UI element
     * @param current The current challenge
     */
    void updateStepProgress(int current);

    /**
     * Returns the total number of steps for challenges
     * @param totalSteps
     */
    void totalSteps(int totalSteps);

    /**
     * Displays messages for helping to take a correct capture
     * @param message Message Helper
     */
    void showFeedback(String message);

    /**
     * If an error exists (like bad movement, bad face capture, etc.) this method is called.
     * @param captureError Capture Error
     */
    void onCaptureError(CaptureError captureError);

    /**
     * Called when capture finish
     */
    void onCaptureFinished();

    /**
     * Called after capture finished without errors
     */
    void onCaptureFinishedWithoutErrors();
}
