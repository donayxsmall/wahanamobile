package com.wahana.wahanamobile.ModelApiOPS.PickupScan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 26/11/19.
 */

public class aoCreateMSPUOpsRetail_Result {

    @SerializedName("nomorpu")
    @Expose
    private String nomorpu;


    @SerializedName("urlfilepu")
    @Expose
    private String urlfilepu;

    @SerializedName("flagstat")
    @Expose
    private String flagstat;

    public String getFlagstat() {
        return flagstat;
    }

    public void setFlagstat(String flagstat) {
        this.flagstat = flagstat;
    }



    public String getNomorpu() {
        return nomorpu;
    }

    public void setNomorpu(String nomorpu) {
        this.nomorpu = nomorpu;
    }

    public String getUrlfilepu() {
        return urlfilepu;
    }

    public void setUrlfilepu(String urlfilepu) {
        this.urlfilepu = urlfilepu;
    }

}
