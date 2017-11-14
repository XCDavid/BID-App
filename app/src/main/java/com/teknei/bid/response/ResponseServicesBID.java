package com.teknei.bid.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 06/11/2017.
 */

public class ResponseServicesBID {

    @SerializedName("resultOK")
    private boolean resultOK;

    @SerializedName("errorMessage")
    private String errorMessage;

    public boolean isResultOK() {
        return resultOK;
    }

    public void setResultOK(boolean resultOK) {
        this.resultOK = resultOK;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
