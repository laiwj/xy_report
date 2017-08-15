package com.shulianxunying.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.PMUser;

/**
 * Created by SuChang on 2017/4/27 14:05.
 */
public interface IDataApiService {

    Model talent_distribution(PMUser user,
                              String city,
                              String industry,
                              String cf,
                              Integer type, String time, Integer top);

    Model talent_flow(PMUser user,
                      String city,
                      String industry,
                      Integer type, String time,
                      String direction,
                      String city_or_func, Integer top);

    Model talent_exponential(PMUser user,
                             String city,
                             String industry,
                             Integer type, String time,
                             String func_or_position,
                             String need_or_all, Integer top);

    Model write_info(PMUser user, String user_id, String api_url, JSONObject params, String report_info);

    Model report_modify(PMUser user, String data_id, JSONArray data);

    Model hot_position_pay_keyword(PMUser user,String position,Integer type,Integer top,String time);

    Model talent_salary_analysis(PMUser user,String industry,String index,Integer top,String label,String time,Integer type);

    Model position_salary_analysis(PMUser user, String name,  String industry,String city,String experience,Integer type, Integer top);

    Model func_salary_analysis(PMUser user, String name,  String industry,String city,String experience,Integer type, Integer top);

    Model feature_portraits(PMUser user,String name,String pf,String label,String time,Integer type);

    Model report_interpose(PMUser user,String id,String data,String api_url,String api_time, String params);
}
