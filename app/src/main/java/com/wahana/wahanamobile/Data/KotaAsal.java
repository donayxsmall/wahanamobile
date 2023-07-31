package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 6/27/2016.
 */
public class KotaAsal {
    String _id_kota;
    String _kotaasal;
    String _namelong;

    public KotaAsal(){
    }

    public KotaAsal(String id_kota, String kotaasal, String namelong){
        this._id_kota = id_kota;
        this._kotaasal = kotaasal;
        this._namelong = namelong;
    }

    public String getId(){
        return this._id_kota;
    }

    public void setId(String id_kota){
        this._kotaasal = id_kota;
    }

    public String getKotaAsal(){
        return this._kotaasal;
    }

    public void setKotaAsal(String kotaasal){
        this._kotaasal = kotaasal;
    }
}
