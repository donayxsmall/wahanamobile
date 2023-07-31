package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 8/15/2016.
 */
public class ListTTK {
    int _id, _koli, _berat;
    String _noTTK, _status, _tgl;

    public ListTTK(){
    }

    public ListTTK(String nottk, int koli, int berat, String status, String tgl){
        this._noTTK = nottk;
        this._koli = koli;
        this._berat = berat;
        this._status = status;
        this._tgl = tgl;
    }

    public String getNoTTK(){
        return this._noTTK;
    }

    public void setNoTTK(String nottk){
        this._noTTK = nottk;
    }

    public int getKoli(){
        return this._koli;
    }

    public void setKoli(int koli){
        this._koli = koli;
    }

    public int getBerat(){
        return this._berat;
    }

    public void setBerat(int berat){
        this._berat = berat;
    }

    public String getStatus(){
        return this._status;
    }

    public void setStatus(String status){
        this._status = status;
    }

    public String getTgl(){
        return this._tgl;
    }

    public void setTgl(String tgl){
        this._tgl = tgl;
    }
}
