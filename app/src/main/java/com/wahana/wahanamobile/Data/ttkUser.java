package com.wahana.wahanamobile.Data;

public class ttkUser {
    private String noTtk, ket;

    public ttkUser(){}

    public ttkUser(String noTtk, String ket){
        this.noTtk = noTtk;
        this.ket = ket;
    }

    public String getNoTtk() {
        return noTtk;
    }

    public void setNoTtk(String noTtk) {
        this.noTtk = noTtk;
    }

    public String getKet() {
        return ket;
    }

    public void setKet(String ket) {
        this.ket = ket;
    }
}
