package com.shulianxunying.service;

import com.shulianxunying.controller.Model;

import java.util.Set;


public interface IReportConfigService {

    /**
     * 修改配置
     *
     * @param report_type
     * @param config_type
     * @param checks
     * @return
     */
    Model modify_config(Integer report_type,
                        String config_type,
                        Set<String> checks);

    Model modify_config(Integer report_type, String industry,String demand,
                        String experience,String supply_demand,String label,String type_limit);
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
}
