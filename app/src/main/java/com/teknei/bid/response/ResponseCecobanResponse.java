package com.teknei.bid.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class ResponseCecobanResponse {

    @SerializedName("fechaHoraPeticion")
    @Expose
    private String fechaHoraPeticion;
    @SerializedName("descripcionRespuesta")
    @Expose
    private String descripcionRespuesta;
    @SerializedName("folioCliente")
    @Expose
    private Object folioCliente;
    @SerializedName("minutiaeResponse")
    @Expose
    private ResponseCecobanMinutia minutiaeResponse;
    @SerializedName("dataResponse")
    @Expose
    private ResponseCecobanData dataResponse;
    @SerializedName("peticionId")
    @Expose
    private String peticionId;
    @SerializedName("codigoRespuestaCCB")
    @Expose
    private Integer codigoRespuestaCCB;
    @SerializedName("tiempoProcesamiento")
    @Expose
    private Integer tiempoProcesamiento;
    @SerializedName("codigoRespuesta")
    @Expose
    private Integer codigoRespuesta;
    @SerializedName("indiceSolicitud")
    @Expose
    private String indiceSolicitud;

    public String getFechaHoraPeticion() {
        return fechaHoraPeticion;
    }

    public void setFechaHoraPeticion(String fechaHoraPeticion) {
        this.fechaHoraPeticion = fechaHoraPeticion;
    }

    public String getDescripcionRespuesta() {
        return descripcionRespuesta;
    }

    public void setDescripcionRespuesta(String descripcionRespuesta) {
        this.descripcionRespuesta = descripcionRespuesta;
    }

    public Object getFolioCliente() {
        return folioCliente;
    }

    public void setFolioCliente(Object folioCliente) {
        this.folioCliente = folioCliente;
    }

    public ResponseCecobanMinutia getMinutiaeResponse() {
        return minutiaeResponse;
    }

    public void setMinutiaeResponse(ResponseCecobanMinutia minutiaeResponse) {
        this.minutiaeResponse = minutiaeResponse;
    }

    public ResponseCecobanData getDataResponse() {
        return dataResponse;
    }

    public void setDataResponse(ResponseCecobanData dataResponse) {
        this.dataResponse = dataResponse;
    }

    public String getPeticionId() {
        return peticionId;
    }

    public void setPeticionId(String peticionId) {
        this.peticionId = peticionId;
    }

    public Integer getCodigoRespuestaCCB() {
        return codigoRespuestaCCB;
    }

    public void setCodigoRespuestaCCB(Integer codigoRespuestaCCB) {
        this.codigoRespuestaCCB = codigoRespuestaCCB;
    }

    public Integer getTiempoProcesamiento() {
        return tiempoProcesamiento;
    }

    public void setTiempoProcesamiento(Integer tiempoProcesamiento) {
        this.tiempoProcesamiento = tiempoProcesamiento;
    }

    public Integer getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(Integer codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getIndiceSolicitud() {
        return indiceSolicitud;
    }

    public void setIndiceSolicitud(String indiceSolicitud) {
        this.indiceSolicitud = indiceSolicitud;
    }

}
