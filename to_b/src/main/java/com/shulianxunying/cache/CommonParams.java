package com.shulianxunying.cache;

import java.util.HashSet;
import java.util.Timer;

/**
 * Created by SuChang on 2017/4/27 17:00.
 */
public class CommonParams {

    public static Timer timer = new Timer();
    // collection name 表名集合 仅在dao层使用
    public static String PM_USER = "pm_user";
    public static String USER_NOTICE = "user_notice";
    public static String PM_SYSTEM_LOG = "pm_system_log";
    public static String TEMP_DATA = "api_temp_data";
    public static String REPORT_INFO = "report_info";

    public static String B_USER = "b_user";
    public static String B_SYSTEM_LOG = "b_system_log";
    public static String B_DOWNLOAD_REPORT = "b_download_report";
    public static String B_COLLCET_REPORT = "b_collcet_report";

    // 用户身份
    public static Integer ACCOUNT_TYPE_B = 1;
    public static Integer ACCOUNT_TYPE_B_SUB = 2;

    public static Integer ACCOUNT_TYPE_PM_ROOT = 1;
    public static Integer ACCOUNT_TYPE_PM_ADMIN = 2;
    public static Integer ACCOUNT_TYPE_PM_NORMAL = 3;

    //用户状态 -1：封停 0 待激活 1正常
    public static Integer ACCOUNT_STATUS_ERROR = -1;
    public static Integer ACCOUNT_STATUS_WAIT_ACTIVE = 0;
    public static Integer ACCOUNT_STATUS_NORMAL = 1;
    // 错误返回码
    public static Integer RESULTCODE_AUTH_FAIL = -6;

    // PM端用户权限列表
    public static Integer POWER_ADD_ACCOUNT = 1; // 可否添加账户
    public static Integer POWER_DATA_MODFIY = 2; // 可否进行数据干预
    public static Integer POWER_ADD_INFO = 3; //可否添加数据解释
    // B端用户权限
    public static Integer POWER_REPORT_COLLECT = 100; // 可否收藏报告
    public static Integer POWER_REPORT_DOWNLOAD = 101; // 可否下载报告
    public static Integer POWER_REPORT_1 = 201; // 可否查看报告1
    public static Integer POWER_REPORT_2 = 202;// 可否查看报告2
    public static Integer POWER_REPORT_3 = 203;// 可否查看报告3
    public static Integer POWER_REPORT_4 = 204;// 可否查看报告4
    public static HashSet<Integer> INIT_B_POWER = new HashSet<>();
    public static HashSet<Integer> INIT_PM_POWER = new HashSet<>();

    static {
        INIT_B_POWER.add(POWER_REPORT_COLLECT);
        INIT_B_POWER.add(POWER_REPORT_DOWNLOAD);
        INIT_B_POWER.add(POWER_REPORT_1);
        INIT_B_POWER.add(POWER_REPORT_2);
        INIT_B_POWER.add(POWER_REPORT_3);

        INIT_PM_POWER.add(POWER_ADD_ACCOUNT);
        INIT_PM_POWER.add(POWER_ADD_INFO);
    }
}
