package com.wahana.wahanamobile.Data;

/**
 * Created by donay on 22/04/20.
 */

public class ListMsSTSM {

    private String noMs;

    private String noMa;

    public ListMsSTSM(){}

    public ListMsSTSM(String noMs){
        this.noMs = noMs;
    }

    public ListMsSTSM(String noMs,String noMa){
        this.noMs = noMs;
        this.noMa = noMa;
    }

    public String getNoMs() {
        return noMs;
    }

    public void setNoMs(String noMs) {
        this.noMs = noMs;
    }

    public String getNoMa() {
        return noMa;
    }

    public void setNoMa(String noMa) {
        this.noMa = noMa;
    }


}
