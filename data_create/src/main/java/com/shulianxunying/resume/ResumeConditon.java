package com.shulianxunying.resume;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by SuChang on 2017/3/8 17:28.
 */
public class ResumeConditon implements Serializable {
    private static final long serialVersionUID = 1L;

    String city;
    String industry;
    String position;
    String workyear;
    String degree;
    String gender;
    String age;
    String salary;

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(city);
        builder.append(position);
        builder.append(workyear);
        builder.append(degree);
        builder.append(gender);
        builder.append(age);
        builder.append(salary);
        builder.append(industry);
        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        ResumeConditon obj1 = (ResumeConditon) obj;
        boolean equals = StringUtils.equals(this.city, obj1.getCity());
        boolean equals2 = StringUtils.equals(this.position, obj1.getPosition());
        boolean equals3 = StringUtils.equals(this.workyear, obj1.getWorkyear());
        boolean equals4 = StringUtils.equals(this.degree, obj1.getDegree());
        boolean equals5 = StringUtils.equals(this.gender, obj1.getGender());
        boolean equals6 = StringUtils.equals(this.age, obj1.getAge());
        boolean equals7 = StringUtils.equals(this.salary, obj1.getSalary());
        if (equals && equals2 && equals3 && equals4 && equals5 && equals6 && equals7)
            return true;
        return false;
    }


    public ResumeConditon() {
    }

    public ResumeConditon(String city, String position, String workyear, String degree, String gender, String age, String salary) {
        this.city = city;
        this.position = position;
        this.workyear = workyear;
        this.degree = degree;
        this.gender = gender;
        this.age = age;
        this.salary = salary;
    }

    public ResumeConditon(String workyear, String degree, String gender, String age, String salary) {
        this.workyear = workyear;
        this.degree = degree;
        this.gender = gender;
        this.age = age;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
