package com.shulianxunying.controller.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.INoticeService;
import com.shulianxunying.service.IReportService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by SuChang on 2017/4/27 16:29.
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Resource
    private IReportService reportService;

    /**
     * 获取用户 下载简历 历史的列表
     *
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping("/download/list")
    public Model download_report_list(@RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.download_report_list(user.get_id(), page, 10);
        return model;
    }

    /**
     * 下载报告
     * 目前 仅做到 添加报告到下载列表，尚未提供文件下载
     *
     * @param report_name
     * @param request
     * @return
     */
    @RequestMapping("/download")
    public void download_report(@RequestParam(required = true) String report_name,
                                @RequestParam(required = true) String data_id,
                                @RequestParam(required = true) String info_id,
                                HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(report_name);
        } catch (JSONException e) {
            return;
//            return new Model(-1, "参数错误");
        }
        Model model = reportService.download_report(user, report_name, data_id, info_id);
        OutputStream sos = null;
        try {
            sos = response.getOutputStream();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 获取原文件名
            InputStream resourceAsStream = ReportController.class.getResourceAsStream("/test.txt");
            if (resourceAsStream == null) {
                sos.write("没有这个文件".getBytes());
                return;
            }
            String fileName = new String("test.txt".getBytes(), "ISO8859-1");
            // 设置下载文件名
            response.addHeader("Content-Disposition", "attachment; filename=\""
                    + fileName + "\"");
            // 向客户端输出文件
            InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "utf-8");
            IOUtils.copy(inputStreamReader, sos, "utf-8");
        } catch (IOException e) {
//            return new Model(-1,"链接异常");
        } finally {
            try {
                sos.flush();
                sos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        return model;
    }

    @RequestMapping("/collect/list")
    public Model collect_report_list(@RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.collect_report_list(user.get_id(), page, 10);
        return model;
    }

    /**
     * 添加 报告收藏
     *
     * @param report_name 报告名称 把筛选条件 按顺序 value1-value2-value3 连接在一起 例如 城市-行业-职能-时间-TOP5
     * @param request
     * @return
     */
    @RequestMapping("/collect")
    public Model notic_add(@RequestParam(required = true) String report_name,
                           @RequestParam(required = true) String data_id,
                           @RequestParam(required = true) String info_id,
                           HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.add_collect_report(user, report_name, data_id, info_id);
        return model;
    }

    /**
     * 从列表中移除
     *
     * @param report_id
     * @param request
     * @return
     */
    @RequestMapping("/collect/del")
    public Model notic_del(@RequestParam(required = true) String report_id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.del_collect_report(user.get_id(), report_id);
        return model;
    }

    /**
     * 从列表中移除
     *
     * @param report_id
     * @param request
     * @return
     */
    @RequestMapping("/collect/info")
    public Model notic_info(@RequestParam(required = true) String report_id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.collect_report_info(user.get_id(), report_id);
        return model;
    }
}
