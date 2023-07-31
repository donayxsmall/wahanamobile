package com.wahana.wahanamobile.model.Pickup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by team-it on 09/11/18.
 */

public class pickupVerPackage {

    //REQUEST
    @SerializedName("sessionId")
    @Expose
    private String sessionId;

    @SerializedName("requestId")
    @Expose
    private String requestId;

    @SerializedName("employeeCode")
    @Expose
    private String employeeCode;

    @SerializedName("agentCode")
    @Expose
    private String agentCode;

    @SerializedName("sortingCode")
    @Expose
    private String sortingCode;

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

    public String getRequestId() {
        return requestId;
    }

    public String getEmployeeCode() {
        return sessionId;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public String getSortingCode() {
        return sortingCode;
    }

    public String getResCode() {
        return resCode;
    }

    public String getResText() {
        return resText;
    }


}
