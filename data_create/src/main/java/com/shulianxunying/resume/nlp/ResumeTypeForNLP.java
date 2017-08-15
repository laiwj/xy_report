package com.shulianxunying.resume.nlp;

import com.alibaba.fastjson.JSON;
import com.shulianxunying.resume.Area;
import com.shulianxunying.resume.ResumeConditon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by SuChang on 2017/3/8 10:55.
 */
public class ResumeTypeForNLP implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private HashSet<Area> expect_city;
    private HashSet<Area> living_city;
    private HashSet<Area> hometown_city;
    private String func;
    private String second_level;
    private String industry;
    private String position;
    private String workyear;
    private String degree;
    private String gender;
    private String age;
    private String expect_salary;
    private String last_salary;
    private String level;
    private String max_salary;
    private String min_salary;
    private String keywords;
    private HashSet<String> last_job;
    private HashSet<String> ik_job;

    @Override
    protected ResumeTypeForNLP clone() throws CloneNotSupportedException {
        return (ResumeTypeForNLP) super.clone();
    }

    public ResumeTypeForNLP() {
        expect_city = new HashSet<>();
        living_city = new HashSet<>();
        hometown_city = new HashSet<>();
        func = "unknown";
        second_level = "unknown";
        industry = "unknown";
        position = "unknown";
        workyear = "unknown";
        degree = "unknown";
        gender = "unknown";
        age = "unknown";
        expect_salary = "unknown";
        last_salary = "unknown";
        level = "unknown";
        max_salary = "unknown";
        min_salary = "unknown";
        keywords = "";
        last_job = new HashSet<>();
        ik_job = new HashSet<>();
    }

    public ArrayList<ResumeConditon> resumeType2ResumeConditons() {
        ArrayList<ResumeConditon> out = new ArrayList<ResumeConditon>();
        expect_city.addAll(living_city);
        expect_city.addAll(hometown_city);
        expect_city.remove("unknown");
        last_job.remove("unknown");
        if (expect_city.size() > 0) {
            for (Area city : expect_city) {
                if (last_job.size() > 0) {
                    for (String p : last_job) {
                        ResumeConditon condition = null;
                        if (this.getExpect_salary() != null) {
                            condition = new ResumeConditon(
                                    city, p,
                                    this.getWorkyear(),
                                    this.getDegree(),
                                    this.getGender(),
                                    this.getAge(),
                                    this.getExpect_salary());
                        } else {
                            condition = new ResumeConditon(
                                    city, p,
                                    this.getWorkyear(),
                                    this.getDegree(),
                                    this.getGender(),
                                    this.getAge(),
                                    this.getLast_salary());
                        }
                        out.add(condition);
                    }
                } else {
                    ResumeConditon condition = null;
                    if (this.getExpect_salary() != null) {
                        condition = new ResumeConditon(
                                city, "unknown",
                                this.getWorkyear(),
                                this.getDegree(),
                                this.getGender(),
                                this.getAge(),
                                this.getExpect_salary());
                    } else {
                        condition = new ResumeConditon(
                                city, "unknown",
                                this.getWorkyear(),
                                this.getDegree(),
                                this.getGender(),
                                this.getAge(),
                                this.getLast_salary());
                    }
                    out.add(condition);
                }
            }
        } else {
            if (last_job.size() > 0) {
                for (String p : last_job) {
                    ResumeConditon condition = null;
                    if (this.getExpect_salary() != null) {
                        condition = new ResumeConditon(
                                new Area(), p,
                                this.getWorkyear(),
                                this.getDegree(),
                                this.getGender(),
                                this.getAge(),
                                this.getExpect_salary());
                    } else
                        condition = new ResumeConditon(
                                new Area(),
                                p,
                                this.getWorkyear(),
                                this.getDegree(),
                                this.getGender(),
                                this.getAge(),
                                this.getLast_salary());
                    out.add(condition);
                }
            } else {
                ResumeConditon condition = null;
                if (this.getExpect_salary() != null) {
                    condition = new ResumeConditon(
                            new Area(),
                            "unknown",
                            this.getWorkyear(),
                            this.getDegree(),
                            this.getGender(),
                            this.getAge(),
                            this.getExpect_salary());
                } else {
                    condition = new ResumeConditon(
                            new Area(),
                            "unknown",
                            this.getWorkyear(),
                            this.getDegree(),
                            this.getGender(),
                            this.getAge(),
                            this.getLast_salary());
                }
                out.add(condition);
            }
        }
        return out;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public ResumeTypeForNLP union(ResumeTypeForNLP other) {
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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMax_salary() {
        return max_salary;
    }

    public void setMax_salary(String max_salary) {
        this.max_salary = max_salary;
    }

    public String getMin_salary() {
        return min_salary;
    }

    public void setMin_salary(String min_salary) {
        this.min_salary = min_salary;
    }
}
