package com.teknei.bid.tools;

/**
 * Created by G509494 on 15/03/2016.
 */
public enum DeviceDetectionMode {
    /**
     * Using MorphoSmart Sdk to enumerate connected devices.
     * The user can call MorphoDevice.initUsbDevicesNameEnum and MorphoDevice.getUsbDeviceName methods to select a device.
     * After device detection, the user can use MorphoDevice.openUsbDevice with serial number to open a connection to the device.
     */
    SdkDetection,
    /**
     * Using Android Usb host api to enumerate connected devices.
     * The user can use MorphoDevice.openUsbDeviceFD to open a connection to the device.
     */
    UserDetection
}
