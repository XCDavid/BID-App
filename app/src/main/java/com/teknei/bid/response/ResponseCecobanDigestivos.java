package com.teknei.bid.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class ResponseCecobanDigestivos {

    @SerializedName("digTimeStamp")
    @Expose
    private String digTimeStamp;
    @SerializedName("digEntradaDatos")
    @Expose
    private String digEntradaDatos;
    @SerializedName("digSalidaDatos")
    @Expose
    private String digSalidaDatos;

    public String getDigTimeStamp() {
        return digTimeStamp;
    }

    public void setDigTimeStamp(String digTimeStamp) {
        this.digTimeStamp = digTimeStamp;
    }

    public String getDigEntradaDatos() {
        return digEntradaDatos;
    }

    public void setDigEntradaDatos(String digEntradaDatos) {
        this.digEntradaDatos = digEntradaDatos;
    }

    public String getDigSalidaDatos() {
        return digSalidaDatos;
    }

    public void setDigSalidaDatos(String digSalidaDatos) {
        this.digSalidaDatos = digSalidaDatos;
    }
}
