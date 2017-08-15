package com.shulianxunying.position;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.exponention.MongoClientHelper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SuChang on 2017/6/2 18:01.
 */
public class PositionPathAGG {
    MongoClient mongoClient;
    MongoCollection<Document> collection;

    public PositionPathAGG() {
        String database = "resume_report";
        String collection_name = "position_path";
        mongoClient = new MongoClientHelper().getClient(database);
        collection = mongoClient.getDatabase(database).getCollection(collection_name);
    }

    /**
     * 职业路径
     *
     * @param position 职位名称
     * @param depth    查询深度
     * @return
     */
    public JSONObject getPositionPath(String position, int depth) {
        List<JSONObject> children = getSubPath(position, 1, depth);
        JSONObject out = new JSONObject();
        out.put("name", position);
        out.put("children", children);
        return out;
    }

    public List<JSONObject> getSubPath(String position, int stage, int depth) {
        List<JSONObject> out = new ArrayList<>();
        if (stage > depth)
            return out;
        Document query = new Document();
        query.append("stage", stage);
        query.append("pre_position", position);
        query.append("post_position", new Document("$nin", Arrays.asList(position, "unknow")));
        query.append("count", new Document("$gt", 10));
        MongoCursor<Document> sub_paths = collection.find(query).limit(3).sort(new Document("count", -1)).iterator();
        while (sub_paths.hasNext()) {
            Document document = sub_paths.next();
            String p = document.getString("post_position");
            int count = document.getInteger("count");
            JSONObject sub_path = new JSONObject();
            sub_path.put("name", p);
            sub_path.put("value", count);
            sub_path.put("children", getSubPath(p, stage + 1, depth));
            out.add(sub_path);
        }
        return out;
    }

    public JSONObject getStageFlowOut(String position, int depth) {
        return getStageFlow(position, depth, "pre_position", "post_position");
    }

    public JSONObject getStageFlowIn(String position, int depth) {
        return getStageFlow(position, depth, "post_position", "pre_position");
    }

    public JSONObject getStageFlow(String position, int depth, String key1, String key2) {
        JSONObject out = new JSONObject();
        out.put("position", position);
        Document query = new Document();
        query.append(key1, position);
        query.append(key2, new Document("$nin", Arrays.asList(position, "unknow")));
        query.append("count", new Document("$gt", 10));
        for (int i = 1; i <= depth; i++) {
            query.put("stage", i);
            MongoCursor<Document> sub_paths = collection.find(query).limit(3).sort(new Document("count", -1)).iterator();
            ArrayList<JSONObject> data = new ArrayList<>();
            while (sub_paths.hasNext()) {
                Document document = sub_paths.next();
                String p = document.getString(key2);
                int count = document.getInteger("count");
                JSONObject sub_path = new JSONObject();
                sub_path.put("name", p);
                sub_path.put("value", count);
                data.add(sub_path);
            }
            out.put("" + i, data);
        }
        return out;
    }

    public static void main(String[] args) {
        PositionPathAGG positionPathAGG = new PositionPathAGG();
        String position = "开发-管理类";
//        JSONObject out = positionPathAGG.getPositionPath(position, 4);
//        System.out.println(out);
        JSONObject out = positionPathAGG.getStageFlowOut(position, 4);
        System.out.println(out);
        JSONObject out1 = positionPathAGG.getStageFlowIn(position, 4);
        System.out.println(out1);
    }
}
