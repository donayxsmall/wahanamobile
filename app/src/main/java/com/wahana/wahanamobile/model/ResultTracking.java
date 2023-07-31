package com.wahana.wahanamobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by team-it on 09/10/18.
 */

public class ResultTracking {

    @SerializedName("Tanggal")
    @Expose
    private String Tanggal;

    @SerializedName("StatusInternal")
    @Expose
    private String statusinternal;

    @SerializedName("TrackStatusNama")
    @Expose
    private String trackstatusnama;


    public String getTanggalHistory() {
        return Tanggal;
    }

    public String getStatusinternal() {
        return statusinternal;
    }

    public String getTrackstatusnama() {
        return trackstatusnama;
    }
}
