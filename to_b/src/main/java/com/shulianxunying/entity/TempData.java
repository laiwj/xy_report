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
    String _id;// 根据 api_url 和 params 生成的id
    String collection; // 数据结果 对应的源数据表名
    String api_url; // 接口地址
    JSONObject params; //参数
    List<Document> data;
    Integer t;// 1:周 2：月 3：季 4：年 目前只用 2 3 4
    String start_time;
    String end_time;

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
}
