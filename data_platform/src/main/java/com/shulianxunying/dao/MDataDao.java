package com.shulianxunying.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.shulianxunying.dao.impldao.MTempDataDao;
import com.shulianxunying.entity.TempData;
import com.shulianxunying.util.CommonUtil;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.List;

/**
 * Created by SuChang on 2017/4/27 15:05.
 */
@Repository
public class MDataDao {

    public static final Logger logger = Logger.getLogger(MBaseDao.class.getName());

    @Resource(name = "data_info_mongo")
    private MongoClient mongoClient;
    @Resource
    private MTempDataDao tempDataDao;

    public static String base_database = "resume_report";

    public static String data_route_colletion = "analysis_collection_temp";


    // upsert true
    public static UpdateOptions upsertOptions = new UpdateOptions().upsert(true);

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

    public MongoDatabase getDatabase() {
        return getDatabase(base_database);
    }

    public MongoDatabase getDatabase(@NotNull String databaseName) {
        return getMongoClient().getDatabase(databaseName);
    }

    public MongoCollection<Document> getColletion(@NotNull String collectionName) {
        return getDatabase().getCollection(collectionName);
    }


    /**
     * @return 构造 查询语句
     */
    public Document createQuery(Set<String> cityList,
                                Set<String> genderList,
                                Set<String> ageList,
                                Set<String> degreeList,
                                Set<String> salaryList,
                                Set<String> workyearList,
                                Set<String> positionList,
                                Set<String> industryList,
                                Set<String> functionList
    ) {
        Document query = new Document();
        if (!cityList.contains("全国")) {
            putList(query, "city", cityList);
        }
        putList(query, "gender", genderList);
        putList(query, "age", ageList);
        putList(query, "degree", degreeList);
        putList(query, "salary", salaryList);
        putList(query, "workyear", workyearList);
        putList(query, "industry", industryList);
        putList(query, "position", positionList);
        putList(query, "function", functionList);
        return query;
    }

    public void putList(Document query, String key, Set<String> value) {
        if (value != null && value.size() > 0) {
            query.append(key, new Document("$in", value));
        }
    }

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

    public AggregateIterable<Document> talent_func_flow_top(Document query, String collectionName, String key) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", "func_flow");
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资", "unknow")));
        query.append("per_func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资", "unknow")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    public AggregateIterable<Document> talent_func_flow_one(String collectionName, Document query, String key) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", "func_flow");
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

    public AggregateIterable<Document> talent_supply_one(String collectionName, Document query, String key) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", "city_position_count");
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    public AggregateIterable<Document> talent_city_flow_top_one(String collectionName, String key) {
        Document query = new Document();
        List<Document> pipelines = new ArrayList<>();
        query.append("type", "flow");
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    public AggregateIterable<Document> talent_city_flow_one(String collectionName, Document query, String key) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", "flow");
        // 排除掉 部分职能
        query.append("func", new Document("$nin", Arrays.asList("人力资源", "行政&采购", "财务", "风控&法务", "公司事务&投融资")));
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        pipelines.add(Document.parse("{\"$limit\":12}"));
        return getColletion(collectionName).aggregate(pipelines);
    }


    public List<TempData> talent_demand(Integer t, String result_type, String time, Integer count, String key, String url, JSONObject params) {
        List<TempData> out = new ArrayList<>();
        MongoCursor<Document> route_data = collectionRoute("resume", t, time, null, 1);
        int i = 0;
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
                tempData.setEnd_time(next.getString("end_time"));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

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
                AggregateIterable<Document> documents = talent_supply_one(collection_name, query, key);
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time"));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

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
                AggregateIterable<Document> documents = talent_city_flow_top_one(collection_name, key);
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time"));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

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
                AggregateIterable<Document> documents = talent_func_flow_top(query, collection_name, key);
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time"));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

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
                AggregateIterable<Document> documents = talent_func_flow_one(collection_name, query, key);
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time"));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

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
                AggregateIterable<Document> documents = talent_city_flow_one(collection_name, query, key);
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time"));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);
            }
        }
        return out;
    }

    /**
     * @param query
     * @param time
     * @param count
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
                AggregateIterable<Document> documents = talent_distribution_data_one(collection_name, query, key, "city_position_count");
                List<Document> data = getValue(documents, "_id", "count", count);
                TempData tempData = new TempData();
                tempData.set_id(CommonUtil.md5(collection_name + url + JSON.toJSONString(params)));
                tempData.setData(data);
                tempData.setParams(params);
                tempData.setApi_url(url);
                tempData.setCollection(collection_name);
                tempData.setT(next.getInteger("t"));
                tempData.setStart_time(next.getString("start_time"));
                tempData.setEnd_time(next.getString("end_time"));
                out.add(tempData);
                tempDataDao.saveTempData(tempData);

            }
        }
        return out;
    }


    /**
     * 根据 路由表 查询对应数据所在表的名字
     *
     * @param type
     * @param t
     * @param start_time
     * @param end_time
     * @return 返回一个迭代器
     */
    public MongoCursor<Document> collectionRoute(String type, Integer t, String start_time, String end_time, Integer limit) {
        Document route_query = new Document();
        route_query.append("type", type).append("t", t).append("start_time", new Document("$lte", start_time));
        return getColletion(data_route_colletion).find(route_query).sort(new Document("time", -1)).limit(limit).iterator();
    }

    /**
     * 从迭代器中 获取数据
     *
     * @param iterable
     * @param key1
     * @param key2
     * @param count
     * @return
     */
    public List<Document> getValue(Iterable<Document> iterable, String key1, String key2, Integer count) {
        List<Document> out = new ArrayList<>();
        int i = 0;
        for (Document d : iterable) {
            Object id = d.get("_id");
            if (id != null && !id.equals("unknow")) {
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

    public <TEntity> TEntity document_2_class(Document document, Class<TEntity> cls) {
        if (document != null) {
            return JSON.parseObject(JSON.toJSONString(document), cls);
        } else {
            return null;
        }
    }
}
