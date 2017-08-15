package com.shulianxunying.controller.report;

import com.shulianxunying.annotation.AuthAnnotation;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.service.IReportConfigService;
import com.shulianxunying.util.ApiParamsUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by SuChang on 2017/5/26 16:26.
 * 报告标签配置，
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Resource
    private IReportConfigService reportConfigService;

    /**
     * 报告 标签配置
     *
     * @param report_type 报告类型  分布:201 流动:202 供需:203 薪酬分析:204
     * @param config_type 报告标签类别
     * @param check       选中项，多个选项 用,逗号 分割
     * @param request
     * @return
     */
    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_CONFIG_MANAGE)
    @RequestMapping("/config/modify")
    public Model modify_config(@RequestParam(required = true) Integer report_type,
                               @RequestParam(required = true) String config_type,
                               @RequestParam(required = true) String check,
                               HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        Set<String> citySet = ApiParamsUtils.splitParam(check);
        return reportConfigService.modify_config(report_type, config_type, citySet);
    }

    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_CONFIG_MANAGE)
    @RequestMapping("/config/allmodify")
    public Model save_modify_config(@RequestParam(required = true) Integer report_type,
                               @RequestParam(required = true) String industry,
                               @RequestParam(required = true) String demand,
                               @RequestParam(required = true) String experience,
                               @RequestParam(required = true) String supply,
                               @RequestParam(required = true) String label,
                               @RequestParam(required = true) String type_limit,
//                               @RequestParam(required = true) String check,
                               HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
//        Set<String> citySet = ApiParamsUtils.splitParam(check);
        return reportConfigService.modify_config(report_type, industry,demand,experience,supply,label,type_limit);
    }

    /**
     * 获取某报告的某条配置信息
     *
     * @param report_type 报告类型 --对应 报告权限id 例： 人才分布201 人才流动202
     * @param config_type 配置类型
     * @param request
     * @return
     */
    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_CONFIG_MANAGE)
    @RequestMapping("/config/one")
    public Model config_one(@RequestParam(required = true) Integer report_type,
                            @RequestParam(required = true) String config_type,
                            HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        return reportConfigService.config_info(report_type, config_type);
    }

    /**
     * 获取某报告的所有配置信息
     *
     * @param report_type 报告类型 --对应 报告权限id 例： 人才分布201 人才流动202
     * @param request
     * @return
     */
//    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_CONFIG_MANAGE)
    @RequestMapping("/config/all")
    public Model config_all(@RequestParam(required = true) Integer report_type,
                            HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        return reportConfigService.config_info(report_type);
    }
}
