package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.entity.ReportInfo;
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

    public List<Document> getReportList(String api_url, JSONObject params, String user_id) {
        Document query = new Document("api_url", api_url).append("params", params).append("pm_user_id", user_id);
        MongoCursor<Document> iterator = getCollection(getCollectionName()).find(query).iterator();
        List<Document> out = new ArrayList<>();
        while (iterator.hasNext()) {
            Document next = iterator.next();
            String pm_user_id = next.getString("pm_user_id");
            PMUser pmUser = pmUserDao.find_by_id(MPMUserDao.collectionName, pm_user_id, PMUser.class);
            next.append("pm_username", pmUser.getUsername());
            out.add(next);
        }
        return out;
    }


}
