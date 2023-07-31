package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 6/27/2016.
 */
public class TTk {
    int _id_ttk,  _status;
    String _nottk, _kodesortir;

    public TTk(){
    }

    public TTk(String nottk, int status, String kodesortir){
        this._nottk = nottk;
        this._status = status;
        this._kodesortir = kodesortir;
    }

    public String getKodesortir(){
        return this._kodesortir;
    }

    public void setKodesortir(String kodesortir){
        this._kodesortir = kodesortir;
    }

    public String getNottk(){
        return this._nottk;
    }

    public void setNottk(String nottk){
        this._nottk = nottk;
    }

    public int getStatus(){
        return this._status;
    }

    public void setStatus(int status){
        this._status = status;
    }
}
