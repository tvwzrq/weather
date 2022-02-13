package com.example.weather.db;

import org.litepal.crud.LitePalSupport;

public class Countydb extends LitePalSupport {
    private  String county;
    private int cityid;
    private String weatherid;

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public void setWeatherid(String weatherid) {
        this.weatherid = weatherid;
    }

    public int getCityid() {
        return cityid;
    }

    public String getCounty() {
        return county;
    }

    public String getWeatherid() {
        return weatherid;
    }
}
