package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 6/27/2016.
 */
public class AgentCode {
    int _id_agent;
    String _agentcode;

    public AgentCode(){
    }

    public AgentCode(String agentcode){
        this._agentcode = agentcode;;
    }

    public String getAgentCode(){
        return this._agentcode;
    }

    public void setAgentCode(String agentcode){
        this._agentcode = agentcode;
    }
}
