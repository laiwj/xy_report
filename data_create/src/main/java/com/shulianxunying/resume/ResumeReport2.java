package com.shulianxunying.resume;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.shulianxunying.utils.RDDUtils;
import com.shulianxunying.utils.SparkMongoHelper;
import com.shulianxunying.utils.WordSegment;
import com.shulianxunying.utils.locationrecognizeutil.SpecifyCityRecognize;
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
import scala.Tuple4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SuChang on 2017/5/8 15:09.
 * 简历维度分析
 */
public class ResumeReport2 {
    private static String cities = "北京,上海,广州,深圳,成都,杭州,武汉,天津,南京,重庆,西安,长沙,青岛,沈阳,大连,厦门,苏州,宁波,无锡";
    private static SpecifyCityRecognize specifyCityRecognize = new SpecifyCityRecognize(cities);

    /**
     * 职能流动
     *
     * @param dimenResume
     * @param broadcast
     * @return
     */
    private static JavaRDD<Document> analysisFuncFlow(JavaPairRDD<Document, ResumeCityFunc> dimenResume, Broadcast<List<PositionFunc>> broadcast) {
        JavaPairRDD<String, Integer> reduceByKey = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeCityFunc>, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
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

    private static class unionString implements Function2<String, String, String> {
        @Override
        public String call(String a, String b) {
            return a + b;
        }
    }

    public static JavaRDD<Document> resumeKeyword(JavaPairRDD<Document, ResumeCityFunc> dimenResume) {
        JavaPairRDD<String, String> resumeKeywordPairRdd = dimenResume.mapToPair(p -> {
            Document resume = p._1;
            ResumeCityFunc rcf = p._2;
            String funcSecondPositionIndustry = "unknownn";
            String segmentLine = "";
            if (!rcf.getPosition().equals("unknown")) {
                funcSecondPositionIndustry = rcf.getFunc() + "\t" + rcf.getSecond_level() + "\t" + rcf.getPosition() + "\t" + rcf.getIndustry();
                String position_name = rcf.getPosition();
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
                segmentLine = self_introduction + skill + certificate;
                ArrayList<Document> workExperienceList = (ArrayList<Document>) resume.get("workExperienceList");
                if (!position_name.equals("unknown")) {
                    if (workExperienceList != null && workExperienceList.size() > 0) {
                        Document work = workExperienceList.get(0);
                        if (StringUtils.isNotEmpty(work.getString("position_name")))
                            segmentLine += work.getString("experience_desc");
                        if (StringUtils.isEmpty(position_name) && workExperienceList.size() > 1) {
                            Document work1 = workExperienceList.get(1);
                            if (StringUtils.isNotEmpty(work1.getString("position_name")))
                                segmentLine += work1.getString("experience_desc");
                        }
                    }
                }
            }
            return new Tuple2<>(funcSecondPositionIndustry, segmentLine);
        });
        JavaPairRDD<String, String> keyAndLinesPairRdd = resumeKeywordPairRdd.filter(p -> !p._1().contains("unknown")).reduceByKey(new unionString());
        JavaPairRDD<String, ArrayList<String>> ikSegmentPairRdd = keyAndLinesPairRdd.mapToPair(p -> {
            return new Tuple2<String, ArrayList<String>>(p._1, WordSegment.queryWords(p._2));
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
                insert.append("industry", "unknown");
            else
                insert.append("industry", funcSecondPosKey[3]);
            insert.append("keyword", funcSecondPosKey[4]);
            insert.append("count", Integer.parseInt(funcSecondPosKey[5]));
            insert.append("type", "position_keyword");
            return insert;
        });
        return out;
    }

    /**
     * 生成 某年某周的数据
     *
     * @param args
     */
    public static void execute(String[] args, Date start_time, Date end_time) {
        // 生成 职能 子分类 职位 的三层映射关系
        List<PositionFunc> positionFuncList = FuncPositionMap.getList();
        // 配置spark
        String database = "resume_report";
        HashMap<String, String> optins = new HashMap<String, String>();
        optins.put("authSource", "admin");
        String inputUri = SparkMongoHelper.createMongoUrl("10.101.1.171", 27017, "root", "sc123456", database, null, optins);
        // 根据 日期参数 设置appName
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        SparkConf conf = new SparkConf()
                .setAppName("ResumeReport" + sdf.format(start_time) + "_" + sdf.format(end_time))
                .setMaster("local[*]")
//                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);

        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", "resume_report");
//        writeOverrides.put("collection", "resume_report_" + sdf.format(start_time) + "_" + sdf.format(end_time));
        writeOverrides.put("collection", "resume_keyword_test_3");
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
//
//                }
//                return false;
//            }
//        });


        // 转为 用于分类的rdd
        JavaPairRDD<Document, ResumeCityFunc> dimenResume = resumeMongoRDD.mapToPair(new PairFunction<Document, Document, ResumeCityFunc>() {
            public Tuple2<Document, ResumeCityFunc> call(Document document) throws Exception {
                return new Tuple2<Document, ResumeCityFunc>(document, new ResumeCityFunc());
            }
        });

//        // 给简历打  城市、职位、行业 标签
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
//                                            ResumeCityFunc clone = tuple2._2().clone();
//                                            clone.setIndustry(industry);
//                                            out.add(new Tuple2<>(tuple2._1(), clone));
                                        }
                                    }
                                }
                            } else {
                                if (s.contains(companyKey.getKeyword())) {
                                    for (String industry : companyKey.getIndustry()) {
                                        industrySet.add(industry);
//                                        ResumeCityFunc clone = tuple2._2().clone();
//                                        clone.setIndustry(industry);
//                                        out.add(new Tuple2<>(tuple2._1(), clone));
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


        JavaRDD<Document> keywordCountRdd = resumeKeyword(dimenResume);
        MongoSpark.save(keywordCountRdd, wc);

        // 计算人才职能-岗位流动
        JavaRDD<Document> funcFlowOut = analysisFuncFlow(dimenResume, broadcast);
        MongoSpark.save(funcFlowOut, wc);
    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start_time = null, end_time = null;
        if (args.length >= 3) {
            start_time = sdf.parse(args[1]);
            end_time = sdf.parse(args[2]);
        } else {
            start_time = new Date(114, 3, 1);
            end_time = new Date(118, 3, 31);
        }
        execute(args, start_time, end_time);
    }
}
