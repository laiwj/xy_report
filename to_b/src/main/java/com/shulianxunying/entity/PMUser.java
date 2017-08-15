package com.shulianxunying.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.HashSet;

/**
 * PM端用户 用户  分3个级别 超管 -- 管理 -- 业务
 * Created by suchang on 2017/4/26 0015.
 */
public class PMUser {

    @JSONField(name = "_id")
    private String _id;
    private String email;//邮箱
    private String password;//密码
    private String username;//用户名
    private String telphone;//电话
    private Integer type;//账号类型 1：PM端超管  2：PM端普管 3:业务员
    private Integer status = 0; //账号状态  -1：封停 0 待激活 1正常
    private Date createTime;//创建时间
    private String parent_id = "";//父账号id
    private HashSet<Integer> power_list;//权限列表
    private String last_ip;//最后一次登录的ip
    private String default_city;//默认城市
    private Date expire_time;//账户到期时间
    private String short_id = "";
    public PMUser() {
    }

    public PMUser(String email, String telphone, String password, String username) {
        this.email = email;
        this.telphone = telphone;
        this.password = password;
        this.username = username;
    }

    public String getShort_id() {
        return short_id;
    }

    public void setShort_id(String short_id) {
        this.short_id = short_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDefault_city() {
        return default_city;
    }

    public void setDefault_city(String default_city) {
        this.default_city = default_city;
    }


    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public HashSet<Integer> getPower_list() {
        return power_list;
    }

    public void setPower_list(HashSet<Integer> power_list) {
        this.power_list = power_list;
    }

    public String getLast_ip() {
        return last_ip;
    }

    public void setLast_ip(String last_ip) {
        this.last_ip = last_ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Date expire_time) {
        this.expire_time = expire_time;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
