package com.wahana.wahanamobile.ModelApiOPS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by donay on 07/11/19.
 */

public class aoAuthUser_Result {


    @SerializedName("benm")
    @Expose
    private String benm;

    @SerializedName("jwt")
    @Expose
    private String jwt;

    @SerializedName("photo")
    @Expose
    private String photo;

    @SerializedName("vaksin")
    @Expose
    private String vaksin;

    public String getVaksin() {
        return vaksin;
    }

    public void setVaksin(String vaksin) {
        this.vaksin = vaksin;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBenm() {
        return benm;
    }

    public void setBenm(String benm) {
        this.benm = benm;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }


}
