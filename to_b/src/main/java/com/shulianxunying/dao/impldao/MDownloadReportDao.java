package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
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
public class MDownloadReportDao extends MBaseDao<DownloadReport> {

    public static String collectionName = CommonParams.B_DOWNLOAD_REPORT;

    public List<DownloadReport> find_user_download_report(String user_id, int page, int pageSize) {
        Document query = new Document("user_id", user_id);
        MongoCursor<Document> iterator = getCollection(collectionName).find(query)
                .sort(new Document("download_time", -1))
                .skip((page - 1) * pageSize)
                .limit(pageSize).iterator();
        List<DownloadReport> out = new ArrayList<>();
        while (iterator.hasNext()) {
            DownloadReport downloadReport = document_2_class(iterator.next(), DownloadReport.class);
            out.add(downloadReport);
        }
        return out;
    }

    public long count_user_download_report(String user_id) {
        Document query = new Document("user_id", user_id);
        return getCollection(collectionName).count(query);
    }

    public boolean add_user_download_report(DownloadReport downloadReport) {
        Document parse = Document.parse(JSON.toJSONString(downloadReport));
        if (parse.containsKey("download_time"))
            parse.put("download_time", new BsonDateTime(downloadReport.getDownload_time().getTime()));
        try {
            getCollection(collectionName).insertOne(parse);
            return true;
        } catch (MongoException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
