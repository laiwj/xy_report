package com.shulianxunying.resume;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by SuChang on 2017/5/8 15:12.
 */
public class ResumeCityFunc implements Serializable, Cloneable {
    private String func = "unknow";
    private String second_level = "unknow";
    private String position = "unknow";
    private String industry = "unknow";
    private HashSet<String> expect_city = new HashSet<>();
    private HashSet<String> living_city = new HashSet<>();
    private HashSet<String> hometown_city = new HashSet<>();

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    public ResumeCityFunc clone() throws CloneNotSupportedException {
        return (ResumeCityFunc) super.clone();
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getSecond_level() {
        return second_level;
    }

    public void setSecond_level(String second_level) {
        this.second_level = second_level;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public HashSet<String> getExpect_city() {
        return expect_city;
    }

    public void setExpect_city(HashSet<String> expect_city) {
        this.expect_city = expect_city;
    }

    public HashSet<String> getLiving_city() {
        return living_city;
    }

    public void setLiving_city(HashSet<String> living_city) {
        this.living_city = living_city;
    }

    public HashSet<String> getHometown_city() {
        return hometown_city;
    }

    public void setHometown_city(HashSet<String> hometown_city) {
        this.hometown_city = hometown_city;
    }
}
