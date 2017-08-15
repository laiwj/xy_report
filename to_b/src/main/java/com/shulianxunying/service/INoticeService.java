package com.shulianxunying.service;

import com.shulianxunying.controller.Model;

/**
 * Created by SuChang on 2017/4/27 16:13.
 */
public interface INoticeService {


    Model notice_list(String user_id, Boolean status, Integer page, Integer pageSize);

    Model click(String notice_id);

}
