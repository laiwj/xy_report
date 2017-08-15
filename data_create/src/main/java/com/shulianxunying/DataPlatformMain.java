package com.shulianxunying;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import com.shulianxunying.exponention.ExponentionCal;
import com.shulianxunying.position.PositionReport;
import com.shulianxunying.resume.CityFlow;
import com.shulianxunying.resume.ResumePositionFlow;
import com.shulianxunying.resume.ResumeReport2;
import com.shulianxunying.resume.ResumeSalaryReport;
import com.shulianxunying.utils.SparkMongoHelper;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SuChang on 2017/6/21 15:10.
 */
public class DataPlatformMain {


    /**
     * 数据报告平台 所需的报告数据的spark程序 入口。
     * 1、人才地域流动  area_flow
     * 2、人才职能流动  position_flow
     * 3、人才需求分布  position_demand
     * 4、人才薪资分析  resume_feature
     * 5、计算供需指数  position_exponention
     * 6、创建索引  create_index
     * args 包含传递给 子程序的参数 和 入口程序的路由参数
     *
     * @param args
     */
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start_time = null, end_time = null;
        // 读取参数
        HashMap<String, String> argsMap = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            argsMap.put(args[i], args[i + 1]);
            System.out.println(args[i] + " \t " + args[i + 1]);
        }
        try {
            start_time = sdf.parse(argsMap.get("--start_time"));
            end_time = sdf.parse(argsMap.get("--end_time"));
//            start_time = sdf.parse("2017-03-01");
//            end_time = sdf.parse("2017-04-01");
        } catch (Exception e) {
            System.out.println("筛选数据的时间格式 错误");
            System.exit(0);
        }
        // 需要 计算的报告 种类
        String types = argsMap.get("--types");
        List<String> typeList = new ArrayList<>();
        setType(types, "area_flow", typeList);
        setType(types, "position_flow", typeList);
        setType(types, "position_demand", typeList);
        setType(types, "resume_feature", typeList);
        setType(types, "position_exponention", typeList);
        setType(types, "create_index", typeList);
        setType(types, "drop_indexs", typeList);
        if (typeList.size() > 0) {
            System.out.println("计划进行计算的报告:");
            for (String s : typeList) {
                System.out.println(s);
            }
        } else {
            System.out.println("没有选择 需要计算的报告");
            System.out.println("--types area_flow,position_flow,position_demand,resume_feature,position_exponention,create_index,drop_indexs");
            System.exit(-1);
        }
        // 读取mongo相关配置
        JSONObject mongoConfig = new JSONObject();
        mongoConfig.put("database", argsMap.getOrDefault("--database", "resume_report"));
        mongoConfig.put("in_database", argsMap.getOrDefault("--in_database", "resume_report"));
        mongoConfig.put("in_collection", argsMap.getOrDefault("--in_collection", "resume_report"));
        mongoConfig.put("out_collection", argsMap.getOrDefault("--out_collection", "resume_report"));
        mongoConfig.put("ip", argsMap.getOrDefault("--ip", "10.101.1.171"));
        mongoConfig.put("port", argsMap.getOrDefault("--port", "27017"));
        mongoConfig.put("username", argsMap.getOrDefault("--username", "root"));
        mongoConfig.put("password", argsMap.getOrDefault("--password", "sc123456"));
        mongoConfig.put("authSource", argsMap.getOrDefault("--authSource", "admin"));
        // 检查 数据文件配置
        if (!argsMap.containsKey("--resume_paths") && !argsMap.containsKey("--jd_paths")) {
            System.out.println("没有选择 数据文件");
            System.out.println("--resume_paths hdfs://10.101.1.230:9000/resume/resume20170531.json");
            System.out.println("--jd_paths file:///home/xybigdata/resume241/resume20170531.json");
            System.out.println("多个路径 用逗号(,)分割,将会合并为一个rdd进行计算");
            System.exit(-1);
        }
        if (types.contains("area_flow")) {
            argsMap.put("--paths", argsMap.get("--resume_paths"));
            System.out.println("开始计算 地域流动");
            CityFlow.excute(argsMap, start_time, end_time, mongoConfig);
            System.out.println("完成计算 地域流动");
            argsMap.remove("--paths");
        }
        if (types.contains("position_flow")) {
            argsMap.put("--paths", argsMap.get("--resume_paths"));
            System.out.println("开始计算 职能流动");
            ResumePositionFlow.execute(argsMap, start_time, end_time, mongoConfig);
            System.out.println("完成计算 职能流动");
            argsMap.remove("--paths");
        }
        if (types.contains("position_demand")) {
            argsMap.put("--paths", argsMap.get("--jd_paths"));
            System.out.println("开始计算 职能需求");
            PositionReport.execute(argsMap, start_time, end_time, mongoConfig);
            System.out.println("完成计算 职能需求");
            argsMap.remove("--jd_paths");
        }
        if (types.contains("position_exponention")) {
            System.out.println("开始计算 职能岗位 供需指数");
            ExponentionCal.execute(start_time, end_time, mongoConfig);
            System.out.println("开始计算 职能岗位 供需指数");
        }
        if (types.contains("resume_feature")) {
            argsMap.put("--paths", argsMap.get("--resume_paths"));
            System.out.println("开始计算 薪酬报告");
            ResumeSalaryReport.execute(argsMap, start_time, end_time, mongoConfig);
            System.out.println("完成计算 薪酬报告");
            argsMap.remove("--paths");
        }
        if(types.contains("create_index")){
            System.out.println("开始连接数据库");
            MongoClient mongoClient = createMongoClient(mongoConfig);
            MongoDatabase database = mongoClient.getDatabase(mongoConfig.getString("database"));
            MongoCollection<Document> out_collection = database.getCollection(mongoConfig.getString("out_collection") + "_" + argsMap.get("--start_time") + "_" + argsMap.get("--end_time"));
            MongoCursor<String> type = out_collection.distinct("type", String.class).iterator();
            List<String> myList= IteratorUtils.toList(type);
            if(myList.size() == 13){
                System.out.println("开始创建索引");
                out_collection.createIndex(new Document("type",1));
                out_collection.createIndex(new Document("func",1));
                out_collection.createIndex(new Document("position",1));
                out_collection.createIndex(new Document("industry",1));
                out_collection.createIndex(new Document("city",1));
                out_collection.createIndex(new Document("living",1));
                out_collection.createIndex(new Document("preCity",1));
                out_collection.createIndex(new Document("pre_func",1));
                out_collection.createIndex(new Document("pre_position",1));
                out_collection.createIndex(new Document("work_year",1));
                out_collection.createIndex(new Document("p",1));
                System.out.println("完成创建索引");
            }else {
                System.out.println("尚未计算完成");
            }
        }
        if(types.contains("drop_indexs")){
            System.out.println("开始连接数据库");
            MongoClient mongoClient = createMongoClient(mongoConfig);
            MongoDatabase database = mongoClient.getDatabase(mongoConfig.getString("database"));
            MongoCollection<Document> out_collection = database.getCollection(mongoConfig.getString("out_collection") + "_" + argsMap.get("--start_time") + "_" + argsMap.get("--end_time"));
            out_collection.dropIndexes();
        }
    }

    public static void setType(String typeString, String key, List<String> typeList) {
        if (StringUtils.isNotEmpty(typeString) && typeString.contains(key))
            typeList.add(key);
    }

    public static MongoClient createMongoClient(JSONObject mongoConfig){
        //配置 MongoDB
        String database = mongoConfig.getString("database");
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
        return  new MongoClient(new MongoClientURI(inputUri));
    }
}
