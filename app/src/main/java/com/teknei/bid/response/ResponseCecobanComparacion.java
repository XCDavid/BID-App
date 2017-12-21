package com.teknei.bid.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class ResponseCecobanComparacion {

    @SerializedName("anioRegistro")
    @Expose
    private Boolean anioRegistro;
    @SerializedName("claveElector")
    @Expose
    private Boolean claveElector;
    @SerializedName("apellidoPaterno")
    @Expose
    private Boolean apellidoPaterno;
    @SerializedName("anioEmision")
    @Expose
    private Boolean anioEmision;
    @SerializedName("numeroEmisionCredencial")
    @Expose
    private Boolean numeroEmisionCredencial;
    @SerializedName("cic")
    @Expose
    private Boolean cic;
    @SerializedName("nombre")
    @Expose
    private Boolean nombre;
    @SerializedName("curp")
    @Expose
    private Boolean curp;
    @SerializedName("ocr")
    @Expose
    private Boolean ocr;
    @SerializedName("apellidoMaterno")
    @Expose
    private Boolean apellidoMaterno;

    public Boolean getAnioRegistro() {
        return anioRegistro;
    }

    public void setAnioRegistro(Boolean anioRegistro) {
        this.anioRegistro = anioRegistro;
    }

    public Boolean getClaveElector() {
        return claveElector;
    }

    public void setClaveElector(Boolean claveElector) {
        this.claveElector = claveElector;
    }

    public Boolean getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(Boolean apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public Boolean getAnioEmision() {
        return anioEmision;
    }

    public void setAnioEmision(Boolean anioEmision) {
        this.anioEmision = anioEmision;
    }

    public Boolean getNumeroEmisionCredencial() {
        return numeroEmisionCredencial;
    }

    public void setNumeroEmisionCredencial(Boolean numeroEmisionCredencial) {
        this.numeroEmisionCredencial = numeroEmisionCredencial;
    }

    public Boolean getCic() {
        return cic;
    }

    public void setCic(Boolean cic) {
        this.cic = cic;
    }

    public Boolean getNombre() {
        return nombre;
    }

    public void setNombre(Boolean nombre) {
        this.nombre = nombre;
    }

    public Boolean getCurp() {
        return curp;
    }

    public void setCurp(Boolean curp) {
        this.curp = curp;
    }

    public Boolean getOcr() {
        return ocr;
    }

    public void setOcr(Boolean ocr) {
        this.ocr = ocr;
    }

    public Boolean getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(Boolean apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }
}
