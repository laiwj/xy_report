package com.shulianxunying.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.gridfs.GridFS;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Created by jiangwei on 2016/7/26 0026.
 */
@Repository
public class MBaseDao<TEntity> {
    public static final Logger logger = Logger.getLogger(MBaseDao.class.getName());

    @Resource(name = "base_info_mongo")
    private MongoClient mongoClient;
    public static String base_database = "resume_report";

    // upsert true
    public static UpdateOptions upsertOptions = new UpdateOptions().upsert(true);

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

    public MongoDatabase getDatabase() {
        return getDatabase(base_database);
    }

    public MongoDatabase getDatabase(@NotNull String databaseName) {
        return getMongoClient().getDatabase(databaseName);
    }

    /**
     * 获取老版本 DB
     *
     * @return
     */
    public DB getDB() {
        return getMongoClient().getDB(base_database);
    }

    /**
     * 获取GridFS 句柄
     *
     * @param collectionName
     * @return
     */
    public GridFS getGridFS(@NotNull String collectionName) {
        if (collectionName == null || collectionName.isEmpty()) {
            return new GridFS(getDB());
        } else {
            return new GridFS(getDB(), collectionName);
        }
    }

    public MongoCollection<Document> getCollection(@NotNull String collectionName) {
        return getDatabase().getCollection(collectionName);
    }

    public boolean insert_new_document(@NotNull String collectionName, @NotNull TEntity tEntity) {
        try {
            getDatabase().getCollection(collectionName).insertOne(Document.parse(JSON.toJSONString(tEntity)));
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }


    /**
     * 根据id查只能 得到最多一个
     *
     * @param collectionName
     * @param id
     * @return
     */
    public Document find_by_id(@NotNull String collectionName, @NotNull String id) {
        return find_by_id_base(collectionName, id);
    }

    public TEntity find_by_id(@NotNull String collectionName, @NotNull String id, Class<TEntity> cls) {
        return find_by_id_base_2_class(collectionName, id, cls);
    }

    public Document find_by_id(String collectionName, ObjectId id) {
        return find_by_id_base(collectionName, id);
    }

    public TEntity find_by_id(String collectionName, ObjectId id, Class<TEntity> cls) {
        return find_by_id_base_2_class(collectionName, id, cls);
    }

    private Document find_by_id_base(String collectionName, Object id) {
        return getDatabase().getCollection(collectionName).find(new Document("_id", id)).first();
    }

    private TEntity find_by_id_base_2_class(String collectionName, Object id, Class<TEntity> cls) {
        return document_2_class(find_by_id_base(collectionName, id), cls);
    }

    /**
     * @param collectionName
     * @param key_values
     * @return 返回一个游标
     */
    public Document findOne_by_key_values(String collectionName, Object... key_values) {
        Document query = new Document();
        int length = key_values.length;
        if (length <= 0 || length % 2 != 0)
            throw new MongoException("not pair key and value");
        for (int i = 0; i < length; i += 2) {
            query.append(key_values[i].toString(), key_values[i + 1]);
        }
        try {
            return getDatabase().getCollection(collectionName).find(query).first();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Document findParams_by_key_values(String collectionName, JSONObject key_values) {
        Document query = new Document();
        int length = key_values.size();
        if (length <= 0 || length % 2 != 0)
            throw new MongoException("not pair key and value");
        for(String key : key_values.keySet() ){
            query.append(key,key_values.get(key));
        }
//            key = (String)iterator.next();
//            if(!"".equals(key) && !"".equals(params.getString(key))){
//                param += key+","+params.getString(key)+",";
//            }
//        }
//        for (int i = 0; i < length; i += 2) {
//            query.append(key_values.get(i).toString(), key_values.get(i + 1));
//        }
        try {
            return getDatabase().getCollection(collectionName).find(query).first();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public TEntity findOneEntity(String collectionNam, Document query, Class<TEntity> cls) {
        Document first = getCollection(collectionNam).find(query).first();
        return document_2_class(first, cls);
    }

    public TEntity findOne_by_key_values(String collectionName, Class<TEntity> cls, Object... key_values) {
        Document document = findOne_by_key_values(collectionName, key_values);
        return document_2_class(document, cls);
    }

    public TEntity findParams_by_key_values(String collectionName, Class<TEntity> cls, JSONObject key_values) {
        Document document = findParams_by_key_values(collectionName, key_values);
        return document_2_class(document, cls);
    }

    /**
     * @param collectionName
     * @param key_values
     * @return 返回一个游标
     */
    public long count_by_key_values(String collectionName, JSONObject key_values) {
        Document query = new Document();
        for (Map.Entry<String, Object> entry : key_values.entrySet()) {
            query.append(entry.getKey(), entry.getValue());
        }
        try {
            return getDatabase().getCollection(collectionName).count(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据id ，以全部覆盖的方式 更新数据
     *
     * @param collectionName
     * @param id
     * @param entity
     * @return
     */
    public boolean update_all_key_by_id(String collectionName, String id, TEntity entity) {
        Document parse = Document.parse(JSON.toJSONString(entity));
        parse.remove("_id");
        try {
            UpdateResult updateResult = getDatabase().getCollection(collectionName).updateOne(new Document("_id", id), new Document("$set", parse));
            if (updateResult.getMatchedCount() == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("更新数据失败(update error)" + e.getMessage());
            return false;
        }
    }

    /**
     * 根据id ，以全部覆盖的方式 更新数据
     *
     * @param collectionName
     * @param id
     * @param key_values     部分字段的 key_values
     * @return
     */
    public boolean update_some_key_by_id(String collectionName, String id, Map<String, Object> key_values) {
        Document parse = Document.parse(JSON.toJSONString(key_values));
        parse.remove("_id");
        try {
            UpdateResult updateResult = getDatabase().getCollection(collectionName).updateOne(new Document("_id", id), new Document("$set", parse));
            if (updateResult.getMatchedCount() == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("更新数据失败(update error)" + e.getMessage());
            return false;
        }
    }

    /**
     * 根据id ，以全部覆盖的方式 更新数据
     *
     * @param collectionName
     * @param id
     * @param key_values     部分字段的 key_values
     * @return
     */
    public boolean update_some_key_by_id(String collectionName, String id, JSONObject key_values) {
        Document parse = Document.parse(key_values.toJSONString());
        parse.remove("_id");
        try {
            UpdateResult updateResult = getDatabase().getCollection(collectionName).updateOne(new Document("_id", id), new Document("$set", parse));
            return true;
        } catch (Exception e) {
            logger.error("更新数据失败(update error)" + e.getMessage());
            return false;
        }
    }


    public boolean update_some_key_by_id(String collectionName, String id, Object... key_values) {
        Document update = new Document();
        int length = key_values.length;
        if (length <= 0 || length % 2 != 0)
            throw new MongoException("not pair key and value");
        for (int i = 0; i < length; i += 2) {
            update.append(key_values[i].toString(), key_values[i + 1]);
        }
        try {
            UpdateResult updateResult = getDatabase().getCollection(collectionName).updateOne(new Document("_id", id), new Document("$set", update));
            return true;
        } catch (Exception e) {
            logger.error("更新数据失败(update error)" + e.getMessage());
            return false;
        }
    }

    /**
     * 根据id, 如果查到则覆盖 没有则插入
     *
     * @param collectionName
     * @param id
     * @param tEntity
     * @return
     */
    public boolean upsert_all_by_id(String collectionName, String id, TEntity tEntity) {
        Document up = Document.parse(JSON.toJSONString(tEntity));
        try {
            UpdateOptions upo = new UpdateOptions();
            upo.upsert(true);
            UpdateResult updateResult = getDatabase().getCollection(collectionName).updateOne(new Document("_id", id), new Document("$set", up), upo);
            if (updateResult.getMatchedCount() == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("更新数据失败(update error)" + e.getMessage());
            return false;
        }
    }


    public TEntity document_2_class(Document document, Class<TEntity> cls) {
        if (document != null) {
            return JSON.parseObject(JSON.toJSONString(document), cls);
        } else {
            return null;
        }
    }
}
