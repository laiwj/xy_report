package com.shulianxunying.dao.impldao;

import com.shulianxunying.dao.MBaseDao;
import org.bson.Document;
import org.springframework.stereotype.Repository;

/**
 * Created by yangxue on 2017/6/26.
 * Cell:15884457479
 * Email:yangxue.liu@hirebigdata.cn
 * Description:
 * <p/>
 * Functions:
 * 1.
 */
@Repository
public class MSearchDao extends MBaseDao{

    public static String collectionName = "search_cache";

    public static String getCollectionName() {
        return collectionName;
    }

    public boolean insert_one(Document data){
//        Document document = new Document(data);
        try {
            getCollection(getCollectionName()).updateOne(new Document("_id", data.get("_id")), new Document("$set", data));
            return true;
        }catch (Exception e){

        }
        return false;
    }





}
