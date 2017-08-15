package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoException;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.User;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

/**
 * Created by SuChang on 2017/4/25 10:25.
 */
@Repository
public class MUserDao extends MBaseDao<User> {
    public static String collectionName = CommonParams.B_USER;

    public static String getCollectionName() {
        return collectionName;
    }

    /**
     * @param email_or_telphone
     * @return 重复返回 true
     */
    public User findUser(@NotNull String email_or_telphone) {
        Document query = new Document();
        ArrayList<Document> or = new ArrayList<Document>();
        or.add(new Document("email", email_or_telphone));
        or.add(new Document("telphone", email_or_telphone));
        query.append("$or", or);
        return findOneEntity(MUserDao.getCollectionName(), query, User.class);
    }


    public boolean saveIp(String user_id, String ip) {
        return update_some_key_by_id(collectionName, user_id, "last_ip", ip);
    }

    public boolean registAccount(User user) {
        Document parse = Document.parse(JSON.toJSONString(user));
        parse.put("expire_time", new BsonDateTime(user.getExpire_time().getTime()));
        parse.put("createTime", new BsonDateTime(user.getCreateTime().getTime()));
        try {
            getCollection(collectionName).insertOne(parse);
            return true;
        } catch (MongoException e) {

        }
        return false;
    }
}
