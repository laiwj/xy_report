package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.ReportConfig;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuChang on 2017/4/27 9:11.
 */
@Repository
public class MReportConfigDao extends MBaseDao<ReportConfig> {

    public static String collectionName = CommonParams.REPORT_CONFIG;

    public boolean upsert_one(@NotNull ReportConfig reportConfig) {
        try {
            getCollection(collectionName).updateOne(
                    new Document("report_type", reportConfig.getReport_type()).append("config_type", reportConfig.getConfig_type()),
                    new Document("$set", Document.parse(JSON.toJSONString(reportConfig))), upsertOptions);
            return true;
        } catch (MongoException e) {

        }
        return false;
    }

    public ReportConfig find_one_config(Integer report_type, String config_type) {
        return findOne_by_key_values(collectionName, ReportConfig.class, "report_type", report_type, "config_type", config_type);
    }

    public List<ReportConfig> find_all_config(Integer report_type) {
        MongoCursor<Document> cursor = getCollection(collectionName).find(new Document("report_type", report_type)).iterator();
        ArrayList<ReportConfig> out = new ArrayList<>();
        while (cursor.hasNext()) {
            try {
                ReportConfig reportConfig = document_2_class(cursor.next(), ReportConfig.class);
                out.add(reportConfig);
            } catch (JSONException e) {
                continue;
            }
        }
        return out;
    }
}
