package com.wahana.wahanamobile.ModelApiOPS.EditBeratTTK;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 18/02/20.
 */

public class aoCekTTK_Result {

    @SerializedName("ttk")
    @Expose
    private String ttk;

    @SerializedName("pengirim")
    @Expose
    private String pengirim;

    @SerializedName("berat")
    @Expose
    private String berat;


    @SerializedName("dimensi")
    @Expose
    private String dimensi;

    public String getTtk() {
        return ttk;
    }

    public void setTtk(String ttk) {
        this.ttk = ttk;
    }

    public String getPengirim() {
        return pengirim;
    }

    public void setPengirim(String pengirim) {
        this.pengirim = pengirim;
    }

    public String getBerat() {
        return berat;
    }

    public void setBerat(String berat) {
        this.berat = berat;
    }

    public String getDimensi() {
        return dimensi;
    }

    public void setDimensi(String dimensi) {
        this.dimensi = dimensi;
    }


}
