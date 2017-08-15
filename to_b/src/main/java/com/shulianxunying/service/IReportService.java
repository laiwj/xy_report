package com.shulianxunying.service;

import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.CollectReport;
import com.shulianxunying.entity.SearchCache;
import com.shulianxunying.entity.User;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by SuChang on 2017/4/27 16:30.
 */
public interface IReportService  {

    Model download_report_list(String user_id, Integer page, Integer pageSize);

    Model download_report(User user, String[] report_name, String[] data_id, Integer[] type,HttpServletResponse response,String path) throws Exception;

    Model collect_report_list(String user_id, Integer page, Integer pageSize);

    Model del_collect_report_one(String user_id, String report_id);

    Model del_collect_report_more(String user_id, String[] report_id);

    Model collect_report_info(String user_id, String report_id);

    CollectReport find_report_info(String user_id, String data_id,String report_name);

    Model add_collect_report(User user, String report_name, String data_id, String info_id);

    /**
     * 某报告 某条配置的信息
     *
     * @param report_type
     * @param config_type
     * @return
     */
    Model config_info(Integer report_type,
                      String config_type);

    /**
     * 某报告所有的配置信息
     *
     * @param report_type
     * @return
     */
    Model config_info(Integer report_type);

    String find_url_by_id(String data_id);

    SearchCache find_by_id(String id);

//    void download_report(String report_name,Integer type,HttpServletResponse response,String path,Object data,ReportInfo info);


}
