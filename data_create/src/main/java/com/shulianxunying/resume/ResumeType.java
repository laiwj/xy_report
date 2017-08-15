package com.shulianxunying.resume;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by SuChang on 2017/3/8 10:55.
 */
public class ResumeType implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashSet<String> expect_city;
    private HashSet<String> living_city;
    private HashSet<String> hometown_city;
    private String industry;
    private String position;
    private String workyear;
    private String degree;
    private String gender;
    private String age;
    private String expect_salary;
    private String last_salary;
    private HashSet<String> keywords;
    private HashSet<String> last_job;
    private HashSet<String> ik_job;

    public ArrayList<ResumeConditon> resumeType2ResumeConditons() {
        ArrayList<ResumeConditon> out = new ArrayList<ResumeConditon>();
        expect_city.addAll(living_city);
        expect_city.addAll(hometown_city);
        expect_city.remove("unknow");
        last_job.remove("unknow");
        if (expect_city.size() > 0) {
            for (String city : expect_city) {
                if (last_job.size() > 0) {
                    for (String p : last_job) {
                        ResumeConditon conditon = null;
                        if (this.getExpect_salary() != null) {
                            conditon = new ResumeConditon(
                                    city, p,
                                    this.getWorkyear(),
                                    this.getDegree(),
                                    this.getGender(),
                                    this.getAge(),
                                    this.getExpect_salary());
                        } else {
                            conditon = new ResumeConditon(
                                    city, p,
                                    this.getWorkyear(),
                                    this.getDegree(),
                                    this.getGender(),
                                    this.getAge(),
                                    this.getLast_salary());
                        }
                        out.add(conditon);
                    }
                } else {
                    ResumeConditon conditon = null;
                    if (this.getExpect_salary() != null) {
                        conditon = new ResumeConditon(
                                city, "unknow",
                                this.getWorkyear(),
                                this.getDegree(),
                                this.getGender(),
                                this.getAge(),
                                this.getExpect_salary());
                    } else {
                        conditon = new ResumeConditon(
                                city, "unknow",
                                this.getWorkyear(),
                                this.getDegree(),
                                this.getGender(),
                                this.getAge(),
                                this.getLast_salary());
                    }
                    out.add(conditon);
                }
            }
        } else {
            if (last_job.size() > 0) {
                for (String p : last_job) {
                    ResumeConditon conditon = null;
                    if (this.getExpect_salary() != null) {
                        conditon = new ResumeConditon(
                                "unknow", p,
                                this.getWorkyear(),
                                this.getDegree(),
                                this.getGender(),
                                this.getAge(),
                                this.getExpect_salary());
                    } else
                        conditon = new ResumeConditon(
                                "unknow", p,
                                this.getWorkyear(),
                                this.getDegree(),
                                this.getGender(),
                                this.getAge(),
                                this.getLast_salary());
                    out.add(conditon);
                }
            } else {
                ResumeConditon conditon = null;
                if (this.getExpect_salary() != null) {
                    conditon = new ResumeConditon(
                            "unknow", "unknow",
                            this.getWorkyear(),
                            this.getDegree(),
                            this.getGender(),
                            this.getAge(),
                            this.getExpect_salary());
                } else {
                    conditon = new ResumeConditon(
                            "unknow", "unknow",
                            this.getWorkyear(),
                            this.getDegree(),
                            this.getGender(),
                            this.getAge(),
                            this.getLast_salary());
                }
                out.add(conditon);
            }
        }
        return out;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public ResumeType union(ResumeType other) {
        if (this.expect_city == null)
            this.expect_city = other.getExpect_city();
        if (this.living_city == null)
            this.living_city = other.getLiving_city();
        if (this.hometown_city == null)
            this.hometown_city = other.getHometown_city();
        if (this.industry == null)
            this.industry = other.getIndustry();
        if (this.position == null)
            this.position = other.getPosition();
        if (this.workyear == null)
            this.workyear = other.getWorkyear();
        if (this.degree == null)
            this.degree = other.getDegree();
        if (this.gender == null)
            this.gender = other.getGender();
        if (this.age == null)
            this.age = other.getAge();
        if (this.expect_salary == null)
            this.expect_salary = other.getExpect_salary();
        if (this.last_salary == null)
            this.last_salary = other.getLast_salary();
        if (this.keywords == null)
            this.keywords = other.getKeywords();
        if (this.last_job == null)
            this.last_job = other.getLast_job();
        if (this.ik_job == null)
            this.ik_job = other.getIk_job();
        return this;
    }

    public HashSet<String> getIk_job() {
        return ik_job;
    }

    public void setIk_job(HashSet<String> ik_job) {
        this.ik_job = ik_job;
    }

    public HashSet<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(HashSet<String> keywords) {
        this.keywords = keywords;
    }

    public HashSet<String> getLast_job() {
        return last_job;
    }

    public void setLast_job(HashSet<String> last_job) {
        this.last_job = last_job;
    }

    public String getLast_salary() {
        return last_salary;
    }

    public void setLast_salary(String last_salary) {
        this.last_salary = last_salary;
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

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWorkyear() {
        return workyear;
    }

    public void setWorkyear(String workyear) {
        this.workyear = workyear;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getExpect_salary() {
        return expect_salary;
    }

    public void setExpect_salary(String expect_salary) {
        this.expect_salary = expect_salary;
    }


}
