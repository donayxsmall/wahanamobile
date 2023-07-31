package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 6/27/2016.
 */
public class KodeSortir {
    int _id_kodesortir, _jumlahttk, _verify, _unverify, _noverify;
    String _status, _kodesortir, _datemin, _datemax, _input;

    public KodeSortir(){
    }

    public KodeSortir(String id, int jumlah,int verify){
        this._kodesortir = id;
        this._jumlahttk = jumlah;
        this._verify = verify;

    }

    public KodeSortir(String kodesortir, int verify, int unverify, int noverify, String datemin, String datemax, int jumlahttk, String status, String input){
        this._kodesortir = kodesortir;
        this._verify = verify;
        this._noverify = noverify;
        this._unverify = unverify;
        this._datemin = datemin;
        this._datemax = datemax;
        this._jumlahttk = jumlahttk;
        this._status = status;
        this._input = input;
    }

    public KodeSortir(String datemin, String datemax){
        this._datemin = datemin;
        this._datemax = datemax;
    }

    public int getIdKode(){
        return this._id_kodesortir;
    }

    public void setIdKode(int id){
        this._id_kodesortir = id;
    }

    public String getKodesortir(){
        return this._kodesortir;
    }

    public void setKodesortir(String kodesortir){
        this._kodesortir = kodesortir;
    }

    public int getVerify(){
        return this._verify;
    }

    public void setVerify(int verify){
        this._verify = verify;
    }

    public int getNoverify(){
        return this._noverify;
    }

    public void setNoverify(int noverify){
        this._verify = noverify;
    }

    public int getUnverify(){
        return this._unverify;
    }

    public void setUnverify(int unverify){
        this._unverify = unverify;
    }

    public String getDatemin(){
        return this._datemin;
    }

    public void setDatemin(String datemin){
        this._datemin = datemin;
    }

    public String getDatemax(){
        return this._datemax;
    }

    public void setDatemax(String datemax){
        this._datemax = datemax;
    }

    public int getJumlahttk(){
        return this._jumlahttk;
    }

    public void setJumlahttk(int jumlahttk){
        this._jumlahttk = jumlahttk;
    }

    public String getStatus(){
        return this._status;
    }

    public void setStatus(String status){
        this._status = status;
    }

    public String getInput(){
        return this._input;
    }

    public void setInput(String input){
        this._input = input;
    }
}
