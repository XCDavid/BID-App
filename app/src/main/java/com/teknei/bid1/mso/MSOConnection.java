package com.teknei.bid1.mso;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;

import com.teknei.bid1.activities.FingerPrintsActivity;
import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.CallbackMask;
import com.morpho.morphosmart.sdk.CallbackMessage;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.MorphoImage;
//import com.teknei.aescobar.helloworld.MainActivity;
import com.teknei.bid1.tools.TKN_MSO_CODES;
import com.teknei.bid1.tools.TKN_MSO_ERROR;

import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

public class MSOConnection extends FingerPrintsActivity implements Observer, MSOShower {

    MorphoDevice morphoDevice;

//    private String fingerPrint;

    private Bitmap imageBmp;

//    private String coded;

    private String sensorName;

    private Handler mHandler = new Handler();

    private MSOShower msoShower;

    private String strMessage = null;

    //    private MainActivity activity;
    private Activity activity;

    private int callbackCmd = CallbackMask.MORPHO_CALLBACK_IMAGE_CMD.getValue()
            | CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue()
            | CallbackMask.MORPHO_CALLBACK_COMMAND_CMD.getValue()
            | CallbackMask.MORPHO_CALLBACK_CODEQUALITY.getValue()
            | CallbackMask.MORPHO_CALLBACK_DETECTQUALITY.getValue();

    private static MSOConnection singleton;

    public static MSOConnection getInstance() {
        if (singleton == null)
            singleton = new MSOConnection();

        return singleton;
    }

//    public String getCoded(){
//        return coded;
//    }


    public Bitmap getBitMap()
    {
        return imageBmp;
    }


//    public String getString()
//    {
//        return fingerPrint;
//    }


    private MSOConnection() {
        morphoDevice = new MorphoDevice();
    }


    public void setMsoShower(MSOShower msoShower) {
        this.msoShower = msoShower;
    }


    public int getCallbackCmd() {

        return callbackCmd;
    }

    public void tkn_mso_get_usb_permission(Activity activity) throws TKN_MSO_ERROR {

        USBManager.getInstance().initialize(activity, "com.morpho.morphosample.USB_ACTION");

        if (USBManager.getInstance().isDevicesHasPermission() != true) {
            throw new TKN_MSO_ERROR(TKN_MSO_CODES.USB_PERMISSIONS_NOT_ALLOWED);
        }


    }

    public boolean tkn_mso_connect() throws TKN_MSO_ERROR {

        boolean res = false;
        Integer nbUsbDevice = new Integer(0);

        int ret = morphoDevice.initUsbDevicesNameEnum(nbUsbDevice);

        if (ret != ErrorCodes.MORPHO_OK)
            throw new TKN_MSO_ERROR(TKN_MSO_CODES.USB_NOT_INIT);

        if (nbUsbDevice < 1)
            throw new TKN_MSO_ERROR(TKN_MSO_CODES.USB_DEVICES_ZERO);

        sensorName = morphoDevice.getUsbDeviceName(0);
        if (nbUsbDevice == 2)
            sensorName = morphoDevice.getUsbDeviceName(1);

        ret = morphoDevice.openUsbDevice(sensorName, 0);

        if (ret != 0)
            throw new TKN_MSO_ERROR(TKN_MSO_CODES.USB_DEVICE_NOT_OPENED);

        res = true;
        return res;

    }

    public void tkn_mso_capture(final Activity context) throws TKN_MSO_ERROR {

        boolean res = false;

        try {
            tkn_mso_get_usb_permission(context);
        } catch (TKN_MSO_ERROR e) {
            throw e;
        }


        try {
             //Thread.sleep(400);
            res = tkn_mso_connect();
            if(res != true){
             Thread.sleep(1000);
                res = tkn_mso_connect();
                if(res != true)
                {
                    Thread.sleep(3500);
                    tkn_mso_connect();
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
            throw new TKN_MSO_ERROR(TKN_MSO_CODES.THREAD_INTERRUPTED);
        }

//        if(res == true) {
            final Thread commandThread = (new Thread(new Runnable() {
                @Override
                public void run() {


        int timeOut = 20;
        int acquisitionThreshold = 0;
        CompressionAlgorithm compressAlgo;
        int compressRate = 10;
        int detectModeChoice;
        LatentDetection latentDetection;
        MorphoImage morphoImage = new MorphoImage();

        int callbackCmd = MSOConnection.getInstance().getCallbackCmd();

        callbackCmd &= ~CallbackMask.MORPHO_CALLBACK_ENROLLMENT_CMD.getValue();

        // ---------- Recomendado
        compressAlgo = CompressionAlgorithm.MORPHO_COMPRESS_WSQ;

        // ---------- Imagen sin comprimir
        //compressAlgo = CompressionAlgorithm.MORPHO_NO_COMPRESS;

        // ---------- Recomendado con dispositivos RS232
        //compressAlgo = CompressionAlgorithm.MORPHO_COMPRESS_V1;


        detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue();

        detectModeChoice |= DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();

        latentDetection = LatentDetection.LATENT_DETECT_DISABLE;

        final int ret = morphoDevice.getImage(timeOut, acquisitionThreshold, compressAlgo, compressRate, detectModeChoice, latentDetection, morphoImage, callbackCmd, MSOConnection.getInstance());

        if (msoShower != null)
            msoShower.updateImageView(morphoImage.getCompressedImage(), ret);


                }
            }));
            commandThread.start();


//        }else{
//            if (msoShower != null)
//                msoShower.updateImageView(null, ErrorCodes.MORPHOERR_USB_PERMISSION_DENIED);
//        }

    }


    public synchronized void update(Observable o, Object arg) {
        try {
            // convert the object to a callback back message.
            CallbackMessage message = (CallbackMessage) arg;

            int type = message.getMessageType();

            switch (type) {

                case 1:
                    // message is a command.
                    Integer command = (Integer) message.getMessage();

                    // Analyze the command.
                    switch (command) {
                        case 0:
                            strMessage = "move-no-finger";
                            break;
                        case 1:
                            strMessage = "move-finger-up";
                            break;
                        case 2:
                            strMessage = "move-finger-down";
                            break;
                        case 3:
                            strMessage = "move-finger-left";
                            break;
                        case 4:
                            strMessage = "move-finger-right";
                            break;
                        case 5:
                            strMessage = "press-harder";
                            break;
                        case 6:
                            strMessage = "move-latent";
                            break;
                        case 7:
                            strMessage = "remove-finger";
                            break;
                        case 8:
                            strMessage = "finger-ok";

                    }

                    if (msoShower != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public synchronized void run() {
                                msoShower.updateSensorMessage(strMessage);
                            }
                        });
                    }

                    break;
                case 2:

                    // message is a low resolution image, display it.
                    byte[] image = (byte[]) message.getMessage();

                    MorphoImage morphoImage = MorphoImage.getMorphoImageFromLive(image);
                    int imageRowNumber = morphoImage.getMorphoImageHeader().getNbRow();
                    int imageColumnNumber = morphoImage.getMorphoImageHeader().getNbColumn();
                    imageBmp = Bitmap.createBitmap(imageColumnNumber, imageRowNumber, Bitmap.Config.ALPHA_8);

                    imageBmp.copyPixelsFromBuffer(ByteBuffer.wrap(morphoImage.getImage(), 0, morphoImage.getImage().length));



//                    morphoImage.getImage();
//
//                    morphoImage.getCompressedImage();
//
//                    fingerPrint = imageBmp.toString();
//
//                    coded = Base64.encode(image);
//
//                    Log.e("a ver", "bytes:"+image);
//                    Log.e("a ver", "bytes L:"+image.length);
//                    Log.e("a ver", "SHOW ME:"+coded);
//                    File f = new File(Environment.getExternalStorageDirectory()
//                            + File.separator + "finger.jpg");
//                    if (f.exists()) {
//                        f.delete();
//                        f = new File(Environment.getExternalStorageDirectory() + File.separator + "finger.jpg");
//                    }
//                    try {
//                        f.createNewFile();
//                        //write the bytes in file
//                        FileOutputStream fo = new FileOutputStream(f);
//                        fo.write(image);
//                        // remember close de FileOutput
//                        fo.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                    if (msoShower != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public synchronized void run() {
                                msoShower.updateImage(imageBmp);
                            }
                        });
                    }
                    break;
                case 3:
                    if (msoShower != null) {
                        // message is the coded image quality.
                        final Integer quality = (Integer) message.getMessage();
                        mHandler.post(new Runnable() {
                            @Override
                            public synchronized void run() {
                                msoShower.updateSensorProgressBar(quality);
                            }
                        });

                    }
                    break;
            }
        } catch (Exception e) {
            if (msoShower != null)
                msoShower.showAlert("" + e.getMessage());
        }
    }

}
