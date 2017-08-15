package com.shulianxunying.dao.impldao;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.shulianxunying.dao.MBaseParentDao;
import com.shulianxunying.entity.TempData;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;


/**
 * Created by SuChang on 2017/5/2 16:03.
 */
@Repository
public class MTempDataDao extends MBaseParentDao<TempData> {

    @Override
    public String setDatabase() {
        return "resume_report";
    }

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

    /**
     * 查询某个数据是否存在
     * @param id    数据唯一ID
     * @return 存在返回true,反之为false
     */
    public Boolean findById(@NotNull String id) {
        boolean result = false;
        Document data = find_by_id(collectionName, id);
        if (data != null) {
            result = true;
        }
        return result;
    }

    /**
     * 根据id更新数据
     * @param id    唯一ID
     * @param doc   更新document
     * @return 成功返回true,反之为false
     */
    public Boolean upDataOneById(String id, Document doc) {
        try {
            getCollection(collectionName).updateOne(new Document("_id", id), doc);
            return true;
        } catch (MongoException e) {
            return false;
        }
    }

    /**
     * 修改一条数据
     * @param collection_name   集合名
     * @param query             查询条件
     * @param num               修改数量
     */
    public Boolean modifyOneData(String collection_name, Document query, Object num) {
        if (num instanceof Integer) {
            Integer nums = (Integer) num;
            return modifyOneIntData(collection_name, query, nums);
        } else {
            Double nums = (Double) num;
            return modifyOneDoubleData(collection_name, query, nums);
        }
    }

    /**
     * 修改一条int数据
     * @param collection_name   集合名
     * @param query             查询条件
     * @param num               修改数量
     */
    public Boolean modifyOneIntData(String collection_name, Document query, Integer num) {
        Integer flag;   // 减法降序，加法升序
        if (num < 0) {
            flag = -1;
        } else {
            flag = 1;
        }
        num = Math.abs(num);
        FindIterable<Document> documents = getCollection(collection_name).find(query).sort(new Document("count", flag));
        int i = 0;
        try {
            for (Document doc: documents) {
                Integer count = doc.getInteger("count");
                if (count == 0) {
                    continue;
                } else if (i >= num) {
                    break;
                }

                Integer temp;
                // 减法
                if (flag == -1) {
                    if (count <= num-i) {
                        temp = count;
                    } else {
                        temp = num - i;
                    }
                    getCollection(collection_name).updateOne(new Document("_id", doc.get("_id")), new Document("$set", new Document("count", count-temp)));

                // 加法
                } else {
                    temp = num;
                    getCollection(collection_name).updateOne(new Document("_id", doc.get("_id")), new Document("$set", new Document("count", count+temp)));
                }
                i = i + temp;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 修改一条Double数据
     * @param collection_name   集合名
     * @param query             查询条件
     * @param num               修改数量
     */
    public Boolean modifyOneDoubleData(String collection_name, Document query, Double num) {
        Integer flag;   // 减法降序，加法升序
        if (num < 0.0) {
            flag = -1;
        } else {
            flag = 1;
        }
        num = Math.abs(num);
        FindIterable<Document> documents = getCollection(collection_name).find(query).sort(new Document("count", flag));
        double i = 0.0;
        try {
            for (Document doc: documents) {
                Double count = doc.getDouble("count");
                if (count == 0.0) {
                    continue;
                } else if (i >= num) {
                    break;
                }

                Double temp;
                // 减法
                if (flag == -1) {
                    if (count <= num-i) {
                        temp = count;
                    } else {
                        temp = num - i;
                    }
                    getCollection(collection_name).updateOne(new Document("_id", doc.get("_id")), new Document("$set", new Document("count", count-temp)));

                // 加法
                } else {
                    temp = num;
                    getCollection(collection_name).updateOne(new Document("_id", doc.get("_id")), new Document("$set", new Document("count", count+temp)));
                }
                i = i + temp;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 清除所有缓存
     * @return
     */
    public Boolean clearAllCache() {
        try {
            getCollection(collectionName).drop();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

