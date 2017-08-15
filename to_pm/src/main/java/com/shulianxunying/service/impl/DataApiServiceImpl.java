package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MPMUserDao;
import com.shulianxunying.dao.impldao.MReportInfoDao;
import com.shulianxunying.dao.impldao.MTempDataDao;
import com.shulianxunying.entity.ApiUrlBuilder;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.entity.ReportInfo;
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
import java.text.SimpleDateFormat;
import java.util.*;

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
        HashMap<String, String> values = new HashMap<>();
        values.put("city", city);
        values.put("industry", industry);
        values.put("type", type+"");
        values.put("time", time);
        values.put("top", "" + top);
        return getDataAndInfoByPost(user,"/api/talent/distribution/" + cf, values);
    }

    @Override
    public Model talent_flow(PMUser user, String city, String industry, Integer type, String time, String direction, String city_or_func, Integer top) {
        String api_url = null;
        if ("".equals(city)){
            HashMap<String, String> values = new HashMap<>();
            values.put("type", type+"");
            values.put("time", time);
            values.put("top", "" + top);
            values.put("industry", industry);
            return getDataAndInfoByPost(user, "/api/talent/flow/" + direction + "/" + city_or_func + "/top", values);
        }

//            api_url = apiUrlBuilder.create_talent_flow_top_url(industry, "" + type, time, direction, city_or_func, top);
        else{
            HashMap<String, String> values = new HashMap<>();
            values.put("city", city);
            values.put("industry", industry);
            values.put("type", type+"");
            values.put("time", time);
            values.put("top", "" + top);
            return getDataAndInfoByPost(user, "/api/talent/flow/" + direction + "/" + city_or_func, values);
        }
//            api_url = apiUrlBuilder.create_talent_flow_url(city, industry, "" + type, time, direction, city_or_func, top);
//        return getDataAndInfo(user, api_url);
    }

    @Override
    public Model talent_exponential(PMUser user, String city, String industry, Integer type, String time, String func_or_position, String need_or_all, Integer top) {
        String api_url = apiUrlBuilder.create_talent_exponential_url(city, industry, "" + type, time, func_or_position, need_or_all, top);
        HashMap<String, String> values = new HashMap<>();
        values.put("city", city);
        values.put("industry", industry);
        values.put("type", type+"");
        values.put("time", time);
        values.put("top", "" + top);
        return getDataAndInfoByPost(user, "/api/talent/exponention/" + func_or_position + "/" + need_or_all, values);
    }

    @Override
    public Model write_info(PMUser user, String user_id, String api_url, JSONObject params, String report_info) {
        if (StringUtils.isEmpty(api_url) || StringUtils.isEmpty(report_info))
            return new Model(-2, "报告说明不能为空");
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
    public Model getDataByPost(PMUser user, String api_url,HashMap<String, String> param) {
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
            page = httpHelper.doPost(httpClient, apiUrlBuilder.buildFinalUrl(api_url),param);
        } catch (Exception e) {
            logger.error("api web error\r\n" + e.getMessage());
            return new Model(-1, "获取数据失败");
        }
        return new Model();
    }


    public Model getDataAndInfoByPost(PMUser user, String api_url,HashMap<String,String> param) {
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
            page = httpHelper.doPost(httpClient, apiUrlBuilder.buildFinalUrl(api_url),param);
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
            e.printStackTrace();
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
        JSONObject test_parm = new JSONObject();
        test_parm.put("top",10);
        test_parm.put("city",new ArrayList<>());
        test_parm.put("cf","city");
        test_parm.put("function",new ArrayList<>());
        List<String> industry = new ArrayList<>();
        industry.add("互联网全行业");
        test_parm.put("industry",industry);
        test_parm.put("type",2);

        JSONObject data = new JSONObject();
        if(user.getType() == 3){
            Document reportInfo = reportInfoDao.find_by_id(MReportInfoDao.getCollectionName(), CommonUtil.md5(user.get_id() + apiData.getString("api_url") + apiData.getJSONObject("params")));
            data.put("data", model.getData());
            data.put("info", reportInfo);
        }else{
            JSONObject param = create_param(apiData.getJSONObject("params"));
            List<ReportInfo> reportList = reportInfoDao.findParams_by_key_values(MReportInfoDao.getCollectionName(), ReportInfo.class, param);
//        List<Document> reportList = reportInfoDao.getReportList(apiData.getString("api_url"), apiData.getJSONObject("params"), users);
//        List<Document> reportList = reportInfoDao.getReportList(apiData.getString("api_url"), apiData.getJSONObject("params"), users);
//            JSONObject param = create_param(apiData.getJSONObject("params"));
            data.put("data", model.getData());
            data.put("info", reportList);
        }
//        List<ReportInfo> reportList = reportInfoDao.findParams_by_key_values(MReportInfoDao.getCollectionName(), ReportInfo.class, param);
////        List<Document> reportList = reportInfoDao.getReportList(apiData.getString("api_url"), apiData.getJSONObject("params"), users);
////        List<Document> reportList = reportInfoDao.getReportList(apiData.getString("api_url"), apiData.getJSONObject("params"), users);
//
//        data.put("data", model.getData());
//        data.put("info", reportList);
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

    @Override
    public Model hot_position_pay_keyword(PMUser user, String position, Integer type, Integer top, String time) {
        String api_url = apiUrlBuilder.create_hot_position_keyword_url(position, type, top, time);
        HashMap<String, String> values = new HashMap<>();
        values.put("position", position);
        values.put("type", type+"");
        values.put("top", top+"");
        values.put("time", time);
        return getDataAndInfoByPost(user,"/api/hot/position/pay/keyword", values);
    }

    @Override
    public Model talent_salary_analysis(PMUser user,String industry, String index, Integer top,String label,String time,Integer type) {
        String api_url = apiUrlBuilder.create_talent_salary_analysis_url(industry, index, top,time,type,label);
        HashMap<String, String> param = new HashMap<>();
        param.put("industry",industry);
        param.put("index",index);
        param.put("top",top+"");
        param.put("label",label);
        param.put("time",time);
        param.put("type",type+"");
        return getDataAndInfoByPost(user, "/api/talent/salary/analysis",param);
    }

    @Override
    public Model position_salary_analysis(PMUser user, String name,  String industry,String city,String experience,Integer type, Integer top) {
        String api_url = apiUrlBuilder.create_position_salary_analysis_url(name, industry,city,experience,type, top);
        HashMap<String, String> param = new HashMap<>();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        param.put("name",name);
        param.put("industry",industry);
        param.put("city",city);
        param.put("experience",experience);
        param.put("type",type+"");
        param.put("top",top+"");
        param.put("time",sdf.format(new Date()));
//        return getDataAndInfo(user,api_url);
        return getDataAndInfoByPost(user, "/api/position/salary/analysis",param);
    }

    public Model func_salary_analysis(PMUser user,String name,  String industry,String city,String experience,Integer type, Integer top) {
        String api_url = apiUrlBuilder.create_fun_salary_analysis_url(name, industry,city,experience,type, top);
        HashMap<String, String> param = new HashMap<>();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        param.put("name",name);
        param.put("industry",industry);
        param.put("city",city);
        param.put("experience",experience);
        param.put("type",type+"");
        param.put("top",top+"");
        param.put("time",sdf.format(new Date()));
        return getDataAndInfoByPost(user, "/api/func/salary/analysis",param);
    }

    @Override
    public Model feature_portraits(PMUser user, String name, String pf, String label,String time,Integer type) {
        String api_url = apiUrlBuilder.create_feature_portraits_url(name, pf, label,time,type);
        HashMap<String,String> values = new HashMap<>();
        values.put("name",name);
        values.put("pf",pf);
        values.put("label",label);
        values.put("time",time);
        values.put("type",type+"");
        return getDataAndInfoByPost(user, "/api/feature/portraits", values);
    }

    @Override
    public Model report_interpose(PMUser user, String id, String data, String api_url, String api_time, String params) {
        String url = apiUrlBuilder.create_report_interpose(id, data, api_url, api_time, params);
//        Page page = null;
//        HttpClient httpClient = null;
//        RequestConfig.Builder builder = RequestConfig.custom();
//        builder.setConnectionRequestTimeout(1000 * 60 * 2);
//        builder.setConnectTimeout(1000 * 60 * 1);
//        builder.setSocketTimeout(1000 * 60 * 1);
//        RequestConfig requestConfig = builder.build();
//        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
//        String test_url = "http://10.101.1.171:10112/api/data/modify";
        HashMap<String, String> param = new HashMap<>();
        param.put("data",data);
        param.put("api_url",api_url);
        param.put("id",id);
        param.put("api_time",api_time);
        param.put("params",params);
//        page = httpHelper.doPost(httpClient, test_url,param);
        return getDataByPost(user, "/api/data/modify", param);
//        return new Model();
    }

    public JSONObject create_param(JSONObject params) {
        JSONObject param = new JSONObject();
        for (String key : params.keySet()) {
            if (!"".equals(key) && !"".equals(params.getString(key))) {
                if (params.get(key) instanceof JSONArray) {
                    JSONArray array = (JSONArray) params.get(key);
                    param.put("params." + key, Arrays.asList(array.toArray()));
                } else {
                    param.put("params." + key, params.get(key));
                }
            }
        }
        return param;
    }
}
