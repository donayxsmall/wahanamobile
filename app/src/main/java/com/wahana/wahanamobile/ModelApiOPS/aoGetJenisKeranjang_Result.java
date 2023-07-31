package com.wahana.wahanamobile.ModelApiOPS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 11/11/19.
 */

public class aoGetJenisKeranjang_Result {

    @SerializedName("ATRLVCD")
    @Expose
    private String ATRLVCD;

    @SerializedName("ATRLVNM")
    @Expose
    private String ATRLVNM;

    public String getATRLVCD() {
        return ATRLVCD;
    }

    public void setATRLVCD(String ATRLVCD) {
        this.ATRLVCD = ATRLVCD;
    }

    public String getATRLVNM() {
        return ATRLVNM;
    }

    public void setATRLVNM(String ATRLVNM) {
        this.ATRLVNM = ATRLVNM;
    }

}
