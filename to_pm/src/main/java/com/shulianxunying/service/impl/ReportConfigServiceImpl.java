package com.shulianxunying.service.impl;

import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MReportConfigDao;
import com.shulianxunying.entity.ReportConfig;
import com.shulianxunying.service.IReportConfigService;
import com.shulianxunying.util.ApiParamsUtils;
import com.shulianxunying.util.CommonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by SuChang on 2017/5/27 9:28.
 */
@Service("report_config")
public class ReportConfigServiceImpl implements IReportConfigService {

    @Resource
    MReportConfigDao reportConfigDao;

    @Override
    public Model modify_config(Integer report_type, String config_type, Set<String> checks) {
        ReportConfig reportConfig = new ReportConfig(report_type, config_type, checks);
        if (reportConfigDao.upsert_one(reportConfig))
            return new Model();
        return new Model(-1, "修改失败");
    }

    @Override
    public Model modify_config(Integer report_type, String industry,String demand,
                               String experience,String supply_demand,String label,String type_limit) {
        List<ReportConfig> configs = new ArrayList<>();
        if(CommonUtil.isNotEmpty(industry.equals(""))){
            Set<String> checks = ApiParamsUtils.splitParam(industry);
            ReportConfig reportConfig = new ReportConfig(report_type,"industry",checks);
            configs.add(reportConfig);
        }
        if(CommonUtil.isNotEmpty(demand.equals(""))){
            Set<String> checks = ApiParamsUtils.splitParam(demand);
            ReportConfig reportConfig = new ReportConfig(report_type,"demand",checks);
            configs.add(reportConfig);
        }
        if(CommonUtil.isNotEmpty(experience.equals(""))){
            Set<String> checks = ApiParamsUtils.splitParam(experience);
            ReportConfig reportConfig = new ReportConfig(report_type,"experience",checks);
            configs.add(reportConfig);
        }
        if(CommonUtil.isNotEmpty(supply_demand.equals(""))){
            Set<String> checks = ApiParamsUtils.splitParam(supply_demand);
            ReportConfig reportConfig = new ReportConfig(report_type,"supply",checks);
            configs.add(reportConfig);
        }
        if(CommonUtil.isNotEmpty(label.equals(""))){
            Set<String> checks = ApiParamsUtils.splitParam(label);
            ReportConfig reportConfig = new ReportConfig(report_type,"label",checks);
            configs.add(reportConfig);
        }
        if(CommonUtil.isNotEmpty(label.equals(""))){
            Set<String> checks = ApiParamsUtils.splitParam(type_limit);
            ReportConfig reportConfig = new ReportConfig(report_type,"type_limit",checks);
            configs.add(reportConfig);
        }
        for(ReportConfig config :configs){
            if (!reportConfigDao.upsert_one(config)){
                return new Model(-1, "修改失败");
            }
        }
        return new Model();
    }

    @Override
    public Model config_info(Integer report_type, String config_type) {
        ReportConfig one = reportConfigDao.find_one_config(report_type, config_type);
        if (one != null)
            return new Model().setData(one);
        return new Model(-1, config_type + " 查询失败");
    }

    @Override
    public Model config_info(Integer report_type) {
        List<ReportConfig> all_config = reportConfigDao.find_all_config(report_type);
        if (all_config != null)
            return new Model().setData(all_config);
        return new Model(-1, "查询配置失败");
    }
}
