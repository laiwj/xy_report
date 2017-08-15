package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MColletReportDao;
import com.shulianxunying.dao.impldao.MDownloadReportDao;
import com.shulianxunying.dao.impldao.MReportInfoDao;
import com.shulianxunying.dao.impldao.MTempDataDao;
import com.shulianxunying.entity.*;
import com.shulianxunying.service.IReportService;
import com.shulianxunying.util.CommonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by SuChang on 2017/4/27 16:31.
 */
@Service("report")
public class ReportServiceImpl implements IReportService {

    @Resource
    MDownloadReportDao downloadReportDao;
    @Resource
    MColletReportDao colletReportDao;
    @Resource
    MReportInfoDao reportInfoDao;
    @Resource
    MTempDataDao tempDataDao;


    @Override
    public Model download_report_list(String user_id, Integer page, Integer pageSize) {
        List<DownloadReport> user_download_report = downloadReportDao.find_user_download_report(user_id, page, pageSize);
        long count = 0;
        if (page == 1)
            count = downloadReportDao.count_user_download_report(user_id);
        Model model = new Model();
        JSONObject data = new JSONObject();
        data.put("data", user_download_report);
        data.put("count", count);
        model.setData(data);
        return model;
    }

    @Override
    public Model download_report(User user, String report_name, String data_id, String info_id) {
        DownloadReport downloadReport = new DownloadReport();
        TempData apiData = tempDataDao.find_one(data_id);
        ReportInfo reportInfo = reportInfoDao.find_by_id(MReportInfoDao.getCollectionName(), info_id, ReportInfo.class);
        if (apiData == null) {
            return new Model(-1, "参数错误，下载失败");
        }
        downloadReport.set_id(CommonUtil.getUuid());
        downloadReport.setDownload_time(new Date());
        downloadReport.setApi_url(apiData.getApi_url());
        if (reportInfo != null)
            downloadReport.setInfo(reportInfo.getInfo());
        else
            downloadReport.setInfo("");
        downloadReport.setData(apiData.getData());
        downloadReport.setParams(apiData.getParams());
        downloadReport.setReport_name(report_name);
        downloadReport.setUser_id(user.get_id());
        downloadReport.setPm_user_id(user.getPm_user_id());
        if (downloadReportDao.add_user_download_report(downloadReport))
            return new Model();
        return new Model(-1, "添加到下载列表失败");
    }

    @Override
    public Model collect_report_list(String user_id, Integer page, Integer pageSize) {
        List<CollectReport> user_collect_report = colletReportDao.find_user_collect_report(user_id, page, pageSize);
        long count = 0;
        if (page == 1)
            count = colletReportDao.count_user_collect_report(user_id);
        Model model = new Model();
        JSONObject data = new JSONObject();
        data.put("data", user_collect_report);
        data.put("count", count);
        model.setData(data);
        return model;
    }

    @Override
    public Model del_collect_report(String user_id, String report_id) {
        boolean flag = colletReportDao.del_user_collect_report(user_id, report_id);
        return new Model(flag);
    }

    @Override
    public Model collect_report_info(String user_id, String report_id) {
        CollectReport collectReport = colletReportDao.find_by_id(MColletReportDao.collectionName, report_id, CollectReport.class);
        if (collectReport != null && user_id.equals(collectReport.getUser_id()))
            return new Model().setData(collectReport);
        return new Model(-1, "获取报告失败");
    }

    @Override
    public Model add_collect_report(User user, String report_name, String data_id, String info_id) {
        CollectReport collectReport = new CollectReport();
        TempData apiData = tempDataDao.find_one(data_id);
        ReportInfo reportInfo = reportInfoDao.find_by_id(MReportInfoDao.getCollectionName(), info_id, ReportInfo.class);
        if (apiData == null) {
            return new Model(-1, "参数错误，收藏失败");
        }
        collectReport.set_id(CommonUtil.getUuid());
        collectReport.setCollect_time(new Date());
        collectReport.setApi_url(apiData.getApi_url());
        if (reportInfo != null) {
            collectReport.setInfo(reportInfo.getInfo());
        } else
            collectReport.setInfo("");
        collectReport.setData(apiData.getData());
        collectReport.setParams(apiData.getParams());
        collectReport.setReport_name(report_name);
        collectReport.setUser_id(user.get_id());
        collectReport.setPm_user_id(user.getPm_user_id());
        if (colletReportDao.add_user_collect_report(collectReport))
            return new Model();
        return new Model(-1, "收藏失败");
    }

}
