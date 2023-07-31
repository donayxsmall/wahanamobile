package com.wahana.wahanamobile.ModelApiOPS.BongkarMA;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by donay on 21/07/20.
 */

public class opv2GetBongkarMAProcessList {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("data")
    @Expose
    private Data data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data{

        @SerializedName("proses")
        @Expose
        private List<Proses> proses = null;

        @SerializedName("inproses")
        @Expose
        private String inproses;

        @SerializedName("selesai")
        @Expose
        private String selesai;

        @SerializedName("target")
        @Expose
        private String target;

        public String getInproses() {
            return inproses;
        }

        public void setInproses(String inproses) {
            this.inproses = inproses;
        }

        public String getSelesai() {
            return selesai;
        }

        public void setSelesai(String selesai) {
            this.selesai = selesai;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }


        public List<Proses> getProses() {
            return proses;
        }

        public void setProses(List<Proses> proses) {
            this.proses = proses;
        }


    }

    public class Proses{

        @SerializedName("jumlah")
        @Expose
        private String jumlah;

        @SerializedName("petugas")
        @Expose
        private String petugas;

        public String getJumlah() {
            return jumlah;
        }

        public void setJumlah(String jumlah) {
            this.jumlah = jumlah;
        }

        public String getPetugas() {
            return petugas;
        }

        public void setPetugas(String petugas) {
            this.petugas = petugas;
        }

    }




}
