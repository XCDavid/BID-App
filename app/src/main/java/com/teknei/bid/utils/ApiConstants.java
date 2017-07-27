package com.teknei.bid.utils;

/**
 * Created by Desarrollo on 10/07/2017.
 */

public class ApiConstants {
    /**
     * HTTP GET USER
     * user from TAS validate
     */
    public static final int ACTION_LOG_OUT = 0;
    public static final int ACTION_TRY_AGAIN = 1;
    public static final int ACTION_CANCEL_OPERATION = 2;
    public static final int ACTION_BLOCK_CANCEL_OPERATION = 3;
    public static final int ACTION_GO_NEXT = 4;
    public static final int ACTION_TRY_AGAIN_CANCEL = 5;
    public static final String LOG_IN_USER = "login";
    public static final String LOG_OUT_USER = "logout";
    public static final String METHOD_CANCEL_OPERATION = "rest/v1/cancel?operationId=";
    public static final String METHOD_START_OPERATION = "rest/v1/start";
    public static final String METHOD_CREDENTIALS = "rest/v1/credential";
    public static final String METHOD_FACE = "rest/v1/face";
    public static final String METHOD_DOCUMENT = "rest/v1/comprobante";
    public static final String METHOD_FINGERS = "rest/v1/minucias";
    public static final String METHOD_PAY_CONFIRM = "rest/v1/end";

}
