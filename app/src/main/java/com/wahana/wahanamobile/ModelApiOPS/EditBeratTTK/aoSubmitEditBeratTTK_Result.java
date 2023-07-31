package com.wahana.wahanamobile.ModelApiOPS.EditBeratTTK;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 18/02/20.
 */

public class aoSubmitEditBeratTTK_Result {

    @SerializedName("ttk")
    @Expose
    private String ttk;


    @SerializedName("berat")
    @Expose
    private String berat;

    @SerializedName("volume")
    @Expose
    private String volume;

    public String getTtk() {
        return ttk;
    }

    public void setTtk(String ttk) {
        this.ttk = ttk;
    }

    public String getBerat() {
        return berat;
    }

    public void setBerat(String berat) {
        this.berat = berat;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }


}
