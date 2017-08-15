package com.shulianxunying.resume;

import scala.Tuple4;

import java.io.Serializable;

/**
 * Created by 19866 on 2017/6/19.
 */
public class Area implements Serializable {
    private String area;
    private String city;
    private String province;
    private String country;

    public Area() {
        area = "unknown";
        city = "unknown";
        province = "unknown";
        country = "unknown";
    }

    public Area(String area, String city, String province, String country) {
        this.area = area;
        this.city = city;
        this.province = province;
        this.country = country;
    }

    public Area(Tuple4<String, String, String, String> cityTuple) {
        this.area = cityTuple._1();
        this.city = cityTuple._2();
        this.province = cityTuple._3();
        this.country = cityTuple._4();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return area + "," + city + "," + province + "," + country;
    }
}
