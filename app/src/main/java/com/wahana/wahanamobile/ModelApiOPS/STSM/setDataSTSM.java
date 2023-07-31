package com.wahana.wahanamobile.ModelApiOPS.STSM;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 27/04/20.
 */

public class setDataSTSM {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("btno")
    @Expose
    private String btno;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBtno() {
        return btno;
    }

    public void setBtno(String btno) {
        this.btno = btno;
    }



}
