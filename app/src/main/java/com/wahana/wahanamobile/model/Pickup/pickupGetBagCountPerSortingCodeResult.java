package com.wahana.wahanamobile.model.Pickup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.wahana.wahanamobile.model.ResultTracking;

import java.util.List;

/**
 * Created by team-it on 09/11/18.
 */

public class pickupGetBagCountPerSortingCodeResult {


    @SerializedName("resCode")
    @Expose
    private String resCode;

    @SerializedName("resText")
    @Expose
    private String resText;


    @SerializedName("bagCountList")
    @Expose
    private List<pickupGetBagCountPerSortingCodeValue> bagCountList = null;

    public List<pickupGetBagCountPerSortingCodeValue> getBagCountList() {
        return bagCountList;
    }

    public String getResCode() {
        return resCode;
    }

    public String getResText() {
        return resText;
    }
}
