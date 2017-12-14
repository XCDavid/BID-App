package com.morpho.contactless.presenter;

import com.morpho.contactless.interactor.FingersInteractor;
import com.morpho.mph_bio_sdk.android.sdk.msc.data.results.MorphoImage;

import java.util.List;

/**
 * Created by alfredohernandez on 07/06/17.
 * mzr
 */

public class FingersPresenter
        implements FingersInteractor.OnImagesRetrievedListener ,
        FingersInteractor.OnImagesQualityListener{

    protected FingersInteractor interactor;
    protected View view;

    public FingersPresenter(FingersInteractor interactor) {
        this.interactor = interactor;
    }

    public void getImagesFingers(List<MorphoImage> imageList){
        if(view!=null){
            view.showLoader();
            interactor.getImagesFingers(imageList, this);
        }
    }

    public void getFingersQuality(int quality){
        if(view!=null){
            view.showLoader();
            interactor.getFingersQuality(quality, this);
        }
    }

    public void setView(View view) {
        this.view = view;
    }

    public interface View{
        void showLoader();
        void hideLoader();
        void showImages(List<byte[]> jpgImages);
        void showJPGImageError(Exception e);
        void showImageQuality(int quality);
        void showBadImageQuality(int quality);
    }

    /// Fingers retrieved listener
    @Override
    public void onSuccessJPGImages(List<byte[]> jpgImages) {
        if (view != null) {
            view.hideLoader();
            view.showImages(jpgImages);
        }
    }

    @Override
    public void onFailure(Exception e) {
        if (view != null) {
            view.hideLoader();
            view.showJPGImageError(e);
        }
    }

    /// Fingers Quality listener
    @Override
    public void onGoodQuality(int quality) {
        if (view != null) {
            view.showImageQuality(quality);
            view.hideLoader();
        }
    }

    @Override
    public void onBadQuality(int quality) {
        if (view != null) {
            view.showBadImageQuality(quality);
            view.hideLoader();
        }
    }
}
