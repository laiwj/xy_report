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
}
