package com.shulianxunying.resume;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.tresata.spark.sorted.api.java.GroupSorted;
import org.apache.commons.collections.IteratorUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.Document;
import scala.Tuple2;

import java.util.*;

import static com.shulianxunying.utils.AgeUtils.formatAge;
import static com.shulianxunying.utils.DegreeUtils.formatDegree;

/**
 * Created by 19866 on 2017/6/20.
 */
public class ResumeGroupAndStatistics {

    public static JavaRDD<Document> resumeGroupAndStatistics(JavaPairRDD<String, ResumeType> javaPairRDD) {
        //不含p 标签 分组
        JavaPairRDD<String, Iterable<ResumeType>> groupByTag = javaPairRDD.groupByKey();
        //分组后打p标签
        JavaPairRDD<String, ResumeType> resumeTypeWithLevel = groupByTag.flatMapToPair(new PairFlatMapFunction<Tuple2<String, Iterable<ResumeType>>, String, ResumeType>() {
            @Override
            public Iterator<Tuple2<String, ResumeType>> call(Tuple2<String, Iterable<ResumeType>> stringIterableTuple2) throws Exception {
                Iterator<ResumeType> iterator = stringIterableTuple2._2.iterator();
                List<ResumeType> list = IteratorUtils.toList(iterator);
                Collections.sort(list, new Comparator<ResumeType>() {
                    @Override
                    public int compare(ResumeType o1, ResumeType o2) {
                        String salary1 = o1.getExpect_salary().trim();
                        String salary2 = o2.getExpect_salary().trim();
                        if (salary1.length() > salary2.length()) {
                            return 1;
                        } else if (salary1.length() == salary2.length()) {
                            return salary1.compareToIgnoreCase(salary2);
                        } else
                            return -1;
                    }
                });
                int size = list.size();
                Double p25 = (size - 1) * 0.25;
                Double p50 = (size - 1) * 0.5;
                Double p75 = (size - 1) * 0.75;
                String p25MinSalary = size > 0 ? list.get(0).getExpect_salary() : "unknown";
                String p25MaxSalary = size > 0 ? list.get(p25.intValue()).getExpect_salary() : "unknown";
                String p50MinSalary = size > 1 ? list.get(p25.intValue() + 1).getExpect_salary() : "unknown";
                String p50MaxSalary = list.get(p50.intValue()).getExpect_salary();
                String p75MinSalary = size > 1 ? list.get(p50.intValue() + 1).getExpect_salary() : "unknown";
                String p75MaxSalary = list.get(p75.intValue()).getExpect_salary();
                String p100MinSalary = size > 1 ? list.get(p75.intValue() + 1).getExpect_salary() : "unknown";
                String p100MaxSalary = size > 1 ? list.get(size - 1).getExpect_salary() : "unknown";
                for (int index = 0; index < size; index++) {
                    if (index <= size * 0.25) {
                        list.get(index).setLevel("p25");
                        list.get(index).setMax_salary(p25MaxSalary);
                        list.get(index).setMin_salary(p25MinSalary);
                    } else if (index <= size * 0.5) {
                        list.get(index).setLevel("p50");
                        list.get(index).setMax_salary(p50MaxSalary);
                        list.get(index).setMin_salary(p50MinSalary);
                    } else if (index <= size * 0.75) {
                        list.get(index).setLevel("p75");
                        list.get(index).setMax_salary(p75MaxSalary);
                        list.get(index).setMin_salary(p75MinSalary);
                    } else {
                        list.get(index).setLevel("p100");
                        list.get(index).setMax_salary(p100MaxSalary);
                        list.get(index).setMin_salary(p100MinSalary);
                    }
                }

                ArrayList<Tuple2<String, ResumeType>> resultList = new ArrayList<>();
                for (ResumeType resumeType : list) {
                    resultList.add(new Tuple2<>(makeUpLevel(stringIterableTuple2._1(), resumeType.getLevel()), resumeType));
                }
                return resultList.iterator();
            }

            public String makeUpLevel(String oldString, String level) {
                String[] splits = oldString.split("~");
                String groupString;
                if (splits.length == 3) {
                    groupString = oldString + "~" + level;
                } else {
                    groupString = splits[0] + "~" + splits[1] + "~" + splits[2] + "~" + level;
                    for (int index = 3; index < splits.length; index++) {
                        groupString += "~" + splits[index];
                    }
                }
                return groupString;
            }
        });
        //组装 keyword integer Rdd
        JavaPairRDD<String, Integer> keyWordCountRdd = resumeTypeWithLevel.flatMapToPair(new PairFlatMapFunction<Tuple2<String, ResumeType>, String, Integer>() {
            @Override
            public Iterator<Tuple2<String, Integer>> call(Tuple2<String, ResumeType> stringResumeTypeTuple2) throws Exception {
                HashMap<String, Integer> keyWords = stringResumeTypeTuple2._2.getKeywords();
                ArrayList<Tuple2<String, Integer>> resultList = new ArrayList<>();
                for (String key : keyWords.keySet()) {
                    resultList.add(new Tuple2<>(stringResumeTypeTuple2._1() + "###" + key, keyWords.get(key)));
                }
                return resultList.iterator();
            }
        });
        //统计 keyword count
        JavaPairRDD<String, Integer> reducedKeywordRdd = keyWordCountRdd.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        //关键词按 职位 薪资 行业 等需求分组
        JavaPairRDD<String, Tuple2<String, Integer>> groupStrAndWordCountRdd = reducedKeywordRdd.mapToPair(new PairFunction<Tuple2<String, Integer>, String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Tuple2<String, Integer>> call(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
                String[] splits = stringIntegerTuple2._1.split("###");
                return new Tuple2<>(splits[0], new Tuple2<>(splits[1], stringIntegerTuple2._2()));
            }
        });
        //
        GroupSorted<String, Tuple2<String, Integer>> gs = new GroupSorted(groupStrAndWordCountRdd, groupStrAndWordCountRdd.getNumPartitions());

        GroupSorted<String, Set<Tuple2<String, Integer>>> sets = gs.mapStreamByKey(new Function<Iterator<Tuple2<String, Integer>>, Iterator<Set<Tuple2<String, Integer>>>>() {
            @Override
            public Iterator<Set<Tuple2<String, Integer>>> call(Iterator<Tuple2<String, Integer>> v1) throws Exception {
                return Iterators.singletonIterator((Set<Tuple2<String, Integer>>) Sets.newHashSet(v1));
            }
        });
        JavaPairRDD<String, Set<Tuple2<String, Integer>>> testGroupedAndSortedKeyword = sets.mapToPair(new PairFunction<Tuple2<String, Set<Tuple2<String, Integer>>>, String, Set<Tuple2<String, Integer>>>() {
            @Override
            public Tuple2<String, Set<Tuple2<String, Integer>>> call(Tuple2<String, Set<Tuple2<String, Integer>>> stringSetTuple2) throws Exception {
                Set<Tuple2<String, Integer>> resSet;
                if (stringSetTuple2._2().size() <= 1000) {
                    resSet = stringSetTuple2._2();
                } else {
                    HashSet<Tuple2<String, Integer>> hashSet = new HashSet<>();
                    Iterator<Tuple2<String, Integer>> iterator = stringSetTuple2._2().iterator();
                    int count = 0;
                    int max = (stringSetTuple2._2.size() - 1000);
                    while (iterator.hasNext()) {
                        count++;
                        if (count >= max) {
                            hashSet.add(iterator.next());
                        }
                    }
                    resSet = hashSet;
                }
                return new Tuple2<>(stringSetTuple2._1(), resSet);
            }
        });
        //按含p标签的key分组
        JavaPairRDD<String, Iterable<ResumeType>> resumeGroupByKeyWithLevel = resumeTypeWithLevel.groupByKey();
        //对各组分别统计相关字段，返回Document
        JavaPairRDD<String, Document> documentJavaPairRDD = resumeGroupByKeyWithLevel.mapValues(new Function<Iterable<ResumeType>, Document>() {
            @Override

            public Document call(Iterable<ResumeType> v1) throws Exception {
                Document resultDoc = new Document();
                Map<String, Integer> tagMap = new HashMap<>();
                ArrayList<Document> keywordList = new ArrayList<>();
                ArrayList<Document> tagList = new ArrayList<>();
                tagMap.put("gender_male", 0);
                tagMap.put("gender_female", 0);
                tagMap.put("gender_unknown", 0);
                tagMap.put("degree_Bachelor", 0);
                tagMap.put("degree_Master", 0);
                tagMap.put("degree_Diploma", 0);
                tagMap.put("degree_Doctor", 0);
                tagMap.put("degree_Senior", 0);
                tagMap.put("degree_Junior", 0);
                tagMap.put("degree_unknown", 0);
                tagMap.put("age_18-25", 0);
                tagMap.put("age_25-30", 0);
                tagMap.put("age_30-35", 0);
                tagMap.put("age_35-40", 0);
                tagMap.put("age_40+", 0);
                tagMap.put("age_unknown", 0);
                tagMap.put("workyear_0-3", 0);
                tagMap.put("workyear_3-5", 0);
                tagMap.put("workyear_5-8", 0);
                tagMap.put("workyear_8-12", 0);
                tagMap.put("workyear_12+", 0);
                tagMap.put("workyear_unknown", 0);
                Integer count = 0;
                String maxSalary = "unknown";
                String minSalary = "unknown";
                Iterator<ResumeType> iterator = v1.iterator();
                while (iterator.hasNext()) {
                    count++;
                    ResumeType type = iterator.next();
                    if (count == 1) {
                        maxSalary = type.getMax_salary();
                        minSalary = type.getMin_salary();
                    }
                    String sex = type.getGender();
                    String gender = "";
                    if (sex.equals("男"))
                        gender = "gender_male";
                    else if (sex.equals("女"))
                        gender = "gender_female";
                    else
                        gender = "gender_unknown";
                    tagMap.put(gender, tagMap.getOrDefault(gender, 0) + 1);
                    String degree = formatDegree(type.getDegree());
                    tagMap.put(degree, tagMap.getOrDefault(degree, 0) + 1);
                    String age = formatAge(type.getAge());
                    tagMap.put(age, tagMap.getOrDefault(age, 0) + 1);
                    String workYear = "workyear_" + type.getWorkyear();
                    tagMap.put(workYear, tagMap.getOrDefault(workYear, 0) + 1);
                }
                for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
                    Document map = new Document();
                    if (entry.getKey().contains("gender_male")) {
                        map.put("tag", "男");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("gender_female")) {
                        map.put("tag", "女");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("gender_unknown")) {
                        map.put("tag", "性别unknown");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("degree_Bachelor")) {
                        map.put("tag", "本科");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("degree_Master")) {
                        map.put("tag", "硕士");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("degree_Diploma")) {
                        map.put("tag", "大专");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("degree_Doctor")) {
                        map.put("tag", "博士");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("degree_Senior")) {
                        map.put("tag", "高中");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("degree_Junior")) {
                        map.put("tag", "初中及以下");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("degree_unknown")) {
                        map.put("tag", "学历unknown");
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("age_")) {
                        if (entry.getKey().contains("unknown"))
                            map.put("tag", "年龄unknown");
                        else
                            map.put("tag", entry.getKey().split("_")[1]);
                        map.put("count", entry.getValue());
                        tagList.add(map);
                        continue;
                    }
                    if (entry.getKey().contains("workyear")) {
                        if (entry.getKey().contains("unknown"))
                            map.put("tag", "经历unknown");
                        else
                            map.put("tag", entry.getKey().split("_")[1]);
                        map.put("count", entry.getValue());
                        tagList.add(map);
                    }
                }

                resultDoc.append("tag_count", tagList);
                try {
                    resultDoc.append("max_salary", Integer.parseInt(maxSalary));

                } catch (NumberFormatException e) {
                    resultDoc.append("max_salary", 0);
                }
                try {
                    resultDoc.append("min_salary", Integer.parseInt(minSalary));

                } catch (NumberFormatException e) {
                    resultDoc.append("min_salary", 0);
                }
                resultDoc.append("keyWordCount", keywordList);
                resultDoc.append("count", count);
                return resultDoc;
            }
        });
        JavaPairRDD<String, Tuple2<Document, Set<Tuple2<String, Integer>>>> docWithKeyWord = documentJavaPairRDD.join(testGroupedAndSortedKeyword);
        JavaRDD<Document> outWithKeyWord = docWithKeyWord.map(new Function<Tuple2<String, Tuple2<Document, Set<Tuple2<String, Integer>>>>, Document>() {
            @Override
            public Document call(Tuple2<String, Tuple2<Document, Set<Tuple2<String, Integer>>>> v1) throws Exception {
                Document resultDoc = v1._2()._1;
                String[] splits = v1._1.split("~");
                resultDoc.append("type", splits[0]);
                resultDoc.append("position", splits[1]);
                resultDoc.append("func", splits[2]);
                resultDoc.append("p", splits[3]);
                ArrayList<Document> keywordList = new ArrayList<>();
                Iterator<Tuple2<String, Integer>> iterator = v1._2()._2().iterator();
                while (iterator.hasNext()) {
                    Tuple2<String, Integer> tuple2 = iterator.next();
                    Document doc = new Document();
                    doc.append("keyword", tuple2._1());
                    doc.append("count", tuple2._2());
                    keywordList.add(doc);
                }
                resultDoc.append("keyWordCount", keywordList);
                switch (splits[0]) {
                    case "position_only": {
                        resultDoc.append("work_year", "");
                        resultDoc.append("city", "");
                        resultDoc.append("industry", "");
                        break;
                    }
                    case "position_and_industry": {
                        resultDoc.append("work_year", "");
                        resultDoc.append("city", "");
                        resultDoc.append("industry", splits[4]);
                        break;
                    }
                    case "position_and_work_year": {
                        resultDoc.append("work_year", splits[4]);
                        resultDoc.append("city", "");
                        resultDoc.append("industry", "");
                        break;
                    }
                    case "position_and_city": {
                        resultDoc.append("work_year", "");
                        resultDoc.append("city", splits[4]);
                        resultDoc.append("industry", "");
                        break;
                    }
                    case "position_and_industry_and_city": {
                        resultDoc.append("work_year", "");
                        resultDoc.append("city", splits[5]);
                        resultDoc.append("industry", splits[4]);
                        break;
                    }
                    case "position_and_work_year_and_city": {
                        resultDoc.append("work_year", splits[4]);
                        resultDoc.append("city", splits[5]);
                        resultDoc.append("industry", "");
                        break;
                    }
                    case "position_and_industry_and_work_year": {
                        resultDoc.append("work_year", splits[5]);
                        resultDoc.append("city", "");
                        resultDoc.append("industry", splits[4]);
                        break;
                    }
                    case "position_and_industry_and__work_year_and_city": {
                        resultDoc.append("work_year", splits[5]);
                        resultDoc.append("city", splits[6]);
                        resultDoc.append("industry", splits[4]);
                        break;
                    }
                }
                return resultDoc;
            }
        });
        return outWithKeyWord;
    }
}
