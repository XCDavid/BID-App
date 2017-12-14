package com.morpho.license.interactor;

import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsException;
import com.morpho.lkms.android.sdk.lkms_core.license.ILkmsLicense;
import com.morpho.common.data.LicenseService;

/**
 * Created by alfredohernandez on 01/06/17.
 * mzr
 */

public class LicenseInteractor{
    protected LicenseService licenseService;

    public LicenseInteractor(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public void createLicense(final OnLicenseRetrievedListener listener){
        licenseService.createLicense(new LicenseService.OnGetLicenseListener() {
            @Override
            public void onPreExecuteLicenseRetrieve() {
                listener.onPreExecute();
            }

            @Override
            public void onLicenseRetrieved(ILkmsLicense license) {
                listener.onLicenseRetrievedSuccess(license);
            }

            @Override
            public void onLicenseRetrievedError(LkmsException e) {
                listener.onLicenseRetrievedFailed(e);
            }
        });
    }


    public interface OnLicenseRetrievedListener{
        void onPreExecute();
        void onLicenseRetrievedSuccess(ILkmsLicense license);
        void onLicenseRetrievedFailed(LkmsException e);
    }
}
