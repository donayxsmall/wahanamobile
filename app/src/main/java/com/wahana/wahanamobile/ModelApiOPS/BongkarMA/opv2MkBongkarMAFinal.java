package com.wahana.wahanamobile.ModelApiOPS.BongkarMA;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 21/07/20.
 */

public class opv2MkBongkarMAFinal {

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

        @SerializedName("nomorSTMA")
        @Expose
        private String nomorSTMA;

        @SerializedName("urlSTMA")
        @Expose
        private String urlSTMA;

        public String getNomorSTMA() {
            return nomorSTMA;
        }

        public void setNomorSTMA(String nomorSTMA) {
            this.nomorSTMA = nomorSTMA;
        }

        public String getUrlSTMA() {
            return urlSTMA;
        }

        public void setUrlSTMA(String urlSTMA) {
            this.urlSTMA = urlSTMA;
        }


    }

}
