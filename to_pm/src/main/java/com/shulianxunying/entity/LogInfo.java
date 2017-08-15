package com.shulianxunying.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Map;

/**
 * Created by SuChang on 2017/4/27 9:11.
 */
public class LogInfo {
    @JSONField(name = "_id")
    String _id;
    String ip;
    String user_id;
    Map<String,String> params;
    String execMethod;
    Date inputTime;
    Date returnTime;
    Double consumeTime;
    String result;
    String e;
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getExecMethod() {
        return execMethod;
    }

    public void setExecMethod(String execMethod) {
        this.execMethod = execMethod;
    }

    public Date getInputTime() {
        return inputTime;
    }

    public void setInputTime(Date inputTime) {
        this.inputTime = inputTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public Double getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Double consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
