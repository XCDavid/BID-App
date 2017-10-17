package com.teknei.bid.utils;

import android.app.Activity;
import android.content.Context;

import com.teknei.bid.R;

/**
 * Created by Desarrollo on 10/07/2017.
 */

public class ApiConstants {
    public static final int ACTION_LOG_OUT = 0;
    public static final int ACTION_TRY_AGAIN = 1;
    public static final int ACTION_CANCEL_OPERATION = 2;
    public static final int ACTION_BLOCK_CANCEL_OPERATION = 3;
    public static final int ACTION_GO_NEXT = 4;
    public static final int ACTION_TRY_AGAIN_CANCEL = 5;
    public static final int ACTION_GO_STEP = 6;
    public static final int ACTION_TRY_AGAIN_CONTINUE = 7;
    public static final String LOG_IN_USER = "login";
    public static final String LOG_OUT_USER = "logout";
    public static final String METHOD_CANCEL_OPERATION = "rest/v2/cancel?operationId=";
    public static final String METHOD_START_OPERATION = "rest/v2/start";
    public static final String METHOD_CREDENTIALS = "rest/v2/credential";
    public static final String METHOD_FACE = "rest/v2/face";
    public static final String METHOD_DOCUMENT = "rest/v2/comprobante";
    public static final String METHOD_FINGERS = "rest/v2/minucias";
    public static final String METHOD_PAY_CONFIRM = "rest/v2/end";
    public static final String METHOD_GET_CONTRACT = "rest/v2/contrato/";
    public static final String METHOD_SEND_CONTRACT = "rest/v2/contrato/add/";
    public static final String METHOD_CHECK_PENDING_OPERATION = "rest/v2/step";
    public static final String METHOD_GET_PENDING_OPERATION = "rest/v2/detail";
    public static final String METHOD_GET_TIMESTAMP = "rest/v2/search/customer/ts/";

    //Strings values
    public static final String STRING_INE = "INE";
    public static final String STRING_IFE = "IFE";
    public static final String STRING_PASSPORT = "PASAPORTE";
    //Icar INE/IFE Values
    public static final String ICAR_NAME = "name";
    public static final String ICAR_SURNAME = "surname";
    public static final String ICAR_FIRST_SURNAME = "firstSurname";
    public static final String ICAR_SECOND_SURNAME = "secondSurname";
    public static final String ICAR_ADDRESS = "address";
    public static final String ICAR_MRZ = "MRZ";
    public static final String ICAR_OCR = "CRC_SECTION";
    public static final String ICAR_VALIDITY = "vigencia";
    public static final String ICAR_CURP = "curp";
    public static final String ICAR_PASSPORT_VALIDITY = "dateOfExpiry";

    //Excepciones en captura identificativa (INE/IFE/PASAPORTE)
    private static final int errorResponse10001 = 10001;  //ICAR. ERROR AL DIGITALIZAR O VERIFICAR EL DOCUMENTO IDENTIFICATIVO.
    private static final int errorResponse10002 = 10002;  //TAS. ERROR AL ALMACENAR EL DOCUMENTO IDENTIFICATIVO EN EXPEDIENTE DIGITAL.');
    private static final int errorResponse10003 = 10003;  //B.D. ERROR AL PERSISTIR PROCESO IDENTIFICATIVO SOBRE BASE DE DATOS.');

    //Captura Facial
    private static final int errorResponse20001 = 20001;  //TAS. ERROR AL ALMACENAR FOTOGRAFÍA FACIAL EN EXPEDIENTE DIGITAL.');
    private static final int errorResponse20002 = 20002;  //B.D. ERROR AL PERSISTIR PROCESO DE CAPTURA FACIAL SOBRE BASE DE DATOS.');

    //Comprobante de Domicilio
    private static final int errorResponse30001 = 30001; //KOFAX. ERROR AL DIGITALIZAR O VERIFICAR EL COMPROBANTE DE DOMICILIO.');
    private static final int errorResponse30002 = 30002; //TAS. ERROR AL ALMACENAR COMPROBANTE DE DOMICILIO EN EXPEDIENTE DIGITAL.');
    private static final int errorResponse30003 = 30003; //B.D. ERROR AL PERSISTIR PROCESO DE COMPROBANTE DE DOMICILIO SOBRE BASE DE DATOS.');

    //Registro Biométrico
    private static final int errorResponse40001 = 40001; //'TAS. ERROR AL ALMACENAR MINUCIAS EN EXPEDIENTE DIGITAL.');
    private static final int errorResponse40002 = 40002; //'MBSS. ERROR AL ALMACENA MINUCIAS DACTILARES Y FACIALES EN MOTOR BIOMÉTRICO.');
    private static final int errorResponse40003 = 40003; //'MBSS. ERROR, BIOMETRICOS DACTILARES O FACIALES DUPLICADOS.');
    private static final int errorResponse40004 = 40004; //'B.D. ERROR AL PERSISTIR PROCESO DE REGISTRO BIOMÉTRICO SOBRE BASE DE DATOS.

    public static String managerErrorServices (int msgError, Activity activityOrigin) {

        String errorMessage;

        switch (msgError) {

            case errorResponse10001:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_10001);
                break;

            case errorResponse10002:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_10002);
                break;

            case errorResponse10003:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_10003);
                break;

            case errorResponse20001:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_20001);
                break;

            case errorResponse20002:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_20002);
                break;

            case errorResponse30001:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_30001);
                break;

            case errorResponse30002:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_30002);
                break;

            case errorResponse30003:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_30003);
                break;

            case errorResponse40001:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_40001);
                break;

            case errorResponse40002:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_40002);
                break;

            case errorResponse40003:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_40003);
                break;

            case errorResponse40004:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_40004);
                break;
            default:
                errorMessage = msgError + " - " + activityOrigin.getString(R.string.message_ws_response_fail);
                break;
        }

        return errorMessage;
    }

}
