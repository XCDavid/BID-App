package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 04/01/2018.
 */

public class CompanyDTO {

    @SerializedName("idEmpr")
    private Integer idEmpr;

    @SerializedName("empr")
    private String empr;

    public Integer getIdEmpr() {
        return idEmpr;
    }

    public void setIdEmpr(Integer idEmpr) {
        this.idEmpr = idEmpr;
    }

    public String getEmpr() {
        return empr;
    }

    public void setEmpr(String empr) {
        this.empr = empr;
    }
}
