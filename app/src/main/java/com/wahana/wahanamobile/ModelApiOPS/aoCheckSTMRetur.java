package com.wahana.wahanamobile.ModelApiOPS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 11/11/19.
 */

public class aoCheckSTMRetur {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("alert")
    @Expose
    private String alert;


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

}
