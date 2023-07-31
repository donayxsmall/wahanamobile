package com.wahana.wahanamobile.ModelApiOPS.StpuMa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 12/07/20.
 */

public class opv2GetTTKSTPU_Result {
    @SerializedName("filename")
    @Expose
    private String filename;

    @SerializedName("fileurl")
    @Expose
    private String fileurl;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }


}
