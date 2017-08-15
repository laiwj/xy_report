package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.dao.MDataParentDao;
import com.shulianxunying.entity.TempData;
import com.shulianxunying.util.CommonUtil;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import javax.print.attribute.standard.DocumentName;
import java.util.*;

/**
 * Created by zhang on 2017/6/15.
 */
@Repository
public class MDataDao extends MDataParentDao {

    public static final Logger logger = Logger.getLogger(com.shulianxunying.dao.impldao.MDataDao.class.getName());

    // 标签对应
    static HashMap<String, String> tag_to_name = new HashMap<>();
    static {
        tag_to_name.put("本科", "学历");
        tag_to_name.put("硕士", "学历");
        tag_to_name.put("大专", "学历");
        tag_to_name.put("博士", "学历");
        tag_to_name.put("高中", "学历");
        tag_to_name.put("初中及以下", "学历");
        tag_to_name.put("男", "性别");
        tag_to_name.put("女", "性别");
        tag_to_name.put("18-25", "年龄");
        tag_to_name.put("25-30", "年龄");
        tag_to_name.put("30-35", "年龄");
        tag_to_name.put("35-40", "年龄");
        tag_to_name.put("40+", "年龄");
        tag_to_name.put("0-3", "行业经验");
        tag_to_name.put("3-5", "行业经验");
        tag_to_name.put("5-8", "行业经验");
        tag_to_name.put("8-12", "行业经验");
        tag_to_name.put("12+", "行业经验");

    }

    @Override
    public String setDatabase() {
        return "resume_report";
    }

    /**
     * 聚合-人才分布-count by key
     * 需要排除指定的职能
     * @param collectionName    集合名
     * @param query             过滤条件
     * @param key               city or func (城市或职能)
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> talent_distribution_data_one(String collectionName, Document query, String key, String type) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-人才热门流动职能-count by key
     * 需要排除指定的职能
     * @param query             过滤条件
     * @param collectionName    集合名
     * @param key               pre_func or func (前职能或现职能)
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> talent_func_flow_top(Document query, String collectionName, String key, String type) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资", "unknow")));
        query.append("per_func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资", "unknow")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-人才流动职能-count by key
     * @param collectionName    集合名
     * @param query             过滤条件
     * @param key               pre_func or func (前职能或现职能)
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> talent_func_flow_one(String collectionName, Document query, String key, String type) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
//         排除掉 部分职能
//        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资", "unknow")));
//        query.append("per_func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资", "unknow")));
//        query.append(key, new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-人才职能供给-count by key
     * @param collectionName    集合名
     * @param query             过滤条件
     * @param key               func or (职能或)
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> talent_supply_one(String collectionName, Document query, String key, String type) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-人才城市流动TOP-count by key
     * 需要排除指定的职能
     * @param collectionName    集合名称
     * @param key               preCity or living (以前城市或现居地)
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> talent_city_flow_top_one(String collectionName, String key, String type) {
        Document query = new Document();
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-人才城市流动(某城市)-count by key
     * 需要排除指定的职能
     * @param collectionName    集合名称
     * @param query             查询过滤条件
     * @param key               preCity or living (以前城市或现居地)
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> talent_city_flow_one(String collectionName, Document query, String key, String type) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-根据type统计总数
     * @param collectionName    集合名称
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> statisticsCount(String collectionName, String type) {
        List<Document> pipelines = new ArrayList<>();
        pipelines.add(new Document("$match", new Document("type", type)));
        pipelines.add(Document.parse("{$group: {_id: \"$type\", count: {$sum: \"$count\"}}}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-根据type统计某字段总数
     * @param collectionName    集合名称
     * @param type              统计类型
     * @param name              字段名
     * @param value             字段值
     * @return
     */
    public AggregateIterable<Document> statisticsCount(String collectionName, String type, String name, String value) {
        List<Document> pipelines = new ArrayList<>();
        Document query = new Document();
        query.append("type", type);
        query.append(name, value);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group: {_id: \"$type\", count: {$sum: \"$count\"}}}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-根据type统计多个字段总数
     * @param collection_name   集合名称
     * @param type              统计类型
     * @param query             过滤条件
     * @return
     */
    public AggregateIterable<Document> statisticsCount(String collection_name, String type, Document query) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group: {_id: \"$type\", count: {$sum: \"$count\"}}}"));
        return getColletion(collection_name).aggregate(pipelines);
    }

    /**
     * 聚合-职位/职能关键词
     * @param collection_name   集合名称
     * @param query             过滤条件
     * @param key               聚合关键词
     * @param type              统计类型
     * @param top               top指数
     * @return
     */
    public AggregateIterable<Document> statisticsKeywords(String collection_name, Document query, String key, String type, Integer top) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{\"$unwind\": \"$"+ key +"\"}"));
        pipelines.add(Document.parse("{$group: {_id: \"$" + key + ".keyword\", count: {$sum: \"$"+ key +".count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":"+ String.valueOf(top) +"}"));
        return getColletion(collection_name).aggregate(pipelines);
    }

    /**
     * 聚合-人才画像
     * @param collection_name   集合名称
     * @param query             过滤条件
     * @param key               聚合关键词
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> statisticsTags(String collection_name, Document query, String key, String type) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{\"$unwind\": \"$"+ key +"\"}"));
        pipelines.add(Document.parse("{$group: {_id: \"$" + key + ".tag\", count: {$sum: \"$"+ key +".count\"}}}"));
        return getColletion(collection_name).aggregate(pipelines);
    }

    /**
     * 聚合-薪酬的某职能或岗位的数量
     * @param collection_name   集合名称
     * @param query             过滤条件
     * @param key               聚合关键词
     * @param type              统计类型
     * @return
     */
    public AggregateIterable<Document> statisticsPosiOrFuncCount(String collection_name, Document query, String key, String type) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group: {_id: \"$" + key + "\", count: {$sum: \"$count\"}}}"));
        return getColletion(collection_name).aggregate(pipelines);
    }

    /**
     * 聚合-薪酬分析最高/最低薪酬
     * @param collection_name   集合名称
     * @param query             过滤条件
     * @param key               keyword
     * @param type              统计类型
     * @param top               limit数量
     * @return
     */
    public AggregateIterable<Document> salary_analysis(String collection_name, Document query, String key, String type, Integer top) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group: {_id: \"$" + key + "\", max_salary: {$max: \"$max_salary\"}, min_salary: {$min: \"$min_salary\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":"+ String.valueOf(top+2) +"}"));
        return getColletion(collection_name).aggregate(pipelines);
    }

    /**
     * 人才供需指数
     * @param t             报告周期类型
     * @param result_type   统计类型
     * @param time          获取报告时间
     * @param count         Top指数
     * @param key           func or position(职能或岗位)
     * @param url           api相对地址
     * @param params        参数键值对
     * @return
     */
    public List<TempData> talent_demand(Integer t, String result_type, String time, Integer count, String key, String url, JSONObject params) {
        List<TempData> out = new ArrayList<>();
        MongoCursor<Document> route_data = collectionRoute("resume", t, time, null, 1);
        while (route_data.hasNext()) {
            Document next = route_data.next();
            String collection_name = next.getString("collection_name");
            //查询 对应表 是否有 缓存数据
            TempData first = tempDataDao.find_one(collection_name, url, params);
            if (first != null) {
                out.add(first);
            } else {
                FindIterable<Document> limit = getColletion(collection_name).find(new Document("type", result_type)).sort(new Document("count", -1)).limit(11);
                List<Document> data = getValue(limit, key, "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
                tempData.setApi_time(time);
                tempData.setTotal_data(getCountByType(collection_name, "city_position_count") + getCountByType(collection_name, "city_position_count_demand"));
                tempData.setAccord_data(getSupplyAndDemandCountByData(data, key, collection_name));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

    /**
     * 人才需求量
     * @param query     查询条件
     * @param t         报告周期类型
     * @param time      获取报告时间
     * @param count     Top指数
     * @param key       func or position(职能或岗位)
     * @param url       api相对地址
     * @param params    参数键值对
     * @return
     */
    public List<TempData> talent_supply(Document query, Integer t, String time, Integer count, String key, String url, JSONObject params) {
        List<TempData> out = new ArrayList<>();
        MongoCursor<Document> route_data = collectionRoute("resume", t, time, null, 1);
        while (route_data.hasNext()) {
            Document next = route_data.next();
            String collection_name = next.getString("collection_name");
            //查询 对应表 是否有 缓存数据
            TempData first = tempDataDao.find_one(collection_name, url, params);
            if (first != null) {
                out.add(first);
            } else {
                AggregateIterable<Document> documents = talent_supply_one(collection_name, query, key, "city_position_count_demand");
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
                tempData.setApi_time(time);
                tempData.setTotal_data(getCountByType(collection_name, "city_position_count_demand"));
                tempData.setAccord_data(getCountByData(data));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

    /**
     * 人才流动城市Top
     * @param t         报告周期类型
     * @param time      获取报告时间
     * @param count     Top指数
     * @param key       preCity or living (以前城市或现居地)
     * @param url       api相对地址
     * @param params    参数键值对
     * @return
     */
    public List<TempData> talent_city_flow_top(Integer t, String time, Integer count, String key, String url, JSONObject params) {
        List<TempData> out = new ArrayList<>();
        MongoCursor<Document> route_data = collectionRoute("resume", t, time, null, 1);
        while (route_data.hasNext()) {
            Document next = route_data.next();
            String collection_name = next.getString("collection_name");
            //查询 对应表 是否有 缓存数据
            TempData first = tempDataDao.find_one(collection_name, url, params);
            if (first != null) {
                out.add(first);
            } else {
                AggregateIterable<Document> documents = talent_city_flow_top_one(collection_name, key, "flow");
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
                tempData.setApi_time(time);
                tempData.setTotal_data(getCountByType(collection_name, "flow"));
                tempData.setAccord_data(getCountByData(data));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

    /**
     * 人才的热门流动职能TOP
     * @param query     查询过滤条件
     * @param t         报告周期类型
     * @param time      获取报告时间
     * @param count     Top指数
     * @param key       pre_func or func (前职能或现职能)
     * @param url       api相对地址
     * @param params    参数键值对
     * @return
     */
    public List<TempData> talent_func_flow_top(Document query, Integer t, String time, Integer count, String key, String url, JSONObject params) {
        List<TempData> out = new ArrayList<>();
        MongoCursor<Document> route_data = collectionRoute("resume", t, time, null, 1);
        while (route_data.hasNext()) {
            Document next = route_data.next();
            String collection_name = next.getString("collection_name");
            //查询 对应表 是否有 缓存数据
            TempData first = tempDataDao.find_one(collection_name, url, params);
            if (first != null) {
                out.add(first);
            } else {
                AggregateIterable<Document> documents = talent_func_flow_top(query, collection_name, key, "func_flow");
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
                tempData.setApi_time(time);
                tempData.setTotal_data(getCountByType(collection_name, "func_flow"));
                tempData.setAccord_data(getCountByData(data));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

    /**
     * 人才的流动职能TOP
     * @param query     查询过滤条件
     * @param t         报告周期类型
     * @param time      获取报告时间
     * @param count     Top指数
     * @param key       pre_func or func (前职能或现职能)
     * @param url       api相对地址
     * @param params    参数键值对
     * @return
     */
    public List<TempData> talent_func_flow(Document query, Integer t, String time, Integer count, String key, String url, JSONObject params) {
        List<TempData> out = new ArrayList<>();
        MongoCursor<Document> route_data = collectionRoute("resume", t, time, null, 1);
        while (route_data.hasNext()) {
            Document next = route_data.next();
            String collection_name = next.getString("collection_name");
            //查询 对应表 是否有 缓存数据
            TempData first = tempDataDao.find_one(collection_name, url, params);
            if (first != null) {
                out.add(first);
            } else {
                AggregateIterable<Document> documents = talent_func_flow_one(collection_name, query, key, "func_flow");
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
                tempData.setApi_time(time);
                tempData.setTotal_data(getCountByType(collection_name, "func_flow"));
                tempData.setAccord_data(getCountByData(data));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

    /**
     * 人才流动(过滤某些城市)城市Top
     * @param query     查询过滤条件
     * @param t         报告周期类型
     * @param time      获取报告时间
     * @param count     Top指数
     * @param key       preCity or living (以前城市或现居地)
     * @param url       api相对地址
     * @param params    参数键值对
     * @return
     */
    public List<TempData> talent_city_flow(Document query, Integer t, String time, Integer count, String key, String url, JSONObject params) {
        List<TempData> out = new ArrayList<>();
        MongoCursor<Document> route_data = collectionRoute("resume", t, time, null, 1);
        while (route_data.hasNext()) {
            Document next = route_data.next();
            String collection_name = next.getString("collection_name");
            //查询 对应表 是否有 缓存数据
            TempData first = tempDataDao.find_one(collection_name, url, params);
            if (first != null) {
                out.add(first);
            } else {
                AggregateIterable<Document> documents = talent_city_flow_one(collection_name, query, key, "flow");
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
                tempData.setApi_time(time);
                tempData.setTotal_data(getCountByType(collection_name, "flow"));
                tempData.setAccord_data(getCountByData(data));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

    /**
     * 人才分布Top
     * @param query     查询条件
     * @param t         报告周期类型
     * @param time      获取报告时间
     * @param count     Top指数
     * @param key       living or func (城市或职能)
     * @param url       api相对地址
     * @param params    参数键值对
     * @return
     */
    public List<TempData> talent_distribution(Document query, Integer t, String time, Integer count, String key, String url, JSONObject params) {
        List<TempData> out = new ArrayList<>();
        MongoCursor<Document> route_data = collectionRoute("resume", t, time, null, 1);
        while (route_data.hasNext()) {
            Document next = route_data.next();
            String collection_name = next.getString("collection_name");
            //查询 对应表 是否有 缓存数据
            TempData first = tempDataDao.find_one(collection_name, url, params);
            if (first != null) {
                out.add(first);
            } else {
                AggregateIterable<Document> documents = talent_distribution_data_one(collection_name, query, key, "flow");
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time").replace("-", "/"));
                tempData.setApi_time(time);
                tempData.setTotal_data(getCountByType(collection_name, "flow"));
                tempData.setAccord_data(getCountByData(data));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

    /**
     * 根据 路由表 查询对应数据所在表的名字
     * @param type          类型
     * @param t             报告周期类型
     * @param start_time    开始时间
     * @param end_time      结束时间
     * @return 返回一个迭代器
     */
    public MongoCursor<Document> collectionRoute(String type, Integer t, String start_time, String end_time, Integer limit) {
        Document route_query = new Document();
        route_query.append("type", type).append("t", t).append("start_time", new Document("$lte", start_time));
        return getColletion(data_route_colletion).find(route_query).sort(new Document("end_time", -1)).limit(limit).iterator();
    }

    /**
     * 从迭代器中 获取数据
     * @param iterable  迭代器
     * @param key1      关键词1
     * @param key2      关键词2
     * @param count     top指数
     * @return
     */
    public Document getDocValue(Iterable<Document> iterable, String key1, String key2, Integer count) {
        Document out = new Document();
        int i = 0;
        for (Document d: iterable) {
            Object id = d.get("_id");
            if (id != null && !id.equals("unknow") && !id.equals("unknown")) {
                if (i >= count)
                    break;
                out.append(d.getString(key1), d.getInteger(key2));
                i++;
            }
        }
        return out;
    }

    /**
     * 从迭代器中 获取数据
     * @param iterable  迭代器
     * @param key1      关键词1
     * @param key2      关键词2
     * @param count     Top指数
     * @return
     */
    public List<Document> getValue(Iterable<Document> iterable, String key1, String key2, Integer count) {
        List<Document> out = new ArrayList<>();
        int i = 0;
        for (Document d: iterable) {
            Object id = d.get("_id");
            if (id != null && !id.equals("unknow") && !id.equals("unknown")) {
                if (i >= count)
                    break;
                d.put("name", d.get(key1));
                d.put("value", d.get(key2));
                d.remove("_id");
                d.remove("count");
                out.add(d);
                i++;
            }
        }
        return out;
    }

    /**
     * 从迭代器中 获取数据
     * @param iterable  迭代器
     * @param key1      关键词1
     * @param key2      关键词2
     * @param key3      关键词3
     * @param count     Top指数
     * @return
     */
    public List<Document> getValue(Iterable<Document> iterable, String key1, String key2, String key3, Integer count) {
        List<Document> out = new ArrayList<>();
        int i = 0;
        for (Document d: iterable) {
            Object id = d.get("_id");
            if (id != null && !id.equals("unknow") && !id.equals("unknown")) {
                if (i >= count)
                    break;

                Document temp = new Document();
                temp.append("name", d.get(key1)).append(key2, d.get(key2)).append(key3, d.get(key3));
                out.add(temp);
                i++;
            }
        }
        return out;
    }

    /**
     * 从迭代器中 获取数据(含过滤)
     * @param iterable  迭代器
     * @param key1      关键词1
     * @param key2      关键词2
     * @param str       过滤字符串
     * @return
     */
    public Document getValue(Iterable<Document> iterable, String key1, String key2, String str) {
        Document res = new Document();
        for (Document d: iterable) {
            String id = d.getString("_id");
            if (id != null && !id.contains("unknow")) {
                String tag_name = tag_to_name.get(id);
                if (!str.contains(tag_name))
                    continue;

                if (res.get(tag_name) == null) {
                    res.append(tag_name, new Document(d.getString(key1), d.get(key2)));
                } else {
                    Document temp = (Document) res.get(tag_name);
                    temp.append(d.getString(key1), d.get(key2));
                }
                d.remove("_id");
                d.remove("count");
            }

        }
        return res;
    }

    /**
     * 从迭代器中 获取数据并组装(字符串放数组，数量叠加)
     * @param iterable  迭代器
     * @param key1      关键词1
     * @param key2      关键词2
     * @param count     Top指数
     * @return
     */
    public Document getValueString(AggregateIterable<Document> iterable, String key1, String key2, int count) {
        Document result = new Document();
        int i = 0;
        ArrayList<String> keys = new ArrayList<>();
        Integer num = 0;
        for (Document d : iterable) {
            Object id = d.get("_id");
            if (id != null && !id.equals("unknow") && !id.equals("unknown")) {
                if (i >= count)
                    break;
                keys.add(d.getString(key1));
                num += d.getInteger(key2);
                i++;
            }
        }
        result.append("keys", keys).append("num", num);
        return result;
    }

    public <TEntity> TEntity document_2_class(Document document, Class<TEntity> cls) {
        if (document != null) {
            return JSON.parseObject(JSON.toJSONString(document), cls);
        } else {
            return null;
        }
    }

    /**
     * 根据type值查询数据总量
     * @param collectionName    集合名称
     * @param type              统计类型
     * @return 有则返回对应数量，没有则返回0
     */
    public Integer getCountByType(String collectionName, String type) {
        AggregateIterable<Document> documents = statisticsCount(collectionName, type);
        for (Document d: documents) {
            Object id = d.get("_id");
            if (id.equals(type)) {
                return d.getInteger("count");
            }
        }
        return 0;
    }

    /**
     * 查询某字段的数据总量
     * @param collectionName    集合名称
     * @param type              统计类型
     * @param name              字段名
     * @param value             字段值
     * @return 有则返回对应数量，没有则返回0
     */
    public Integer getCountBytype(String collectionName, String type, String name, String value) {
        AggregateIterable<Document> documents = statisticsCount(collectionName, type, name, value);
        for (Document d: documents) {
            Object id = d.get("_id");
            if (id.equals(type)) {
                return d.getInteger("count");
            }
        }
        return 0;
    }

    /**
     * 查询多个字段的数据总量
     * @param collection_name   集合名称
     * @param type              统计类型
     * @param key               字段名
     * @param value             多个字段值
     * @return
     */
    public Integer getAllCountBytype(String collection_name, String type, Document query, String key, ArrayList<String> value) {
        query.append(key, new Document("$in", value));
        AggregateIterable<Document> documents = statisticsCount(collection_name, type, query);
        for (Document d: documents) {
            Object id = d.get("_id");
            if (id.equals(type)) {
                return d.getInteger("count");
            }
        }
        return 0;
    }

    /**
     * 数据count求和
     * @param datas    求和的数据
     * @return
     */
    public Integer getCountByData(List<Document> datas) {
        int count = 0;
        for (Document d: datas) {
            try {
                count += d.getInteger("value");
            } catch (ClassCastException e){
                count += Integer.parseInt(d.getString("value"));
            }
        }
        return count;
    }

    /**
     * 根据数据中的name求供需之和
     * @param datas             数据列表
     * @param key               func or position(职能或岗位)
     * @param collectionName    集合名
     * @return
     */
    public Integer getSupplyAndDemandCountByData(List<Document> datas, String key, String collectionName) {
        int count = 0;
        for (Document d: datas) {
            String value = d.getString(key);
            count += getCountBytype(collectionName, "city_position_count", key, value);
            count += getCountBytype(collectionName, "city_position_count_demand", key, value);
        }
        return count;
    }

    /**
     * 修改多条数据
     * @param collection_name   集合名
     * @param query             统计类型
     * @param key_word          修改字段
     * @param modify_json       修改值json对象
     */
    public Boolean modifyData(String collection_name, Document query, String key_word, JSONObject modify_json) {
        for (Map.Entry<String, Object> entry: modify_json.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Integer) {
                if ((Integer) value == 0) {
                    continue;
                }
            } else {
                if ((Double) value == 0.0) {
                    continue;
                }
            }

            query.append(key_word, key);
            boolean is_modify = tempDataDao.modifyOneData(collection_name, query, value);
            if (!is_modify) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查询供需指数大于/小于的职位
     * @param collection_name       表名
     * @param exponention_position  统计类型
     * @param index1                最小供需指数
     * @param index1                最大供需指数
     * @return
     */
    public ArrayList<String> getPositionByExponention(String collection_name, String exponention_position, String index1, String index2) {
        ArrayList<String> result = new ArrayList<>();
        Double index_1 = Double.parseDouble(index1);
        Double index_2 = Double.parseDouble(index2);
        Document query = new Document();
        query.append("type", exponention_position);
        query.append("count", new Document("$gte", index_1).append("$lte", index_2));

        FindIterable<Document> datas = getColletion(collection_name).find(query);
        for (Document data: datas)
            result.add(data.getString("position"));
        return result;
    }



}