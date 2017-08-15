package com.shulianxunying.utils;

import com.shulianxunying.resume.Area;
import com.shulianxunying.resume.PositionFunc;
import com.shulianxunying.resume.ResumeCityFunc;
import com.shulianxunying.utils.locationrecognizeutil.SpecifyCityRecognize;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.bson.Document;
import scala.Tuple2;
import scala.Tuple4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 19866 on 2017/6/23.
 */
public class CityAnalysisUtils {
    public static JavaRDD<Document> analysisCount(JavaPairRDD<Document, ResumeCityFunc> dimenResume) {
        JavaPairRDD<String, Integer> tempRDD = dimenResume.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, String, Integer>() {
            @Override
            public Iterator<Tuple2<String, Integer>> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
                ArrayList<Tuple2<String, Integer>> out = new ArrayList<Tuple2<String, Integer>>();
                ResumeCityFunc resumeCityFunc = tuple2._2();
                HashSet<Area> city = resumeCityFunc.getLiving_city();
                city.remove(new Area("unknown", "unknown", "unknown", "unknown"));
                if (city.size() <= 0) {
                    city = resumeCityFunc.getExpect_city();
                }
                city.remove(new Area("unknown", "unknown", "unknown", "unknown"));
                if (city.size() <= 0) {
                    city = resumeCityFunc.getHometown_city();
                }
                for (Area s : city) {
                    String data = s.getCity() + "\t" + resumeCityFunc.getFunc() + "\t" + resumeCityFunc.getSecond_level() + "\t" + resumeCityFunc.getPosition() + "\t" + resumeCityFunc.getIndustry();
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
     * @param
     * @param
     * @return
     */
    public static JavaRDD<Document> analysisFlow(JavaPairRDD<Document, ResumeCityFunc> dimenResume, Broadcast<List<PositionFunc>> broadcast, SpecifyCityRecognize specifyCityRecognize) {
        JavaPairRDD<String, Integer> reduceByKey = dimenResume.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, String, Integer>() {
            private static final long serialVersionUID = 1L;

            public Iterator<Tuple2<String, Integer>> call(Tuple2<Document, ResumeCityFunc> tuple2) throws Exception {
                ArrayList<Tuple2<String, Integer>> out = new ArrayList<Tuple2<String, Integer>>();
                ResumeCityFunc resumeCityFunc = tuple2._2();
                HashSet<Area> living_city = resumeCityFunc.getLiving_city();
                if (living_city.size() <= 0)
                    living_city = resumeCityFunc.getHometown_city();
                ArrayList<Document> workExperienceList = (ArrayList<Document>) tuple2._1().get("workExperienceList");
                String enterprise_name = null;
                HashSet<Area> preCitys = new HashSet<>();
                if (workExperienceList != null && workExperienceList.size() > 2) {
                    String nowCompany = workExperienceList.get(0).getString("enterprise_name");
                    if (StringUtils.isNotEmpty(nowCompany)) {
                        for (Tuple4<String, String, String, String> city : specifyCityRecognize.locationRecognize(nowCompany)) {
                            living_city.add(new Area(city));
                        }
                    }
                    enterprise_name = workExperienceList.get(1).getString("enterprise_name");
                    if (StringUtils.isEmpty(enterprise_name) && workExperienceList.size() > 3) {
                        enterprise_name = workExperienceList.get(2).getString("enterprise_name");
                    }
                    if (StringUtils.isNotEmpty(enterprise_name)) {
                        for (Tuple4<String, String, String, String> city : specifyCityRecognize.locationRecognize(enterprise_name)) {
                            preCitys.add(new Area(city));
                        }
                    }
                } else if (workExperienceList != null && workExperienceList.size() == 1) {
                    String nowCompany = workExperienceList.get(0).getString("enterprise_name");
                    if (StringUtils.isNotEmpty(nowCompany)) {
                        for (Tuple4<String, String, String, String> city : specifyCityRecognize.locationRecognize(nowCompany)) {
                            living_city.add(new Area(city));
                        }
                    }
                }
                // todo 需要一份学校所在地的数据支撑
                String college_name = tuple2._1().getString("college_name");
                HashSet<Area> hometown_city = tuple2._2().getHometown_city();
                if (StringUtils.isNotEmpty(college_name)) {
                    for (Tuple4<String, String, String, String> city : specifyCityRecognize.locationRecognize(college_name)) {
                        preCitys.add(new Area(city));
                    }
                } else {
                    for (Area s : hometown_city)
                        preCitys.add(s);
                }


                living_city.remove(new Area("unknown", "unknown", "unknown", "unknown"));
                if (living_city.size() == 0)
                    living_city.add(new Area("unknown", "unknown", "unknown", "unknown"));
                if (preCitys.size() == 0)
                    preCitys.add(new Area("unknown", "unknown", "unknown", "unknown"));
                for (Area living : living_city) {
                    for (Area preCity : preCitys) {
                        if (!living.equals(preCity))
                            out.add(new Tuple2<>(preCity.getCity() + "\t" + living.getCity() + "\t" + tuple2._2().getFunc() + "\t" + tuple2._2().getSecond_level() + "\t" + tuple2._2().getPosition() + "\t" + resumeCityFunc.getIndustry(), 1));
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
}
