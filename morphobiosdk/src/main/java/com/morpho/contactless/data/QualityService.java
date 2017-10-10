package com.morpho.contactless.data;

import android.net.ParseException;
import android.util.Log;

/**
 * Created by J. Alfredo Hernández Alarcón on 07/06/17.
 * QualityService
 */
public class QualityService {
    private static final String TAG = "QualityService";

    /**
     * Verifiy the quality for an Image
     * @param minQuality MinQuality
     * @param globalImageQuality The image(s) quality (Global Image Quality)
     * @param callback A Callback
     */
    public static void verify(int minQuality, int globalImageQuality, QualityCallback callback){
        try{
            Log.i(TAG, "Verify Quality: " + minQuality);
            if (globalImageQuality >= minQuality){
                callback.onQualitySuccess(true);
            }else {
                callback.onQualitySuccess(false);
            }
        }catch (ParseException e){
            callback.onQualityFail(e);
        }
    }

    /**
     * Quality Callback
     */
    public interface QualityCallback {
        void onQualitySuccess(boolean goodQuality);
        void onQualityFail(Exception e);
    }
}

