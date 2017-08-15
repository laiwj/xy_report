package com.shulianxunying.entity.impl;

import com.shulianxunying.entity.ApiData;

import java.util.Date;

/**
 * Created by zhang on 2017/5/31.
 */
public class ApiTempData extends ApiData {
    Integer total_data = 0;     // 总数据量
    Integer accord_data = 0;    // 符合数据量
    String end_time;        // 结束时间
    String api_time;        // 请求api时间

    public String getApi_time() {
        return api_time;
    }

    public void setApi_time(String api_time) {
        this.api_time = api_time;
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

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
