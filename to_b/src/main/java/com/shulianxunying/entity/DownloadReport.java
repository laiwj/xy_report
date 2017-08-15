package com.shulianxunying.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by SuChang on 2017/4/27 14:20.
 */
public class DownloadReport extends ReportInfo {

    private String user_id;
    private String report_name;
    private Date download_time;
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getReport_name() {
        return report_name;
    }

    public void setReport_name(String report_name) {
        this.report_name = report_name;
    }

    public Date getDownload_time() {
        return download_time;
    }

    public void setDownload_time(Date download_time) {
        this.download_time = download_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
