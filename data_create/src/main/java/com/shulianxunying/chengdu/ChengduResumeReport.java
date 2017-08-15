package com.shulianxunying.chengdu;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.shulianxunying.data_watch.AreaEntity;
import com.shulianxunying.resume.CompanyKey;
import com.shulianxunying.resume.FuncPositionMap;
import com.shulianxunying.resume.PositionFunc;
import com.shulianxunying.resume.ResumeCityFunc;
import com.shulianxunying.utils.AreaUtils;
import com.shulianxunying.utils.CityEntity;
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
 * Created by SuChang on 2017/5/8 15:09.
 */
public class ChengduResumeReport {

    /**
     * 人才分布
     *
     * @param dimenResuem
     * @return
     */
    public static JavaRDD<Document> analysisCount(JavaPairRDD<Document, ResumeFlowEntity> dimenResuem) {
        JavaPairRDD<String, Integer> tempRDD = dimenResuem.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeFlowEntity>, String, Integer>() {
            @Override
            public Iterator<Tuple2<String, Integer>> call(Tuple2<Document, ResumeFlowEntity> tuple2) throws Exception {
                ArrayList<Tuple2<String, Integer>> out = new ArrayList<Tuple2<String, Integer>>();
                ResumeFlowEntity resumeCityFunc = tuple2._2();
                HashSet<ResumeArea> city = resumeCityFunc.getLiving_city();
                city.remove("unknow");
                if (city.size() <= 0) {
                    city = resumeCityFunc.getHometown_city();
                }
                city.remove("unknow");
                if (city.size() <= 0) {
                    city = resumeCityFunc.getExpect_city();
                }
                for (ResumeArea s : city) {
                    String data = "";
                    out.add(new Tuple2<>(data, 1));
                }
                return out.iterator();
            }
        });
        tempRDD = tempRDD.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        return tempRDD.map(new Function<Tuple2<String, Integer>, Document>() {
            @Override
            public Document call(Tuple2<String, Integer> v1) throws Exception {
                String[] split = v1._1().split("\t");
                Document out = new Document();
                out.put("city", split[0]);
                out.put("func", split[1]);
                out.put("second", split[2]);
                out.put("position", split[3]);
                out.put("industry", split[4]);
                out.put("count", v1._2());
                out.put("type", "city_position_count");
                return out;
            }
        });
    }


    /**
     * 人才流动分析
     *
     * @param dimenResuem
     * @param out_citys
     * @return
     */
    public static JavaRDD<Document> analysisFlow(JavaPairRDD<Document, ResumeCityFunc> dimenResuem, String out_citys) {
        String[] citys = out_citys.split(",");
        JavaPairRDD<String, Integer> reduceByKey = dimenResuem.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, String, Integer>() {
            private static final long serialVersionUID = 1L;

            public Iterator<Tuple2<String, Integer>> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
                ArrayList<Tuple2<String, Integer>> out = new ArrayList<Tuple2<String, Integer>>();
                ResumeCityFunc resumeCityFunc = tuple2._2();
                HashSet<String> living_city = resumeCityFunc.getLiving_city();
                if (living_city.size() <= 0)
                    living_city = resumeCityFunc.getHometown_city();
                ArrayList<Document> workExperienceList = (ArrayList<Document>) tuple2._1().get("workExperienceList");
                String enterprise_name = null;
                HashSet<String> preCitys = new HashSet<String>();
                if (workExperienceList != null && workExperienceList.size() > 2) {
                    String nowCompany = workExperienceList.get(0).getString("enterprise_name");
                    if (StringUtils.isNotEmpty(nowCompany)) {
                        for (String city : citys) {
                            if (nowCompany.contains(city))
                                living_city.add(city);
                        }
                    }
                    enterprise_name = workExperienceList.get(1).getString("enterprise_name");
                    if (StringUtils.isEmpty(enterprise_name) && workExperienceList.size() > 3) {
                        enterprise_name = workExperienceList.get(2).getString("enterprise_name");
                    }
                    if (StringUtils.isNotEmpty(enterprise_name)) {
                        for (String city : citys) {
                            if (enterprise_name.contains(city))
                                preCitys.add(city);
                        }
                    }
                } else if (workExperienceList != null && workExperienceList.size() == 1) {
                    String nowCompany = workExperienceList.get(0).getString("enterprise_name");
                    if (StringUtils.isNotEmpty(nowCompany)) {
                        for (String city : citys) {
                            if (nowCompany.contains(city))
                                living_city.add(city);
                        }
                    }
                    // todo 需要一份学校所在地的数据支撑
                    String college_name = tuple2._1().getString("college_name");
                    HashSet<String> hometown_city = tuple2._2().getHometown_city();
                    if (StringUtils.isNotEmpty(college_name)) {
                        for (String city : citys) {
                            if (college_name.contains(city))
                                preCitys.add(city);
                        }
                    } else {
                        for (String s : hometown_city)
                            preCitys.add(s);
                    }

                }

                living_city.remove("unknow");
                if (living_city.size() == 0)
                    living_city.add("unknow");
                if (preCitys.size() == 0)
                    preCitys.add("unknow");
                for (String living : living_city) {
                    for (String preCity : preCitys) {
                        if (!living.equals(preCity))
                            out.add(new Tuple2<>(preCity + "\t" + living + "\t" + tuple2._2().getFunc() + "\t" + tuple2._2().getSecond_level() + "\t" + tuple2._2().getPosition() + "\t" + resumeCityFunc.getIndustry(), 1));
                    }
                }
                return out.iterator();
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });

        JavaRDD<Document> out = reduceByKey.map(new Function<Tuple2<String, Integer>, Document>() {
            @Override
            public Document call(Tuple2<String, Integer> v1) throws Exception {
                String[] split = v1._1().split("\t");
                Document out = new Document();
                out.append("preCity", split[0]);
                out.append("living", split[1]);
                out.append("func", split[2]);
                out.append("second", split[3]);
                out.append("position", split[4]);
                out.append("industry", split[5]);
                out.append("count", v1._2());
                out.append("type", "flow");
                return out;
            }
        });
        return out;
    }


    /**
     * 生成 某个时间段的数据
     *
     * @param args
     */
    public static void excute(String[] args, Date start_time, Date end_time) {

        // 配置spark
        String database = "resume_report";
        String collectionName = "resume241_20170321";
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

        // 根据参数 读取mongo数据
        Map<String, String> readOverWrite = new HashMap<>();
        readOverWrite.put("collection", collectionName);
        ReadConfig readConfig = ReadConfig.create(conf, readOverWrite);
        String query = "{$match : {'crawled_time' : {$gte: " + start_time.getTime() + ",$lt: " + end_time.getTime() + "} } }";
//        String query = "{$match : {'crawled_time' : {$gte: " + 1488741963000l + ",$lt: " + 1488742565000l + "} } }";
//        JavaMongoRDD<Document> resumeMongoRDD = MongoSpark.load(jsc, readConfig)
//                .withPipeline(Arrays.asList(Document.parse(query)));

        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(null, jsc);
//        JavaRDD<Document> resumeMongoRDD = RDDUtils.readTextToMongoRDD(args[0], jsc);
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

//        System.out.println(resumeMongoRDD.count());

        // 转为 用于分类的rdd
        JavaPairRDD<Document, ResumeFlowEntity> dimenResuem = resumeMongoRDD.mapToPair(new PairFunction<Document, Document, ResumeFlowEntity>() {
            public Tuple2<Document, ResumeFlowEntity> call(Document document) throws Exception {
                return new Tuple2<Document, ResumeFlowEntity>(document, new ResumeFlowEntity());
            }
        });

        List<CityEntity> citys = AreaUtils.getCitys();
        final Broadcast<List<CityEntity>> broadcast = jsc.broadcast(citys);
        // 给简历打  城市 标签
        dimenResuem = dimenResuem.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeFlowEntity>, Document, ResumeFlowEntity>() {


            @Override
            public Iterator<Tuple2<Document, ResumeFlowEntity>> call(Tuple2<Document, ResumeFlowEntity> tuple2) throws Exception {
                ArrayList<Tuple2<Document, ResumeFlowEntity>> out = new ArrayList<Tuple2<Document, ResumeFlowEntity>>();
                Document document = tuple2._1();
                getValue(document, "expect_city", tuple2._2().getExpect_city());
                getValue(document, "living", tuple2._2().getLiving_city());
                getValue(document, "hometown", tuple2._2().getHometown_city());

                return out.iterator();
            }

            public void getValue(Document document, String key, HashSet<ResumeArea> set) throws CloneNotSupportedException {
                String value = document.getString(key);
                if (StringUtils.isNotEmpty(value)) {
                    // 识别城市
                    for (CityEntity cityEntity : broadcast.getValue()) {
                        if (value.contains(cityEntity.getCity())) {
                            ResumeArea resumeArea = new ResumeArea();
                            resumeArea.setCity(cityEntity.getCity());
                            resumeArea.setProvince(cityEntity.getProvince());
                            resumeArea.setCountry(cityEntity.getCountry());
                            boolean flag = true;
                            for (String area : cityEntity.getArea()) {
                                if (value.contains(area)) {
                                    flag = false;
                                    ResumeArea clone = resumeArea.clone();
                                    clone.setArea(area);
                                    set.add(clone);
                                }
                            }
                            if(flag)
                                set.add(resumeArea);

                        }
                    }
                }
            }
        });


        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", "resume_report");
        writeOverrides.put("collection", "resume_report_" + sdf.format(start_time) + "_" + sdf.format(end_time));
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);

        dimenResuem.take(100).forEach(p -> System.out.println(p._2()));

//         按照 城市+职位统计个数
//        JavaRDD<Document> cityPositionOut = analysisCount(dimenResuem);
//        cityPositionOut.take(100).forEach(p -> System.out.println(p));
//        System.out.println(cityPositionOut.count());
//        MongoSpark.save(cityPositionOut, wc);

        // 计算人才地域流动
//        JavaRDD<Document> flowOut = analysisFlow(dimenResuem, out_citys_exclude);
//        System.out.println(flowOut.count());
//        MongoSpark.save(flowOut, wc);
//        flowOut.take(100).forEach(p -> System.out.println(p));

        // 计算人才职能-岗位流动
//        JavaRDD<Document> funcFlowOut = analysisFuncFlow(dimenResuem, broadcast);
//        MongoSpark.save(funcFlowOut, wc);
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
        excute(args, start_time, end_time);
    }
}
