package com.shulianxunying.exponention;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by SuChang on 2017/5/12 14:17.
 * 已弃用，不用计算流动指数
 */
public class TalentFlowCal {

    public static void main(String[] args) {
        String database = "resume_report";
        MongoClient mongoClient = new MongoClientHelper().getClient(database);
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection("resume_report_2017-01-01_2017-03-31");
        List<Document> pipelines = new ArrayList<>();
        pipelines.add(new Document("$match", new Document("type", "flow")));
        Document value = new Document("_id", "$living");
        pipelines.add(new Document("$group", value));
        HashSet<String> citys = new HashSet<>();
        for (Document doc : collection.aggregate(pipelines)) {
            citys.add((String) doc.get("_id"));
        }
        value.put("_id", "$preCity");
        for (Document doc : collection.aggregate(pipelines)) {
            citys.add((String) doc.get("_id"));
        }
        citys.remove("unknow");


        // 计算 流入 流出 总和
        for (String city : citys) {
            int in_count = 0, out_count = 0;
            // 流入
            List<Document> pipes = new ArrayList<>();
            pipes.add(Document.parse("{$match:{type:\"flow\",living:\"" + city + "\"}}"));
            pipes.add(Document.parse("{$group:{_id:\"$living\",count:{$sum:\"$count\"}}}"));
            Document first = collection.aggregate(pipelines).first();
            if (first != null)
                in_count = first.getInteger("count");
            pipes.clear();
            pipes.add(Document.parse("{$match:{type:\"flow\",preCity:\"" + city + "\"}}"));
            pipes.add(Document.parse("{$group:{_id:\"$preCity\",count:{$sum:\"$count\"}}}"));
            Document first1 = collection.aggregate(pipelines).first();
            if (first1 != null)
                out_count = first1.getInteger("count");
            int total = in_count + out_count;
            System.out.println(city + "\t" + total);
        }

        System.out.println();
    }
}
