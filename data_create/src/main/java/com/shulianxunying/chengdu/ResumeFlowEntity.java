package com.shulianxunying.chengdu;

import com.shulianxunying.resume.Area;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by SuChang on 2017/5/22 9:46.
 */
public class ResumeFlowEntity implements Serializable {
    private HashSet<Area> expect_city;
    private HashSet<Area> living_city;
    private HashSet<Area> hometown_city;

    public HashSet<Area> getExpect_city() {
        return expect_city;
    }

    public void setExpect_city(HashSet<Area> expect_city) {
        this.expect_city = expect_city;
    }

    public HashSet<Area> getLiving_city() {
        return living_city;
    }

    public void setLiving_city(HashSet<Area> living_city) {
        this.living_city = living_city;
    }

    public HashSet<Area> getHometown_city() {
        return hometown_city;
    }

    public void setHometown_city(HashSet<Area> hometown_city) {
        this.hometown_city = hometown_city;
    }
}
