package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoException;
import com.shulianxunying.dao.MBaseParentDao;
import com.shulianxunying.entity.RadarData;
import org.bson.Document;
import org.springframework.stereotype.Repository;

/**
 * Created by zhang on 2017/6/13.
 * 成都人才雷达-缓存操作
 */
@Repository
public class MRadarBaseDao extends MBaseParentDao<RadarData> {

    @Override
    public String setDatabase() {
        return "chengdu_resume_report";
    }

    public static String collectionName = "base_temp_data";

    public static String getCollectionName() {
        return collectionName;
    }

    /**
     * 查询是否有缓存
     * @param collection_name   表名
     * @param api_url           请求url
     * @param params            参数键值对
     * @return
     */
    public RadarData find_one(String collection_name, String api_url, JSONObject params){
        return findOne_by_key_values(collectionName,RadarData.class,
                "collection", collection_name, "api_url", api_url, "params", params);
    }

    /**
     * 保存数据到缓存
     * @param radarData 数据对象
     * @return
     */
    public boolean saveRadarData(RadarData radarData) {
        if (radarData != null && (radarData.getCity_data().size() > 0 || radarData.getProvince_data().size() > 0 || radarData.getCountry_data().size() > 0 || radarData.getIndustry_data().size() > 0 || radarData.getPosition_data().size() > 0)) {
            Document query = Document.parse(JSON.toJSONString(radarData));
            try {
                getCollection(collectionName).updateOne(new Document("_id", radarData.get_id()), new Document("$set", query), upsertOptions);
                return true;
            } catch (MongoException e) {

            }
            return false;
        }
        return false;
    }
}
