package com.teknei.bid.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 10/11/2017.
 */

public class ResponseTimeStamp {

    @SerializedName("operationId")
    Long id;

    @SerializedName("curp")
    String curp;

    @SerializedName("credentials")
    Long credentials;

    @SerializedName("credentialsStr")
    String credentialsStr;

    @SerializedName("facial")
    Long facial;

    @SerializedName("facialStr")
    String facialStr;

    @SerializedName("address")
    Long address;

    @SerializedName("addressStr")
    String addressStr;

    @SerializedName("fingers")
    Long fingers;

    @SerializedName("fingersStr")
    String fingersStr;

    @SerializedName("contract")
    Long contract;

    @SerializedName("contractStr")
    String contractStr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public Long getCredentials() {
        return credentials;
    }

    public void setCredentials(Long credentials) {
        this.credentials = credentials;
    }

    public String getCredentialsStr() {
        return credentialsStr;
    }

    public void setCredentialsStr(String credentialsStr) {
        this.credentialsStr = credentialsStr;
    }

    public Long getFacial() {
        return facial;
    }

    public void setFacial(Long facial) {
        this.facial = facial;
    }

    public String getFacialStr() {
        return facialStr;
    }

    public void setFacialStr(String facialStr) {
        this.facialStr = facialStr;
    }

    public Long getAddress() {
        return address;
    }

    public void setAddress(Long address) {
        this.address = address;
    }

    public String getAddressStr() {
        return addressStr;
    }

    public void setAddressStr(String addressStr) {
        this.addressStr = addressStr;
    }

    public Long getFingers() {
        return fingers;
    }

    public void setFingers(Long fingers) {
        this.fingers = fingers;
    }

    public String getFingersStr() {
        return fingersStr;
    }

    public void setFingersStr(String fingersStr) {
        this.fingersStr = fingersStr;
    }

    public Long getContract() {
        return contract;
    }

    public void setContract(Long contract) {
        this.contract = contract;
    }

    public String getContractStr() {
        return contractStr;
    }

    public void setContractStr(String contractStr) {
        this.contractStr = contractStr;
    }
}
