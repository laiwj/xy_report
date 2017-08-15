package com.shulianxunying.service;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import org.bson.Document;

/**
 * Created by zhang on 2017/6/12.
 * 人才雷达接口
 */
public interface IDataRadarService {
    Model check_update(String start_time, Integer t);

    Model talent_flow(Document query, String key, String api_url, JSONObject params);

    Model talent_demand_distribute(String api_url, Document query, JSONObject params);

    Model talent_distribute(String api_url, JSONObject params);
}
