package com.shulianxunying.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.shulianxunying.dao.impldao.MTempDataDao;
import org.bson.Document;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by SuChang on 2017/6/15 16:51.
 * 终极父类，你懂的！
 */
public abstract class MDataParentDao {

    String base_database = null;
    @Resource(name = "data_info_mongo")
    public MongoClient mongoClient;
    @Resource
    public MTempDataDao tempDataDao;

    public static String data_route_colletion = "analysis_collection_temp";

    // upsert true
    public static UpdateOptions upsertOptions = new UpdateOptions().upsert(true);

    public MDataParentDao() {
        this.base_database = setDatabase();
    }

    public abstract String setDatabase();

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

}
