package com.morpho.common.data;

import android.content.Context;

import com.morpho.lkms.android.sdk.lkms_core.exceptions.LkmsException;
import com.morpho.lkms.android.sdk.lkms_core.license.ILkmsLicense;
import com.morpho.lkms.android.sdk.lkms_core.network.INetworkSettings;
import com.morpho.lkms.android.sdk.lkms_core.network.NetworkSettings;
import com.morpho.mph_bio_sdk.android.sdk.BioSdk;
import com.morpho.mph_bio_sdk.android.sdk.licence.async.BioSdkLicenceAsyncCallbacks;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by J. Alfredo Hernández Alarcón on 01/06/17.
 * License Service
 */

public class LicenseService {

    /**
     * Application context
     */
    private Context context;

    /**
     * Activation Server URL
     */
    private String activationServerUrl;

    /**
     * LKMS Url
     */
    private String lkmsUrl;

    /**
     * License Service
     * @param context The context
     * @param activationServerUrl Activation server URL
     * @param lkmsUrl LKMS URL
     */
    public LicenseService(Context context,String activationServerUrl, String lkmsUrl) {
        this.context = context;
        this.activationServerUrl = activationServerUrl;
        this.lkmsUrl = lkmsUrl;
    }

    /**
     * Call this method when you whant to create the License from LKMS Server
     */
    public void createLicense(final OnGetLicenseListener onGetLicenseListener){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(activationServerUrl)
                .get()
                .addHeader("Accept", "application/octet-stream")
                .addHeader("Content-type", "application/octet-stream")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                createLKMSLicense(response.body().bytes(), onGetLicenseListener);
            }
        });
    }

    /**
     * Create LKMS License
     * @param activationData The activation data retrieved from server
     */
    private void createLKMSLicense(byte[] activationData,final OnGetLicenseListener onGetLicenseListener){
        INetworkSettings networkSettings = new NetworkSettings();
        networkSettings.setTimeoutInSeconds(60);
        networkSettings.setSSLSocketFactory(null);
        networkSettings.setX509TrustManager(null);
        networkSettings.setHostNameVerifier(null);

        BioSdk.createLicenseManager(context).createLicense(context, lkmsUrl, networkSettings,
                activationData, new BioSdkLicenceAsyncCallbacks<ILkmsLicense>() {
            @Override
            public void onPreExecute() {
                onGetLicenseListener.onPreExecuteLicenseRetrieve();
            }

            @Override
            public void onSuccess(ILkmsLicense result) {
                onGetLicenseListener.onLicenseRetrieved(result);
            }

            @Override
            public void onError(LkmsException e) {
                onGetLicenseListener.onLicenseRetrievedError(e);
            }
        });
    }


    public interface OnGetLicenseListener{
        void onPreExecuteLicenseRetrieve();
        void onLicenseRetrieved(ILkmsLicense license);
        void onLicenseRetrievedError(LkmsException e);
    }

}
