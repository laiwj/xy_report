package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoException;
import com.shulianxunying.dao.MBaseParentDao;
import com.shulianxunying.entity.LogInfo;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Repository;

/**
 * Created by SuChang on 2017/4/27 9:11.
 */
@Repository
public class MLogDao extends MBaseParentDao<LogInfo> {

    @Override
    public String setDatabase() {
        return "resume_report";
    }

    public static String collectionName = "data_system_log";

    public void addLog(LogInfo logInfo) {
        insert_new_document(collectionName, logInfo);
    }

    @Override
    public boolean insert_new_document(String collectionName, LogInfo logInfo) {
        Document parse = Document.parse(JSON.toJSONString(logInfo));
        parse.put("inputTime", new BsonDateTime(logInfo.getInputTime().getTime()));
        parse.put("returnTime", new BsonDateTime(logInfo.getInputTime().getTime()));
        try {
            getCollection(collectionName).insertOne(parse);
            return true;
        } catch (MongoException e) {

        }
        return false;
    }
}
