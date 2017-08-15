package com.shulianxunying.dao.impldao;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoException;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.TempData;
import org.bson.Document;
import org.springframework.stereotype.Repository;


/**
 * Created by SuChang on 2017/5/2 16:03.
 */
@Repository
public class MTempDataDao extends MBaseDao<TempData> {
    public static String collectionName = "base_temp_data";

    public static String getCollectionName() {
        return collectionName;
    }

    public boolean report_modify(String data_id, JSONArray data) {
        try {
            getCollection(collectionName).updateOne(new Document("_id", data_id), new Document("$set", new Document("data", data)));
            return true;
        } catch (MongoException e) {
        }
        return false;
    }
}
