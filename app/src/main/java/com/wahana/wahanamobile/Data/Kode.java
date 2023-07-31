package com.wahana.wahanamobile.Data;

/**
 * Created by Reza on 11/06/2016.
 */
public class Kode {
    private  String kodeSortir;
    private  boolean isSubmitted = false;

    public Kode(String kodeSortir, boolean isSubmitted) {
        this.kodeSortir = kodeSortir;
        this.isSubmitted = isSubmitted;

    }

    public String getKodeSortir() {
        return kodeSortir;
    }

    public boolean getisSubmitted() {
        return isSubmitted;
    }

    public void setKodeSortir(String kodeSortir) {
        this.kodeSortir = kodeSortir;
    }

    public void setSubmitted(boolean isSubmitted)
    {
        this.isSubmitted = isSubmitted;
    }
}
