package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 8/15/2016.
 */
public class SuratJalan {
    int _id, _total, _terkirim, _belum, _aktif, _serah;
    String _sj, _tgl;

    public SuratJalan(){
    }

    public SuratJalan(String sj, String tgl, int total, int terkirim, int belum, int aktif, int serah){
        this._sj = sj;
        this._tgl = tgl;
        this._total = total;
        this._terkirim = terkirim;
        this._belum = belum;
        this._aktif = aktif;
        this._serah = serah;
    }

    public String getSJ(){
        return this._sj;
    }

    public void setSJ(String sj){
        this._sj = sj;
    }

    public String getTgl(){
        return this._tgl;
    }

    public void setTgl(String tgl){
        this._tgl = tgl;
    }

    public int getTotal(){
        return this._total;
    }

    public void setTotal(int total){
        this._total = total;
    }

    public int getTerkirim(){
        return this._terkirim;
    }

    public void setTerkirim(int terkirim){
        this._terkirim = terkirim;
    }

    public int getBelum(){
        return this._belum;
    }

    public void setBelum(int belum){
        this._belum = belum;
    }

    public int getAktif(){
        return this._aktif;
    }

    public void setAktif(int aktif){
        this._aktif = aktif;
    }

    public int getSerah(){
        return this._serah;
    }

    public void setSerah(int serah){
        this._serah = serah;
    }
}
