package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 06/11/2017.
 */

public class AddressDTO {

    @SerializedName("country")
    private String country;

    @SerializedName("extNumber")
    private Long extNumber;

    @SerializedName("intNumber")
    private Long intNumber;

    @SerializedName("locality")
    private String locality;

    @SerializedName("municipio")
    private String municipio;

    @SerializedName("state")
    private String state;

    @SerializedName("street")
    private String street;

    @SerializedName("suburb")
    private String suburb;

    @SerializedName("zipCode")
    private String zipCode;

    public AddressDTO () {
        this.country    = "";

        this.extNumber    = new Long(0);

        this.intNumber    = new Long(0);

        this.locality    = "";

        this.municipio    = "";

        this.state    = "";

        this.street    = "";

        this.suburb    = "";

        this.zipCode    = "";

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getExtNumber() {
        return extNumber;
    }

    public void setExtNumber(Long extNumber) {
        this.extNumber = extNumber;
    }

    public Long getIntNumber() {
        return intNumber;
    }

    public void setIntNumber(Long intNumber) {
        this.intNumber = intNumber;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}
