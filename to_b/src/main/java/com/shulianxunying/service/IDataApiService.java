package com.shulianxunying.service;

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
                              Integer type, String time, Integer top);

    Model talent_flow(User user,
                      String city,
                      String industry,
                      Integer type, String time,
                      String direction,
                      String city_or_func,Integer top);

    Model talent_exponential(User user,
                             String city,
                             String industry,
                             Integer type, String time,
                             String func_or_position,
                             String need_or_all,Integer top);

}
