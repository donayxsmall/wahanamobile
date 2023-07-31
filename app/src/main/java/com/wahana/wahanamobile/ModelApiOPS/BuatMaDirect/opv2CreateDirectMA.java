package com.wahana.wahanamobile.ModelApiOPS.BuatMaDirect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.wahana.wahanamobile.ModelApiOPS.StpuMa.opv2CreateSTPU2MA;

/**
 * Created by donay on 18/07/20.
 */

public class opv2CreateDirectMA {
    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data{
        @SerializedName("nomorMA")
        @Expose
        private String nomorMA;

        @SerializedName("urlMA")
        @Expose
        private String urlMA;

        public String getNomorMA() {
            return nomorMA;
        }

        public void setNomorMA(String nomorMA) {
            this.nomorMA = nomorMA;
        }

        public String getUrlMA() {
            return urlMA;
        }

        public void setUrlMA(String urlMA) {
            this.urlMA = urlMA;
        }

    }

}
