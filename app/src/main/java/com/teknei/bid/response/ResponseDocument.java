package com.teknei.bid.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 25/10/2017.
 */

public class ResponseDocument {

    @SerializedName("resultOK")
    private boolean resultOK;

    @SerializedName("errorMessage")
    private String errorMessage;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("status")
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("exception")
    private String exception;

    @SerializedName("message")
    private String message;

    @SerializedName("path")
    private String path;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
