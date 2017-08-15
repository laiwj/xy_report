package com.shulianxunying.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import org.bson.Document;

/**
 * Created by SuChang on 2017/4/27 14:05.
 */
public interface IDataApiService {

    Model talent_distribution_city(String url, JSONObject params, String time, Integer type);

    Model talent_distribution_func(String url, JSONObject params, String time, Integer type);

    Model talent_city_flow_in_top(String url, JSONObject params, String time, Integer type);

    Model talent_city_flow_out_top(String url, JSONObject params, String time, Integer type);

    Model talent_flow_in_city(String url, JSONObject params, String time, Integer type);

    Model talent_flow_out_city(String url, JSONObject params, String time, Integer type);

    Model talent_func_flow_in_top(String url, JSONObject params, String time, Integer type);

    Model talent_func_flow_out_top(String url, JSONObject params, String time, Integer type);

    Model talent_flow_in_func(String url, JSONObject params, String time, Integer type);

    Model talent_flow_out_func(String url, JSONObject params, String time, Integer type);

    Model talent_demand_func(String url, JSONObject params, String time, Integer type);

    Model talent_demand_position(String url, JSONObject params, String time, Integer type);

    Model talent_supply_func(String url, JSONObject params, String time, Integer type);

    Model talent_supply_position(String url, JSONObject params, String time, Integer type);

    Model report_modify(String id, String api_url, String api_time, JSONArray data_arr, JSONObject params_jsonb);

    Model salary_analysis(JSONObject params, String time, String api_url);

    Model salary_analysis(JSONObject params, String time, String api_url, String key, String type);
}
