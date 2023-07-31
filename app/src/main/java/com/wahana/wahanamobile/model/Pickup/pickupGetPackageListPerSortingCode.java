package com.wahana.wahanamobile.model.Pickup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by team-it on 09/11/18.
 */

public class pickupGetPackageListPerSortingCode {

    //REQUEST
    @SerializedName("sessionId")
    @Expose
    private String sessionId;

    @SerializedName("employeeCode")
    @Expose
    private String employeeCode;

    @SerializedName("agentCode")
    @Expose
    private String agentCode;

    @SerializedName("sortingCode")
    @Expose
    private String sortingCode;

    @SerializedName("postingDateMin")
    @Expose
    private String postingDateMin;

    @SerializedName("postingDateMax")
    @Expose
    private String postingDateMax;

    //RESPONSE
    @SerializedName("resCode")
    @Expose
    private String resCode;

    @SerializedName("resText")
    @Expose
    private String resText;


    public String getSessionId() {
        return sessionId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public String getSortingCode() {
        return sortingCode;
    }

    public String getPostingDateMin() {
        return postingDateMin;
    }

    public String getPostingDateMax() {
        return postingDateMax;
    }

    public String getResCode() {
        return resCode;
    }

    public String getResText() {
        return resText;
    }




}
