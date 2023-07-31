package com.wahana.wahanamobile.model.Pickup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by team-it on 09/11/18.
 */

public class PickupVerify {

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

    @SerializedName("verificationCode")
    @Expose
    private String verificationCode;

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

    public String getVerificationCode() {
        return verificationCode;
    }

    public String getResCode() {
        return resCode;
    }

    public String getResText() {
        return resText;
    }
}
