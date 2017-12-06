package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 05/12/2017.
 */

public class FingerLoginDTO {

    @SerializedName("rm")
    String fingerIndex;

    @SerializedName("id")
    String id;

    @SerializedName("contentType")
    String contentType;

    public String getFingerIndex() {
        return fingerIndex;
    }

    public void setFingerIndex(String fingerIndex) {
        this.fingerIndex = fingerIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = "image/wsq";
    }
}
