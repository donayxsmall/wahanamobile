package com.wahana.wahanamobile.model.Pickup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by team-it on 09/11/18.
 */

public class pickupGetBagCountPerSortingCodeValue {

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

    //RESPONSE
    @SerializedName("sortingCode")
    @Expose
    private String sortingCode;

    @SerializedName("postingDateMin")
    @Expose
    private String postingDateMin;

    @SerializedName("postingDateMax")
    @Expose
    private String postingDateMax;

    @SerializedName("bagCountVerified")
    @Expose
    private String bagCountVerified;

    @SerializedName("bagCountUnverified")
    @Expose
    private String bagCountUnverified;

    @SerializedName("bagCountNoVerify")
    @Expose
    private String bagCountNoVerify;

    @SerializedName("bagCountTotal")
    @Expose
    private String bagCountTotal;




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

    public String getBagCountVerified() {
        return bagCountVerified;
    }

    public String getBagCountUnverified() {
        return bagCountUnverified;
    }

    public String getBagCountNoVerify() {
        return bagCountNoVerify;
    }

    public String getBagCountTotal() {
        return bagCountTotal;
    }


}
