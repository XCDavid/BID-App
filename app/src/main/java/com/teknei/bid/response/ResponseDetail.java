package com.teknei.bid.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 08/11/2017.
 */

public class ResponseDetail {

    @SerializedName("nombre")
    private String name;

    @SerializedName("apellidoPaterno")
    private String fatherLastName;

    @SerializedName("apellidoMaterno")
    private String motherLastName;

    @SerializedName("curpCapturado")
    private String capturedCurp;

    @SerializedName("curpIdentificado")
    private String identifiedCurp;

    @SerializedName("direccion")
    private String address;

    @SerializedName("scanId")
    private String scanId;

    @SerializedName("documentManagerId")
    private String documentManagerId;

    @SerializedName("mrz")
    private String mrz;

    @SerializedName("ocr")
    private String ocr;

    @SerializedName("vig")
    private String vig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherLastName() {
        return fatherLastName;
    }

    public void setFatherLastName(String fatherLastName) {
        this.fatherLastName = fatherLastName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public void setMotherLastName(String motherLastName) {
        this.motherLastName = motherLastName;
    }

    public String getCapturedCurp() {
        return capturedCurp;
    }

    public void setCapturedCurp(String capturedCurp) {
        this.capturedCurp = capturedCurp;
    }

    public String getIdentifiedCurp() {
        return identifiedCurp;
    }

    public void setIdentifiedCurp(String identifiedCurp) {
        this.identifiedCurp = identifiedCurp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public String getDocumentManagerId() {
        return documentManagerId;
    }

    public void setDocumentManagerId(String documentManagerId) {
        this.documentManagerId = documentManagerId;
    }

    public String getMrz() {
        return mrz;
    }

    public void setMrz(String mrz) {
        this.mrz = mrz;
    }

    public String getOcr() {
        return ocr;
    }

    public void setOcr(String ocr) {
        this.ocr = ocr;
    }

    public String getVig() {
        return vig;
    }

    public void setVig(String vig) {
        this.vig = vig;
    }
}
