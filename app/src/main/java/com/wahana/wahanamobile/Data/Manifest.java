package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 6/27/2016.
 */
public class Manifest {
    int _id_manifest, _nomanifest;
    String _url;

    public Manifest(){
    }

    public Manifest(int nomanifest, String url){
        this._nomanifest = nomanifest;
        this._url = url;
    }

    public int getNomanifest(){
        return this._nomanifest;
    }

    public void setNomanifest(int nomanifest){
        this._nomanifest = nomanifest;
    }

    public String getURL(){
        return this._url;
    }

    public void setURL(String url){
        this._url = url;
    }
}
