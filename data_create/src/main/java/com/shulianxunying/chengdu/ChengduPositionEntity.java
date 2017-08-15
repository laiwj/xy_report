package com.shulianxunying.chengdu;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by SuChang on 2017/6/13 11:38.
 */
public class ChengduPositionEntity implements Serializable, Cloneable {

    String city; // 职位城市
    String area; // 职位区
    String hire_count; // 招聘人数
    String company_type; // 公司行业类别
    String position;//

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    protected ChengduPositionEntity clone() throws CloneNotSupportedException {
        return (ChengduPositionEntity) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ChengduPositionEntity that = (ChengduPositionEntity) o;

        return new EqualsBuilder()
                .append(city, that.city)
                .append(area, that.area)
                .append(company_type, that.company_type)
                .append(position, that.position)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(city)
                .append(area)
                .append(company_type)
                .append(position)
                .toHashCode();
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

    public String getHire_count() {
        return hire_count;
    }

    public void setHire_count(String hire_count) {
        this.hire_count = hire_count;
    }

    public String getCompany_type() {
        return company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
