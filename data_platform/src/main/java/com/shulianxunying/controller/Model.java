package com.shulianxunying.controller;

import com.alibaba.fastjson.JSONObject;

public class Model {
    private Integer code;
    private String msg;
    private Object data = null;

    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }

    public Model() {
        this.code = 0; //RESULTCODE_SUCCESS代表0,登录成功
        this.msg = "OK";
    }

    public Model(Integer code) {
        this.code = code; //RESULTCODE_SUCCESS代表0,登录成功
        this.msg = EnumResultCode.getMsgByCode(code);
    }

    public Model(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Model(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public void setCode(Integer code) {
        this.code = code;
        this.msg = EnumResultCode.getMsgByCode(code);
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public Model setData(Object data) {
        this.data = data;
        return this;
    }

}
