package com.shulianxunying.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by SuChang on 2017/5/26 17:10.
 */
public class ReportConfig {

    Integer report_type;
    String config_type;
    Set<String> checks = new HashSet<>();

    public ReportConfig() {
    }

    public ReportConfig(Integer report_type, String config_type, Set<String> checks) {
        this.report_type = report_type;
        this.config_type = config_type;
        this.checks = checks;
    }

    public Integer getReport_type() {
        return report_type;
    }

    public void setReport_type(Integer report_type) {
        this.report_type = report_type;
    }

    public String getConfig_type() {
        return config_type;
    }

    public void setConfig_type(String config_type) {
        this.config_type = config_type;
    }

    public Set<String> getChecks() {
        return checks;
    }

    public void setChecks(Set<String> checks) {
        this.checks = checks;
    }
}
