package com.shulianxunying.service;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.ReportInfo;
import com.shulianxunying.entity.User;

/**
 * Created by SuChang on 2017/4/27 16:30.
 */
public interface IReportService {

    Model download_report_list(String user_id, Integer page, Integer pageSize);

    Model download_report(User user, String report_name, String data_id, String info_id);

    Model collect_report_list(String user_id, Integer page, Integer pageSize);

    Model del_collect_report(String user_id, String report_id);

    Model collect_report_info(String user_id, String report_id);

    Model add_collect_report(User user, String report_name, String data_id,String info_id);
}
