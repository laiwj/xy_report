package com.shulianxunying.service;

import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.PMUser;

import java.util.List;

/**
 * Created by jiangwei on 2016/8/10 0010.
 */
public interface IUserService {
    Model login(String email_or_telphone, String password, String last_ip);

    Model regist(PMUser op_user, String email_or_phone, String password, String username);

    Model regist();

    Model password(PMUser user, String user_id, String old_password, String password, String type);

    Model info(String user_id);

    Model user_list(PMUser op_user, String user_id, int page, int pageSize);

    Model b_user_list(PMUser op_user, String user_id, int page, int pageSize);

    Model addPower(PMUser op_user, String user_id, String source, List<Integer> power, List<Integer> power_del);

    Model removePower(PMUser op_user, String user_id, List<Integer> power);
}
