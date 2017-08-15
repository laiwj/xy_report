package com.shulianxunying.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created by SuChang on 2017/5/2 15:21.
 */
public class ReportInfo {
    @JSONField(name = "_id")
    private String _id;
    private Integer report_type; // 报告类型
    /**
     * 报告1 ：city industry func
     * 报告2 ：city industry in_or_our city_or_func
     * 报告3 ：city industry func_or_position 需求or供需指数
     */
    private String api_url; // 从api平台获取报告数据的url
    private JSONObject params; // 从api平台获取报告数据的参数
    private String info = ""; //报告的解释说明
    private String pm_user_id= ""; //报告解释说明 撰写人
    private Date modify_time;

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getReport_type() {
        return report_type;
    }

    public void setReport_type(Integer report_type) {
        this.report_type = report_type;
    }

    public Date getModify_time() {
        return modify_time;
    }

    public void setModify_time(Date modify_time) {
        this.modify_time = modify_time;
    }

    public String getApi_url() {
        return api_url;
    }

    public void setApi_url(String api_url) {
        this.api_url = api_url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPm_user_id() {
        return pm_user_id;
    }

    public void setPm_user_id(String pm_user_id) {
        this.pm_user_id = pm_user_id;
    }
}
