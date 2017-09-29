package com.teknei.bid1.tools;

/**
 * Created by MDCALDERON on 28/06/2017.
 */

public enum TKN_MSO_CODES {

    USB_PERMISSIONS_NOT_ALLOWED(0x2001, "Error: USB Permissions not allowed"),
    USB_NOT_INIT(0x2002, "Error: USB not init"),
    USB_DEVICES_ZERO(0x2003, "Error: There are zero USB devices connected."),
    USB_DEVICE_NOT_OPENED(0x2004, "Error: Device Can't be opened"),
    THREAD_INTERRUPTED(0x2005, "Error: Thread was interrupted (it was waiting, sleeping or occupied)");


    private final int id;
    private final String msg;

    TKN_MSO_CODES(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public int getId() {
        return this.id;
    }

    public String getMsg() {
        return this.msg;
    }
}
