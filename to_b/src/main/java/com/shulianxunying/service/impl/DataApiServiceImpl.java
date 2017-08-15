package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MReportInfoDao;
import com.shulianxunying.dao.impldao.MSearchDao;
import com.shulianxunying.entity.ApiUrlBuilder;
import com.shulianxunying.entity.ReportInfo;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
    MSearchDao msearchDao;
    @Resource(name = "httpHelper")
    HttpHelper2 httpHelper;
    @Resource(name = "apiUrlBuilder")
    ApiUrlBuilder apiUrlBuilder;
    @Resource
    MSearchDao mSearchDao;

    @Override
    public Model talent_distribution(User user, String city, String industry, String cf, Integer type, String time, Integer top,JSONObject param) {
        // 构造api url
        String api_url = apiUrlBuilder.create_talent_distribution_url(city, industry, cf, "" + type, time, top);
        param.put("api_url",api_url);
//        return getDataAndInfo(user, api_url,param);
        String url = "/api/talent/distribution/" + cf;
        HashMap<String, String> values = new HashMap<>();
        values.put("city", city);
        values.put("industry", industry);
        values.put("type", type+"");
        values.put("time", time);
        values.put("top", "" + top);
        return getDataAndInfoByPost(user,url,values);
    }

    @Override
    public Model talent_flow(User user, String city, String industry, Integer type,
                             String time, String direction, String city_or_func, Integer top,JSONObject param) {
        String api_url = null;
        if ("".equals(city)) {
            api_url = apiUrlBuilder.create_talent_flow_top_url(industry, "" + type, time, direction, city_or_func, top);
            String url  = "/api/talent/flow/" + direction + "/" + city_or_func + "/top";
            HashMap<String, String> values = new HashMap<>();
            values.put("type", type + "");
            values.put("time", time);
            values.put("top", "" + top);
            values.put("industry", industry);
            return getDataAndInfoByPost(user, url, values);
        }
        else{
            api_url = apiUrlBuilder.create_talent_flow_url(city, industry, "" + type, time, direction, city_or_func, top);
            String url = "/api/talent/flow/" + direction + "/" + city_or_func;
            HashMap<String, String> values = new HashMap<>();
            values.put("city", city);
            values.put("industry", industry);
            values.put("type", type+"");
            values.put("time", time);
            values.put("top", "" + top);
            param.put("api_url", api_url);
            return getDataAndInfoByPost(user, url, values);
        }

    }

    @Override
    public Model talent_exponential(User user, String city, String industry, Integer type, String time,
                                    String func_or_position, String need_or_all, Integer top,JSONObject param) {
        String api_url = apiUrlBuilder.create_talent_exponential_url(city, industry, "" + type, time, func_or_position, need_or_all, top);
        param.put("api_url",api_url);
        String url = "/api/talent/exponention/" + func_or_position + "/" + need_or_all;
        HashMap<String, String> values = new HashMap<>();
        values.put("city", city);
        values.put("industry", industry);
        values.put("type", type+"");
        values.put("time", time);
        values.put("top", "" + top);
        return getDataAndInfoByPost(user, url, values);
    }

    @Override
    public Model scan_info(String report_id) {
        Model model = new Model();
        model.setData(reportInfoDao.find_by_id(MReportInfoDao.collectionName, report_id, ReportInfo.class));
        return model;
    }

    @Override
    public Model hot_position_pay_keyword(User user, String position, Integer type, Integer top, String time,JSONObject param) {
        String api_url = apiUrlBuilder.create_hot_position_keyword_url(position, type, top, time);
        param.put("api_url",api_url);
//        return getDataAndInfo(user,api_url,param);
        String url = "/api/hot/position/pay/keyword";
        HashMap<String, String> values = new HashMap<>();
        values.put("position", position);
        values.put("type", type+"");
        values.put("top", top+"");
        values.put("time", time);
        return getDataAndInfoByPost(user, url, values);
    }

    @Override
    public Model talent_salary_analysis(User user,String industry, String index, Integer top,String time,Integer type,String label,JSONObject param) {
        String api_url = apiUrlBuilder.create_talent_salary_analysis_url(industry, index, top,time,type,label);
        param.put("api_url",api_url);
        String url = "/api/talent/salary/analysis";
        HashMap<String,String> values = new HashMap<>();
        values.put("industry",industry);
        values.put("index",index);
        values.put("time",time);
        values.put("type",type+"");
        values.put("top",top+"");
        values.put("label",label+"");
        return getDataAndInfoByPost(user, url,values);
    }

    @Override
    public Model position_salary_analysis(User user,String name,String industry,String city,String experience,Integer type, Integer top,String time,JSONObject param) {
        String api_url = apiUrlBuilder.create_position_salary_analysis_url(name, industry,city,experience,type, top,time);
        param.put("api_url",api_url);
        String url = "/api/position/salary/analysis";
        HashMap<String, String> getparam = new HashMap<>();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        getparam.put("name",name);
        getparam.put("industry",industry);
        getparam.put("city",city);
        getparam.put("experience",experience);
        getparam.put("type",type+"");
        getparam.put("top",top+"");
        getparam.put("time",sdf.format(new Date()));
        return getDataAndInfoByPost(user, url, getparam);
    }

    public Model func_salary_analysis(User user, String name,  String industry,String city,String experience,Integer type, Integer top,String time,JSONObject param) {
        String api_url = apiUrlBuilder.create_fun_salary_analysis_url(name, industry,city,experience,type, top,time);
        param.put("api_url",api_url);
        HashMap<String, String> getparam = new HashMap<>();
        String url = "/api/func/salary/analysis";
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        getparam.put("name",name);
        getparam.put("industry",industry);
        getparam.put("city",city);
        getparam.put("experience",experience);
        getparam.put("type",type+"");
        getparam.put("top",top+"");
        getparam.put("time",sdf.format(new Date()));
        return getDataAndInfoByPost(user, url ,getparam);
//        return getDataAndInfo(user,api_url,param);
    }

    @Override
    public Model feature_portraits(User user, String name, String pf, String label,String time ,Integer type,JSONObject param) {
        String api_url = apiUrlBuilder.create_feature_portraits_url(name, pf, label,time,type);
        param.put("api_url",api_url);
        String url = "/api/feature/portraits";
        HashMap<String,String> values = new HashMap<>();
        values.put("name",name);
        values.put("pf",pf);
        values.put("label",label);
        values.put("time",time);
        values.put("type",type+"");
        return getDataAndInfoByPost(user, url,values);
    }


    public Model getDataAndInfoByPost(User user, String api_url,HashMap<String,String> param) {
        Model model = null;
        HttpClient httpClient = null;
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(1000 * 60 * 2);
        builder.setConnectTimeout(1000 * 60 * 2);
        builder.setSocketTimeout(1000 * 60 * 2);
        RequestConfig requestConfig = builder.build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        Page page = null;
        JSONObject params = null;
        String id = null;
        String url = apiUrlBuilder.buildFinalUrl(api_url);
        try {
            page = httpHelper.doPost(httpClient, apiUrlBuilder.buildFinalUrl(api_url),param);
        } catch (Exception e) {
            logger.error("api web error\r\n" + e.getMessage());
            e.printStackTrace();
            return new Model(-1, "获取数据失败");
        }
        if (page != null && page.getStatusCode() == 200 && StringUtils.isNotEmpty(page.getRawText())) {
            try {
                model = JSONObject.parseObject(page.getRawText(), Model.class);
                params = ((JSONObject) JSONObject.parse(page.getRawText()));
                id = (String) ((JSONObject) params.get("data")).get("_id");
            } catch (JSONException e) {
                return new Model(-1, "获取数据失败");
            }
        } else {
            return new Model(-1, "获取数据失败");
        }
        try {
//            msearchDao.find_by_id(MSearchDao.collectionName,id);
            ((JSONObject)params.get("data")).put("params", param);
        }catch (Exception e){
            logger.error("search mongoDB error");

        }
        JSONObject apiData = (JSONObject) model.getData();
        List<Document> reportList = reportInfoDao.getReportList(apiData.getString("api_url"), apiData.getJSONObject("params"), user.getPm_user_id());
        JSONObject data = new JSONObject();
        data.put("data", model.getData());
        data.put("info", reportList);
        Document search_param = new Document();
        search_param.put("param",param);
        search_param.put("_id",(String) ((JSONObject)params.get("data")).get("_id"));
        search_param.put("api_url",url);
//        search_param.put("data", params.getJSONObject("data").getJSONArray("data").toJSONString());
        search_param.put("data", params.getJSONObject("data").getJSONArray("data"));
        search_param.put("user",user.get_id());
//        msearchDao.insert_one();
        msearchDao.insert_one(search_param);
        logger.info("insert data to search_param");
        return new Model().setData(data);
    }

    public Model getDataAndInfo(User user, String api_url,JSONObject param) {
        Model model = null;
        // 获取数据
        HttpClient httpClient = null;
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(1000 * 60 * 5);
        builder.setConnectTimeout(10000 * 60 * 1);
        builder.setSocketTimeout(10000 * 60 * 1);
        RequestConfig requestConfig = builder.build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        Page page = null;
        JSONObject params = null;
        String id = null;
        String url = apiUrlBuilder.buildFinalUrl(api_url);
        try {
            page = httpHelper.doGet(httpClient, url);
        } catch (Exception e) {
            logger.error("api web error\r\n" + e.getMessage());
            return new Model(-1, "获取数据失败");
        }

        if (page != null && page.getStatusCode() == 200 && StringUtils.isNotEmpty(page.getRawText())) {
            try {
                model = JSONObject.parseObject(page.getRawText(), Model.class);
                params = ((JSONObject) JSONObject.parse(page.getRawText()));
                id = (String) ((JSONObject) params.get("data")).get("_id");
            } catch (JSONException e) {
                return new Model(-1, "获取数据失败");
            }
        } else {
            return new Model(-1, "获取数据失败");
        }
        try {
//            msearchDao.find_by_id(MSearchDao.collectionName,id);
            ((JSONObject)params.get("data")).put("params", param);
        }catch (Exception e){
            logger.error("search mongoDB error");

        }
        JSONObject apiData = (JSONObject) model.getData();
        List<Document> reportList = reportInfoDao.getReportList(apiData.getString("api_url"), apiData.getJSONObject("params"), user.getPm_user_id());
        JSONObject data = new JSONObject();
        data.put("data", model.getData());
        data.put("info", reportList);
        Document search_param = new Document();
        search_param.put("param",param);
        search_param.put("_id",(String) ((JSONObject)params.get("data")).get("_id"));
        search_param.put("api_url", url);
        search_param.put("data", params.getJSONObject("data").getJSONArray("data").toJSONString());
        msearchDao.insert_one(search_param);
        logger.info("insert data to search_param");
        return new Model().setData(data);
    }
}
