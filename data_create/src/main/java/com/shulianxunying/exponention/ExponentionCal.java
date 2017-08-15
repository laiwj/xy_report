package com.shulianxunying.exponention;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.shulianxunying.resume.FuncPositionMap;
import com.shulianxunying.resume.PositionFunc;
import com.shulianxunying.utils.SparkMongoHelper;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SuChang on 2017/5/11 16:45.
 * 职位 或者 职能 的供需指数计算
 */
public class ExponentionCal {


    public static void execute(Date start_time, Date end_time, JSONObject mongoConfig) {
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //配置 MongoDB
        String database = mongoConfig.getString("database");
//        String in_database = mongoConfig.getString("in_database");
//        String in_collectionName = mongoConfig.getString("in_collection");
        String out_collectionName = mongoConfig.getString("out_collection");
        String authSource = mongoConfig.getString("authSource");
        String ip = mongoConfig.getString("ip");
        String port = mongoConfig.getString("port");
        String inputUri = "";
        if (StringUtils.isNotEmpty(mongoConfig.getString("username"))) {
            HashMap<String, String> optins = new HashMap<String, String>();
            optins.put("authSource", authSource);
            inputUri = SparkMongoHelper.createMongoUrl(ip, Integer.parseInt(port), mongoConfig.getString("username"), mongoConfig.getString("password"), database, out_collectionName, optins);
        } else {
            inputUri = SparkMongoHelper.createMongoUrl(ip, Integer.parseInt(port), null, null, database, out_collectionName);
        }

        String collectionName = out_collectionName + "_" + sdf.format(start_time) + "_" + sdf.format(end_time);
        MongoClient mongoClient = new MongoClient(new MongoClientURI(inputUri));
        MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(collectionName);
        // 计算职能 供需指数
        for (String func : funcs) {
            List<Document> pipelines = new ArrayList<>();
            Document match = new Document();
            match.append("type", "flow");
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
            match.append("type", "flow");
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
            if (demand_result != null) {
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

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String, String> argsMap = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            argsMap.put(args[i], args[i + 1]);
            System.out.println(args[i] + " \t " + args[i + 1]);
        }
        Date start_time = sdf.parse("2017-01-01");
        Date end_time = sdf.parse("2017-04-01");
        JSONObject mongoConfig = new JSONObject();
        mongoConfig.put("database", argsMap.getOrDefault("--database", "resume_report"));
        mongoConfig.put("out_collection", argsMap.getOrDefault("--out_collection", "resume_report"));
        execute(start_time, end_time, mongoConfig);
    }
}
