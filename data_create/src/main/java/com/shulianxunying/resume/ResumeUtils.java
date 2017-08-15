package com.shulianxunying.resume;

import com.shulianxunying.utils.KeyValueUtils;
import com.shulianxunying.utils.WordSegment;
import com.shulianxunying.utils.locationrecognizeutil.SpecifyCityRecognize;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.Document;
import scala.Tuple2;
import scala.Tuple4;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shulianxunying.utils.WorkYearUtils.formatWorkYear;

/**
 * Created by SuChang on 2017/3/28 14:45.
 */
public class ResumeUtils implements Serializable {

    /**
     * 打全部的标签
     *
     * @param dimenResume
     * @return
     */
    public static JavaPairRDD<Document, ResumeType> resumeTag(JavaPairRDD<Document, ResumeType> dimenResume) {
        return resumeTag(dimenResume,
                Arrays.asList(
                        "gender",
                        "degree",
                        "birthday",
                        "work_year",
                        "expect_city",
                        "living",
                        "hometown",
                        "expect_salary",
                        "history_salary",
                        "keywords"
                ));
    }


    /**
     * 给简历打标签  List中的选项，选择打部分标签
     *
     * @param dimenResume
     * @return
     */
    public static JavaPairRDD<Document, ResumeType> resumeTag(JavaPairRDD<Document, ResumeType> dimenResume, List<String> steps) {
        // gender
        if (steps.contains("gender")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String gender = tuple2._1().getString("gender");
                    if (gender == null || gender.equals("")) {
                        tuple2._2().setGender("unknown");
                    } else {
                        tuple2._2().setGender(gender);
                    }
                    return tuple2;
                }
            });
        }
        // degree
        if (steps.contains("degree")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {

                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String degree = tuple2._1().getString("degree");
                    if (degree == null || degree.equals("")) {
                        ArrayList<Document> educationList = (ArrayList<Document>) tuple2._1().get("educationList");
                        if (educationList != null && educationList.size() > 0) {
                            for (Document doc : educationList) {
                                degree = doc.getString("degree");
                                if (degree != null && !degree.equals("")) {
                                    tuple2._2().setDegree(degree);
                                    return tuple2;
                                }
                            }
                            tuple2._2().setDegree("unknown");
                        }
                    } else {
                        tuple2._2().setDegree(degree);
                    }
                    return tuple2;
                }
            });
        }
        // birthday
        if (steps.contains("birthday")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String birthday = tuple2._1().getString("birthday");
                    String age = tuple2._1().getString("age");
                    if (StringUtils.isNotEmpty(birthday)) {
                        if (birthday.equals("未知")) {
                            tuple2._2().setAge("unknown");
                            return tuple2;
                        }
                        String s = "error";
                        try {
                            s = String.valueOf(getAge(birthday.trim()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                        tuple2._2().setAge(s);
                    } else if (age != null && !age.equals("")) {
                        tuple2._2().setAge(age);
                    } else {
                        tuple2._2().setAge("unknown");
                    }
                    return tuple2;
                }

                public SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
                public SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyy-MM");

                public int getAge(String dateOfBirth) throws ParseException, NumberFormatException, ArrayIndexOutOfBoundsException {
                    int length = dateOfBirth.length();
                    if (length == 10 || length == 9)
                        return getAge(yyyyMMdd.parse(dateOfBirth));
                    else if (length == 7 || length == 6)
                        return getAge(yyyyMM.parse(dateOfBirth));
                    else
                        return getAge(yyyyMMdd.parse(dateOfBirth));
                }

                /**
                 * 计算年龄
                 *
                 * @param dateOfBirth
                 * @return -1 则为 出生日期大于当前时间
                 */
                public int getAge(Date dateOfBirth) {
                    int age = 0;
                    Calendar born = Calendar.getInstance();
                    Calendar now = Calendar.getInstance();
                    if (dateOfBirth != null) {
                        now.setTime(new Date());
                        born.setTime(dateOfBirth);
                        if (born.after(now)) {
                            return -1;
                        }
                        age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                        if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
                            age -= 1;
                        }
                    }
                    return age;
                }
            });
        }
        // work_year
        if (steps.contains("work_year")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String work_year = tuple2._1().getString("work_year");
                    if (StringUtils.isNotEmpty(work_year)) {
                        if (work_year.startsWith("0")) {
                            tuple2._2().setWorkyear(formatWorkYear(String.valueOf("0")).split("_")[1]);
                        } else {
                            int index = work_year.indexOf("-");
                            int start = 0;
                            String regEx = "[^0-9]+";// 正则
                            Pattern p = Pattern.compile(regEx);
                            Matcher m = p.matcher(work_year);
                            String trim = m.replaceAll(" ").replaceAll("  ", " ").trim();
                            if (StringUtils.isNotEmpty(trim)) {
                                String[] split = trim.split(" ");
                                try {
                                    if (index > 0) {
                                        start = (Integer.parseInt(split[0]) + Integer.parseInt(split[1])) / 12 / 2;
                                    } else {
                                        start = Integer.parseInt(split[0]) / 12;
                                    }
                                    tuple2._2().setWorkyear(formatWorkYear(String.valueOf(start)).split("_")[1]);
                                } catch (Exception e) {
                                    tuple2._2().setWorkyear("unknown");
                                }
                            } else {
                                tuple2._2().setWorkyear("unknown");
                            }
                        }
                    } else
                        tuple2._2().setWorkyear("unknown");
                    return tuple2;
                }
            });
        }
        //city  期望工作城市
        if (steps.contains("expect_city")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                SpecifyCityRecognize specifyCityRecognize = new SpecifyCityRecognize();

                @Override
                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String expect_city = tuple2._1().getString("expect_city");
                    if (StringUtils.isNotEmpty(expect_city)) {
                        HashSet<Tuple4<String, String, String, String>> recognizedCity = specifyCityRecognize.locationRecognize(expect_city);

                        for (Tuple4<String, String, String, String> t : recognizedCity) {
                            tuple2._2.getExpect_city().add(new Area(t._1(), t._2(), t._3(), t._4()));
                        }
                    }
                    return tuple2;
                }
            });
        }
        // 居住的
        if (steps.contains("living")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                SpecifyCityRecognize specifyCityRecognize = new SpecifyCityRecognize();

                @Override
                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String living_city = tuple2._1().getString("living");
                    tuple2._2().setLiving_city(new HashSet<>());
                    if (StringUtils.isNotEmpty(living_city)) {
                        HashSet<Tuple4<String, String, String, String>> recognizedCity = specifyCityRecognize.locationRecognize(living_city);
                        for (Tuple4<String, String, String, String> t : recognizedCity) {
                            tuple2._2.getExpect_city().add(new Area(t._1(), t._2(), t._3(), t._4()));
                        }
                    }
                    return tuple2;
                }
            });
        }
        // 家乡
        if (steps.contains("hometown")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                SpecifyCityRecognize specifyCityRecognize = new SpecifyCityRecognize();

                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String hometown = tuple2._1().getString("hometown");
                    tuple2._2().setHometown_city(new HashSet<>());
                    if (StringUtils.isNotEmpty(hometown)) {
                        HashSet<Tuple4<String, String, String, String>> recognizedCity = specifyCityRecognize.locationRecognize(hometown);
                        for (Tuple4<String, String, String, String> t : recognizedCity) {
                            tuple2._2.getExpect_city().add(new Area(t._1(), t._2(), t._3(), t._4()));
                        }
                    }
                    return tuple2;
                }
            });
        }
        // salary 期望薪资
        if (steps.contains("expect_salary")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String expect_salary = tuple2._1().getString("expect_salary");
                    if (StringUtils.isNotEmpty(expect_salary)) {
                        if (expect_salary.indexOf("面议") >= 0) {
                            tuple2._2().setExpect_salary("unknown");
                            return tuple2;
                        }
                        String s = KeyValueUtils.dealSalary(expect_salary);
                        if (StringUtils.isNotEmpty(s)) {
                            if (s.indexOf("-") > 0) {
                                String[] split = s.split("-");
                                int salary = 0;
                                if (split.length >= 2) {
                                    if (Integer.parseInt(split[0]) == 0) {
                                        salary = Integer.parseInt(split[1]);
                                    } else if ((Integer.parseInt(split[1]) / Integer.parseInt(split[0]) >= 10)) {
                                        salary = Integer.parseInt(split[0]);
                                    } else {
                                        salary = (Integer.parseInt(split[0]) + Integer.parseInt(split[1])) / 2;
                                    }
                                } else if (split.length == 1)
                                    salary = Integer.parseInt(split[0]);
                                //使薪资小于30w
                                if (salary > 999999) {
                                    salary = Integer.parseInt((salary + "").substring(5));
                                } else if (salary > 299999) {
                                    salary = (int) (salary * 0.25);
                                }
                                tuple2._2().setExpect_salary("" + salary);
                            } else {
                                if (s.length() > 6) {
                                    s = s.substring(5);
                                }
                                tuple2._2().setExpect_salary(s);
                            }
                        } else {
                            tuple2._2().setExpect_salary("unknown");
                        }
                    } else {
                        tuple2._2().setExpect_salary("unknown");
                    }
                    return tuple2;
                }
            });
        }
        // 历史薪资
        if (steps.contains("history_salary")) {
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                    String last_enterprise_salary = tuple2._1().getString("last_enterprise_salary");
                    if (StringUtils.isNotEmpty(last_enterprise_salary)) {
                        if (last_enterprise_salary.indexOf("面议") >= 0) {
                            tuple2._2().setLast_salary("面议");
                            return tuple2;
                        }
                        String s = KeyValueUtils.dealSalary(last_enterprise_salary);
                        if (StringUtils.isNotEmpty(s)) {
                            if (s.indexOf("-") > 0) {
                                String[] split = s.split("-");
                                int salary = 0;
                                if (split.length >= 2) {
                                    if (Integer.parseInt(split[0]) == 0) {
                                        salary = Integer.parseInt(split[1]);
                                    } else if ((Integer.parseInt(split[1]) / Integer.parseInt(split[0]) >= 10)) {
                                        salary = Integer.parseInt(split[0]);
                                    } else {
                                        salary = (Integer.parseInt(split[0]) + Integer.parseInt(split[1])) / 2;
                                    }
                                } else if (split.length == 1)
                                    salary = Integer.parseInt(split[0]);
                                if (salary > 999999) {
                                    salary = Integer.parseInt((salary + "").substring(5));
                                } else if (salary > 299999) {
                                    salary = (int) (salary * 0.25);
                                }
                                tuple2._2().setLast_salary("" + salary);
                            } else {
                                if (s.length() > 6) {
                                    s = s.substring(5);
                                }
                                tuple2._2().setLast_salary(s);
                            }
                        } else
                            tuple2._2().setLast_salary("unknown");
                    } else {
                        ArrayList<Document> workExperienceList = (ArrayList<Document>) tuple2._1().get("workExperienceList");
                        if (workExperienceList != null && workExperienceList.size() > 0) {
                            for (Document workExperience : workExperienceList) {
                                String salary = workExperience.getString("salary");
                                if (StringUtils.isNotEmpty(salary)) {
                                    String s = KeyValueUtils.dealSalary(salary);
                                    if (StringUtils.isNotEmpty(s)) {
                                        if (s.indexOf("-") >= 0) {
                                            String[] split = s.split("-");
                                            int salary2 = 0;
                                            if (split.length >= 2)
                                                salary2 = (Integer.parseInt(split[0]) + Integer.parseInt(split[1])) / 2;
                                            else if (split.length == 1)
                                                salary2 = Integer.parseInt(split[0]);
                                            tuple2._2().setLast_salary("" + salary2);
                                        } else
                                            tuple2._2().setLast_salary(s);
                                        return tuple2;
                                    }
                                }
                            }
                            tuple2._2().setLast_salary("unknown");
                        } else {
                            tuple2._2().setLast_salary("unknown");
                        }
                    }
                    return tuple2;
                }
            });
        }
        if (steps.contains("keywords")) {
            //keywords
            dimenResume = dimenResume.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
                @Override
                public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> documentResumeTypeTuple2) throws Exception {
                    String self_introduction = (String) documentResumeTypeTuple2._1().getOrDefault("self_introduction", "");
                    Object skillList = documentResumeTypeTuple2._1().get("skillList");
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
                    ArrayList<Document> certificateList = (ArrayList<Document>) documentResumeTypeTuple2._1().get("certificateList");
                    if (certificateList == null)
                        certificateList = new ArrayList<>();
                    String certificate = "";
                    for (Document certificateDoc : certificateList) {
                        String certificateName = StringUtils.isEmpty(certificateDoc.getString("certificate_name")) ? "" : certificateDoc.getString("certificate_name");
                        if (!certificateName.equals(""))
                            certificate += certificateName + ";";
                    }
                    String segmentLine = self_introduction + skill + certificate;
                    ArrayList<Document> workExperienceList = (ArrayList<Document>) documentResumeTypeTuple2._1().get("workExperienceList");
                    if (workExperienceList != null && workExperienceList.size() > 0) {
                        Document work = workExperienceList.get(0);
                        segmentLine += work.getString("experience_desc");
                        if (workExperienceList.size() > 1) {
                            Document work1 = workExperienceList.get(1);
                            segmentLine += work1.getString("experience_desc");
                        }
                    }
                    HashMap<String, Integer> keyWords = new HashMap<>();
                    for (String keyword : WordSegment.queryWords(segmentLine)) {
                        keyWords.put(keyword, keyWords.getOrDefault(keyword, 0) + 1);
                    }
                    documentResumeTypeTuple2._2.setKeywords(keyWords);
                    return documentResumeTypeTuple2;
                }
            });
        }
        return dimenResume;
    }
}
