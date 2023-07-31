package com.wahana.wahanamobile.ModelApiOPS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 07/11/19.
 */

public class aoAuthUser {

    ///RESPONSE

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("data")
    @Expose
    private aoAuthUser_Result data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public aoAuthUser_Result getData() {
        return data;
    }

    public void setData(aoAuthUser_Result data) {
        this.data = data;
    }









}
