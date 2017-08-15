package com.shulianxunying.chengdu;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.shulianxunying.utils.KeyValueUtils;
import com.shulianxunying.utils.RDDUtils;
import com.shulianxunying.utils.SparkMongoHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.apache.spark.broadcast.Broadcast;
import org.bson.Document;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SuChang on 2017/6/13 10:18.
 */
public class PositionDistribution {

    public static HashMap<String, String> getIndustry(String path) {
        InputStream resourceAsStream = PositionDistribution.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            HashMap<String, String> out = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                String value = split[0].trim();
                for (int i = 1; i < split.length; i++) {
                    for (String key : split[i].split(" ")) {
                        key = key.trim();
                        if (StringUtils.isNotEmpty(key))
                            out.put(key, value);
                    }
                }
            }
            return out;
        } catch (IOException e) {

        }
        return null;
    }


    public static void excute(HashMap<String, String> argsMap, Date start_time, Date end_time, JSONObject mongoConfig) {
        // 配置spark
        String database = mongoConfig.getString("database");
        String in_database = mongoConfig.getString("in_database");
        String in_collectionName = mongoConfig.getString("in_collection");
        String out_collectionName = mongoConfig.getString("out_collection");
        String authSource = mongoConfig.getString("authSource");
        String ip = mongoConfig.getString("ip");
        String port = mongoConfig.getString("port");
        String inputUri = "";
        if (StringUtils.isNotEmpty(mongoConfig.getString("username"))) {
            HashMap<String, String> optins = new HashMap<String, String>();
            optins.put("authSource", authSource);
            inputUri = SparkMongoHelper.createMongoUrl(ip, Integer.parseInt(port), mongoConfig.getString("username"), mongoConfig.getString("password"), in_database, in_collectionName, optins);
        } else {
            inputUri = SparkMongoHelper.createMongoUrl(ip, Integer.parseInt(port), null, null, in_database, in_collectionName);
        }

        // 根据 日期参数 设置appName
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        SparkConf conf = new SparkConf()
                .setAppName("PositionDistribution" + sdf.format(start_time) + "_" + sdf.format(end_time))
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);

        HashSet<String> areaSet = new HashSet<>();
        areaSet.add("锦江");
        areaSet.add("青羊");
        areaSet.add("金牛");
        areaSet.add("武侯");
        areaSet.add("成华");
        areaSet.add("龙泉驿");
        areaSet.add("青白江");
        areaSet.add("新都");
        areaSet.add("温江");
        areaSet.add("双流");
        areaSet.add("金堂");
        areaSet.add("郫县");
        areaSet.add("大邑");
        areaSet.add("蒲江");
        areaSet.add("新津");
        areaSet.add("都江堰");
        areaSet.add("彭州");
        areaSet.add("邛崃");
        areaSet.add("崇州");
        areaSet.add("简阳");
        Broadcast<HashSet<String>> areaSetBroadcast = jsc.broadcast(areaSet);
        HashMap<String, String> industry = getIndustry("/寻英-前程-智联-行业映射");
        Broadcast<HashMap<String, String>> industryBroadcast = jsc.broadcast(industry);

        // 根据参数 读取mongo数据
//        Map<String, String> readOverWrite = new HashMap<>();
//        readOverWrite.put("collection", in_collectionName);
//        ReadConfig readConfig = ReadConfig.create(conf, readOverWrite);
//        Document query = new Document();
//        // {"$match":{"crawled_time":{"$gte":"2017-01-01 00:00:00","$lt":"2017-03-31 23:59:59"}}}
//        query.append("$match", new Document("crawl_time", new Document("$gte", sdf2.format(start_time)).append("$lt", sdf2.format(end_time))));
//        JavaMongoRDD<Document> positionMongoRDD = MongoSpark.load(jsc, readConfig)
//                .withPipeline(Arrays.asList(query));

//        JavaRDD<Document> positionMongoRDD = RDDUtils.readTextToMongoRDD("C:\\Users\\Su\\Desktop\\temp1.json", jsc);
        JavaRDD<Document> positionMongoRDD = RDDUtils.readTextToMongoRDD(argsMap.get("--path"), jsc);

//         根据时间过滤部分数据
        positionMongoRDD = positionMongoRDD.filter(new Function<Document, Boolean>() {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            public Boolean call(Document v1) throws Exception {

                String publish_time = v1.getString("publish_time");
                String crawl_time = v1.getString("crawl_time");
                if (StringUtils.isNotEmpty(publish_time)) {
                    try {
                        Date parse = sdf1.parse(publish_time);
                        if (parse.after(start_time) && parse.before(end_time))
                            return true;
                    } catch (ParseException e) {
                    }
                }
                if (StringUtils.isNotEmpty(crawl_time)) {
                    try {
                        Date parse = sdf2.parse(crawl_time);
                        if (parse.after(start_time) && parse.before(end_time))
                            return true;
                    } catch (ParseException e) {
                        return false;
                    }
                }
                return false;
            }
        });

        // 根据work_id分组去重
        positionMongoRDD = positionMongoRDD.groupBy(new Function<Document, String>() {
            @Override
            public String call(Document v1) throws Exception {
                return v1.getString("work_id");
            }
        }).map(new Function<Tuple2<String, Iterable<Document>>, Document>() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            @Override
            public Document call(Tuple2<String, Iterable<Document>> v1) throws Exception {
                Iterator<Document> iterator = v1._2().iterator();
                Document out = iterator.next();
                Date crawl_time = sdf.parse(out.getString("crawl_time"));
                while (iterator.hasNext()) {
                    Document next = iterator.next();
                    Date next_time = sdf.parse(next.getString("crawl_time"));
                    if (crawl_time.before(next_time)) {
                        out = next;
                        crawl_time = next_time;
                    }
                }
                return out;
            }
        });

        // 识别 地区
        JavaPairRDD<ChengduPositionEntity, Document> positionRDD = positionMongoRDD.mapToPair(new PairFunction<Document, ChengduPositionEntity, Document>() {
            @Override
            public Tuple2<ChengduPositionEntity, Document> call(Document document) throws Exception {
                ChengduPositionEntity positionEntity = new ChengduPositionEntity();
                String work_city = document.getString("work_city") != null ? document.getString("work_city") : "";
                String work_address = document.getString("work_address");
                String area = document.getString("area") != null ? document.getString("area") : "";
                if (StringUtils.contains(work_city, "成都") || StringUtils.contains(work_address, "成都")) {
                    positionEntity.setCity("成都");
                    if (StringUtils.isNotEmpty(area)) {
                        positionEntity.setArea(area);
                    } else {
                        area = containArea(work_address);
                        positionEntity.setArea(area);
                    }
                    return new Tuple2<ChengduPositionEntity, Document>(positionEntity, document);
                }
                return null;
            }

            public String containArea(String str) {
                for (String area : areaSetBroadcast.getValue()) {
                    if (StringUtils.contains(str, area))
                        return area;
                }
                return "";
            }
        }).filter(tuple -> tuple != null);

        // 识别 招聘人数
        positionRDD = positionRDD.mapToPair((PairFunction<Tuple2<ChengduPositionEntity, Document>, ChengduPositionEntity, Document>) tuple2 -> {
            String work_hiring = tuple2._2().getString("work_hiring");
            if (StringUtils.isNotEmpty(work_hiring)) {
                tuple2._1().setHire_count(work_hiring);
            } else {
                tuple2._1().setHire_count("1");
            }
            return tuple2;
        });

        // 识别公司类别
        positionRDD = positionRDD.flatMapToPair(new PairFlatMapFunction<Tuple2<ChengduPositionEntity, Document>, ChengduPositionEntity, Document>() {
            @Override
            public Iterator<Tuple2<ChengduPositionEntity, Document>> call(Tuple2<ChengduPositionEntity, Document> tuple2) throws Exception {
                ArrayList<Tuple2<ChengduPositionEntity, Document>> out = new ArrayList<Tuple2<ChengduPositionEntity, Document>>();
                String company_type = tuple2._2().getString("company_type");
                List<String> industry = null;
                if (StringUtils.isNotEmpty(company_type)) {
                    industry = getIndustryList(KeyValueUtils.trim(company_type));
                }
                if (industry == null || industry.size() == 0) {
                    industry = new ArrayList<String>();
                    industry.add("其他");
                }
                for (String i : industry) {
                    ChengduPositionEntity clone = tuple2._1().clone();
                    clone.setCompany_type(i);
                    out.add(new Tuple2<>(clone, tuple2._2()));
                }
                return out.iterator();
            }

            public List<String> getIndustryList(String industry) {
                List<String> out = new ArrayList<String>();
                for (Map.Entry<String, String> entry : industryBroadcast.getValue().entrySet()) {
                    if (StringUtils.contains(industry, entry.getKey())) {
                        out.add(entry.getValue());
                    }
                }
                return out;
            }
        });


        // 识别 职位分类
        positionRDD = positionRDD.flatMapToPair((PairFlatMapFunction<Tuple2<ChengduPositionEntity, Document>, ChengduPositionEntity, Document>) tuple2 -> {
            ArrayList<Tuple2<ChengduPositionEntity, Document>> out = new ArrayList<Tuple2<ChengduPositionEntity, Document>>();
            ArrayList<Document> work_type_mapping = (ArrayList<Document>) tuple2._2().get("work_type_mapping");
            if (work_type_mapping != null) {
                for (Document document : work_type_mapping) {
                    ChengduPositionEntity clone = tuple2._1().clone();
                    String type = document.getString("type");
                    if (StringUtils.isNotEmpty(type)) {
                        clone.setPosition(type);
                        out.add(new Tuple2<>(clone, tuple2._2()));
                    }
                }
            }
            if (out.size() == 0) {
                tuple2._1().setPosition("其他");
                out.add(new Tuple2<>(tuple2._1(), tuple2._2()));
            }
            return out.iterator();
        });

        JavaPairRDD<ChengduPositionEntity, Integer> reduceTemp = positionRDD.mapToPair((PairFunction<Tuple2<ChengduPositionEntity, Document>, ChengduPositionEntity, Integer>) tuple2 -> {
            int count = 0;
            try {
                count = Integer.parseInt(tuple2._1().getHire_count());
            } catch (Exception e) {

            }
            return new Tuple2<ChengduPositionEntity, Integer>(tuple2._1(), count);
        }).reduceByKey((Function2<Integer, Integer, Integer>) (v1, v2) -> v1 + v2);


        JavaRDD<Document> out = reduceTemp.map((Function<Tuple2<ChengduPositionEntity, Integer>, Document>) v1 -> {
            Document out1 = new Document();
//            String[] split = v1._1().split("\t");
//            out1.append("city", split[0]);
//            out1.append("area", split[1]);
//            out1.append("company_type", split[2]);
//            out1.append("position", split[3]);
//            out1.append("count", v1._2());
            out1.append("city", v1._1().getCity());
            out1.append("area", v1._1().getArea());
            out1.append("company_type", v1._1().getCompany_type());
            out1.append("position", v1._1().getPosition());
            out1.append("count", v1._2());
            out1.append("type", "chengdu_position_demand");
            return out1;
        });
        String collectionName = out_collectionName + "_" + sdf.format(start_time) + "_" + sdf.format(end_time);
        Map<String, String> writeOverrides = new HashMap<>();
        writeOverrides.put("database", database);
        writeOverrides.put("collection", collectionName);
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);
        MongoSpark.save(out, wc);
        jsc.close();
    }

    public static void printErrInfo() {
        System.out.println("error: --start_time");
        System.out.println("error: --end_time");
        System.out.println("error: --database");
        System.out.println("error: --in_collection");
        System.out.println("error: --out_collectionName");
        System.out.println("error: --ip");
        System.out.println("error: --port");
        System.out.println("error: --username");
        System.out.println("error: --password");
        System.out.println("error: --authSource");
    }

    public static void main(String[] args) throws ParseException {
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
            printErrInfo();
            System.exit(0);
        }

        JSONObject mongoConfig = new JSONObject();
        mongoConfig.put("database", argsMap.getOrDefault("--database", "xy_report"));
        mongoConfig.put("in_database", argsMap.getOrDefault("--in_database", "xy_report"));
        mongoConfig.put("in_collection", argsMap.getOrDefault("--in_collection", "parse"));
        mongoConfig.put("out_collection", argsMap.getOrDefault("--out_collection", "chengdu_position_distribution"));
        mongoConfig.put("ip", argsMap.getOrDefault("--ip", "10.101.1.222"));
        mongoConfig.put("port", argsMap.getOrDefault("--port", "27018"));
        mongoConfig.put("username", argsMap.getOrDefault("--username", null));
        mongoConfig.put("password", argsMap.getOrDefault("--password", null));
        mongoConfig.put("authSource", argsMap.getOrDefault("--authSource", "admin"));
        if (!argsMap.containsKey("--path"))
            argsMap.put("--path", "hdfs://10.101.1.230:9000/resume/jd_data.json");
        excute(argsMap, start_time, end_time, mongoConfig);
    }
}
