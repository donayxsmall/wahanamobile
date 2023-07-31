package com.wahana.wahanamobile.ModelApiOPS.BongkarMA;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 08/10/20.
 */

public class opv2GetLastSess {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("nomorMA")
    @Expose
    private String nomorMA;

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

    public String getNomorMA() {
        return nomorMA;
    }

    public void setNomorMA(String nomorMA) {
        this.nomorMA = nomorMA;
    }

}
