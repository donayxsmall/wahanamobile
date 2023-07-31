package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 4/22/2017.
 */
public class BuatSJ {
    private String ttk, id, seal, tujuan, berat;

    public BuatSJ(){}

    public BuatSJ(String ttk){
        this.ttk = ttk;
    }

    public BuatSJ(String ttk, String berat){
        this.ttk = ttk;
        this.berat = berat;
    }

    public BuatSJ(String ttk, String seal, String tujuan){
        this.ttk = ttk;
        this.seal = seal;
        this.tujuan = tujuan;
    }

    public String getTtk() {
        return ttk;
    }

    public void setTtk(String ttk) {
        this.ttk = ttk;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeal() {
        return seal;
    }

    public void setSeal(String seal) {
        this.seal = seal;
    }

    public String getTujuan() {
        return tujuan;
    }

    public void setTujuan(String tujuan) {
        this.tujuan = tujuan;
    }

    public String getBerat() {
        return berat;
    }

    public void setBerat(String berat) {
        this.berat = berat;
    }
}
