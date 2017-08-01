package com.teknei.bid.mobbsign;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.mobbeel.mobbsign.license.LicenseStatusListener;
import com.mobbeel.mobbsign.license.MobbSignLicenseResult;
import com.mobbeel.mobbsign.view.MobbSignBackButtonPressedListener;
import com.mobbeel.mobbsign.view.MobbSignCustomizationProperties;
import com.mobbeel.mobbsign.view.MobbSignDocumentShownListener;
import com.mobbeel.mobbsign.view.MobbSignDocumentSignedListener;
import com.mobbeel.mobbsign.view.MobbSignErrorOccurredListener;
import com.mobbeel.mobbsign.view.MobbSignProcessEndListener;
import com.mobbeel.mobbsign.view.MobbSignResultCodes;
import com.mobbeel.mobbsign.view.MobbSignView;
import com.teknei.bid.R;
import com.teknei.bid.utils.SharedPreferencesUtils;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.PEMReader;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MobbSignActivity extends AppCompatActivity {

    private MobbSignView mobbSignView;
    public static final String EXTRA_DOC_ID = "EXTRA_DOC_ID";
    public static final String EXTRA_DOCUMENT = "EXTRA_DOCUMENT";
    private final static String PDF_SIGNED_FILE_NAME = "signedDocument.pdf";
    private ByteArrayInputStream inputStream;
    private String docId;
    private String idOperation;
//    DocumentController documentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        documentController = new DocumentController(PreferenceManager.getDefaultSharedPreferences(this).getString("url", ""));

        byte[] bytes = getIntent().getByteArrayExtra(EXTRA_DOCUMENT);
        if (bytes != null) {
            inputStream = new ByteArrayInputStream(bytes);
        }
        docId = getIntent().getStringExtra(EXTRA_DOC_ID);
        idOperation = getIntent().getStringExtra("id_operation");

        // Here is how you could customize MobbSignView (see its javadoc for the full list of customization options available)
        Bundle customizationBundle = new Bundle();
        customizationBundle.putBoolean(MobbSignCustomizationProperties.DEVICE_POSITION_ENABLED, false);
        customizationBundle.putString(MobbSignCustomizationProperties.PDF_FONT_FILENAME, "DroidSerif-Regular.ttf");
        mobbSignView = new MobbSignView(this, customizationBundle);

        setContentView(mobbSignView);

        String licenseMS = SharedPreferencesUtils.readFromPreferencesString(this, SharedPreferencesUtils.MOBBSIGN_LICENSE, getString(R.string.default_license_mobbsign));
        mobbSignView.configureLicense(licenseMS, new LicenseStatusListener() {
            @Override
            public void onLicenseStatusChecked(Date licenseValidTo, MobbSignLicenseResult mobbSignLicenseResult) {
                if (mobbSignLicenseResult == MobbSignLicenseResult.VALID || mobbSignLicenseResult == MobbSignLicenseResult.GRACE_PERIOD) {
                    String validTo;
                    if (licenseValidTo != null) {
                        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                        validTo = df.format(licenseValidTo);
                    } else {
                        validTo = "forever";
                    }
                    Toast.makeText(MobbSignActivity.this, "License is valid until " + validTo + " (" + mobbSignLicenseResult.toString() + ")", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MobbSignActivity.this, "License problem, not a valid license (" + mobbSignLicenseResult.toString() + ")", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mobbSignView.setOnDocumentShownListener(new MobbSignDocumentShownListener() {
            @Override
            public void onDocumentShown(boolean editable) {
                Log.i("MobbSignDemo", "On Document Shown!");
                Toast.makeText(MobbSignActivity.this, "On Document Shown!", Toast.LENGTH_LONG).show();
            }
        });

        mobbSignView.setOnDocumentSignedListener(new MobbSignDocumentSignedListener() {
            @Override
            public void onDocumentSigned(byte[] document, byte[] encryptedSignatureData, Bitmap signatureBitmap, int signaturePage, Rect signatureCoord) {
                Log.d("MobbSignSimple", document != null ? "Document is not null!" : "Document is NULL");
            }
        });

        mobbSignView.setOnErrorOccurredListener(new MobbSignErrorOccurredListener() {
            @Override
            public void onErrorOccurred(int errorCode, HashMap<String, Object> errorDetails) {
                Log.d("MobbSignDemo", "onErrorOcurred: " + errorCode);
                switch (errorCode) {
                    case MobbSignResultCodes.ERROR_LICENSE_INVALID:
                        Toast.makeText(MobbSignActivity.this, "License has not been checked successfully. Please review your network configuration",
                                Toast.LENGTH_LONG).show();
                        break;
                    case MobbSignResultCodes.ERROR_VERIFICATION_MODULE_NOT_PRESENT:
                        Toast.makeText(MobbSignActivity.this, "Verification module not present", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        mobbSignView.setOnBackButtonPressedListener(new MobbSignBackButtonPressedListener() {
            @Override
            public void onBackButtonPressed() {
                finish();
            }
        });

        if (configureEncryptionKey(mobbSignView)) {
            mobbSignView.loadPDFDocument(inputStream, "Teknei document");

            mobbSignView.setOnProcessEndListener(new MobbSignProcessEndListener() {
                @Override
                public void onProcessEnd(byte[] bytes) {
//                    uploadDocument(bytes);
                    File file = new File(Environment.getExternalStorageDirectory()
                            + File.separator + "contract_" + idOperation + ".pdf");
                    try {
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                        bos.write(bytes);
                        bos.flush();
                        bos.close();

//                        File file2 = new File(Environment.getExternalStorageDirectory()
//                                + File.separator + "contract_" + idOperation + "_sign2.pdf");
//                        FileOutputStream out = new FileOutputStream(file2);
//                        out.write(bytes);
//                        out.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Toast.makeText(MobbSignActivity.this, "opcion upload document", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(this, "Cannot get encryption certificate data. Signature feature will NOT work", Toast.LENGTH_LONG).show();
            finish();
        }

    }

//    private void uploadDocument(byte[] bytes) {
//        documentController.uploadDocument(/*docId, */docId, bytes, new RestAPI.UploadDocumentCallback() {
//            @Override
//            public void onUploadDocumentSuccess(int code, String message) {
//                Log.d(TAG, "onUploadDocumentSuccess");
//                createToast(R.string.txt_doc_uploaded);
//                finish();
//            }
//
//            @Override
//            public void onUploadDocumentFailure(int code, String message) {
//                Log.d(TAG, "onUploadDocumentFailure: code: " + code + "-  message: " + message);
//
//            }
//        });
//    }

    protected boolean configureEncryptionKey(MobbSignView mobbSignView) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            PEMReader reader = new PEMReader(new InputStreamReader(getAssets().open("development-public.key")));
            Object obj;
            PublicKey publicKey = null;
            while (((obj = reader.readObject()) != null) && (publicKey == null)) {
                if (obj instanceof X509Certificate) {
                    publicKey = ((X509Certificate) obj).getPublicKey();
                } else {
                    if (obj instanceof PublicKey) {
                        publicKey = (PublicKey) obj;
                    }
                }
            }
            if (publicKey != null) {
                mobbSignView.setEncryptionKey(publicKey);
                return true;
            }
            Log.e("MobbSignDemo", "Cannot find public key in the file");
            return false;
        } catch (IOException e) {
            Log.e("MobbSignDemo", "Cannot access public key file");
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mobbSignView != null) {
            mobbSignView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mobbSignView != null) {
            mobbSignView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mobbSignView != null) {
            mobbSignView.destroy();
            mobbSignView = null;
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (mobbSignView != null) {
            mobbSignView.purgeMemory();
        }
    }
}
