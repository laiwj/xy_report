package com.shulianxunying.service;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.User;

/**
 * Created by SuChang on 2017/4/27 14:05.
 */
public interface IDataApiService {

    Model talent_distribution(User user,
                              String city,
                              String industry,
                              String cf,
                              Integer type, String time, Integer top,JSONObject param);

    Model talent_flow(User user,
                      String city,
                      String industry,
                      Integer type, String time,
                      String direction,
                      String city_or_func,Integer top,JSONObject param);

    Model talent_exponential(User user,
                             String city,
                             String industry,
                             Integer type, String time,
                             String func_or_position,
                             String need_or_all,Integer top,JSONObject param);

    Model scan_info(String report_id);

    Model hot_position_pay_keyword(User user,String position,Integer type,Integer top,String time,JSONObject param);

    Model talent_salary_analysis(User user,String industry,String index,Integer top,String time,Integer type,String label,JSONObject param);

    Model position_salary_analysis(User user, String name,  String industry,String city,String experience,Integer type, Integer top,String time,JSONObject param);

    Model func_salary_analysis(User user, String name,  String industry,String city,String experience,Integer type, Integer top,String time,JSONObject param);

    Model feature_portraits(User user,String name,String pf,String label,String time,Integer type,JSONObject param);
}
