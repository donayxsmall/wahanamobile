package com.wahana.wahanamobile.ModelApiOPS.StpuBerat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 26/08/20.
 */

public class opv2STPUv2 {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("nomorSTPU")
    @Expose
    private String nomorSTPU;

    @SerializedName("urlSTPU")
    @Expose
    private String urlSTPU;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getNomorSTPU() {
        return nomorSTPU;
    }

    public void setNomorSTPU(String nomorSTPU) {
        this.nomorSTPU = nomorSTPU;
    }

    public String getUrlSTPU() {
        return urlSTPU;
    }

    public void setUrlSTPU(String urlSTPU) {
        this.urlSTPU = urlSTPU;
    }
}
