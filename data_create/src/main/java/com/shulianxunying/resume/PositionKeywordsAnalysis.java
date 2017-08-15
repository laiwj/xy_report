package com.shulianxunying.resume;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.shulianxunying.utils.RDDUtils;
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


/**
 * Created by 19866 on 2017/5/31.
 */
public class PositionKeywordsAnalysis {

    private static class unionString implements Function2<String, String, String> {
        @Override
        public String call(String a, String b) {
            return a + b;
        }
    }

    /**
     * 生成 某年某周的数据
     * @param args
     */
    public static void excute(String[] args, Date start_time, Date end_time) {
        // 生成 职能 子分类 职位 的三层映射关系
        List<PositionFunc> positionFuncList = FuncPositionMap.getList();
        // 配置spark
        String database = "resume_report";
        String collectionName = "resume241_20170321";
        HashMap<String, String> options = new HashMap<>();
        options.put("authSource", "admin");
        String mongodbUri = SparkMongoHelper.createMongoUrl("10.101.1.171", 27017, "root", "sc123456", database, null, options);
        // 根据 日期参数 设置appName
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SparkConf conf = new SparkConf()
                .setAppName("ResumeReport" + sdf.format(start_time) + "_" + sdf.format(end_time))
                .setMaster("local[*]")
//                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", mongodbUri)
                .set("spark.mongodb.input.uri", mongodbUri);
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
        final Broadcast<List<CompanyKey>> broadcastCompanyList = jsc.broadcast(companyList);
        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(args[0], jsc);
//        resumeMongoRDD = resumeMongoRDD.filter(new Function<Document, Boolean>() {
//            @Override
//            public Boolean call(Document v1) throws Exception {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                String update_time = v1.getString("update_time");
//                Long crawled_time = 0l;
//                try {
//                    crawled_time = v1.getLong("crawled_time");
//                } catch (Exception e) {
//                }
//
//                Long parser_time = v1.getLong("parser_time");
//                Date parse = null;
//                try {
//                    if (StringUtils.isNotEmpty(update_time)) {
//                        parse = sdf.parse(update_time);
//                        if (parse.after(start_time) && parse.before(end_time))
//                            return true;
//                    } else if (crawled_time != 0) {
//                        if (crawled_time > start_time.getTime() && crawled_time < end_time.getTime())
//                            return true;
//                    } else if (parser_time != 0) {
//                        if (parser_time > start_time.getTime() && parser_time < end_time.getTime())
//                            return true;
//                    }
//                } catch (Exception e) {
//                }
//                return false;
//            }
//        });

        JavaPairRDD<String, String> positionHitPairRDD = resumeMongoRDD.mapToPair((Document resume) -> {
            List<PositionFunc> positionFuncs = broadcast.getValue();

            String position_name = StringUtils.isEmpty(resume.getString("last_position_name")) ? "" : resume.getString("last_position_name");
            String self_introduction = StringUtils.isEmpty(resume.getString("self_introduction")) ? "" : resume.getString("self_introduction");

            Object skillList = resume.get("skillList");
            if (skillList == null)
                skillList = new ArrayList<>();
            String skill = "";
            if (!((ArrayList) skillList).isEmpty() && ((ArrayList) skillList).get(0) instanceof String) {
                for (String s : (ArrayList<String>) skillList)
                    skill += s + ";";
            } else {
                for (Document skillDoc : (ArrayList<Document>) skillList) {
                    String skillName = StringUtils.isEmpty(skillDoc.getString("skill_name")) ? "" : skillDoc.getString("skill_name");
                    if (!skillName.equals(""))
                        skill += skillName + ";";
                }
            }
            ArrayList<Document> certificateList = (ArrayList<Document>) resume.get("certificateList");
            if (certificateList == null)
                certificateList = new ArrayList<>();
            String certificate = "";
            for (Document certificateDoc : certificateList) {
                String certificateName = StringUtils.isEmpty(certificateDoc.getString("certificate_name")) ? "" : certificateDoc.getString("certificate_name");
                if (!certificateName.equals(""))
                    certificate += certificateName + ";";
            }
            String segmentLine = self_introduction + skill + certificate;
            String funcSecondPosition = "";
            String enterpriseName = "";
            ArrayList<Document> workExperienceList = (ArrayList<Document>) resume.get("workExperienceList");
            if (StringUtils.isEmpty(position_name)) {
                if (workExperienceList != null && workExperienceList.size() > 0) {
                    Document work = workExperienceList.get(0);
                    position_name = work.getString("position_name");
                    segmentLine += work.getString("experience_desc");
                    enterpriseName = StringUtils.isEmpty(work.getString("enterprise_name")) ? "" : work.getString("enterprise_name");
                    if (StringUtils.isEmpty(position_name) && workExperienceList.size() > 1) {
                        Document work1 = workExperienceList.get(1);
                        position_name = work1.getString("position_name");
                        segmentLine += work1.getString("experience_desc");
                        enterpriseName = StringUtils.isEmpty(work1.getString("enterprise_name")) ? "" : work1.getString("enterprise_name");
                    }
                }
            }
            if (StringUtils.isNotEmpty(position_name)) {
                for (PositionFunc positionFunc : positionFuncs) {
                    boolean flag = positionFunc.positionHit(position_name);
                    if (!flag)
                        continue;
                    funcSecondPosition += positionFunc.getFunc() + "\t" + positionFunc.getSecond_level() + "\t" + positionFunc.getPosition();
                    break;
                }
                if (StringUtils.isEmpty(funcSecondPosition))
                    funcSecondPosition += "unknown";
            } else {
                funcSecondPosition += "unknown";
            }
            String industry = "";
            if (StringUtils.isNotEmpty(enterpriseName)) {
                for (CompanyKey companyKey : broadcastCompanyList.value()) {
                    if (companyKey.getKeyword().length() <= 3) {
                        if (enterpriseName.contains(companyKey.getKeyword())) {
                            boolean flag = false;
                            for (String key : companyKey.getKeys()) {
                                if (enterpriseName.contains(key)) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) {
                                for (String industryTemp : companyKey.getIndustry()) {
                                    industry = industryTemp;
                                    break;
                                }
                            }
                        }
                    } else {
                        if (enterpriseName.contains(companyKey.getKeyword())) {
                            for (String industryTemp : companyKey.getIndustry()) {
                                industry = industryTemp;
                                break;
                            }
                        }
                    }
                }
            }
            if (StringUtils.isEmpty(industry))
                industry = "industry";
            funcSecondPosition += "\t" + industry;
            return new Tuple2<>(funcSecondPosition, segmentLine);
        });
        System.out.println(positionHitPairRDD.count());
        JavaPairRDD<String, String> keyAndlinesPairRdd = positionHitPairRDD.filter(p -> !p._1().contains("unknown")).reduceByKey(new unionString());
        JavaPairRDD<String, ArrayList<String>> ikSegmentPairRdd = keyAndlinesPairRdd.mapToPair(p -> {
            if(p._1().equals("财务\t传统财务\t税务\t电商"))
                System.out.println(p._2);
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
            insert.append("industry", funcSecondPosKey[3]);
            insert.append("keyword", funcSecondPosKey[4]);
            insert.append("count", Integer.parseInt(funcSecondPosKey[5]));
            return insert;
        });
        Map<String, String> writeOverrides = new HashMap<>();
        writeOverrides.put("database", "resume_report");
//        writeOverrides.put("collection", "resume_report_" + sdf.format(start_time) + "_" + sdf.format(end_time));
        writeOverrides.put("collection", "resume_keyword_test_1");
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);
        MongoSpark.save(out, wc);
    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start_time = null, end_time = null;
        if (args.length >= 3) {
            start_time = sdf.parse(args[1]);
            end_time = sdf.parse(args[2]);
        } else {
            start_time = new Date(0, 3, 1);
            end_time = new Date(118, 3, 31);
        }
        excute(args, start_time, end_time);
    }
}
