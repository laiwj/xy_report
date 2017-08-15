package com.shulianxunying.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuChang on 2017/5/22 17:01.
 */
public class CityEntity {
    String country = "unknow";
    String province = "unknow";
    String city = "unknow";
    List<String> area = new ArrayList<>();

    public CityEntity() {
    }

    public CityEntity(String country, String province, String city, String area) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.area.add(area);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }
}