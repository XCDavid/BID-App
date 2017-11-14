package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 07/11/2017.
 */

public class StartOperationDTO {

    @SerializedName("nombre")
    private String name;

    @SerializedName("customerType")
    private Long   customerType;

    @SerializedName("primerApellido")
    private String firstLastName;

    @SerializedName("email")
    private String email;

    @SerializedName("refContrato")
    private String refContract;

    @SerializedName("telefono")
    private String phoneNumber;

    @SerializedName("curp")
    private String curp;

    @SerializedName("employee")
    private String employee;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("segundoApellido")
    private String secondLastName;

    @SerializedName("emprId")
    private Long companyId;

    public StartOperationDTO(Long companyId, Long customerType, String employee, String deviceId, String curp) {
        this.customerType = customerType;
        this.companyId    = companyId;
        this.employee     = employee;
        this.deviceId     = deviceId;
        this.curp         = curp;

        this.name           = "";
        this.firstLastName  = "";
        this.email          = "";
        this.refContract    = "";
        this.phoneNumber    = "";
        this.secondLastName = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Long customerType) {
        this.customerType = customerType;
    }

    public String getFirstLastName() {
        return firstLastName;
    }

    public void setFirstLastName(String firstLastName) {
        firstLastName = firstLastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRefContract() {
        return refContract;
    }

    public void setRefContract(String refContract) {
        this.refContract = refContract;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        secondLastName = secondLastName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
