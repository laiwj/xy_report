package com.shulianxunying.chengdu;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by SuChang on 2017/5/22 9:46.
 */
public class ResumeFlowEntity implements Serializable {
    private HashSet<ResumeArea> expect_city;
    private HashSet<ResumeArea> living_city;
    private HashSet<ResumeArea> hometown_city;

    public HashSet<ResumeArea> getExpect_city() {
        return expect_city;
    }

    public void setExpect_city(HashSet<ResumeArea> expect_city) {
        this.expect_city = expect_city;
    }

    public HashSet<ResumeArea> getLiving_city() {
        return living_city;
    }

    public void setLiving_city(HashSet<ResumeArea> living_city) {
        this.living_city = living_city;
    }

    public HashSet<ResumeArea> getHometown_city() {
        return hometown_city;
    }

    public void setHometown_city(HashSet<ResumeArea> hometown_city) {
        this.hometown_city = hometown_city;
    }
}
