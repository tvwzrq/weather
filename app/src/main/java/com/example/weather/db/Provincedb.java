package com.example.weather.db;

import org.litepal.crud.LitePalSupport;

public class Provincedb extends LitePalSupport {
    private  String province;
    private int provinceid;

    public void setProvince(String province) {
        this.province = province;
    }

    public void setProvinceid(int provinceid) {
        this.provinceid = provinceid;
    }

    public int getProvinceid() {
        return provinceid;
    }

    public String getProvince() {
        return province;
    }
}
