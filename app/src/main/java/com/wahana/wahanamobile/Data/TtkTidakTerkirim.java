package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 7/30/2016.
 */
public class TtkTidakTerkirim {
    String _nottk,  _alasan, _keterangan;

    public TtkTidakTerkirim(){
    }

    public TtkTidakTerkirim(String nottk){
        this._nottk = nottk;
    }

    public TtkTidakTerkirim(String nottk, String alasan, String keterangan){
        this._nottk = nottk;
        this._alasan = alasan;
        this._keterangan = keterangan;
    }

    public String getNoTtk(){
        return this._nottk;
    }

    public void setNoTtk(String nottk){
        this._nottk = nottk;
    }

    public String getAlasan(){
        return this._alasan;
    }

    public void setAlasan(String alasan){
        this._alasan = alasan;
    }

    public String getKeterangan(){
        return this._keterangan;
    }

    public void setKeterangan(String keterangan){
        this._keterangan = keterangan;
    }
}
