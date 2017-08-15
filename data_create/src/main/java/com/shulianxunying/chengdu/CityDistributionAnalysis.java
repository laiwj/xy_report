package com.shulianxunying.chengdu;


import com.alibaba.fastjson.JSONObject;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;
import com.shulianxunying.utils.RDDUtils;
import com.shulianxunying.utils.SparkMongoHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.Document;
import scala.Tuple2;
import scala.Tuple4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import static com.shulianxunying.utils.locationrecognizeutil.utils.IsContainsArea.getCompanyName;
import static com.shulianxunying.utils.locationrecognizeutil.LocationRecognize.locationRecognize;

/**
 * Created by 19866 on 2017/6/7.
 */
public class CityDistributionAnalysis {


    public static void excute(HashMap<String, String> argsMap, Date start_time, Date end_time, JSONObject mongoConfig) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //配置 MongoDB
        String database = mongoConfig.getString("database");
        String in_database = mongoConfig.getString("in_database");
        String in_collectionName = mongoConfig.getString("in_collection");
        String out_collectionName = mongoConfig.getString("out_collection");
        String authSource = mongoConfig.getString("authSource");
        String ip = mongoConfig.getString("ip");
        String port = mongoConfig.getString("port");
        String mongoUri = "";
        if (StringUtils.isNotEmpty(mongoConfig.getString("username"))) {
            HashMap<String, String> optins = new HashMap<String, String>();
            optins.put("authSource", authSource);
            mongoUri = SparkMongoHelper.createMongoUrl(ip, Integer.parseInt(port), mongoConfig.getString("username"), mongoConfig.getString("password"), in_database, in_collectionName, optins);
        } else {
            mongoUri = SparkMongoHelper.createMongoUrl(ip, Integer.parseInt(port), null, null, in_database, in_collectionName);
        }
        // 根据 日期参数 设置spark
        SparkConf conf = new SparkConf()
                .setAppName("CityDistributionAnalysis" + sdf.format(start_time) + "_" + sdf.format(end_time))
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", mongoUri);
        //生成JavaSpark Context
        JavaSparkContext jsc = new JavaSparkContext(conf);
        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(argsMap.get("--path"), jsc);
        resumeMongoRDD = resumeMongoRDD.filter(new Function<Document, Boolean>() {
            @Override
            public Boolean call(Document v1) throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String update_time = v1.getString("update_time");
                Long crawled_time = 0l;
                try {
                    crawled_time = v1.getLong("crawled_time");
                } catch (Exception e) {
                }

                Long parser_time = v1.getLong("parser_time");
                Date parse = null;
                try {
                    if (StringUtils.isNotEmpty(update_time)) {
                        parse = sdf.parse(update_time);
                        if (parse.after(start_time) && parse.before(end_time))
                            return true;
                    } else if (crawled_time != 0) {
                        if (crawled_time > start_time.getTime() && crawled_time < end_time.getTime())
                            return true;
                    } else if (parser_time != 0) {
                        if (parser_time > start_time.getTime() && parser_time < end_time.getTime())
                            return true;
                    }
                } catch (Exception e) {

                }
                return false;
            }
        });
        resumeMongoRDD.coalesce(resumeMongoRDD.getNumPartitions());

        JavaPairRDD<String, String> test = resumeMongoRDD.mapToPair(new PairFunction<Document, String, String>() {
            @Override
            public Tuple2<String, String> call(Document doc) throws Exception {
                //流出数据 living hometown second company
                String outflowString = "";
                //流入数据 expect city first company
                String inflowString = "";
                String living = doc.getString("living");
                String hometown = doc.getString("hometown");
                String expect_city = doc.getString("expect_city");
                ArrayList<Document> workExp = (ArrayList<Document>) doc.getOrDefault("workExperienceList", new ArrayList<Document>());
                if (StringUtils.isNotEmpty(living)) {
                    outflowString = living;
                } else if (StringUtils.isNotEmpty(hometown)) {
                    outflowString = hometown;
                } else if (workExp.size() >= 2) {
                    Tuple2<String, String> companyTuple = getCompanyName(workExp);
                    if (StringUtils.isNotEmpty(companyTuple._2()))
                        outflowString = companyTuple._2();
                } else {
                    outflowString = "empty";
                }
                if (StringUtils.isNotEmpty(expect_city) && !expect_city.equals("[]")) {
                    inflowString = expect_city;
                } else if (workExp.size() >= 2) {
                    Tuple2<String, String> companyTuple = getCompanyName(workExp);
                    if (StringUtils.isNotEmpty(companyTuple._1()))
                        inflowString = companyTuple._1();
                }
                return new Tuple2<>(outflowString, inflowString);
            }
        });

        JavaPairRDD<Iterator<Tuple4<String, String, String, String>>, Iterator<Tuple4<String, String, String, String>>> recognizedRdd =
                test.mapToPair(new PairFunction<Tuple2<String, String>, Iterator<Tuple4<String, String, String, String>>, Iterator<Tuple4<String, String, String, String>>>() {
                    @Override
                    public Tuple2<
                            Iterator<Tuple4<String, String, String, String>>,
                            Iterator<Tuple4<String, String, String, String>>
                            > call(Tuple2<String, String> tuple2) throws Exception {
                        Iterator<Tuple4<String, String, String, String>> outflowCities = locationRecognize(tuple2._1());
                        Iterator<Tuple4<String, String, String, String>> inflowCities = locationRecognize(tuple2._2());
                        return new Tuple2<>(outflowCities, inflowCities);
                    }
                });
        JavaPairRDD<Tuple2<Tuple4<String, String, String, String>, Tuple4<String, String, String, String>>, Integer> flatMappedRecognizedRdd = recognizedRdd.flatMapToPair(new PairFlatMapFunction<Tuple2<Iterator<Tuple4<String, String, String, String>>, Iterator<Tuple4<String, String, String, String>>>, Tuple2<Tuple4<String, String, String, String>, Tuple4<String, String, String, String>>, Integer>() {
            @Override
            public Iterator<
                    Tuple2<Tuple2<Tuple4<String, String, String, String>, Tuple4<String, String, String, String>>,
                            Integer>> call(Tuple2<Iterator<Tuple4<String, String, String, String>>, Iterator<Tuple4<String, String, String, String>>> iteratorIteratorTuple2) throws Exception {
                Iterator<Tuple4<String, String, String, String>> outflowCities = iteratorIteratorTuple2._1;
                Iterator<Tuple4<String, String, String, String>> inflowCities = iteratorIteratorTuple2._2;
                ArrayList<Tuple2<Tuple2<Tuple4<String, String, String, String>, Tuple4<String, String, String, String>>, Integer>> list = new ArrayList<>();
                while (outflowCities.hasNext()) {
                    Tuple4<String, String, String, String> out = outflowCities.next();
                    while (inflowCities.hasNext()) {
                        Tuple4<String, String, String, String> in = inflowCities.next();
                        list.add(new Tuple2<>(new Tuple2<>(out, in), 1));
                    }
                }
                return list.iterator();
            }
        });

        JavaPairRDD<Tuple2<Tuple4<String, String, String, String>, Tuple4<String, String, String, String>>, Integer> countRecognizedRdd = flatMappedRecognizedRdd.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        JavaRDD<Document> out = countRecognizedRdd.map(p -> {
            Document returnDoc = new Document();
            Integer count = p._2();
            String type = "resume_flow";
            Tuple4<String, String, String, String> outflow = p._1()._1();
            Tuple4<String, String, String, String> inflow = p._1()._2();
            returnDoc.append("living_area", outflow._1());
            returnDoc.append("living_city", outflow._2());
            returnDoc.append("living_province", outflow._3());
            returnDoc.append("living_country", outflow._4());
            returnDoc.append("pre_area", inflow._1());
            returnDoc.append("pre_city", inflow._2());
            returnDoc.append("pre_province", inflow._3());
            returnDoc.append("pre_country", inflow._4());
            returnDoc.append("type", type);
            returnDoc.append("count", count);
            return returnDoc;
        });
        HashMap<String, String> writeOverrides = new HashMap<>();
        writeOverrides.put("database", database);
        writeOverrides.put("collection", out_collectionName + "_" + sdf.format(start_time) + "_" + sdf.format(end_time));
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);
        MongoSpark.save(out, wc);
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
            System.out.println("时间格式错误");
            System.exit(0);
        }
        JSONObject mongoConfig = new JSONObject();
        mongoConfig.put("database", argsMap.getOrDefault("--database", "chengdu_resume_report"));
        mongoConfig.put("in_database", argsMap.getOrDefault("--in_database", "chengdu_resume_report"));
        mongoConfig.put("in_collection", argsMap.getOrDefault("--in_collection", "resume_report"));
        mongoConfig.put("out_collection", argsMap.getOrDefault("--out_collection", "resume_report"));
        mongoConfig.put("ip", argsMap.getOrDefault("--ip", "10.101.1.171"));
        mongoConfig.put("port", argsMap.getOrDefault("--port", "27017"));
        mongoConfig.put("username", argsMap.getOrDefault("--username", "root"));
        mongoConfig.put("password", argsMap.getOrDefault("--password", "sc123456"));
        mongoConfig.put("authSource", argsMap.getOrDefault("--authSource", "admin"));
        if (!argsMap.containsKey("--path"))
            argsMap.put("--path", "hdfs://10.101.1.230:9000/resume/resume20170531.json");
        excute(argsMap, start_time, end_time, mongoConfig);
    }
}
