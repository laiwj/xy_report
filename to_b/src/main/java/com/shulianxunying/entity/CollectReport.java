package com.shulianxunying.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by SuChang on 2017/4/27 14:19.
 */
public class CollectReport extends ReportInfo {

    private String user_id = "";
    private String report_name = "";
    private Date collect_time;
    private Object data;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReport_name() {
        return report_name;
    }

    public void setReport_name(String report_name) {
        this.report_name = report_name;
    }

    public Date getCollect_time() {
        return collect_time;
    }

    public void setCollect_time(Date collect_time) {
        this.collect_time = collect_time;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
