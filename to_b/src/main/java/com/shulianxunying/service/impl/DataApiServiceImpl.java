package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MReportInfoDao;
import com.shulianxunying.entity.ApiUrlBuilder;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.IDataApiService;
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
import java.util.List;

/**
 * Created by SuChang on 2017/4/27 14:05.
 */
@Service("api")
public class DataApiServiceImpl implements IDataApiService {
    private static final Logger logger = Logger.getLogger(DataApiServiceImpl.class.getName());

    @Resource
    MReportInfoDao reportInfoDao;
    @Resource(name = "httpHelper")
    HttpHelper2 httpHelper;
    @Resource(name = "apiUrlBuilder")
    ApiUrlBuilder apiUrlBuilder;

    @Override
    public Model talent_distribution(User user, String city, String industry, String cf, Integer type, String time, Integer top) {
        // 构造api url
        String api_url = apiUrlBuilder.create_talent_distribution_url(city, industry, cf, "" + type, time, top);
        return getDataAndInfo(user, api_url);
    }

    @Override
    public Model talent_flow(User user, String city, String industry, Integer type, String time, String direction, String city_or_func, Integer top) {
        String api_url = null;
        if ("".equals(city))
            api_url = apiUrlBuilder.create_talent_flow_top_url(industry, "" + type, time, direction, city_or_func, top);
        else
            api_url = apiUrlBuilder.create_talent_flow_url(city, industry, "" + type, time, direction, city_or_func, top);
        return getDataAndInfo(user, api_url);
    }

    @Override
    public Model talent_exponential(User user, String city, String industry, Integer type, String time, String func_or_position, String need_or_all, Integer top) {
        String api_url = apiUrlBuilder.create_talent_exponential_url(city, industry, "" + type, time, func_or_position, need_or_all, top);
        return getDataAndInfo(user, api_url);
    }


    public Model getDataAndInfo(User user, String api_url) {
        Model model = null;
        // 获取数据
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
        JSONObject apiData = (JSONObject) model.getData();
        List<Document> reportList = reportInfoDao.getReportList(apiData.getString("api_url"), apiData.getJSONObject("params"), user.getPm_user_id());
        JSONObject data = new JSONObject();
        data.put("data", model.getData());
        data.put("info", reportList);
        return new Model().setData(data);
    }
}
