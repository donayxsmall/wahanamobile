package com.wahana.wahanamobile.Data;

/**
 * Created by donay on 26/08/20.
 */

public class ListTtkSTPUBERAT {

    private String ttk;
    private String id;
    private String nikpickup;
    private String kodesortir;
    private String berat;
    private String panjang;
    private String lebar;
    private String tinggi;
    private String tgl;

    public ListTtkSTPUBERAT(){}

    public ListTtkSTPUBERAT(String ttk,String kodesortir){
        this.ttk = ttk;
        this.kodesortir = kodesortir;
    }

    public ListTtkSTPUBERAT(String ttk){
        this.ttk = ttk;
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

    public String getNikpickup() {
        return nikpickup;
    }

    public void setNikpickup(String nikpickup) {
        this.nikpickup = nikpickup;
    }

    public String getKodesortir() {
        return kodesortir;
    }

    public void setKodesortir(String kodesortir) {
        this.kodesortir = kodesortir;
    }

    public String getBerat() {
        return berat;
    }

    public void setBerat(String berat) {
        this.berat = berat;
    }

    public String getPanjang() {
        return panjang;
    }

    public void setPanjang(String panjang) {
        this.panjang = panjang;
    }

    public String getLebar() {
        return lebar;
    }

    public void setLebar(String lebar) {
        this.lebar = lebar;
    }

    public String getTinggi() {
        return tinggi;
    }

    public void setTinggi(String tinggi) {
        this.tinggi = tinggi;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }
}
