package com.shulianxunying.position;

import com.alibaba.fastjson.JSON;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;
import com.shulianxunying.data_watch.TencentCityData;
import com.shulianxunying.resume.FuncPositionMap;
import com.shulianxunying.resume.PositionFunc;
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
import org.apache.spark.broadcast.Broadcast;
import org.bson.Document;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by SuChang on 2017/6/2 14:33.
 */
public class PositionPathReport {

    public static void readENCNMap(Map<String, String> map, String path) {
        InputStream resourceAsStream = TencentCityData.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                if (split.length >= 2) {
                    map.put(split[0].toLowerCase(), split[1].toLowerCase());
                }
            }
        } catch (IOException e) {

        }
    }


    public static void main(String[] args) {
        // 生成 职能 子分类 职位 的三层映射关系
        List<PositionFunc> positionFuncList = FuncPositionMap.getList();

        String database = "resume_report";
        String collectionName = "position_path";
        HashMap<String, String> optins = new HashMap<String, String>();
        optins.put("authSource", "admin");
        String inputUri = SparkMongoHelper.createMongoUrl("10.101.1.171", 27017, "root", "sc123456", database, null, optins);
        SparkConf conf = new SparkConf()
                .setAppName("PositionPathReport")
//                .setMaster("local[*]")
                .setMaster("spark://10.101.1.230:7077")
                .set("spark.mongodb.output.uri", inputUri)
                .set("spark.mongodb.input.uri", inputUri);
        JavaSparkContext jsc = new JavaSparkContext(conf);
        final Broadcast<List<PositionFunc>> broadcast = jsc.broadcast(positionFuncList);

        JavaRDD<Document> mongoRDD = RDDUtils.readTextToMongoRDD(args[0], jsc);
//        JavaRDD<Document> mongoRDD = RDDUtils.readTextToMongoRDD(null, jsc);


        Map<String, String> professionMap = new HashMap<>();
        // 添加中英对照
        readENCNMap(professionMap, "/专业中英对照");
        // 添加错词纠正
        professionMap.put("计算机科学与", "计算机科学与技术");


        JavaPairRDD<PositionPathEntity, Integer> positionSubPathRDD = mongoRDD.flatMapToPair(new PairFlatMapFunction<Document, PositionPathEntity, Integer>() {
            @Override
            public Iterator<Tuple2<PositionPathEntity, Integer>> call(Document document) throws Exception {
                // 分析专业
                Set<String> profession_names = new HashSet<String>();
                String profession_name = dealProfession_name(document.getString("profession_name"));
                ArrayList<Document> educationList = (ArrayList<Document>) document.get("educationList");
                if (StringUtils.isNotEmpty(profession_name))
                    profession_names.add(profession_name);
                if (educationList != null) {
                    for (Document doc : educationList) {
                        String profession_name1 = dealProfession_name(doc.getString("profession_name"));
                        if (StringUtils.isNotEmpty(profession_name1))
                            profession_names.add(profession_name1);
                    }
                }
                if (profession_names.size() == 0)
                    profession_names.add("unknow");
                // 分析职位
                ArrayList<Tuple2<PositionPathEntity, Integer>> out = new ArrayList<Tuple2<PositionPathEntity, Integer>>();
                String last_position_name = document.getString("last_position_name");
                ArrayList<Document> workExperienceList = (ArrayList<Document>) document.get("workExperienceList");
                ArrayList<String> positions = new ArrayList<String>();
                if (workExperienceList != null)
                    for (Document doc : workExperienceList) {
                        String position_name = doc.getString("position_name");
                        if (StringUtils.isNotEmpty(position_name))
                            positions.add(position_name);
                    }
                // 如果最近一份职位和列表的第一份职位不一样，则把最近一份插入到第一个
                if (StringUtils.isNotEmpty(last_position_name) && positions.size() > 0 && !positions.get(0).equals(last_position_name)) {
                    positions.add(0, last_position_name);
                }
                //大于2份职位的 才算职业路径，目前不把 学校专业 加入计算。
                if (positions.size() >= 2) {
                    ArrayList<String> standard_positions = new ArrayList<String>();
                    for (String position_name : positions) {
                        boolean flag = false;
                        for (PositionFunc positionFunc : broadcast.getValue()) {
                            flag = positionFunc.positionHit(position_name);
                            if (!flag)
                                continue;
                            standard_positions.add(positionFunc.getPosition());
                            break;
                        }
                        if (!flag)
                            standard_positions.add("unknow");
                    }
                    // 取出各阶段 路径 组成   1-java-python  2-python-php
                    for (int i = 0; i < standard_positions.size() - 1; i++) {
                        for (String p : profession_names) {
                            PositionPathEntity positionPathEntity = new PositionPathEntity(i + 1, p, standard_positions.get(i), standard_positions.get(i + 1));
                            out.add(new Tuple2<>(positionPathEntity, 1));
                        }
                    }

                }
                return out.iterator();
            }

            /**
             * 专业字符串规范化
             * @param profession_name
             * @return
             */
            public String dealProfession_name(String profession_name) {
                if (profession_name == null || profession_name.length() >= 40)
                    return "";
                profession_name = profession_name.toLowerCase();
                profession_name = profession_name
                        .replaceAll("本科|硕士|研究生|中专|大专", "")
                        .replaceAll("（", "(")
                        .replaceAll("）", ")")
                        .replaceAll("，", ",")
                        .replaceAll("；", ";")
                        .replaceAll("\\(\\)", "")
                        .replaceAll("\\\\", "")
                        .replaceAll("\r|\n", "")
                        .replaceAll("\\.|•|/|&#x20|\\|;", " ")
                        .replaceAll("  ", " ");
                return profession_name.trim();
            }

            /**
             * 纠正一些 专业名称的错误 和 中英对照
             * @param profession_name
             * @return
             */
            public String Profession_nameMap(String profession_name) {
                if (professionMap.containsKey(profession_name))
                    return professionMap.get(profession_name);
                return profession_name;
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
//        positionSubPathRDD = positionSubPathRDD.filter(p -> p._1().getStage() > 10);
//        positionSubPathRDD.take(100).forEach(p -> System.out.println(p));
        JavaRDD<Document> out = positionSubPathRDD.map(new Function<Tuple2<PositionPathEntity, Integer>, Document>() {
            @Override
            public Document call(Tuple2<PositionPathEntity, Integer> v1) throws Exception {
                Document out = new Document();
                out.append("stage", v1._1().getStage());
                out.append("profession_name", v1._1().getProfession_name());
                out.append("pre_position", v1._1().getPre_postion());
                out.append("post_position", v1._1().getPost_postion());
                out.append("count", v1._2());
                return out;
            }
        });


        Map<String, String> writeOverrides = new HashMap<String, String>();
        writeOverrides.put("database", "resume_report");
        writeOverrides.put("collection", "position_path");
        writeOverrides.put("writeConcern.wTimeoutMS", "" + (1000 * 60 * 60 * 2));
        WriteConfig wc = WriteConfig.create(conf, writeOverrides);
        MongoSpark.save(out, wc);
        jsc.close();
    }
}
