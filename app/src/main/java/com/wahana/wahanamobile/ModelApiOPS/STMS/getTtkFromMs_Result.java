package com.wahana.wahanamobile.ModelApiOPS.STMS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 02/05/20.
 */

public class getTtkFromMs_Result {

    @SerializedName("filename")
    @Expose
    private String filename;

    @SerializedName("urlfile")
    @Expose
    private String urlfile;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrlfile() {
        return urlfile;
    }

    public void setUrlfile(String urlfile) {
        this.urlfile = urlfile;
    }

}
