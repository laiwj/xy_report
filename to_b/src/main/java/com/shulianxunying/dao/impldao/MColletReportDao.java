package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.CollectReport;
import com.shulianxunying.entity.DownloadReport;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuChang on 2017/4/27 16:32.
 */
@Repository
public class MColletReportDao extends MBaseDao<CollectReport> {

    public static String collectionName = CommonParams.B_COLLCET_REPORT;

    public List<CollectReport> find_user_collect_report(String user_id, int page, int pageSize) {
        Document query = new Document("user_id", user_id);

        MongoCursor<Document> iterator = getCollection(collectionName).find(query)
                .sort(new Document("collect_time", -1))
                .skip((page - 1) * pageSize)
                .limit(pageSize).iterator();
        List<CollectReport> out = new ArrayList<>();
        while (iterator.hasNext()) {
            CollectReport collectReport = document_2_class(iterator.next(), CollectReport.class);
            out.add(collectReport);
        }
        return out;
    }

    public long count_user_collect_report(String user_id) {
        Document query = new Document("user_id", user_id);
        return getCollection(collectionName).count(query);
    }

    public boolean del_user_collect_report(String user_id, String report_id) {
        Document query = new Document("_id", report_id).append("user_id", user_id);
        try {
            DeleteResult deleteResult = getCollection(collectionName).deleteOne(query);
            return true;
        } catch (MongoException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean add_user_collect_report(CollectReport collectReport) {
        Document parse = Document.parse(JSON.toJSONString(collectReport));
        if (parse.containsKey("collect_time"))
            parse.put("collect_time", new BsonDateTime(collectReport.getCollect_time().getTime()));
        try {
            getCollection(collectionName).insertOne(parse);
            return true;
        } catch (MongoException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
