package com.teknei.bid.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class ResponseVerifyCecoban {

    @SerializedName("timeStamp")
    @Expose
    private ResponseCecobanTimesStamp timeStamp;
    @SerializedName("digestivos")
    @Expose
    private ResponseCecobanDigestivos digestivos;
    @SerializedName("response")
    @Expose
    private ResponseCecobanResponse response;

    public ResponseCecobanTimesStamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(ResponseCecobanTimesStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ResponseCecobanDigestivos getDigestivos() {
        return digestivos;
    }

    public void setDigestivos(ResponseCecobanDigestivos digestivos) {
        this.digestivos = digestivos;
    }

    public ResponseCecobanResponse getResponse() {
        return response;
    }

    public void setResponse(ResponseCecobanResponse response) {
        this.response = response;
    }
}
