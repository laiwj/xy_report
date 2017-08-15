package com.shulianxunying.service;

import com.shulianxunying.controller.Model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by SuChang on 2017/4/27 16:13.
 */
public interface INoticeService {


    Model notice_list(String user_id, Boolean status, Integer page, Integer pageSize);

    Model send_notice_to_users(String msg, Set<String> user_ids);

    Model send_notice_to_all(String msg);

}
