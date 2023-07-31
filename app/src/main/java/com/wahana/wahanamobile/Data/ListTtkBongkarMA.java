package com.wahana.wahanamobile.Data;

/**
 * Created by donay on 20/07/20.
 */

public class ListTtkBongkarMA {
    private String ttk;
    private String id;
    private String tgl;
    private String noMa;
    private String nikSTMA;

    public String getNoMa() {
        return noMa;
    }

    public void setNoMa(String noMa) {
        this.noMa = noMa;
    }

    public String getNikSTMA() {
        return nikSTMA;
    }

    public void setNikSTMA(String nikSTMA) {
        this.nikSTMA = nikSTMA;
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

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }


    public ListTtkBongkarMA(){}

    public ListTtkBongkarMA(String ttk){
        this.ttk = ttk;
    }


}
