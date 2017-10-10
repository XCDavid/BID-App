package com.morpho.contactless.interactor;

import com.morpho.contactless.data.QualityService;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.results.MorphoImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfredohernandez on 07/06/17.
 * mzr
 */

public class FingersInteractor {

    private List<byte[]> jpgImages = null;

    public void getImagesFingers(List<MorphoImage> imageList, OnImagesRetrievedListener listener){
        jpgImages = new ArrayList<>();
        for (MorphoImage image: imageList) {
            try{
                jpgImages.add(image.getJPEGImage());
            }catch (Exception e){
                listener.onFailure(e);
            }
        }
        listener.onSuccessJPGImages(jpgImages);
    }

    public void getFingersQuality(final int quality, final OnImagesQualityListener listener) {
        QualityService.verify(30, quality, new QualityService.QualityCallback() {
            @Override
            public void onQualitySuccess(boolean goodQuality) {
                if(goodQuality){
                    listener.onGoodQuality(quality);
                }else{
                    listener.onBadQuality(quality);
                }
            }

            @Override
            public void onQualityFail(Exception e) {
                listener.onFailure(e);
            }
        });

    }

    public interface OnImagesQualityListener{
        void onFailure(Exception e);
        void onGoodQuality(int quality);
        void onBadQuality(int quality);
    }


    public interface OnImagesRetrievedListener{
        void onSuccessJPGImages(List<byte[]> jpgImages);
        void onFailure(Exception e);
    }

}
