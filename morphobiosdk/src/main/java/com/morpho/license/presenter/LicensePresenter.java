package com.morpho.license.presenter;

import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsException;
import com.morpho.lkms.android.sdk.lkms_core.license.ILkmsLicense;
import com.morpho.license.interactor.LicenseInteractor;

/**
 * Created by alfredohernandez on 01/06/17.
 * mzr
 */

public class LicensePresenter implements LicenseInteractor.OnLicenseRetrievedListener{

    private View view;
    protected LicenseInteractor licenseInteractor;

    public LicensePresenter(LicenseInteractor licenseInteractor) {
        this.licenseInteractor = licenseInteractor;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void createLicense(){
        if(view!=null){
            view.showProgressBar();
        }
        licenseInteractor.createLicense(this);
    }

    // Methods implemented
    @Override
    public void onPreExecute() {
        if(view!=null){
            view.onPreExecute();
        }
    }

    @Override
    public void onLicenseRetrievedSuccess(ILkmsLicense license) {
        if(view!=null){
            view.hideProgressBar();
            view.showSuccessLicenseRetrievedMessage(license);
            view.onLicenseRetrieved(license);
        }
    }

    @Override
    public void onLicenseRetrievedFailed(LkmsException e) {
        if(view!=null){
            view.hideProgressBar();
            view.showErrorLicenseRetrievedMessage(e);
        }
    }

    public interface View{
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
