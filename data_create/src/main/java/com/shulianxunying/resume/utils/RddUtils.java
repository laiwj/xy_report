package com.shulianxunying.resume.utils;

import com.shulianxunying.resume.Area;
import com.shulianxunying.resume.CompanyKey;
import com.shulianxunying.resume.PositionFunc;
import com.shulianxunying.resume.ResumeCityFunc;
import com.shulianxunying.utils.locationrecognizeutil.SpecifyCityRecognize;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.bson.Document;
import scala.Tuple2;
import scala.Tuple4;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 19866 on 2017/6/23.
 */
public class RddUtils {
    public static JavaRDD<CompanyKey> readCompanyInfoRdd(JavaRDD<Document> company) {
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
        return companyRdd;
    }

    public static JavaRDD<Document> filterByDate(JavaRDD<Document> resumeMongoRDD, Date start_time, Date end_time) {
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
        return resumeMongoRDD;
    }

    public static JavaPairRDD<Document, ResumeCityFunc> tagResume(JavaPairRDD<Document, ResumeCityFunc> dimenResume,
                                                                  Broadcast<List<CompanyKey>> companyBroadcast,
                                                                  Broadcast<List<PositionFunc>> broadcast,
                                                                  SpecifyCityRecognize specifyCityRecognize) {
        dimenResume.flatMapToPair(new PairFlatMapFunction<Tuple2<Document, ResumeCityFunc>, Document, ResumeCityFunc>() {
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
        return dimenResume;
    }
}
