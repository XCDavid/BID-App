package com.mobbeel.mobbscan.simple.tools;

/**
 * Created by MDCALDERON on 28/06/2017.
 */



public class TKN_MSO_ERROR extends Exception {
    private int errorCode;
    private String errorMsg;

    public TKN_MSO_ERROR(TKN_MSO_CODES code) {
        this.errorMsg = code.getMsg();
        this.errorCode = code.getId();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}



