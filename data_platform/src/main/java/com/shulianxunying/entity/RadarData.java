package com.shulianxunying.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2017/6/13.
 * 人才雷达-实体
 */
public class RadarData {
    @JSONField(name = "_id")
    String _id;
    String collection;
    String api_url;
    JSONObject params;
    Integer t;
    String year;
    String month;
    String api_time;   // 请求api时间
    List<Document> city_data = new ArrayList<>();
    List<Document> country_data = new ArrayList<>();
    List<Document> province_data = new ArrayList<>();
    List<Document> industry_data = new ArrayList<>();
    List<Document> position_data = new ArrayList<>();

    public List<Document> getCountry_data() {
        return country_data;
    }

    public void setCountry_data(List<Document> country_data) {
        this.country_data = country_data;
    }

    public List<Document> getProvince_data() {
        return province_data;
    }

    public void setProvince_data(List<Document> province_data) {
        this.province_data = province_data;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getApi_url() {
        return api_url;
    }

    public void setApi_url(String api_url) {
        this.api_url = api_url;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getApi_time() {
        return api_time;
    }

    public void setApi_time(String api_time) {
        this.api_time = api_time;
    }

    public List<Document> getCity_data() {
        return city_data;
    }

    public void setCity_data(List<Document> city_data) {
        this.city_data = city_data;
    }

    public List<Document> getIndustry_data() {
        return industry_data;
    }

    public void setIndustry_data(List<Document> industry_data) {
        this.industry_data = industry_data;
    }

    public List<Document> getPosition_data() {
        return position_data;
    }

    public void setPosition_data(List<Document> position_data) {
        this.position_data = position_data;
    }
}
