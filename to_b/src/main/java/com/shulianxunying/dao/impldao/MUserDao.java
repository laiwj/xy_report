package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.User;
import com.shulianxunying.util.CommonUtil;
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

    public int update_password_by_userId(String userId,String oldPwd,String newPwd){
        Document find = new Document("_id",userId);
        Document document = find_by_id(getCollectionName(),userId);
        if(CommonUtil.isEmpty(document)){
            return -1;
        }else{
            if(!document.get("password").equals(oldPwd)){
                return 2;
            }
        }
        Document update = new Document();
        update.append("password",newPwd);
        try {
            UpdateResult updateResult = getDatabase().getCollection(getCollectionName()).updateOne(new Document("_id",userId),new Document("$set",update));
        }catch (MongoException e){
            return -1;
        }
        return 0;
    }

    public User find_user_city(String userId){
        Document find = new Document("_id",userId);
        return findOneEntity(getCollectionName(),find,User.class);
//        Document document = find_by_id(getCollectionName(),userId);

    }

}
