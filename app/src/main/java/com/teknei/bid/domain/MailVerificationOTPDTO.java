package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 15/12/2017.
 */

public class MailVerificationOTPDTO {

    @SerializedName("idClient")
    private Long operationID;

    public MailVerificationOTPDTO() {
        this.operationID = 0L;
    }

    public MailVerificationOTPDTO(Long operationID) {
        this.operationID = operationID;
    }

    public Long getOperationID() {
        return operationID;
    }

    public void setOperationID(Long operationID) {
        this.operationID = operationID;
    }
}
