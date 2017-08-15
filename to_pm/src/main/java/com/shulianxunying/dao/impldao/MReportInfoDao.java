package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.entity.ReportInfo;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuChang on 2017/5/2 16:33.
 */
@Repository
public class MReportInfoDao extends MBaseDao<ReportInfo> {
    public static String collectionName = CommonParams.REPORT_INFO;
    @Resource
    MPMUserDao pmUserDao;

    public static String getCollectionName() {
        return collectionName;
    }

    public List<Document> getReportList(String api_url, JSONObject params, List<String> user_ids) {
        Document query = new Document("api_url", api_url).append("params", params);
        if (user_ids.size() != 0) {
            query.append("pm_user_id", new Document("$in", user_ids));
        }
        MongoCursor<Document> iterator = getCollection(getCollectionName()).find(query).iterator();
        List<Document> out = new ArrayList<>();
        while (iterator.hasNext()) {
            Document next = iterator.next();
            String pm_user_id = next.getString("pm_user_id");
            PMUser pmUser = pmUserDao.find_by_id(MPMUserDao.collectionName, pm_user_id, PMUser.class);
            next.append("pm_username", pmUser.getUsername());
            out.add(next);
//            out.add(document_2_class(iterator.next(), ReportInfo.class));
        }
        return out;
    }

    public boolean upsertReportInfo(ReportInfo reportInfo) {
        Document parse = Document.parse(JSON.toJSONString(reportInfo));
        if (parse.containsKey("modify_time"))
            parse.put("modify_time", new BsonDateTime(reportInfo.getModify_time().getTime()));
            try {
                getCollection(getCollectionName()).updateOne(
                        new Document("pm_user_id", reportInfo.getPm_user_id()).append("api_url", reportInfo.getApi_url()).append("_id",reportInfo.get_id()),
                        new Document("$set", parse),
                        upsertOptions);
                return true;
            } catch (MongoException e) {
                e.printStackTrace();
            }

        return false;
    }

}
