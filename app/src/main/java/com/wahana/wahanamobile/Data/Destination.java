package com.wahana.wahanamobile.Data;

public class Destination {
    private String province, city, distric;

    public Destination(){}

    public Destination(String province, String city, String distric){
        this.province = province;
        this.city = city;
        this.distric = distric;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistric() {
        return distric;
    }

    public void setDistric(String distric) {
        this.distric = distric;
    }
}
