package com.shulianxunying.resume;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
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
 * Created by 19866 on 2017/6/16.
 */
public class ResumeAnalysis {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws ParseException {
        String path = args.length >= 1 ? args[0] : "E:\\spark_source_and_result\\data\\temp.json";
        Date start_time = args.length >= 2 ? sdf.parse(args[1]) : new Date(114, 3, 1);
        Date end_time = args.length >= 3 ? sdf.parse(args[2]) : new Date(118, 3, 31);
        start(path, start_time, end_time);
    }

    public static void start(String path, Date start, Date end) {
        SpecifyCityRecognize specifyCityRecognize = new SpecifyCityRecognize("中国");
        // 生成 职能 子分类 职位 的三层映射关系
        List<PositionFunc> positionFuncList = FuncPositionMap.getList();
        // 配置spark
        String database = "resume_report";
        String collectionName = "resume241_20170321";
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("authSource", "admin");
        String inputUri = SparkMongoHelper.createMongoUrl("10.101.1.171", 27017, "root", "sc123456", database, null, options);
        // 根据 日期参数 设置appName
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        SparkConf conf = new SparkConf()
                .setAppName("ResumeAnalysis" + sdf.format(start) + "_" + sdf.format(end))
                .setMaster("local[*]")
//                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);
        final Broadcast<List<PositionFunc>> broadcast = jsc.broadcast(positionFuncList);
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
        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(path, jsc);
        // 转为 用于分类的rdd
        JavaPairRDD<Document, ResumeCityFunc> dimenResume = resumeMongoRDD.mapToPair(new PairFunction<Document, Document, ResumeCityFunc>() {
            public Tuple2<Document, ResumeCityFunc> call(Document document) throws Exception {
                return new Tuple2<Document, ResumeCityFunc>(document, new ResumeCityFunc());
            }
        });
        String out_cities = "北京,上海,广州,深圳,成都,杭州,武汉,天津,南京,重庆,西安,长沙,青岛,沈阳,大连,厦门,苏州,宁波,无锡";
        dimenResume = dimenResume.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, Document, ResumeCityFunc>() {
            public String[] cities = out_cities.split(",");

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
        dimenResume.foreach(p -> System.out.println(p._2.toString()));
    }

}
