package com.teknei.bid.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 06/11/2017.
 */

public class SearchDTO {

    @SerializedName("curp")
    private String curp;

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }
}
