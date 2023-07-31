package com.wahana.wahanamobile.Data;

public class Role {
    private String code, name;

    public Role(){}

    public Role(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String toString()
    {
        return(name);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code= code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
