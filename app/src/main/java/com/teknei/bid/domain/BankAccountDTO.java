package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 03/01/2018.
 */

public class BankAccountDTO {

    @SerializedName("active")
    private Boolean active;

    @SerializedName("alias")
    private String alias;

    @SerializedName("ammount")
    private Integer ammount;

    @SerializedName("clabe")
    private String clabe;

    @SerializedName("idClient")
    private Integer idClient;

    @SerializedName("idCreditInstitution")
    private Integer idCreditInstitution;

    @SerializedName("newIndicator")
    private Boolean newIndicator;

    @SerializedName("username")
    private String username;

    public BankAccountDTO(Boolean active, String alias,
                          Integer ammount, String clabe,
                          Integer idClient, Integer idCreditInstitution,
                          Boolean newIndicator, String username) {
        this.active = active;
        this.alias = alias;
        this.ammount = ammount;
        this.clabe = clabe;
        this.idClient = idClient;
        this.idCreditInstitution = idCreditInstitution;
        this.newIndicator = newIndicator;
        this.username = username;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getAmmount() {
        return ammount;
    }

    public void setAmmount(Integer ammount) {
        this.ammount = ammount;
    }

    public String getClabe() {
        return clabe;
    }

    public void setClabe(String clabe) {
        this.clabe = clabe;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public Integer getIdCreditInstitution() {
        return idCreditInstitution;
    }

    public void setIdCreditInstitution(Integer idCreditInstitution) {
        this.idCreditInstitution = idCreditInstitution;
    }

    public Boolean getNewIndicator() {
        return newIndicator;
    }

    public void setNewIndicator(Boolean newIndicator) {
        this.newIndicator = newIndicator;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
