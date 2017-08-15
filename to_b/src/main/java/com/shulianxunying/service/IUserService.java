package com.shulianxunying.service;

import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.User;

import java.io.IOException;

/**
 * Created by jiangwei on 2016/8/10 0010.
 */
public interface IUserService {
    Model login(String email_or_telphone, String password, String last_ip,String sessionId);

    Model regist(String email_or_phone, String password, String username, String inviter);


    Model info(String user_id);

    Model modify_city(String user_id, String ciyt);

    Model modify_industry(String user_id, String industry);

    Model modify_sub_industry(String user_id, String sub_industry);

    Model update_password(String userId,String oldPwd,String newPwd);

    Model sendEmail (User op_user, String email, String username, String info,String subject) throws IOException;

    Model get_defualt_industry(String userId);
}
