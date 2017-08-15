package com.shulianxunying.resume;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
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
import org.apache.spark.broadcast.Broadcast;
import org.bson.Document;
import scala.Tuple2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SuChang on 2017/6/22 10:20.
 * <p>
 * 用于计算 人才岗位流动
 */
public class ResumePositionFlow {

    /**
     * 职能流动
     *
     * @param dimenResuem
     * @param broadcast
     * @return
     */
    public static JavaRDD<Document> analysisFuncFlow(JavaPairRDD<Document, ResumeType> dimenResuem, Broadcast<List<PositionFunc>> broadcast) {
        JavaPairRDD<String, Integer> reduceByKey = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                Document document = tuple2._1();
                String last_position_name = document.getString("last_position_name");
                ArrayList<Document> workExperienceList = (ArrayList<Document>) document.get("workExperienceList");
                String[] strings1 = new String[]{"unknown", "unknown", "unknown"};
                String[] strings2 = new String[]{"unknown", "unknown", "unknown"};

                if (workExperienceList != null && workExperienceList.size() > 1) {
                    String position_name1 = workExperienceList.get(0).getString("position_name");
                    String position_name2 = workExperienceList.get(1).getString("position_name");
                    if (StringUtils.isEmpty(last_position_name))
                        last_position_name = position_name1;
                    if (StringUtils.isEmpty(position_name2) && workExperienceList.size() > 2)
                        position_name2 = workExperienceList.get(2).getString("position_name");
                    strings1 = setFlag(last_position_name);
                    strings2 = setFlag(position_name2);

                } else if (workExperienceList != null && workExperienceList.size() == 1) {
                    String position_name1 = workExperienceList.get(0).getString("position_name");
                    if (StringUtils.isEmpty(last_position_name))
                        last_position_name = position_name1;
                    strings1 = setFlag(last_position_name);

                } else {
                    if (StringUtils.isNotEmpty(last_position_name)) {
                        strings1 = setFlag(last_position_name);
                    }
                }
                return new Tuple2<String, Integer>(createStr(strings1, strings2, tuple2._2().getIndustry()), 1);
            }

            public String createStr(String[] strings1, String[] strings2, String industry) {
                StringBuilder sb = new StringBuilder();
                sb.append(industry);
                sb.append("\t");
                sb.append(strings1[0]);
                sb.append("\t");
                sb.append(strings1[1]);
                sb.append("\t");
                sb.append(strings1[2]);
                sb.append("\t");
                sb.append(strings2[0]);
                sb.append("\t");
                sb.append(strings2[1]);
                sb.append("\t");
                sb.append(strings2[2]);
                return sb.toString();
            }

            public String[] setFlag(String position_name) {
                List<PositionFunc> positionFuncs = broadcast.getValue();
                if (StringUtils.isNotEmpty(position_name)) {
                    for (PositionFunc positionFunc : positionFuncs) {
                        boolean flag = positionFunc.positionHit(position_name);
                        if (!flag)
                            continue;
                        return new String[]{positionFunc.getFunc(), positionFunc.getSecond_level(), positionFunc.getPosition()};
                    }
                } else {
                    return new String[]{"unknown", "unknown", "unknown"};
                }
                return new String[]{"unknown", "unknown", "unknown"};
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        return reduceByKey.map(new Function<Tuple2<String, Integer>, Document>() {
            @Override
            public Document call(Tuple2<String, Integer> v1) throws Exception {
                String[] split = v1._1().split("\t");
                Document out = new Document();
                out.append("industry", split[0]);
                out.append("func", split[1]);
                out.append("second", split[2]);
                out.append("position", split[3]);
                out.append("pre_func", split[4]);
                out.append("pre_second", split[5]);
                out.append("pre_position", split[6]);
                out.append("count", v1._2());
                out.append("type", "func_flow");
                return out;
            }
        });
    }

    /**
     * 生成 某年某周的数据
     *
     * @param argsMap
     */
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
                .setAppName("ResumeReport-PositionFlow" + sdf.format(start_time) + "_" + sdf.format(end_time))
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);

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
//        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(null, jsc);

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
        JavaPairRDD<Document, ResumeType> dimenResuem = resumeMongoRDD.mapToPair(new PairFunction<Document, Document, ResumeType>() {
            public Tuple2<Document, ResumeType> call(Document document) throws Exception {
                return new Tuple2<Document, ResumeType>(document, new ResumeType());
            }
        });

//        // 给简历打  行业 标签
        dimenResuem = dimenResuem.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            @Override
            public Iterator<Tuple2<Document, ResumeType>> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                ArrayList<Tuple2<Document, ResumeType>> out = new ArrayList<Tuple2<Document, ResumeType>>();
                Document document = tuple2._1();
                ArrayList<Document> workExperienceList = (ArrayList<Document>) document.get("workExperienceList");

                // 打行业标签
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
                    ResumeType clone = tuple2._2().clone();
                    clone.setIndustry(industry);
                    out.add(new Tuple2<>(tuple2._1(), clone));
                }
                if (out.size() == 0)
                    out.add(tuple2);
                return out.iterator();
            }
        });

        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", database);
        writeOverrides.put("collection", out_collectionName + "_" + sdf.format(start_time) + "_" + sdf.format(end_time));
//        writeOverrides.put("collection", "resume_keyword_test_3");
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);
//        // 计算人才职能-岗位流动
        JavaRDD<Document> funcFlowOut = analysisFuncFlow(dimenResuem, broadcast);
        MongoSpark.save(funcFlowOut, wc);
        jsc.stop();
        jsc.close();
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
