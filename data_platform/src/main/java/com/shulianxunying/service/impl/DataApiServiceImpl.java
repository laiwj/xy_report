package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MDataDao;
import com.shulianxunying.dao.impldao.MTempDataDao;
import com.shulianxunying.entity.TempData;
import com.shulianxunying.entity.impl.ApiTempData;
import com.shulianxunying.service.IDataApiService;
import com.shulianxunying.util.CommonUtil;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.print.Doc;
import java.util.*;

/**
 * Created by SuChang on 2017/4/27 14:05.
 */
@Service("api")
public class DataApiServiceImpl implements IDataApiService {

    @Resource
    MDataDao dataDao;
    @Resource
    MTempDataDao tempDataDao;

    static HashMap<Integer, Integer> typeMap = new HashMap<>();
    static {
        typeMap.put(1, 1);
        typeMap.put(2, 4);
        typeMap.put(3, 12);
        typeMap.put(4, 52);
    }

    // api_url和(统计类型、修改字段、过滤字段(行业和城市))一一对应关系
    static HashMap<String, String[]> api_to_type = new HashMap<>();
    static {
        api_to_type.put("/api/talent/distribution/city", new String[] {"flow", "living", "industry", "city"});
        api_to_type.put("/api/talent/distribution/func", new String[] {"flow", "func", "industry", "city"});
        api_to_type.put("/api/talent/flow/in/city/top", new String[] {"flow", "living", "industry", ""});
        api_to_type.put("/api/talent/flow/out/city/top", new String[] {"flow", "preCity", "industry", ""});
        api_to_type.put("/api/talent/flow/in/func/top", new String[] {"func_flow", "func", "industry", ""});
        api_to_type.put("/api/talent/flow/out/func/top", new String[] {"func_flow", "pre_func", "industry", ""});
        api_to_type.put("/api/talent/flow/in/city", new String[] {"flow", "preCity", "industry", "living"});
        api_to_type.put("/api/talent/flow/out/city", new String[] {"flow", "living", "industry", "preCity"});
        api_to_type.put("/api/talent/exponention/func/need", new String[] {"city_position_count_demand", "func", "industry", "city"});
        api_to_type.put("/api/talent/exponention/func/all", new String[] {"exponention_func", "func", "industry", "city"});
        api_to_type.put("/api/talent/exponention/position/need", new String[] {"city_position_count_demand", "position", "industry", "city"});
        api_to_type.put("/api/talent/exponention/position/all", new String[] {"exponention_func", "position", "industry", "city"});
//        api_to_type.put("/api/hot/position/pay/keyword", new String[] {"position_keyword", "keyword", "", ""});
    }


    /**
     * 人才城市分布
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model talent_distribution_city(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = dataDao.createQuery(cityList, null, null, null, null, null, null, industryList, null);
        List<TempData> resume = dataDao.talent_distribution(query, type, time, top, "living", url, params);
        return returnOneData(resume, url, params);
    }

    /**
     * 人才职能分布
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
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
     * 人才流入的热门城市Top
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model talent_city_flow_in_top(String url, JSONObject params, String time, Integer type) {
        int top = params.getIntValue("top");
        Set<String> industryList = (Set<String>) params.get("industry");
        Document query = new Document();
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_city_flow(query, type, time, top, "living", url, params);
        return returnOneData(resume, url, params);

    }

    /**
     * 人才流出的热门城市Top
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model talent_city_flow_out_top(String url, JSONObject params, String time, Integer type) {
        int top = params.getIntValue("top");
        Set<String> industryList = (Set<String>) params.get("industry");
        Document query = new Document();
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_city_flow(query, type, time, top, "preCity", url, params);
        return returnOneData(resume, url, params);
    }

    /**
     * 人才流入到某城市的前城市TOP
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model talent_flow_in_city(String url, JSONObject params, String time, Integer type) {
        Set<String> cityList = (Set<String>) params.get("city");
        Set<String> industryList = (Set<String>) params.get("industry");
        int top = params.getIntValue("top");
        Document query = new Document();
        if (!cityList.contains("全国")) {
            dataDao.putList(query, "living", cityList);     // 现居地(某城市)在cityList中
        }
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_city_flow(query, type, time, top, "preCity", url, params);
        return returnOneData(resume, url, params);
    }

    /**
     * 人才的热门流入职能TOP
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model talent_func_flow_in_top(String url, JSONObject params, String time, Integer type) {
        int top = params.getIntValue("top");
        Set<String> industryList = (Set<String>) params.get("industry");
        Document query = new Document();
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_func_flow_top(query, type, time, top, "func", url, params);
        return returnOneData(resume, url, params);
    }

    /**
     * 人才的热门流出职能TOP
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model talent_func_flow_out_top(String url, JSONObject params, String time, Integer type) {
        int top = params.getIntValue("top");
        Set<String> industryList = (Set<String>) params.get("industry");
        Document query = new Document();
        dataDao.putList(query, "industry", industryList);
        List<TempData> resume = dataDao.talent_func_flow_top(query, type, time, top, "pre_func", url, params);
        return returnOneData(resume, url, params);
    }

    /**
     * 人才从某城市流出到其他城市的TOP
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
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

    /**
     * 人才的流入职能TOP
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model talent_flow_in_func(String url, JSONObject params, String time, Integer type) {
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

    /**
     * 人才的流出职能TOP
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model talent_flow_out_func(String url, JSONObject params, String time, Integer type) {
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

    /**
     * 人才职能供需指数
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
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

    /**
     * 人才岗位供需指数
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
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

    /**
     * 人才职能需求量
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
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

    /**
     * 人才岗位需求量
     * @param url       api相对地址
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param type      报告周期类型
     * @return
     */
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

    /**
     * 热门岗位薪酬分析
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param api_url   api相对地址
     */
    @Override
    public Model salary_analysis(JSONObject params, String time, String api_url) {
        List<TempData> out = new ArrayList<>();
        // 查询路由表
        Integer t = params.getInteger("t");
        MongoCursor<Document> route_data = dataDao.collectionRoute("resume", t, time, null, 1);
        if (!route_data.hasNext())
            return new Model(-1, "暂无数据!");

        // 查询是否有缓存
        Document next = route_data.next();
        String collection_name = next.getString("collection_name");
        TempData first = tempDataDao.find_one(collection_name, api_url, params);
        if (first != null) {
            out.add(first);
            return returnOneData(out, api_url, params);
        }

        // 根据供需指数过滤出符合的岗位
        String[] index = params.getString("index").split("-");
        ArrayList<String> position_list = dataDao.getPositionByExponention(collection_name, "exponention_position", index[0], index[1]);
//        ArrayList<String> position_list = new ArrayList<String>(){{add("项目管理"); add("网络推广"); add("算法工程师");}};

        // 聚合P100数据，获取薪资最高的N职位
        Set<String> industry_list = (Set<String>)params.get("industry");
        String type = "";
        if (industry_list.size() > 0)
            type = "position_and_industry";
        else
            type = "position_only";
        String key = "position";
        Integer top = params.getInteger("top");
        Document query = new Document();
        query.append("p", "p100");
        query.append("position", new Document("$in", position_list));
        dataDao.putList(query, "industry", industry_list);
        AggregateIterable<Document> documents = dataDao.salary_analysis(collection_name, query, key, type, top);
        List<Document> datas = dataDao.getValue(documents, "_id", "count", top);

        // 获取每个职位中其他阶段最高薪资
        Document p_query = new Document();
        dataDao.putList(p_query, "industry", (Set<String>)params.get("industry"));
        List<Document> out_data = new ArrayList<>();
        Integer accord = 0;
        for (Document data: datas) {
            String position_name = data.getString("name");
            p_query.append("position", position_name);

            // 聚合职位的每个P阶段最高薪资
            AggregateIterable<Document> p_doc = dataDao.salary_analysis(collection_name, p_query, "p", type, 4);
            List<Document> p_datas = dataDao.getValue(p_doc, "_id", "max_salary", "min_salary", 4);
            for (Document p_data: p_datas) {
                p_query.append("p", p_data.getString("name"));
                // 聚合职位每个P阶段的关键词及数量
                AggregateIterable<Document> keyword_doc = dataDao.statisticsKeywords(collection_name, p_query, "keyWordCount", type, 10);
                Document keyword_datas = dataDao.getValueString(keyword_doc, "_id", "count", 5);
                p_data.append("keywords", keyword_datas.get("keys")).append("keywords_num", keyword_datas.getInteger("num"));

                // 聚合职位每个P阶段的特征画像
                AggregateIterable<Document> tags_doc = dataDao.statisticsTags(collection_name, p_query, "tag_count", type);
                Document tags_datas = dataDao.getValue(tags_doc, "_id", "count", params.getString("label"));
                // 若标签中含有"技能"，则将关键词添加到标签中
                if (params.getString("label").contains("技能"))
                    tags_datas.append("技能", dataDao.getDocValue(keyword_doc, "_id", "count", 5));
                p_data.append("tags", tags_datas);
            }

            // 聚合每个职位的数量
            p_query.remove("p");
            Integer position_num = 0;
            AggregateIterable<Document> position_doc = dataDao.statisticsPosiOrFuncCount(collection_name, p_query, "position", type);
            List<Document> position_datas = dataDao.getValue(position_doc, "_id", "count", 1);
            if (position_datas.size() > 0) {
                position_num = position_datas.get(0).getInteger("value");
                accord += position_num;
            }

            out_data.add(new Document(position_name, p_datas).append("position_nums", position_num));
        }

        // 返回数据并放入缓存
        TempData tempData = new TempData();
        tempData.set_id(CommonUtil.md5(collection_name + api_url + JSON.toJSONString(params)));
        tempData.setData(out_data);
        tempData.setParams(params);
        tempData.setApi_url(api_url);
        tempData.setCollection(collection_name);
        tempData.setT(next.getInteger("t"));
        tempData.setStart_time(next.getString("start_time"));
        tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
        tempData.setAccord_data(accord);
        tempData.setTotal_data(dataDao.getCountByType(collection_name, type));
        tempData.setApi_time(time);
        out.add(tempData);
        tempDataDao.saveTempData(tempData);
        return returnOneData(out, api_url, params);
    }

    /**
     * 职位/职能薪酬分析
     * @param params    参数键值对
     * @param time      获取报告时间
     * @param api_url   api相对地址
     * @param key       关键词
     * @param type      报告周期类型
     * @return
     */
    @Override
    public Model salary_analysis(JSONObject params, String time, String api_url, String key, String type) {
        List<TempData> out = new ArrayList<>();
        // 查询路由表
        Integer t = params.getInteger("t");
        MongoCursor<Document> route_data = dataDao.collectionRoute("resume", t, time, null, 1);
        if (!route_data.hasNext())
            return new Model(-1, "暂无数据!");

        // 查询是否有缓存
        Document next = route_data.next();
        String collection_name = next.getString("collection_name");
        TempData first = tempDataDao.find_one(collection_name, api_url, params);
        if (first != null) {
            out.add(first);
            return returnOneData(out, api_url, params);
        }

        // 创建查询语句
        Set<String> name_List = (Set<String>)params.get("name");
        Set<String> industry = (Set<String>)params.get("industry");
        Set<String> city = (Set<String>)params.get("city");
        Set<String> experience = (Set<String>)params.get("experience");
        Document query = new Document();
        dataDao.putList(query, "industry", industry);
        dataDao.putList(query, "city", city);
        dataDao.putList(query, "work_year", experience);

        Integer accord = 0;
        List<Document> out_data = new ArrayList<>();
        for (String name: name_List) {
            // 聚合职位的每个P阶段最高薪资
            query.append(key, name);
            AggregateIterable<Document> p_doc = dataDao.salary_analysis(collection_name, query, "p", type, 4);
            List<Document> p_datas = dataDao.getValue(p_doc, "_id", "max_salary", "min_salary", 4);
            for (Document p_data: p_datas) {
                // 聚合职位每个P阶段的关键词及数量
                query.append("p", p_data.getString("name"));
                AggregateIterable<Document> keyword_doc = dataDao.statisticsKeywords(collection_name, query, "keyWordCount", type, 10);
                Document keyword_datas = dataDao.getValueString(keyword_doc, "_id", "count", 5);
                p_data.append("keywords", keyword_datas.get("keys")).append("keywords_num", keyword_datas.getInteger("num"));
            }

            // 聚合每个职位的数量
            query.remove("p");
            Integer position_num = 0;
            AggregateIterable<Document> position_doc = dataDao.statisticsPosiOrFuncCount(collection_name, query, key, type);
            List<Document> position_datas = dataDao.getValue(position_doc, "_id", "count", 1);
            if (position_datas.size() > 0) {
                position_num = position_datas.get(0).getInteger("value");
                accord += position_num;
            }

            out_data.add(new Document(name, p_datas).append("position_nums", position_num));
        }

        // 返回数据并放入缓存
        TempData tempData = new TempData();
        tempData.set_id(CommonUtil.md5(collection_name + api_url + JSON.toJSONString(params)));
        tempData.setData(out_data);
        tempData.setParams(params);
        tempData.setApi_url(api_url);
        tempData.setCollection(collection_name);
        tempData.setT(next.getInteger("t"));
        tempData.setStart_time(next.getString("start_time"));
        tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
        tempData.setAccord_data(accord);
        tempData.setTotal_data(dataDao.getCountByType(collection_name, type));
        tempData.setApi_time(time);
        out.add(tempData);
        tempDataDao.saveTempData(tempData);
        return returnOneData(out, api_url, params);
    }

    /**
     * 数据报告干预
     * @param id            缓存报告唯一ID
     * @param api_url       生成报告的api地址
     * @param api_time      生成报告的时间
     * @param data_arr      即将修改的json数组
     * @param params_jsonb  参数json对象
     * @return
     */
    public Model report_modify(String id, String api_url, String api_time, JSONArray data_arr, JSONObject params_jsonb) {
        Document temp_doc = tempDataDao.find_by_id("base_temp_data", id);
        boolean is_suss = true;
        // 判断缓存是否存在
        if (temp_doc != null) {
            // 有缓存，则先计算出修改量，放入json对象中
            String[] type_arr = api_to_type.get(api_url);
            // 热门职能： 1、修改的数据是相反的，若是流入，修改的是每个流入对应的流出值，所以这里应该变动。2、每个流入关联的流出是单独缓存的
            if (type_arr[0].equals("func_flow")) {
                boolean is_clear_cache = false;
                for (Object b: data_arr) {
                    JSONObject data = (JSONObject) b;
                    Iterator iterator = data.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        JSONArray arr = (JSONArray) data.get(key);
                        params_jsonb.put(type_arr[1], key);
                        Document temp_arr_doc = getOneCache(temp_doc, params_jsonb);
                        JSONObject modify_json = getModifyIntData(arr, temp_arr_doc);
                        params_jsonb.remove(type_arr[1]);
                        Document query = new Document();
                        query.append(type_arr[1], key);
                        boolean is_modify = modifyData(temp_arr_doc, type_arr, query, modify_json);
                        if (modify_json.size() != 0) {
                            is_clear_cache = true;
                        }
                        if (!is_modify) {
                            is_suss = false;
                        }
                    }
                }
                if (is_clear_cache) {
                    tempDataDao.clearAllCache();
                }
            // 供需指数，数据是double类型
            } else if (type_arr[0].equals("exponention_func")) {
                Document query = new Document();
                JSONObject modify_json = getModifyDoubleData(data_arr, temp_doc);
                if (modify_json.size() != 0) {
                    tempDataDao.clearAllCache();
                }
                is_suss = modifyData(temp_doc, type_arr, query, modify_json);
            // 其他类型
            } else {
                Document query = new Document();
                JSONObject modify_json = getModifyIntData(data_arr, temp_doc);
                if (modify_json.size() != 0) {
                    tempDataDao.clearAllCache();
                }
                is_suss = modifyData(temp_doc, type_arr, query, modify_json);
            }
        } else {
            // 逻辑和步骤上是必须先有缓存，才能进行数据干预
        }

        if (is_suss)
            return new Model();
        else
            return new Model(-2);
    }

    /**
     * 返回接口data数据
     * @param resume    数据列表
     * @param url       api相对地址
     * @param params    参数键值对
     * @return
     */
    public Model returnOneData(List<TempData> resume, String url, JSONObject params) {
        ApiTempData apiData = new ApiTempData();
        if (resume.size() > 0) {
            apiData.setData(resume.get(0).getData()); // 此处不考虑 同比环比，所以直接取第一个
            apiData.set_id(resume.get(0).get_id());
            apiData.setApi_url(url);
            apiData.setParams(params);
            apiData.setEnd_time(resume.get(0).getEnd_time());
            apiData.setApi_time(resume.get(0).getApi_time());
            apiData.setTotal_data(resume.get(0).getTotal_data());
            apiData.setAccord_data(resume.get(0).getAccord_data());
            return new Model().setData(apiData);
        } else
            return new Model(-1, "查询数据失败");
    }

    /**
     * 获取将要改动的Int数据
     * @param data_arr  前端的json数组
     * @param temp_doc  缓存的数据
     * @return
     */
    public JSONObject getModifyIntData(JSONArray data_arr, Document temp_doc) {
        JSONObject modify_json = new JSONObject();
        JSONObject new_data = new JSONObject();
        for (Object b: data_arr) {
            Document document = Document.parse(JSONObject.toJSONString(b));
            Object value = document.get("value");
            if (value.equals("")) {
                new_data.put(document.getString("name"), 0);
            } else {
                new_data.put(document.getString("name"), document.getInteger("value"));
            }
        }

        ArrayList temp_data = (ArrayList) temp_doc.get("data");
        for (Object b: temp_data) {
            Document doc = (Document) b;
            String key = doc.getString("name");
            Integer value = new_data.getIntValue(key) - doc.getInteger("value");
            if (value == 0) {
                continue;
            }

            modify_json.put(key, value);
        }
        return modify_json;
    }

    /**
     * 获取将要改动的Double数据
     * @param data_arr  前端的json数组
     * @param temp_doc  缓存的数据
     * @return
     */
    public JSONObject getModifyDoubleData(JSONArray data_arr, Document temp_doc) {
        JSONObject modify_json = new JSONObject();
        JSONObject new_data = new JSONObject();
        for (Object b: data_arr) {
            Document document = Document.parse(JSONObject.toJSONString(b));
            Object value = document.get("value");
            if (value.equals("")) {
                new_data.put(document.getString("name"), 0.0);
            } else {
                new_data.put(document.getString("name"), document.getDouble("value"));
            }
        }

        ArrayList temp_data = (ArrayList) temp_doc.get("data");
        for (Object b: temp_data) {
            Document doc = (Document) b;
            String key = doc.getString("name");
            Double value = new_data.getDouble(key) - doc.getDouble("value");
            if (value == 0.0) {
                continue;
            }

            modify_json.put(key, value);
        }
        return modify_json;
    }

    /**
     * 修改原始数据
     * @param temp_doc      缓存doc
     * @param type_arr      url和统计类型对应关系
     * @param query         过滤条件
     * @param modify_json   将要修改的json数据
     * @return
     */
    public Boolean modifyData(Document temp_doc, String[] type_arr, Document query, JSONObject modify_json) {
        // 修改原始数据
        String collection_name = temp_doc.getString("collection");
        Document params = (Document) temp_doc.get("params");
        query.append("type", type_arr[0]);
        // 添加查询关键字(职能流动是相反的)
        String key_word;
        if (type_arr[0].equals("func_flow")) {
            if (type_arr[1].equals("func")) {
                key_word = "pre_func";
            } else {
                key_word = type_arr[1];
            }
        } else {
            key_word = type_arr[1];
        }
        // 添加行业
        if (!type_arr[2].isEmpty() && params.get("industry") != null) {
            ArrayList<String> industryList = (ArrayList<String>) params.get("industry");
            dataDao.putList(query, type_arr[2], new HashSet<String>(industryList));
        }
        // 添加城市
        if (!type_arr[3].isEmpty() && params.get("city") != null) {
            ArrayList<String> cityList = (ArrayList<String>) params.get("city");
            dataDao.putList(query, type_arr[3], new HashSet<String>(cityList));
        }
        // 过滤某些职能(职能流动不用过滤)
        if (!type_arr[0].equals("func_flow")) {
            query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        }

        boolean is_modify = dataDao.modifyData(collection_name, query, key_word, modify_json);
        if (!is_modify) {
            return false;
        }

        return true;
    }

    /**
     * 获取一个缓存数据
     * @param temp_doc  缓存doc
     * @param params    参数键值对
     * @return
     */
    public Document getOneCache(Document temp_doc, JSONObject params) {
        Integer t = temp_doc.getInteger("t");
        String time = temp_doc.getString("api_time");
        MongoCursor<Document> route_data = dataDao.collectionRoute("resume", t, time, null, 1);
        Document next = route_data.next();
        String collection_name = next.getString("collection_name");
        String api_url = temp_doc.getString("api_url").replace("/top", "");
        String id = CommonUtil.md5(collection_name + api_url + JSON.toJSONString(params));
        return tempDataDao.find_by_id("base_temp_data", id);
    }


}
