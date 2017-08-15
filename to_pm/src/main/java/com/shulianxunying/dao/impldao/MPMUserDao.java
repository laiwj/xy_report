package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.PMUser;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuChang on 2017/4/25 10:25.
 */
@Repository
public class MPMUserDao extends MBaseDao<PMUser> {
    public static String collectionName = CommonParams.PM_USER;
    public static String b_user_collection = CommonParams.B_USER;

    public static String getCollectionName() {
        return collectionName;
    }

    public List<PMUser> user_list(String user_id, int page, int pageSize) {
        Document query = new Document();
        if (StringUtils.isNotEmpty(user_id))
            query.append("parent_id", user_id);
        FindIterable<Document> createTime = getCollection(collectionName)
                .find(query)
                .sort(new Document("createTime", 1));
        if (page > 0) {
            createTime = createTime.skip((page - 1) * pageSize).limit(pageSize);
        }
        MongoCursor<Document> users = createTime.iterator();
        List<PMUser> out = new ArrayList<>();
        while (users.hasNext()) {
            PMUser user = document_2_class(users.next(), PMUser.class);
            user.setPassword("");
            user.setParent_id("");
//            user.setPower_list(null);
            out.add(user);
        }
        return out;
    }

    public long userCount(String user_id) {
        Document query = new Document();
        query.append("parent_id", user_id);
        try {
            return getCollection(collectionName).count(query);
        } catch (MongoException e) {

        }
        return 0;
    }


    public boolean password(PMUser user, String old_password, String password) {
        if (user.getPassword().equals(old_password)) {
            if (!old_password.equals(password)) {
                try {
                    UpdateResult updateResult = getCollection(collectionName).updateOne(new Document("_id", user.get_id()), new Document("$set", new Document("password", password)));
                    if (updateResult.getModifiedCount() == 1) {
                        return true;
                    }
                } catch (MongoException e) {
                    return false;
                }
            } else
                return true;
        }
        return false;
    }

    public boolean addPower(String user_id, List<Integer> power) {
        Document update = new Document();
        update.append("$addToSet", new Document("power_list", new Document("$each", power)));
        try {
            getCollection(collectionName).updateOne(new Document("_id", user_id), update);
            return true;
        } catch (MongoException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public boolean removePower(String user_id, List<Integer> power) {
        Document update = new Document();
        update.append("$pullAll", new Document("power_list", power));
        try {
            getCollection(collectionName).updateOne(new Document("_id", user_id), update);
            return true;
        } catch (MongoException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * @param email_or_telphone
     * @return 重复返回 true
     */
    public PMUser findUser(@NotNull String email_or_telphone) {
        Document query = new Document();
        ArrayList<Document> or = new ArrayList<Document>();
        or.add(new Document("email", email_or_telphone));
        or.add(new Document("telphone", email_or_telphone));
        query.append("$or", or);
        return findOneEntity(MPMUserDao.getCollectionName(), query, PMUser.class);
    }


    public boolean saveIp(String user_id, String ip) {
        return update_some_key_by_id(collectionName, user_id, "last_ip", ip);
    }

    public boolean registAccount(PMUser user) {
        Document parse = Document.parse(JSON.toJSONString(user));
//        parse.put("expire_time", new BsonDateTime(user.getExpire_time().getTime()));
        parse.put("createTime", new BsonDateTime(user.getCreateTime().getTime()));
        try {
            getCollection(collectionName).insertOne(parse);
            return true;
        } catch (MongoException e) {

        }
        return false;
    }
}
