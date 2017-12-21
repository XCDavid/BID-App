package com.teknei.bid.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rgarciav on 18/12/2017.
 */

public class ResponseCecobanRegistral {

    @SerializedName("tipoReporteRoboExtravio")
    @Expose
    private String tipoReporteRoboExtravio;
    @SerializedName("tipoSituacionRegistral")
    @Expose
    private String tipoSituacionRegistral;

    public String getTipoReporteRoboExtravio() {
        return tipoReporteRoboExtravio;
    }

    public void setTipoReporteRoboExtravio(String tipoReporteRoboExtravio) {
        this.tipoReporteRoboExtravio = tipoReporteRoboExtravio;
    }

    public String getTipoSituacionRegistral() {
        return tipoSituacionRegistral;
    }

    public void setTipoSituacionRegistral(String tipoSituacionRegistral) {
        this.tipoSituacionRegistral = tipoSituacionRegistral;
    }
}
