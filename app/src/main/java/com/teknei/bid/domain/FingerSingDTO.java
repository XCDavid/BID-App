package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 14/12/2017.
 */

public class FingerSingDTO {
    @SerializedName("base64Finger")
    String fingerIndex;

    @SerializedName("operationId")
    Long operationId;

    @SerializedName("contentType")
    String contentType;

    public FingerSingDTO() {
        this.fingerIndex = "";
        this.operationId = 0L;
        this.contentType = "image/wsq";
    }

    public FingerSingDTO(String fingerIndex, Long operationId) {
        this.fingerIndex = fingerIndex;
        this.operationId = operationId;
        this.contentType = "image/wsq";
    }

    public String getFingerIndex() {
        return fingerIndex;
    }

    public void setFingerIndex(String fingerIndex) {
        this.fingerIndex = fingerIndex;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = "image/wsq";
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }
}
