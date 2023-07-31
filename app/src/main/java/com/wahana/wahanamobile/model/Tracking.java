package com.wahana.wahanamobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by team-it on 09/10/18.
 */

public class Tracking {

    @SerializedName("Tanggal")
    @Expose
    private String Tanggal;

    @SerializedName("data")
    @Expose
    private List<ResultTracking> data = null;



    public String getTanggal() {
        return Tanggal;
    }

    public List<ResultTracking> getData() {
        return data;
    }

}
