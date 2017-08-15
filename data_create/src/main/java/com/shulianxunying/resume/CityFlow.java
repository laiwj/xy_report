package com.shulianxunying.resume;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.shulianxunying.utils.CityAnalysisUtils;
import com.shulianxunying.utils.RDDUtils;
import com.shulianxunying.utils.SparkMongoHelper;
import com.shulianxunying.utils.locationrecognizeutil.SpecifyCityRecognize;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.bson.Document;
import scala.Tuple2;
import scala.Tuple4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 19866 on 2017/6/23.
 */
public class CityFlow {
    private static SpecifyCityRecognize specifyCityRecognize = new SpecifyCityRecognize("中国");

    public static void excute(HashMap<String, String> argsMap, Date start_time, Date end_time, JSONObject mongoConfig) {
        // 生成 职能 子分类 职位 的三层映射关系
        List<PositionFunc> positionFuncList = FuncPositionMap.getList();
        // 配置spark
        //配置 MongoDB
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

        SparkConf conf = new SparkConf()
                .setAppName("ResumeReport-CityFlow" + sdf.format(start_time) + "_" + sdf.format(end_time))
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);

        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", database);
        writeOverrides.put("collection", out_collectionName + "_" + sdf.format(start_time) + "_" + sdf.format(end_time));
//        writeOverrides.put("collection", "resume_city_flow_test");
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);

        final Broadcast<List<PositionFunc>> broadcast = jsc.broadcast(positionFuncList);
        // 根据参数 读取mongo数据
//        Map<String, String> readOverWrite = new HashMap<>();
//        readOverWrite.put("collection", collectionName);
//        ReadConfig readConfig = ReadConfig.create(conf, readOverWrite);
//        String query = "{$match : {'crawled_time' : {$gte: " + start_time.getTime() + ",$lt: " + end_time.getTime() + "} } }";
//        String query = "{$match : {'crawled_time' : {$gte: " + 1488741963000l + ",$lt: " + 1488742565000l + "} } }";
//        JavaMongoRDD<Document> resumeMongoRDD = MongoSpark.load(jsc, readConfig)
//                .withPipeline(Arrays.asList(Document.parse(query)));


        // 读取公司分类
        Map<String, String> readOverWrite2 = new HashMap<>();
        readOverWrite2.put("collection", "resume_compay_industy");
        ReadConfig readConfig2 = ReadConfig.create(conf, readOverWrite2);
        JavaMongoRDD<Document> company = MongoSpark.load(jsc, readConfig2);


        JavaRDD<CompanyKey> companyRdd = company.map(new Function<Document, CompanyKey>() {
            @Override
            public CompanyKey call(Document document) throws Exception {
                CompanyKey companyKey = new CompanyKey(document.getString("sub_name"));
                for (String s : document.getString("industy").split(",")) {
                    if (StringUtils.isNotEmpty(s))
                        companyKey.getIndustry().add(s);
                }
                for (String s : document.getString("hit_word").split(",")) {
                    if (StringUtils.isNotEmpty(s))
                        companyKey.getKeys().add(s);
                }
                return companyKey;
            }
        });

        List<CompanyKey> companyList = companyRdd.collect();
        final Broadcast<List<CompanyKey>> companyBroadcast = jsc.broadcast(companyList);

        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(argsMap.get("--paths"), jsc);

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


        // 转为 用于分类的rdd
        JavaPairRDD<Document, ResumeCityFunc> dimenResume = resumeMongoRDD.mapToPair(new PairFunction<Document, Document, ResumeCityFunc>() {
            public Tuple2<Document, ResumeCityFunc> call(Document document) throws Exception {
                return new Tuple2<Document, ResumeCityFunc>(document, new ResumeCityFunc());
            }
        });
        // 给简历打  城市、职位、行业 标签
        dimenResume = dimenResume.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, Document, ResumeCityFunc>() {
            @Override
            public Iterator<Tuple2<Document, ResumeCityFunc>> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
                ArrayList<Tuple2<Document, ResumeCityFunc>> out = new ArrayList<Tuple2<Document, ResumeCityFunc>>();
                Document document = tuple2._1();
                getValue(document, "expect_city", tuple2._2().getExpect_city());
                getValue(document, "living", tuple2._2().getLiving_city());
                getValue(document, "hometown", tuple2._2().getHometown_city());
                List<PositionFunc> positionFuncs = broadcast.getValue();
                String position_name = document.getString("last_position_name");
                ArrayList<Document> workExperienceList = (ArrayList<Document>) document.get("workExperienceList");
                if (StringUtils.isEmpty(position_name)) {
                    if (workExperienceList != null && workExperienceList.size() > 0) {
                        position_name = workExperienceList.get(0).getString("position_name");
                        if (StringUtils.isEmpty(position_name) && workExperienceList.size() > 1)
                            position_name = workExperienceList.get(1).getString("position_name");
                    }
                }
                if (StringUtils.isNotEmpty(position_name)) {
                    for (PositionFunc positionFunc : positionFuncs) {
                        boolean flag = positionFunc.positionHit(position_name);
                        if (!flag)
                            continue;
                        tuple2._2().setFunc(positionFunc.getFunc());
                        tuple2._2().setSecond_level(positionFunc.getSecond_level());
                        tuple2._2().setPosition(positionFunc.getPosition());
                        break;
                    }
                } else {
                    tuple2._2().setFunc("unknown");
                    tuple2._2().setSecond_level("unknown");
                    tuple2._2().setPosition("unknown");
                }

                // 行业
                HashSet<String> companyNames = new HashSet<String>();
                String last_enterprise_name = document.getString("last_enterprise_name");
                companyNames.add(last_enterprise_name);
                if (workExperienceList != null && workExperienceList.size() > 0) {
                    for (Document document1 : workExperienceList) {
                        String enterprise_name = document1.getString("enterprise_name");
                        companyNames.add(enterprise_name);
                    }
                }
                companyNames.remove("");
                companyNames.remove(null);
                HashSet<String> industrySet = new HashSet<>();
                if (companyNames.size() > 0) {
                    for (String s : companyNames) {
                        for (CompanyKey companyKey : companyBroadcast.value()) {
                            if (companyKey.getKeyword().length() <= 3) {
                                if (s.contains(companyKey.getKeyword())) {
                                    boolean flag = false;
                                    for (String key : companyKey.getKeys()) {
                                        if (s.contains(key)) {
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if (flag) {
                                        for (String industry : companyKey.getIndustry()) {
                                            industrySet.add(industry);
                                        }
                                    }
                                }
                            } else {
                                if (s.contains(companyKey.getKeyword())) {
                                    for (String industry : companyKey.getIndustry()) {
                                        industrySet.add(industry);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    out.add(tuple2);
                }
                for (String industry : industrySet) {
                    ResumeCityFunc clone = tuple2._2().clone();
                    clone.setIndustry(industry);
                    out.add(new Tuple2<>(tuple2._1(), clone));
                }
                if (out.size() == 0)
                    out.add(tuple2);
                return out.iterator();
            }

            public void getValue(Document document, String key, HashSet<Area> set) {
                String value = document.getString(key);
                if (StringUtils.isNotEmpty(value)) {
                    for (Tuple4<String, String, String, String> city : specifyCityRecognize.locationRecognize(value)) {
                        set.add(new Area(city));
                    }
                    if (set.size() == 0)
                        set.add(new Area("unknown", "unknown", "unknown", "unknown"));
                } else {
                    set.add(new Area("unknown", "unknown", "unknown", "unknown"));
                }
            }
        });

        // 计算人才地域流动
        JavaRDD<Document> flowOut = CityAnalysisUtils.analysisFlow(dimenResume, broadcast, specifyCityRecognize);
        MongoSpark.save(flowOut, wc);
        jsc.stop();
        jsc.close();
    }

//    public static void main(String[] args) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date start_time = null, end_time = null;
//        if (args.length >= 3) {
//            start_time = sdf.parse(args[1]);
//            end_time = sdf.parse(args[2]);
//        } else {
//            start_time = new Date(114, 3, 1);
//            end_time = new Date(118, 3, 31);
//        }
//        excute(args, start_time, end_time);
//    }
}
