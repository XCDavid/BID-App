package com.teknei.bid.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 07/11/2017.
 */

public class ResponseStartOpe extends ResponseServicesBID {

    @SerializedName("operationId")
    private Long operationId;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }
}
