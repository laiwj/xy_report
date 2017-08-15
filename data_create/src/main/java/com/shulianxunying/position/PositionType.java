package com.shulianxunying.position;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by SuChang on 2017/3/22 15:16.
 */
public class PositionType implements Cloneable, Serializable {

    String city; // 职位城市
    String hire_count; // 招聘人数
    String salary; // 薪资
    String work_type;
    String experience;
    String company_property; //公司性质
    String company_type; // 公司行业类别
    String company_size;
    String min_education;
    String error;
    private String func = "unknow";
    private String second_level = "unknow";
    private String position = "unknow";
    private String industry = "unknow";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PositionType that = (PositionType) o;

        return new EqualsBuilder()
                .append(city, that.city)
                .append(hire_count, that.hire_count)
                .append(salary, that.salary)
                .append(work_type, that.work_type)
                .append(experience, that.experience)
                .append(company_property, that.company_property)
                .append(company_type, that.company_type)
                .append(company_size, that.company_size)
                .append(min_education, that.min_education)
                .append(error, that.error)
                .append(func, that.func)
                .append(second_level, that.second_level)
                .append(position, that.position)
                .append(industry, that.industry)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(city)
                .append(hire_count)
                .append(salary)
                .append(work_type)
                .append(experience)
                .append(company_property)
                .append(company_type)
                .append(company_size)
                .append(min_education)
                .append(error)
                .append(func)
                .append(second_level)
                .append(position)
                .append(industry)
                .toHashCode();
    }

    @Override
    protected PositionType clone() throws CloneNotSupportedException {
        return (PositionType) super.clone();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
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

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCompany_size() {
        return company_size;
    }

    public void setCompany_size(String company_size) {
        this.company_size = company_size;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHire_count() {
        return hire_count;
    }

    public void setHire_count(String hire_count) {
        this.hire_count = hire_count;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getWork_type() {
        return work_type;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCompany_property() {
        return company_property;
    }

    public void setCompany_property(String company_property) {
        this.company_property = company_property;
    }

    public String getCompany_type() {
        return company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }

    public String getMin_education() {
        return min_education;
    }

    public void setMin_education(String min_education) {
        this.min_education = min_education;
    }
}
