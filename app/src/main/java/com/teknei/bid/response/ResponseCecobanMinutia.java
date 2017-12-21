package com.teknei.bid.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class ResponseCecobanMinutia {

    @SerializedName("similitud7")
    @Expose
    private Integer similitud7;
    @SerializedName("codigoRespuestaMinucia")
    @Expose
    private Integer codigoRespuestaMinucia;
    @SerializedName("similitud2")
    @Expose
    private Integer similitud2;

    public Integer getSimilitud7() {
        return similitud7;
    }

    public void setSimilitud7(Integer similitud7) {
        this.similitud7 = similitud7;
    }

    public Integer getCodigoRespuestaMinucia() {
        return codigoRespuestaMinucia;
    }

    public void setCodigoRespuestaMinucia(Integer codigoRespuestaMinucia) {
        this.codigoRespuestaMinucia = codigoRespuestaMinucia;
    }

    public Integer getSimilitud2() {
        return similitud2;
    }

    public void setSimilitud2(Integer similitud2) {
        this.similitud2 = similitud2;
    }

}
