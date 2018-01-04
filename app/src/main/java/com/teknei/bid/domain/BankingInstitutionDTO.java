package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 03/01/2018.
 */

public class BankingInstitutionDTO {

    @SerializedName("idInstCred")
    private Integer idInstCred;

    @SerializedName("nomCort")
    private String nomCort;

    @SerializedName("nomLarg")
    private String nomLarg;

    @SerializedName("idEsta")
    private Integer idEsta;

    @SerializedName("idTipo")
    private Integer idTipo;

    @SerializedName("usrCrea")
    private String usrCrea;

    @SerializedName("fchCrea")
    private Long fchCrea;

    @SerializedName("usrModi")
    private String usrModi;

    @SerializedName("fchModi")
    private Long fchModi;

    public Integer getIdInstCred() {
        return idInstCred;
    }

    public void setIdInstCred(Integer idInstCred) {
        this.idInstCred = idInstCred;
    }

    public String getNomCort() {
        return nomCort;
    }

    public void setNomCort(String nomCort) {
        this.nomCort = nomCort;
    }

    public String getNomLarg() {
        return nomLarg;
    }

    public void setNomLarg(String nomLarg) {
        this.nomLarg = nomLarg;
    }

    public Integer getIdEsta() {
        return idEsta;
    }

    public void setIdEsta(Integer idEsta) {
        this.idEsta = idEsta;
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getUsrCrea() {
        return usrCrea;
    }

    public void setUsrCrea(String usrCrea) {
        this.usrCrea = usrCrea;
    }

    public Long getFchCrea() {
        return fchCrea;
    }

    public void setFchCrea(Long fchCrea) {
        this.fchCrea = fchCrea;
    }

    public String getUsrModi() {
        return usrModi;
    }

    public void setUsrModi(String usrModi) {
        this.usrModi = usrModi;
    }

    public Long getFchModi() {
        return fchModi;
    }

    public void setFchModi(Long fchModi) {
        this.fchModi = fchModi;
    }
}
