package com.shulianxunying.dao.impldao;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuChang on 2017/4/25 10:25.
 */
@Repository
public class MUserDao extends MBaseDao<User> {
    public static String collectionName = CommonParams.B_USER;
    @Resource
    MPMUserDao pmUserDao;

    public static String getCollectionName() {
        return collectionName;
    }

    public List<User> user_list(String user_id, int page, int pageSize) {
        Document query = new Document();
        if (StringUtils.isNotEmpty(user_id))
            query.append("pm_user_id", user_id);

        FindIterable<Document> createTime = getCollection(collectionName)
                .find(query)
                .sort(new Document("createTime", 1));
        if (page > 0) {
            createTime = createTime.skip((page - 1) * pageSize).limit(pageSize);
        }
        MongoCursor<Document> users = createTime.iterator();
        List<User> out = new ArrayList<>();
        while (users.hasNext()) {
            User user = document_2_class(users.next(), User.class);
            PMUser pmUser = pmUserDao.find_by_id(MPMUserDao.collectionName, user.getPm_user_id(), PMUser.class);
            user.setPm_user_name(pmUser.getUsername());
            user.setPassword("");
            user.setParent_id("");
//            user.setPower_list(null);
            out.add(user);
        }
        return out;
    }

    public long getCount(String user_id) {
        Document query = new Document();
        if (StringUtils.isNotEmpty(user_id))
            query.append("pm_user_id", user_id);
        try {
            return getCollection(collectionName).count(query);
        } catch (MongoException e) {
        }
        return 0;
    }


    public boolean password(User user, String old_password, String password) {
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
}
