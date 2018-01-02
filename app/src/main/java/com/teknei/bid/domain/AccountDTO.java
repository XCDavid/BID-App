package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 27/12/2017.
 */

public class AccountDTO {

    @SerializedName("idUsua")
    private Long idOperation;

    @SerializedName("password")
    private String password;

    @SerializedName("usua")
    private String userName;

    public AccountDTO(Long idOperation, String password, String userName) {
        this.idOperation = idOperation;
        this.password = password;
        this.userName = userName;
    }

    public Long getIdOperation() {
        return idOperation;
    }

    public void setIdOperation(Long idOperation) {
        this.idOperation = idOperation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
