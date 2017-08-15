package com.shulianxunying.cache;

import java.util.HashSet;
import java.util.Timer;

/**
 * Created by SuChang on 2017/4/27 17:00.
 */
public class CommonParams {

    public static Timer timer = new Timer();
    // collection name 表名集合 仅在dao层使用
    public static final String PM_USER = "pm_user";
    public static final String USER_NOTICE = "user_notice";
    public static final String PM_SYSTEM_LOG = "pm_system_log";
    public static final String TEMP_DATA = "api_temp_data";
    public static final String REPORT_INFO = "report_info";
    public static final String REPORT_CONFIG = "report_config";

    public static final String B_USER = "b_user";
    public static final String B_SYSTEM_LOG = "b_system_log";
    public static final String B_DOWNLOAD_REPORT = "b_download_report";
    public static final String B_COLLCET_REPORT = "b_collcet_report";
    public static final String SEARCH_CACHE = "search_cache";

    // 用户身份
    public static final int ACCOUNT_TYPE_B = 1; //B端账号类型-公司
    public static final int ACCOUNT_TYPE_B_SUB = 2;//B端账号类型-公司子账号 (暂未启用)

    public static final int ACCOUNT_TYPE_PM_ROOT = 1;//PM端 账号类型-超管
    public static final int ACCOUNT_TYPE_PM_ADMIN = 2;//PM端 账号类型-咨询公司
    public static final int ACCOUNT_TYPE_PM_NORMAL = 3;//PM端 账号类型-业务员

    //用户状态 -1：封停 0 待激活 1正常
    public static final int ACCOUNT_STATUS_ERROR = -1;
    public static final int ACCOUNT_STATUS_WAIT_ACTIVE = 0;
    public static final int ACCOUNT_STATUS_NORMAL = 1;
    // 错误返回码
    public static final int RESULTCODE_AUTH_FAIL = -6;

    // PM端用户权限列表
    public static final int POWER_ADD_ACCOUNT = 1; // 可否添加账户
    public static final int POWER_DATA_MODFIY = 2; // 可否进行数据干预
    public static final int POWER_ADD_INFO = 3; //可否添加数据解释
    public static final int POWER_REPORT_CONFIG_MANAGE = 4; // 可否进行报告标签配置
    public static final int POWER_NOTIC = 5; // 可否进行系统通知发送
    public static final int POWER_AUTH = 6; // 可否进行 权限修改
    // B端用户权限
    public static final int POWER_REPORT_COLLECT = 100; // 可否收藏报告
    public static final int POWER_REPORT_DOWNLOAD = 101; // 可否下载报告
    //报告权限
    public static final int POWER_REPORT_1 = 201; // 可否查看报告1 - 热门人才分布
    public static final int POWER_REPORT_2 = 202;// 可否查看报告2  - 热门人才流动
    public static final int POWER_REPORT_3 = 203;// 可否查看报告3  - 热门人才供需
    public static final int POWER_REPORT_4 = 204;// 可否查看报告4  - 热门人才薪酬分析
    public static final int POWER_REPORT_5 = 205;// 可否查看报告4  - 人才职能薪酬分析
    public static final int POWER_REPORT_6 = 206;// 可否查看报告4  - 人才岗位薪酬分析
    public static HashSet<Integer> INIT_B_POWER = new HashSet<>();
    public static HashSet<Integer> INIT_PM_POWER = new HashSet<>();

    static {
        INIT_B_POWER.add(POWER_REPORT_COLLECT);
        INIT_B_POWER.add(POWER_REPORT_DOWNLOAD);
        INIT_B_POWER.add(POWER_REPORT_1);
        INIT_B_POWER.add(POWER_REPORT_2);
        INIT_B_POWER.add(POWER_REPORT_3);
        INIT_B_POWER.add(POWER_REPORT_4);

    }
}
