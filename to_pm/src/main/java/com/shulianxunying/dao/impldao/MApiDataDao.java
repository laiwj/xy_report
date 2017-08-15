package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mongodb.MongoException;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.ApiData;
import com.shulianxunying.util.CommonUtil;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

/**
 * Created by SuChang on 2017/5/2 16:03.
 */
@Repository
public class MApiDataDao extends MBaseDao<ApiData> {
    public static String collectionName = CommonParams.TEMP_DATA;

    public static String getCollectionName() {
        return collectionName;
    }

    public ApiData find_in_loacl(String api_url) {
        return find_by_id(collectionName, CommonUtil.md5(api_url), ApiData.class);
    }

    public boolean upsert_new_document(@NotNull ApiData apiData) {
        return insert_new_document(collectionName, apiData);
    }

    @Override
    public boolean insert_new_document(@NotNull String collectionName, @NotNull ApiData apiData) {
        Document parse = Document.parse(JSON.toJSONString(apiData));
        parse.put("create_time", new BsonDateTime(apiData.getCreate_time().getTime()));
        try {
            getDatabase().getCollection(collectionName).updateOne(new Document("_id", apiData.get_id()), new Document("$set", parse), upsertOptions);
            return true;
        } catch (MongoException e) {
        }
        return false;
    }

    public boolean report_modify(String api_url, JSONArray data) {
        try {
            getCollection(getCollectionName()).updateOne(new Document("api_url", api_url), new Document("$set", new Document("data", data)));
            return true;
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return false;
    }
}
