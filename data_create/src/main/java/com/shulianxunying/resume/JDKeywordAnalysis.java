package com.shulianxunying.resume;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.shulianxunying.utils.SparkMongoHelper;
import com.shulianxunying.utils.WordSegment;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;
import org.bson.Document;
import scala.Tuple2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 19866 on 2017/6/2.
 * 招聘信息关键词提取和统计
 */
public class JDKeywordAnalysis {
    private static class unionString implements Function2<String, String, String> {
        @Override
        public String call(String a, String b) {
            return a + b;
        }
    }

    public static void main(String[] args) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String input = args.length > 0 ? args[0] : "E:\\spark_source_and_result\\data\\parser.json";
        final Date start_time = args.length >= 2 ? sdf.parse(args[1]) : new Date(114, 3, 1);
        final Date end_time = args.length >= 3 ? sdf.parse(args[2]) : new Date(118, 3, 31);

        // 配置sparkMongoDB
        String database = "resume_report";
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("authSource", "admin");
        String mongoUri = SparkMongoHelper.createMongoUrl("10.101.1.171", 27017, "root", "sc123456", database, null, options);

        // 根据 日期参数 设置spark
        SparkConf conf = new SparkConf()
                .setAppName("JDKeywordAnalysis")
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", mongoUri)
                .set("spark.mongodb.input.uri", mongoUri);
        //生成JavaSpark Context
        JavaSparkContext jsc = new JavaSparkContext(conf);

        // 生成 职能 子分类 职位 的三层映射关系
        List<PositionFunc> positionFuncList = FuncPositionMap.getList();
        final Broadcast<List<PositionFunc>> broadcast = jsc.broadcast(positionFuncList);

        //公司和行业映射关系
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
        final Broadcast<List<CompanyKey>> broadcastCompanyList = jsc.broadcast(companyList);

        //从hdfs 读取文件
        JavaRDD<String> jdStringRdd = jsc.textFile(input);
        //转为Document Rdd
        JavaRDD<Document> jdRdd = jdStringRdd.map(p -> {
            return Document.parse(p);
        });
        //按职位发布时间时间过滤JD
        Pattern datePattern = Pattern.compile("\\d{4}");
        jdRdd = jdRdd.filter(p -> {
            String time = "";
            String publish_time = p.getString("publish_time");
            Matcher matcher = datePattern.matcher(publish_time);
            if(matcher.find())
                time = publish_time;
            else
                time = p.getString("crawl_time");
            Date parse = null;
            if (StringUtils.isNotEmpty(time)) {
                parse = sdf.parse(time);
                if (parse.after(start_time) && parse.before(end_time))
                    return true;
                else
                    return false;
            } else
                return false;
        });
        //给jd 打标签
        JavaPairRDD<String, String> jdWithTagRdd = jdRdd.flatMapToPair(p -> {
            String funcSecondPosition = "";
            ArrayList<Tuple2<String, String>> result = new ArrayList<>();
            String companyInfo = p.getString("company_info");
            String jd = p.getString("work_jd");
            String companyName = p.getString("company_name");
            ArrayList<Document> workType = (ArrayList<Document>) p.get("work_type_mapping");
            ArrayList<String> positionList = new ArrayList<>();
            ArrayList<String> funSecondPositionList = new ArrayList<>();
            for (Document work : workType) {
                positionList.add(work.getString("type"));
            }
            for (String position : positionList) {
                if (StringUtils.isNotEmpty(position)) {
                    for (PositionFunc positionFunc : broadcast.getValue()) {
                        boolean flag = positionFunc.positionHit(position);
                        if (!flag)
                            continue;
                        funcSecondPosition += positionFunc.getFunc() + "\t" + positionFunc.getSecond_level() + "\t" + positionFunc.getPosition();
                        break;
                    }
                    if (funSecondPositionList.isEmpty() && StringUtils.isEmpty(funcSecondPosition))
                        funcSecondPosition += "unknown";
                } else {
                    funcSecondPosition += "unknown";
                }
                HashSet<String> industrySet = new HashSet<>();
                for (CompanyKey companyKey : broadcastCompanyList.value()) {
                    if (companyKey.getKeyword().length() <= 3) {
                        if (companyName.contains(companyKey.getKeyword())) {
                            boolean flag = false;
                            for (String key : companyKey.getKeys()) {
                                if (companyName.contains(key)) {
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
                        if (companyName.contains(companyKey.getKeyword())) {
                            for (String industry : companyKey.getIndustry()) {
                                industrySet.add(industry);
                            }
                        }
                    }
                }
                for (String industry : industrySet) {
                    result.add(new Tuple2<>(funcSecondPosition + "\t" + industry, jd + companyInfo));
                }
                if (result.isEmpty())
                    result.add(new Tuple2<>(funcSecondPosition + "\t" + "industry", jd + companyInfo));
                funcSecondPosition = "";
            }
            return result.iterator();
        });
        //过滤掉未能匹配职位的JD
        JavaPairRDD<String, String> keyAndLinesPairRdd = jdWithTagRdd.filter(p -> !p._1().contains("unknow")).reduceByKey(new unionString());
        //分词
        JavaPairRDD<String, ArrayList<String>> ikSegmentPairRdd = keyAndLinesPairRdd.mapToPair(p -> {
            return new Tuple2<>(p._1, WordSegment.queryWords(p._2));
        });
        JavaPairRDD<String, HashMap<String, Integer>> resumeKeywordCount = ikSegmentPairRdd.mapToPair(tuple -> {
            String key = tuple._1();
            HashMap<String, Integer> result = new HashMap<>();
            for (String k : tuple._2) {
                if (result.keySet().contains(k)) {
                    result.put(k, result.get(k) + 1);
                } else
                    result.put(k, 1);
            }
            return new Tuple2<>(key, result);
        });
        JavaRDD<String> keyValueString = resumeKeywordCount.flatMap(p -> {
            ArrayList<String> res = new ArrayList<>();
            for (String key : p._2().keySet()) {
                res.add(p._1() + "\t" + key + "\t" + p._2().get(key));
            }
            return res.iterator();
        });

        JavaRDD<Document> out = keyValueString.map(p -> {
            Document insert = new Document();
            String[] funcSecondPosKey = p.split("\t");
            insert.append("func", funcSecondPosKey[0]);
            insert.append("second", funcSecondPosKey[1]);
            insert.append("position", funcSecondPosKey[2]);
            if (funcSecondPosKey[3].equals("industry"))
                insert.append("industry", "unknow");
            else
                insert.append("industry", funcSecondPosKey[3]);
            insert.append("keyword", funcSecondPosKey[4]);
            insert.append("count", Integer.parseInt(funcSecondPosKey[5]));
            insert.append("type", "jd_position_keyword");
            return insert;
        });
        out.foreach(p -> System.out.println(p));
        Map<String, String> writeOverrides = new HashMap<String, String>();
//        writeOverrides.put("collection", "jd_keyword_v4");
        writeOverrides.put("collection", "resume_report_" + sdf.format(start_time) + "_" + sdf.format(end_time));
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);
        MongoSpark.save(out, wc);
    }
}
