package com.shulianxunying.position;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by SuChang on 2017/6/2 15:09.
 */
public class PositionPathEntity implements Serializable {
    int stage;
    String profession_name;
    String pre_postion;
    String post_postion;

    public PositionPathEntity(int stage, String profession_name, String pre_postion, String post_postion) {
        this.stage = stage;
        this.profession_name = profession_name;
        this.pre_postion = pre_postion;
        this.post_postion = post_postion;
    }

    public PositionPathEntity(int stage, String pre_postion, String post_postion) {
        this.stage = stage;
        this.pre_postion = pre_postion;
        this.post_postion = post_postion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PositionPathEntity that = (PositionPathEntity) o;

        return new EqualsBuilder()
                .append(stage, that.stage)
                .append(profession_name, that.profession_name)
                .append(pre_postion, that.pre_postion)
                .append(post_postion, that.post_postion)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(stage)
                .append(profession_name)
                .append(pre_postion)
                .append(post_postion)
                .toHashCode();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public String getPre_postion() {
        return pre_postion;
    }

    public void setPre_postion(String pre_postion) {
        this.pre_postion = pre_postion;
    }

    public String getPost_postion() {
        return post_postion;
    }

    public void setPost_postion(String post_postion) {
        this.post_postion = post_postion;
    }

    public String getProfession_name() {
        return profession_name;
    }

    public void setProfession_name(String profession_name) {
        this.profession_name = profession_name;
    }
}
