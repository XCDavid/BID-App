package com.morpho.license.presenter;

import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsException;
import com.morpho.lkms.android.sdk.lkms_core.license.ILkmsLicense;
import com.morpho.license.interactor.LicenseInteractor;

import java.lang.ref.WeakReference;

/**
 * Created by alfredohernandez on 01/06/17.
 * mzr
 */

public class LicensePresenter implements LicenseInteractor.OnLicenseRetrievedListener{

    private WeakReference<Ui> ui;
    protected LicenseInteractor licenseInteractor;

    public LicensePresenter(LicenseInteractor licenseInteractor) {
        this.licenseInteractor = licenseInteractor;
    }

    public void setUi(Ui ui) {
        this.ui = new WeakReference<>(ui);
    }

    public void createLicense(){
        if(ui!=null){
            ui.get().showProgressBar();
        }
        licenseInteractor.createLicense(this);
    }

    public void stop(){
        this.ui.clear();
    }

    public void terminate(){
        ui = null;
    }

    // Methods implemented
    @Override
    public void onPreExecute() {
        if(ui!=null){
            ui.get().onPreExecute();
        }
    }

    @Override
    public void onLicenseRetrievedSuccess(ILkmsLicense license) {
        if(ui!=null){
            ui.get().hideProgressBar();
            ui.get().showSuccessLicenseRetrievedMessage(license);
            ui.get().onLicenseRetrieved(license);
        }
    }

    @Override
    public void onLicenseRetrievedFailed(LkmsException e) {
        if(ui!=null){
            ui.get().hideProgressBar();
            ui.get().showErrorLicenseRetrievedMessage(e);
        }
    }

    public interface Ui{
        /**
         * This method is called before getting license.
         */
        void onPreExecute();

        /**
         * This method is called when get the license.
         * You can display a progress bar
         */
        void showProgressBar();

        /**
         * This method is called after license is retrieved.
         * You can hide a progress bar.
         */
        void hideProgressBar();

        /**
         * Is called when the license is retrieved successfully.
         * You can show the Profile ID, features, etc.
         * @param license The license
         */
        void showSuccessLicenseRetrievedMessage(ILkmsLicense license);

        /**
         * This method is called when an error happened
         * @param e The error exception
         */
        void showErrorLicenseRetrievedMessage(LkmsException e);

        /**
         * This method is called after license is retrieved.
         * You need to activate your license on device
         * @param license License
         */
        void onLicenseRetrieved(ILkmsLicense license);
    }
}
