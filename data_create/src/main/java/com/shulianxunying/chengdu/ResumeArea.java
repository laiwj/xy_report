package com.shulianxunying.chengdu;

import java.io.Serializable;

/**
 * Created by SuChang on 2017/5/22 9:44.
 */
public class ResumeArea implements Serializable, Cloneable {
    String country = "unknow";
    String province = "unknow";
    String city = "unknow";
    String area = "unknow";

    @Override
    protected ResumeArea clone() throws CloneNotSupportedException {
        return (ResumeArea) super.clone();
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
