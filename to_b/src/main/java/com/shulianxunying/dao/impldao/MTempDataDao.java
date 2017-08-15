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

    public TempData find_one(String collection_name, String api_url, JSONObject params) {
        return findOne_by_key_values(collectionName, TempData.class,
                "collection", collection_name, "api_url", api_url, "params", params);
    }

    public TempData find_one(String data_id) {
        return find_by_id(collectionName, data_id, TempData.class);
    }
}
