package com.wahana.wahanamobile.ModelApiOPS.EditBeratTTK;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 18/02/20.
 */

public class aoSubmitEditBeratTTK {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("beratsebelum")
    @Expose
    private String beratsebelum;

    @SerializedName("beratsesudah")
    @Expose
    private String beratsesudah;

    @SerializedName("dimensisebelum")
    @Expose
    private String dimensisebelum;

    @SerializedName("dimensisesudah")
    @Expose
    private String dimensisesudah;



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

    public String getBeratsebelum() {
        return beratsebelum;
    }

    public void setBeratsebelum(String beratsebelum) {
        this.beratsebelum = beratsebelum;
    }

    public String getBeratsesudah() {
        return beratsesudah;
    }

    public void setBeratsesudah(String beratsesudah) {
        this.beratsesudah = beratsesudah;
    }

    public String getDimensisebelum() {
        return dimensisebelum;
    }

    public void setDimensisebelum(String dimensisebelum) {
        this.dimensisebelum = dimensisebelum;
    }

    public String getDimensisesudah() {
        return dimensisesudah;
    }

    public void setDimensisesudah(String dimensisesudah) {
        this.dimensisesudah = dimensisesudah;
    }





}
