package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 04/01/2018.
 */

public class UserCompanyDTO {

    @SerializedName("idCompany")
    private Long idCompany;

    @SerializedName("idCustomer")
    private Long idCustomer;

    @SerializedName("idDisp")
    private Long idDisp;

    public UserCompanyDTO(Long idCompany, Long idCustomer, Long idDisp) {
        this.idCompany  = idCompany;
        this.idCustomer = idCustomer;
        this.idDisp     = idDisp;
    }

    public Long getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(Long idCompany) {
        this.idCompany = idCompany;
    }

    public Long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public Long getIdDisp() {
        return idDisp;
    }

    public void setIdDisp(Long idDisp) {
        this.idDisp = idDisp;
    }
}
