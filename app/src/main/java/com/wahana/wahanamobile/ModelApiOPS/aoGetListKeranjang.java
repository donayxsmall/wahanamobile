package com.wahana.wahanamobile.ModelApiOPS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by donay on 11/11/19.
 */

public class aoGetListKeranjang {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("alert")
    @Expose
    private String alert;


    @SerializedName("data")
    @Expose
    private List<aoGetListKeranjang_Result> data = null;

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

    public List<aoGetListKeranjang_Result> getData() {
        return data;
    }

    public void setData(List<aoGetListKeranjang_Result> data) {
        this.data = data;
    }


}
