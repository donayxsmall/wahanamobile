package com.wahana.wahanamobile.ModelApiOPS.STMS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by donay on 02/05/20.
 */

public class getTtkFromMs {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("data")
    @Expose
    private List<getTtkFromMs_Result> data = null;

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

    public List<getTtkFromMs_Result> getData() {
        return data;
    }

    public void setData(List<getTtkFromMs_Result> data) {
        this.data = data;
    }


}
