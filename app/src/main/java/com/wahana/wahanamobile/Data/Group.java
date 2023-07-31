package com.wahana.wahanamobile.Data;

/**
 * Created by Sadewa on 6/27/2016.
 */
public class Group {
    int _id_group;
    String _group;

    public Group(){
    }

    public Group(String group){
        this._group = group;;
    }

    public String getGroup(){
        return this._group;
    }

    public void setGroup(String group){
        this._group = group;
    }
}
