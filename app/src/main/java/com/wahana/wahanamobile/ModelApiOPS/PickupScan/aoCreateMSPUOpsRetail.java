package com.wahana.wahanamobile.ModelApiOPS.PickupScan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by donay on 26/11/19.
 */

public class aoCreateMSPUOpsRetail {

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
    private List<aoCreateMSPUOpsRetail_Result> data = null;

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

    public List<aoCreateMSPUOpsRetail_Result> getData() {
        return data;
    }

    public void setData(List<aoCreateMSPUOpsRetail_Result> data) {
        this.data = data;
    }



}
