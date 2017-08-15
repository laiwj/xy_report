package com.shulianxunying.dao.impldao;


import com.alibaba.fastjson.JSON;
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

    public TempData find_one(String collection_name, String api_url, JSONObject params){
        return findOne_by_key_values(collectionName,TempData.class,
                "collection",collection_name,"api_url", api_url,"params", params);
    }

    public boolean saveTempData(TempData tempData) {
        if (tempData != null && tempData.getData().size() > 0) {
            Document query = Document.parse(JSON.toJSONString(tempData));
            try {
                getCollection(collectionName).updateOne(new Document("_id", tempData.get_id()), new Document("$set", query), upsertOptions);
                return true;
            } catch (MongoException e) {

            }
            return false;
        }
        return false;
    }
}
