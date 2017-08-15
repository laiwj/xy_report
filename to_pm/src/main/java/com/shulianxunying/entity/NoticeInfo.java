package com.shulianxunying.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by SuChang on 2017/4/27 14:23.
 */
public class NoticeInfo {
    @JSONField(name = "_id")
    private String _id;
    private String user_id;
    private String msg;
    private String time;
    private String mess_type;
    private boolean isClick = false;
    private String click_time = "";

    public String getMess_type() {
        return mess_type;
    }

    public void setMess_type(String mess_type) {
        this.mess_type = mess_type;
    }

    public String getClick_time() {
        return click_time;
    }

    public void setClick_time(String click_time) {
        this.click_time = click_time;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}
