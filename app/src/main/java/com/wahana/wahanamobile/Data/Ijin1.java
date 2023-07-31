package com.wahana.wahanamobile.Data;

/**
 * Created by team-it on 26/06/18.
 */

public class Ijin1 {
    private String jenis, type, id,jumlah;

    public Ijin1(){}

    public Ijin1(String jenis, String type, String id, String jumlah){
        this.jenis = jenis;
        this.type = type;
        this.id = id;
        this.jumlah = jumlah;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

}
