package com.shulianxunying.service;

import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.User;

/**
 * Created by jiangwei on 2016/8/10 0010.
 */
public interface IUserService {
    Model login(String email_or_telphone, String password, String last_ip);

    Model regist(String email_or_phone, String password, String username, String inviter);


    Model info(String user_id);

    Model modify_city(String user_id, String ciyt);

    Model modify_industry(String user_id, String industry);

    Model modify_sub_industry(String user_id, String sub_industry);
}
