package com.wahana.wahanamobile.model.Pickup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by team-it on 09/11/18.
 */

public class PickupBegin {

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

    //RESPONSE

    @SerializedName("resCode")
    @Expose
    private String resCode;

    @SerializedName("resText")
    @Expose
    private String resText;

    @SerializedName("verificationCode")
    @Expose
    private String verificationCode;

    public String getSessionId() {
        return sessionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public String getResCode() {
        return resCode;
    }

    public String getResText() {
        return resText;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
