package com.wahana.wahanamobile.Data;

/**
 * Created by donay on 05/07/20.
 */

public class ListTtkSTPUMA {
    private String ttk;
    private String id;
    private String niksupir;
    private String kodesortir;
    private String tgl;

    public ListTtkSTPUMA(){}

    public ListTtkSTPUMA(String ttk,String kodesortir){
        this.ttk = ttk;
        this.kodesortir = kodesortir;
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

    public String getNiksupir() {
        return niksupir;
    }

    public void setNiksupir(String niksupir) {
        this.niksupir = niksupir;
    }

    public String getKodesortir() {
        return kodesortir;
    }

    public void setKodesortir(String kodesortir) {
        this.kodesortir = kodesortir;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }



}
