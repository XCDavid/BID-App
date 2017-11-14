package com.teknei.bid.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 07/11/2017.
 */

public class ResponseStep {

    @SerializedName("operationId")
    private Long operationId;

    @SerializedName("step")
    private Long step;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Long getStep() {
        return step;
    }

    public void setStep(Long step) {
        this.step = step;
    }
}
