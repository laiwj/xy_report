package com.shulianxunying.dao.impldao;

import com.mongodb.client.MongoCursor;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.NoticeInfo;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by SuChang on 2017/4/27 15:35.
 */
@Repository
public class MNoticeDao extends MBaseDao<NoticeInfo> {
    public static String collectionName = CommonParams.USER_NOTICE;

    public static String getCollectionName() {
        return collectionName;
    }

    public List<NoticeInfo> find_user_notices(String user_id, Boolean status, int page, int pageSize) {
        Document query = new Document("user_id", user_id);
        if (status != null)
            query.append("isClick", status);
        MongoCursor<Document> iterator = getCollection(collectionName).find(query)
                .sort(new Document("time", 1))
                .skip((page - 1) * pageSize)
                .limit(pageSize).iterator();
        List<NoticeInfo> out = new ArrayList<>();
        while (iterator.hasNext()) {
            NoticeInfo noticeInfo = document_2_class(iterator.next(), NoticeInfo.class);
            noticeInfo.setUser_id("");
            out.add(noticeInfo);
        }
        return out;
    }

    public long count_user_notices(String user_id, Boolean status) {
        Document query = new Document("user_id", user_id);
        if (status != null)
            query.append("isClick", status);
        return getCollection(collectionName).count(query);
    }
}
