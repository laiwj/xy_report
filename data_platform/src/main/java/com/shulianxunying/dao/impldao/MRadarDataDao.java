package com.shulianxunying.dao.impldao;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.dao.MBaseParentDao;
import com.shulianxunying.dao.MDataParentDao;
import com.shulianxunying.entity.RadarData;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2017/6/13.
 * 人才雷达-数据库相关操作
 */
@Repository
public class MRadarDataDao extends MDataParentDao {

    @Override
    public String setDatabase() {
        return "chengdu_resume_report";
    }

    /**
     * 根据 路由表 查询最新数据表
     * @param type      类型
     * @param t         报告周期类型
     * @param startTime 开始时间
     * @return  返回一个迭代器
     */
    public MongoCursor<Document> collectionRoute(String type, Integer t, String startTime) {
        Document route_query = new Document();
        route_query.append("type", type).append("t", t).append("start_time", startTime);
        return getColletion(data_route_colletion).find(route_query).sort(new Document("end_time", -1)).limit(1).iterator();
    }

    /**
     * 聚合-人才流动
     * @param collectionName    集合名
     * @param query             过滤条件
     * @param key               group关键词
     * @param type              统计类型
     * @param top               返回数量
     * @return
     */
    public AggregateIterable<Document> talent_flow(String collectionName, Document query, String key, String type, Integer top) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\", count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        if (top != 0)
            pipelines.add(Document.parse("{\"$limit\":" + String.valueOf(top) + "}"));
        return getColletion(collectionName).aggregate(pipelines);
    }

    /**
     * 聚合-人才需求分布
     * @param collection_name   集合名
     * @param query             过滤条件
     * @param key               group关键词
     * @param type              统计类型
     * @param top               返回数量
     * @return
     */
    public AggregateIterable<Document> talent_demand_distribute(String collection_name, Document query, String key, String type, Integer top) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        if (top != 0)
            pipelines.add(Document.parse("{\"$limit\":" + String.valueOf(top) + "}"));
        return getColletion(collection_name).aggregate(pipelines);
    }

    /**
     * 聚合-人才分布
     * @param collection_name   集合名
     * @param key               group关键词
     * @param type              统计类型
     * @param top               返回数量
     * @return
     */
    public AggregateIterable<Document> talent_distribute(String collection_name, Document query, String key, String type, Integer top) {
        List<Document> pipelines = new ArrayList<>();
        query.append("type", type);
        pipelines.add(new Document("$match", query));
        pipelines.add(Document.parse("{$group:{_id:\"$" + key + "\",count:{$sum:\"$count\"}}}"));
        pipelines.add(Document.parse("{\"$sort\":{count:-1}}"));
        if (top != 0)
            pipelines.add(Document.parse("{\"$limit\":" + String.valueOf(top) + "}"));
        return getColletion(collection_name).aggregate(pipelines);
    }
}
