package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 15/12/2017.
 */

public class ConfirmFingerSingDTO {

    @SerializedName("idClient")
    Long idOperation;

    public ConfirmFingerSingDTO(Long idOperation) {
        this.idOperation = idOperation;
    }

    public Long getIdOperation() {
        return idOperation;
    }

    public void setIdOperation(Long idOperation) {
        this.idOperation = idOperation;
    }
}
