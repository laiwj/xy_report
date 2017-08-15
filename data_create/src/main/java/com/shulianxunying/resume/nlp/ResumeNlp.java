package com.shulianxunying.resume.nlp;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.shulianxunying.resume.CompanyKey;
import com.shulianxunying.resume.FuncPositionMap;
import com.shulianxunying.resume.PositionFunc;
import com.shulianxunying.utils.MyRegistrator;
import com.shulianxunying.utils.RDDUtils;
import com.shulianxunying.utils.SparkMongoHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.storage.StorageLevel;
import org.bson.Document;
import scala.Tuple2;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 19866 on 2017/7/10.
 */
public class ResumeNlp {

    public static void execute(HashMap<String, String> argsMap, Date start_time, Date end_time, JSONObject mongoConfig) {
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
                .setAppName("ResumeReport-Feature" + sdf.format(start_time) + "_" + sdf.format(end_time))
                .setMaster("local[*]")
//                .setMaster("spark://10.101.1.230:7077")
                .set("spark.kryo.registrator", MyRegistrator.class.getName())
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.rdd.compress", "true")
                .set("spark.shuffle.memoryFraction", "0.4")
                .set("spark.shuffle.file.buffer.kb", "64")
                .set("spark.reducer.maxMbInFlight", "350")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);

        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", database);
        writeOverrides.put("collection", out_collectionName + "_" + sdf.format(start_time) + "_" + sdf.format(end_time));
//        writeOverrides.put("collection", "resume_feature_v6");
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);


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
        //读取源文件
        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(argsMap.get("--paths"), jsc);
        //按时间过滤简历
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
        JavaPairRDD<Document, ResumeTypeForNLP> dimenResuem = resumeMongoRDD.mapToPair(new PairFunction<Document, Document, ResumeTypeForNLP>() {
            public Tuple2<Document, ResumeTypeForNLP> call(Document document) throws Exception {
                return new Tuple2<Document, ResumeTypeForNLP>(document, new ResumeTypeForNLP());
            }
        });

        //给简历打  职位、行业 标签
        dimenResuem = dimenResuem.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeTypeForNLP>, Document, ResumeTypeForNLP>() {
            @Override
            public Iterator<Tuple2<Document, ResumeTypeForNLP>> call(Tuple2<Document, ResumeTypeForNLP> tuple2) throws Exception {
                ArrayList<Tuple2<Document, ResumeTypeForNLP>> out = new ArrayList<>();
                Document document = tuple2._1();
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
                    for (PositionFunc positionFunc : positionFuncList) {
                        boolean flag = positionFunc.positionHit(position_name);
                        if (!flag) {
                            continue;
                        }
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
                HashSet<String> companyNames = new HashSet<>();
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
                        for (CompanyKey companyKey : companyList) {
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
                                    } else {
                                        industrySet.add("unknown");
                                    }
                                }
                            } else {
                                if (s.contains(companyKey.getKeyword())) {
                                    for (String industry : companyKey.getIndustry()) {
                                        industrySet.add(industry);
                                    }
                                } else {
                                    industrySet.add("unknown");
                                }
                            }
                        }
                    }
                } else {
                    out.add(tuple2);
                }
                for (String industry : industrySet) {
                    ResumeTypeForNLP clone = tuple2._2().clone();
                    clone.setIndustry(industry);
                    out.add(new Tuple2<>(tuple2._1(), clone));
                }
                if (out.size() == 0)
                    out.add(tuple2);
                return out.iterator();
            }
        });
        //给简历打 性别 年龄 工作经验 城市 薪资 标签
        dimenResuem = ResumeUtilsForNLP.resumeTag(dimenResuem);
        //获取标签
        JavaRDD<ResumeTypeForNLP> resumeTypeJavaRDD = dimenResuem.values();
        resumeTypeJavaRDD.persist(StorageLevel.MEMORY_AND_DISK());
        // 按要求 输出 8个分类的结果
        //生成 职位
        JavaPairRDD<String, ResumeTypeForNLP> keyWithNothing = resumeTypeJavaRDD.mapToPair(new PairFunction<ResumeTypeForNLP, String, ResumeTypeForNLP>() {
            @Override
            public Tuple2<String, ResumeTypeForNLP> call(ResumeTypeForNLP resumeType) throws Exception {
                String position = resumeType.getPosition();
                String func = resumeType.getFunc();
                return new Tuple2<>("position_only~" + position + "~" + func, resumeType);
            }
        });
        keyWithNothing.foreach(a -> System.out.println(a._1() + ":\n" + a._2.getKeywords() + "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"));
        jsc.stop();
    }

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
        } catch (Exception e) {
            System.out.println("时间格式错误");
            System.exit(0);
        }
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
        if (!argsMap.containsKey("--paths"))
            argsMap.put("--paths", "file:///home/xybigdata/resume241/resume20170531.json");
        execute(argsMap, start_time, end_time, mongoConfig);
    }

}
