package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 14/12/2017.
 */

public class ValidateOtpDTO {

    @SerializedName("idClient")
    private Long operationID;

    @SerializedName("otp")
    private String otpCode;

    public ValidateOtpDTO() {
        this.operationID = 0L;
        this.otpCode  = "";
    }

    public ValidateOtpDTO(Long clientID) {
        this.operationID = clientID;
        this.otpCode = "";
    }

    public ValidateOtpDTO(Long clientID, String otpCode) {
        this.operationID = clientID;
        this.otpCode = otpCode;
    }

    public Long getOperationID() {
        return operationID;
    }

    public void setOperationID(Long operationID) {
        this.operationID = operationID;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
