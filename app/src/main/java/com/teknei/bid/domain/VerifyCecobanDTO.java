package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class VerifyCecobanDTO {

    @SerializedName("id")
    Long idOperation;

    @SerializedName("leftIndexB64")
    String leftIndexB64;

    @SerializedName("rightIndexB64")
    String rightIndexB64;

    public VerifyCecobanDTO() {
        this.idOperation = 0L;
        leftIndexB64     ="";
        rightIndexB64    ="";
    }

    public VerifyCecobanDTO(Long idOperation) {
        this.idOperation = idOperation;
        leftIndexB64  ="";
        rightIndexB64 ="";
    }

    public Long getIdOperation() {
        return idOperation;
    }

    public void setIdOperation(Long idOperation) {
        this.idOperation = idOperation;
    }

    public String getLeftIndexB64() {
        return leftIndexB64;
    }

    public void setLeftIndexB64(String leftIndexB64) {
        this.leftIndexB64 = leftIndexB64;
    }

    public String getRightIndexB64() {
        return rightIndexB64;
    }

    public void setRightIndexB64(String rightIndexB64) {
        this.rightIndexB64 = rightIndexB64;
    }
}
