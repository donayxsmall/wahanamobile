package com.wahana.wahanamobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by team-it on 12/10/18.
 */

public class resultSTM {

    @SerializedName("nostm")
    @Expose
    private String nostm;

    public String getNostm() {
        return nostm;
    }
}
