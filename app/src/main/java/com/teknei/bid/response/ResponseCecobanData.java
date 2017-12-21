package com.teknei.bid.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class ResponseCecobanData {

    @SerializedName("codigoRespuestaDatos")
    @Expose
    private Integer codigoRespuestaDatos;
    @SerializedName("respuestaComparacion")
    @Expose
    private ResponseCecobanComparacion respuestaComparacion;
    @SerializedName("respuestaSituacionRegistral")
    @Expose
    private ResponseCecobanRegistral respuestaSituacionRegistral;

    public Integer getCodigoRespuestaDatos() {
        return codigoRespuestaDatos;
    }

    public void setCodigoRespuestaDatos(Integer codigoRespuestaDatos) {
        this.codigoRespuestaDatos = codigoRespuestaDatos;
    }

    public ResponseCecobanComparacion getRespuestaComparacion() {
        return respuestaComparacion;
    }

    public void setRespuestaComparacion(ResponseCecobanComparacion respuestaComparacion) {
        this.respuestaComparacion = respuestaComparacion;
    }

    public ResponseCecobanRegistral getRespuestaSituacionRegistral() {
        return respuestaSituacionRegistral;
    }

    public void setRespuestaSituacionRegistral(ResponseCecobanRegistral respuestaSituacionRegistral) {
        this.respuestaSituacionRegistral = respuestaSituacionRegistral;
    }

}
