package com.wahana.wahanamobile.ModelApiOPS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 11/11/19.
 */

public class aoCekAlasanRetur_Result {

    @SerializedName("idketeranganretur")
    @Expose
    private String idketeranganretur;

    @SerializedName("keteranganretur")
    @Expose
    private String keteranganretur;

    @SerializedName("ttklama")
    @Expose
    private String ttklama;

    @SerializedName("ttkretur")
    @Expose
    private String ttkretur;

    public String getIdketeranganretur() {
        return idketeranganretur;
    }

    public void setIdketeranganretur(String idketeranganretur) {
        this.idketeranganretur = idketeranganretur;
    }

    public String getKeteranganretur() {
        return keteranganretur;
    }

    public void setKeteranganretur(String keteranganretur) {
        this.keteranganretur = keteranganretur;
    }

    public String getTtklama() {
        return ttklama;
    }

    public void setTtklama(String ttklama) {
        this.ttklama = ttklama;
    }

    public String getTtkretur() {
        return ttkretur;
    }

    public void setTtkretur(String ttkretur) {
        this.ttkretur = ttkretur;
    }



}
