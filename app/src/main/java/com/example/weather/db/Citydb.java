package com.example.weather.db;

import org.litepal.crud.LitePalSupport;

public class Citydb extends LitePalSupport {
    private String city;
    private int provinceid;
    private  int cityid;

    public void setProvinceid(int provinceid) {
        this.provinceid = provinceid;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public int getProvinceid() {
        return provinceid;
    }

    public int getCityid() {
        return cityid;
    }

    public String getCity() {
        return city;
    }
}
