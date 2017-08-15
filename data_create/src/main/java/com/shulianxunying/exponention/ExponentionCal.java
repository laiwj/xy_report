package com.shulianxunying.exponention;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.shulianxunying.resume.FuncPositionMap;
import com.shulianxunying.resume.PositionFunc;
import com.shulianxunying.utils.SparkMongoHelper;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by SuChang on 2017/5/11 16:45.
 * 职位 或者 职能 的供需指数计算
 */
public class ExponentionCal {

    public static void main(String[] args) {
        HashSet<String> funcs = new HashSet<>();
        HashSet<String> positions = new HashSet<>();
        String path = "/职能岗位对应.txt";
        InputStream resourceAsStream = ExponentionCal.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                funcs.add(split[0]);
                positions.add(split[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String database = "resume_report";
        MongoClient mongoClient = new MongoClientHelper().getClient(database);
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection("resume_report_2017-01-01_2017-03-31");
        // 计算职能 供需指数
        for (String func : funcs) {
            List<Document> pipelines = new ArrayList<>();
            Document match = new Document();
            match.append("type", "city_position_count");
            match.append("func", func);
            pipelines.add(new Document("$match", match));
            pipelines.add(Document.parse("{$group:{_id:\"$func\",count:{$sum:\"$count\"}}}"));
            Document supply_result = collection.aggregate(pipelines).first();
            long supply_count = 0;
            if (supply_result != null) {
                supply_count = supply_result.getInteger("count");
            }

            match.put("type", "city_position_count_demand");
            Document demand_result = collection.aggregate(pipelines).first();
            long demand_count = 0;
            if (supply_result != null) {
                demand_count = demand_result.getInteger("count");
            }
            double v = (Math.abs((double) (supply_count - demand_count) / (double) (supply_count + demand_count)) + 1) * 100;
            Document document = new Document();
            document.append("func", func);
            document.append("count", v);
            document.append("type", "exponention_func");
            System.out.println(document);
            collection.insertOne(document);
        }
        for (String position : positions) {
            List<Document> pipelines = new ArrayList<>();
            Document match = new Document();
            match.append("type", "city_position_count");
            match.append("position", position);
            pipelines.add(new Document("$match", match));
            pipelines.add(Document.parse("{$group:{_id:\"$position\",count:{$sum:\"$count\"}}}"));
            Document supply_result = collection.aggregate(pipelines).first();
            long supply_count = 0;
            if (supply_result != null) {
                supply_count = supply_result.getInteger("count");
            }

            match.put("type", "city_position_count_demand");
            Document demand_result = collection.aggregate(pipelines).first();
            long demand_count = 0;
            if (supply_result != null) {
                demand_count = demand_result.getInteger("count");
            }
            double v = (Math.abs((double) (supply_count - demand_count) / (double) (supply_count + demand_count)) + 1) * 100;
            Document document = new Document();
            document.append("position", position);
            document.append("count", v);
            document.append("type", "exponention_position");
            System.out.println(document);
            collection.insertOne(document);
        }
    }
}
