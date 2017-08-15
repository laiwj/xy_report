package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.*;
import com.shulianxunying.entity.*;
import com.shulianxunying.service.IDataApiService;
import com.shulianxunying.util.CommonUtil;
import com.shulianxunying.util.HttpUtils.HttpHelper2;
import com.shulianxunying.util.HttpUtils.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SuChang on 2017/4/27 14:05.
 */
@Service("api")
public class DataApiServiceImpl implements IDataApiService {
    private static final Logger logger = Logger.getLogger(DataApiServiceImpl.class.getName());

    @Resource
    MReportInfoDao reportInfoDao;
    @Resource
    MTempDataDao tempDataDao;
    @Resource
    MPMUserDao pmUserDao;
    @Resource(name = "httpHelper")
    HttpHelper2 httpHelper;
    @Resource(name = "apiUrlBuilder")
    ApiUrlBuilder apiUrlBuilder;

    @Override
    public Model talent_distribution(PMUser user, String city, String industry, String cf, Integer type, String time, Integer top) {
        // 构造api url
        String api_url = apiUrlBuilder.create_talent_distribution_url(city, industry, cf, "" + type, time, top);
        return getDataAndInfo(user, api_url);
    }

    @Override
    public Model talent_flow(PMUser user, String city, String industry, Integer type, String time, String direction, String city_or_func, Integer top) {
        String api_url = null;
        if ("".equals(city))
            api_url = apiUrlBuilder.create_talent_flow_top_url(industry, "" + type, time, direction, city_or_func, top);
        else
            api_url = apiUrlBuilder.create_talent_flow_url(city, industry, "" + type, time, direction, city_or_func, top);
        return getDataAndInfo(user, api_url);
    }

    @Override
    public Model talent_exponential(PMUser user, String city, String industry, Integer type, String time, String func_or_position, String need_or_all, Integer top) {
        String api_url = apiUrlBuilder.create_talent_exponential_url(city, industry, "" + type, time, func_or_position, need_or_all, top);
        return getDataAndInfo(user, api_url);
    }

    @Override
    public Model write_info(PMUser user, String user_id, String api_url, JSONObject params, String report_info) {
        if (StringUtils.isEmpty(api_url) || StringUtils.isEmpty(report_info))
            return new Model(-2, "参数为空");
        ReportInfo reportInfo = new ReportInfo();
        reportInfo.set_id(CommonUtil.md5(user_id + api_url + params.toJSONString()));
        reportInfo.setPm_user_id(user_id);
        reportInfo.setInfo(report_info);
        reportInfo.setApi_url(api_url);
        reportInfo.setParams(params);
        reportInfo.setModify_time(new Date());
        if (reportInfoDao.upsertReportInfo(reportInfo)) {
            return new Model();
        }
        return new Model(-1, "填写失败");
    }


    public Model getDataAndInfo(PMUser user, String api_url) {
        Model model = null;
        HttpClient httpClient = null;
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(1000 * 60 * 2);
        builder.setConnectTimeout(1000 * 60 * 1);
        builder.setSocketTimeout(1000 * 60 * 1);
        RequestConfig requestConfig = builder.build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        Page page = null;
        try {
            page = httpHelper.doGet(httpClient, apiUrlBuilder.buildFinalUrl(api_url));
        } catch (Exception e) {
            logger.error("api web error\r\n" + e.getMessage());
            return new Model(-1, "获取数据失败");
        }
        if (page.getStatusCode() == 200 && StringUtils.isNotEmpty(page.getRawText())) {
            try {
                model = JSONObject.parseObject(page.getRawText(), Model.class);
            } catch (JSONException e) {
                return new Model(-1, "获取数据失败");
            }
        } else {
            return new Model(-1, "获取数据失败");
        }

        List<String> users = new ArrayList<>();
        if (user.getType() == 1) {

        } else if (user.getType() == 2) {
            List<PMUser> users1 = pmUserDao.user_list(user.get_id(), 0, 10);
            for (PMUser u : users1)
                users.add(u.get_id());
        } else {
            users.add(user.get_id());
        }
        JSONObject apiData = (JSONObject) model.getData();
        List<Document> reportList = reportInfoDao.getReportList(apiData.getString("api_url"), apiData.getJSONObject("params"), users);
        JSONObject data = new JSONObject();
        data.put("data", model.getData());
        data.put("info", reportList);
        return new Model().setData(data);
    }

    @Override
    public Model report_modify(PMUser user, String data_id, JSONArray data) {
        if (user.getType() != 1) {
            return new Model(-6);
        }
        if (tempDataDao.report_modify(data_id, data)) {
            return new Model();
        }
        return new Model(-2);
    }
}
