package com.wahana.wahanamobile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by team-it on 09/10/18.
 */

public class ApiPickup {

    @SerializedName("employeeCode")
    @Expose
    private String employeeCode;

    @SerializedName("agenCode")
    @Expose
    private String agenCode;

    @SerializedName("sessionId")
    @Expose
    private String sessionId;

    @SerializedName("requestId")
    @Expose
    private String requestId;


    @SerializedName("verificationCode")
    @Expose
    private String verificationCode;

    @SerializedName("vrcode")
    @Expose
    private String vrcode;

    @SerializedName("vrmesg")
    @Expose
    private String vrmesg;

    @SerializedName("vUrl")
    @Expose
    private String vUrl;

    @SerializedName("ttkpu")
    @Expose
    private String ttkpu;

    @SerializedName("ATRBTID")
    @Expose
    private String ATRBTID;

    @SerializedName("kurir")
    @Expose
    private String kurir;

    @SerializedName("data")
    @Expose
    private List<resultSTM> data = null;

    @SerializedName("nostm")
    @Expose
    private String nostm;

    @SerializedName("kodesortir")
    @Expose
    private String kodesortir;

    @SerializedName("vFile")
    @Expose
    private String vFile;




    public ApiPickup(String employeeCode, String agenCode,String sessionId,String requestId ) {
        this.employeeCode = employeeCode;
        this.agenCode = agenCode;
        this.sessionId = sessionId;
        this.requestId = requestId;
    }


    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getAgenCode() {
        return agenCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRequestId() {
        return requestId;
    }


    public String getVerificationCode() {
        return verificationCode;
    }

    public String getVrcode() {
        return vrcode;
    }

    public String getVrmesg() {
        return vrmesg;
    }

    public String getvUrl() {
        return vUrl;
    }

    public String getATRBTID() {
        return ATRBTID;
    }

    public String getTtkPU() {
        return ttkpu;
    }

    public String getKurir() {
        return kurir;
    }

    public List<resultSTM> getData() {
        return data;
    }

    public String getNostm() {
        return nostm;
    }

    public String getvFile() {
        return vFile;
    }



    @Override
    public String toString() {
        return "nostm{" +
                ", stm=" + data +
                '}';
    }



}
