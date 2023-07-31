package com.wahana.wahanamobile.Data;

/**
 * Created by donay on 02/05/20.
 */

public class ListTtkSTMS {

    private String noMs;
    private String noTtk;

    public ListTtkSTMS(String noTtk){
        this.noTtk = noTtk;
    }
    public ListTtkSTMS(){}

    public String getNoMs() {
        return noMs;
    }

    public void setNoMs(String noMs) {
        this.noMs = noMs;
    }

    public String getNoTtk() {
        return noTtk;
    }

    public void setNoTtk(String noTtk) {
        this.noTtk = noTtk;
    }

}
