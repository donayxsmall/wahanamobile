package com.wahana.wahanamobile.Data;

/**
 * Created by team-it on 04/10/18.
 */

public class ListTtkPickup {

    private String ttk, id, noref,kodeagen,kodeverifikasi,niksupir,tgl,kodeopsttk,kodesortir,kodesortirstpu;

    private String status;

    public ListTtkPickup(){}

    public ListTtkPickup(String ttk, String id, String noref,String kodeagen,String kodeverifikasi,String niksupir,String tgl,String kodeopsttk,String kodesortir){
        this.ttk = ttk;
        this.id = id;
        this.noref = noref;
        this.kodeagen = kodeagen;
        this.kodeverifikasi = kodeverifikasi;
        this.niksupir=niksupir;
        this.tgl=tgl;
        this.kodeopsttk=kodeopsttk;
        this.kodesortir=kodesortir;
    }

    public ListTtkPickup(String ttk){
        this.ttk = ttk;
    }


    public ListTtkPickup(String ttk,String kodeopsttk,String kodesortir){
        this.ttk = ttk;
        this.kodeopsttk=kodeopsttk;
        this.kodesortir=kodesortir;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId(){
        return id;
    }

    public String getTtk(){
        return ttk;
    }

    public String getNoref(){
        return noref;
    }

    public String getKodeagen(){
        return kodeagen;
    }

    public String getKodeverifikasi(){
        return kodeverifikasi;
    }

    public String getNiksupir(){
        return niksupir;
    }

    public String getTgl(){
        return tgl;
    }

    public String getKodeopsttk(){
        return kodeopsttk;
    }

    public String getKodesortir(){
        return kodesortir;
    }

    public String getKodesortirstpu(){
        return kodesortirstpu;
    }


    public void setTtk(String ttk) {
        this.ttk = ttk;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNoref(String noref) {
        this.noref = noref;
    }

    public void setKodeagen(String kodeagen) {
        this.kodeagen = kodeagen;
    }

    public void setKodeverifikasi(String kodeverifikasi) {
        this.kodeverifikasi = kodeverifikasi;
    }
    public void setNiksupir(String niksupir) {
        this.niksupir = niksupir;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public void setKodeopsttk(String kodeopsttk) {
        this.kodeopsttk = kodeopsttk;
    }

    public void setKodesortir(String kodesortir) {
        this.kodesortir = kodesortir;
    }

    public void setKodesortirstpu(String kodesortirstpu) {
        this.kodesortirstpu = kodesortirstpu;
    }


}
