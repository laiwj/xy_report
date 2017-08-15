package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.MDataDao;
import com.shulianxunying.entity.ApiData;
import com.shulianxunying.entity.TempData;
import com.shulianxunying.service.IDataApiService;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by SuChang on 2017/4/27 14:05.
 */
@Service("api")
public class DataApiServiceImpl implements IDataApiService {

    @Resource
    MDataDao dataDao;
    static HashMap<Integer, Integer> typeMap = new HashMap<>();

    static {
        typeMap.put(1, 1);
        typeMap.put(2, 4);
        typeMap.put(3, 12);
        typeMap.put(4, 52);
    }


    /**
     * 人才城市分布
     *
     * @param url
     * @param params
     * @param time
     * @param type
     * @return
     */
    @Override
    public Model talent_distribution_city(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = dataDao.createQuery(cityList, null, null, null, null, null, null, industryList, null);
        List<TempData> resume = dataDao.talent_distribution(query, type, time, top, "city", url, params);
        return returnOneData(resume, url, params);
    }

    /**
     * 人才 职能分布
     *
     * @param url
     * @param params
     * @param time
     * @param type
     * @return
     */
    @Override
    public Model talent_distribution_func(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = dataDao.createQuery(cityList, null, null, null, null, null, null, industryList, null);
        List<TempData> resume = dataDao.talent_distribution(query, type, time, top, "func", url, params);
        return returnOneData(resume, url, params);
    }

    /**
     * 人才 地域流动 流入热门城市
     *
     * @param url
     * @param params
     * @param time
     * @param type
     * @return
     */
    @Override
    public Model talent_city_flow_in_top(String url, JSONObject params, String time, Integer type) {
        int top = params.getIntValue("top");
        List<TempData> resume = dataDao.talent_city_flow_top(type, time, top, "preCity", url, params);
        return returnOneData(resume, url, params);
    }

    /**
     * 人才 地域流动 流出 热门城市
     *
     * @param url
     * @param params
     * @param time
     * @param type
     * @return
     */
    @Override
    public Model talent_city_flow_out_top(String url, JSONObject params, String time, Integer type) {
        int top = params.getIntValue("top");
        List<TempData> resume = dataDao.talent_city_flow_top(type, time, top, "living", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_flow_in_city(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = new Document();
        if (!cityList.contains("全国")) {
            dataDao.putList(query, "living", cityList);
        }
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_city_flow(query, type, time, top, "preCity", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_func_flow_in_top(String url, JSONObject params, String time, Integer type) {
        int top = params.getIntValue("top");
        Set<String> industryList = (Set<String>) params.get("industry");
        Document query = new Document();
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_func_flow_top(query, type, time, top, "pre_func", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_func_flow_out_top(String url, JSONObject params, String time, Integer type) {
        int top = params.getIntValue("top");
        Set<String> industryList = (Set<String>) params.get("industry");
        Document query = new Document();
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_func_flow_top(query, type, time, top, "func", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_flow_out_city(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = new Document();
        if (!cityList.contains("全国")) {
            dataDao.putList(query, "preCity", cityList);
        }
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_city_flow(query, type, time, top, "living", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_flow_in_func(String url, JSONObject params, String time, Integer type) {
//        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        String func = params.getString("func");
        int top = params.getIntValue("top");
        Document query = new Document();
        dataDao.putList(query, "industry", industryList);
        query.put("func", func);
        query.put("pre_func", new Document("$ne", func));
        List<TempData> resume = dataDao.talent_func_flow(query, type, time, top, "pre_func", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_flow_out_func(String url, JSONObject params, String time, Integer type) {
//        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        String pre_func = params.getString("pre_func");
        int top = params.getIntValue("top");
        Document query = new Document();
        dataDao.putList(query, "industry", industryList);
        query.put("pre_func", pre_func);
        query.put("func", new Document("$ne", pre_func));
        List<TempData> resume = dataDao.talent_func_flow(query, type, time, top, "func", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_demand_func(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = new Document();
        if (!cityList.contains("全国")) {
            dataDao.putList(query, "city", cityList);
        }
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_demand(type, "exponention_func", time, top, "func", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_demand_position(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = new Document();
        if (!cityList.contains("全国")) {
            dataDao.putList(query, "city", cityList);
        }
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_demand(type, "exponention_position", time, top, "position", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_supply_func(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = new Document();
        if (!cityList.contains("全国")) {
            dataDao.putList(query, "city", cityList);
        }
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_supply(query, type, time, top, "func", url, params);
        return returnOneData(resume, url, params);
    }

    @Override
    public Model talent_supply_position(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = new Document();
        if (!cityList.contains("全国")) {
            dataDao.putList(query, "city", cityList);
        }
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_supply(query, type, time, top, "position", url, params);
        return returnOneData(resume, url, params);
    }

    public Model returnOneData(List<TempData> resume, String url, JSONObject params) {
        ApiData apiData = new ApiData();
        if (resume.size() > 0) {
            apiData.setData(resume.get(0).getData()); // 此处不考虑 同比环比，所以直接取第一个
            apiData.set_id(resume.get(0).get_id());
            apiData.setApi_url(url);
            apiData.setParams(params);
            return new Model().setData(apiData);
        } else
            return new Model(-1, "查询数据失败");
    }

    /**
     * 预处理 城市
     *
     * @param cityList
     * @return
     */
    public Set<String> deal_city_set(Set<String> cityList) {

        return cityList;
    }

    public Set<String> deal_func_to_position(Set<String> funcList) {

        return funcList;
    }
}
