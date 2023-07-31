package com.wahana.wahanamobile.ModelApiOPS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 15/11/19.
 */

public class aoSubmitPenerimaanRetur {

    @SerializedName("code")
    @Expose
    private String code;


    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("number")
    @Expose
    private String number;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
