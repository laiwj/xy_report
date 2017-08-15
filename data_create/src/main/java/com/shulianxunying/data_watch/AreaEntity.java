package com.shulianxunying.data_watch;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by SuChang on 2017/5/22 16:09.
 */
public class AreaEntity implements Serializable {
    String province = "unknow";
    String city = "unknow";
    String area = "unknow";
    int type = -1; //0:国 1：省 2：市 3：区

    public AreaEntity() {
    }

    public AreaEntity(String province, String city, String area) {
        this.province = province;
        this.city = city;
        this.area = area;
    }

    public AreaEntity(String province, String city, String area, int type) {
        this.province = province;
        this.city = city;
        this.area = area;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AreaEntity that = (AreaEntity) o;

        return new EqualsBuilder()
                .append(province, that.province)
                .append(city, that.city)
                .append(area, that.area)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(province)
                .append(city)
                .append(area)
                .toHashCode();
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
