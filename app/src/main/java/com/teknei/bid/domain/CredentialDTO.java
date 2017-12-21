package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 13/11/2017.
 */

public class CredentialDTO {

    @SerializedName("apeMat")
    private String apeMat;

    @SerializedName("apePat")
    private String apePat;

    @SerializedName("call")
    private String call;

    @SerializedName("clavElec")
    private String clavElec;

    @SerializedName("col")
    private String col;

    @SerializedName("cp")
    private String cp;

    @SerializedName("dist")
    private String dist;

    @SerializedName("esta")
    private String esta;

    @SerializedName("foli")
    private String foli;

    @SerializedName("loca")
    private String loca;

    @SerializedName("mrz")
    private String mrz;

    @SerializedName("muni")
    private String muni;

    @SerializedName("noExt")
    private String noExt;

    @SerializedName("noInt")
    private String noInt;

    @SerializedName("nomb")
    private String nomb;

    @SerializedName("ocr")
    private String ocr;

    @SerializedName("secc")
    private String secc;

    @SerializedName("user")
    private String user;

    @SerializedName("vige")
    private String vige;

    @SerializedName("curp")
    private String curp;

    public CredentialDTO() {
        this.apeMat = "";
        this.apePat = "";
        this.call = "";
        this.clavElec = "";
        this.col = "";
        this.cp = "";
        this.dist = "";
        this.esta = "";
        this.foli = "";
        this.loca = "";
        this.mrz = "";
        this.muni = "";
        this.noExt = "";
        this.noInt = "";
        this.nomb = "";
        this.ocr = "";
        this.secc = "";
        this.user = "";
        this.vige = "";
        this.curp = "";
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getApeMat() {
        return apeMat;
    }

    public void setApeMat(String apeMat) {
        this.apeMat = apeMat;
    }

    public String getApePat() {
        return apePat;
    }

    public void setApePat(String apePat) {
        this.apePat = apePat;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getClavElec() {
        return clavElec;
    }

    public void setClavElec(String clavElec) {
        this.clavElec = clavElec;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getEsta() {
        return esta;
    }

    public void setEsta(String esta) {
        this.esta = esta;
    }

    public String getFoli() {
        return foli;
    }

    public void setFoli(String foli) {
        this.foli = foli;
    }

    public String getLoca() {
        return loca;
    }

    public void setLoca(String loca) {
        this.loca = loca;
    }

    public String getMrz() {
        return mrz;
    }

    public void setMrz(String mrz) {
        this.mrz = mrz;
    }

    public String getMuni() {
        return muni;
    }

    public void setMuni(String muni) {
        this.muni = muni;
    }

    public String getNoExt() {
        return noExt;
    }

    public void setNoExt(String noExt) {
        this.noExt = noExt;
    }

    public String getNoInt() {
        return noInt;
    }

    public void setNoInt(String noInt) {
        this.noInt = noInt;
    }

    public String getNomb() {
        return nomb;
    }

    public void setNomb(String nomb) {
        this.nomb = nomb;
    }

    public String getOcr() {
        return ocr;
    }

    public void setOcr(String ocr) {
        this.ocr = ocr;
    }

    public String getSecc() {
        return secc;
    }

    public void setSecc(String secc) {
        this.secc = secc;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVige() {
        return vige;
    }

    public void setVige(String vige) {
        this.vige = vige;
    }
}
