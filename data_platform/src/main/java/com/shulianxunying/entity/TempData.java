package com.shulianxunying.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.bson.Document;

import java.util.List;

/**
 * Created by SuChang on 2017/5/16 11:07.
 */
public class TempData {

    @JSONField(name = "_id")
    String _id;
    String collection;
    String api_url;
    JSONObject params;
    List<Document> data;
    Integer t;
    String start_time;      // 开始时间
    String end_time;        // 结束时间
    String api_time;        // 请求api时间
    Integer total_data;     // 总数据量
    Integer accord_data;    // 符合数据量

    public String getApi_time() {
        return api_time;
    }

    public void setApi_time(String api_time) {
        this.api_time = api_time;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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

    public List<Document> getData() {
        return data;
    }

    public void setData(List<Document> data) {
        this.data = data;
    }

    public Integer getTotal_data() {
        return total_data;
    }

    public void setTotal_data(Integer total_data) {
        this.total_data = total_data;
    }

    public Integer getAccord_data() {
        return accord_data;
    }

    public void setAccord_data(Integer accord_data) {
        this.accord_data = accord_data;
    }
}
