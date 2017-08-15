package com.shulianxunying.resume;

import com.shulianxunying.utils.KeyValueUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.Document;
import scala.Tuple2;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SuChang on 2017/3/28 14:45.
 */
public class ResumeUtils implements Serializable {


    public static JavaPairRDD<Document, ResumeType> resumeTag(JavaPairRDD<Document, ResumeType> dimenResuem) {
        String out_citys = "北京,上海,广州,深圳,成都,杭州,武汉,天津,南京,重庆,西安,长沙,青岛,沈阳,大连,厦门,苏州,宁波,无锡";
        return resumeTag(dimenResuem, out_citys);
    }

    /**
     * 给简历打标签
     *
     * @param dimenResuem
     * @return
     */
    public static JavaPairRDD<Document, ResumeType> resumeTag(JavaPairRDD<Document, ResumeType> dimenResuem, String out_citys) {
        // gender
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                String gender = tuple2._1().getString("gender");
                if (gender == null || gender.equals("")) {
                    tuple2._2().setGender("unknow");
                } else {
                    tuple2._2().setGender(gender);
                }
                return tuple2;
            }
        });
        // degree
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {

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
                        tuple2._2().setDegree("unknow");
                    }
                } else {
                    tuple2._2().setDegree(degree);
                }
                return tuple2;
            }
        });
        // birthday
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                String birthday = tuple2._1().getString("birthday");
                String age = tuple2._1().getString("age");
                if (StringUtils.isNotEmpty(birthday)) {
                    if (birthday.equals("未知")) {
                        tuple2._2().setAge("unknow");
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
                    tuple2._2().setAge("unknow");
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
        // work_year
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                String work_year = tuple2._1().getString("work_year");
                if (StringUtils.isNotEmpty(work_year)) {
                    if (work_year.startsWith("0")) {
                        tuple2._2().setWorkyear("0");
                    } else {
                        int index = work_year.indexOf("-");
                        int start = 0;
                        String regEx = "[^0-9]+";// 正则
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(work_year);
                        String trim = m.replaceAll(" ").trim();
                        if (StringUtils.isNotEmpty(trim)) {
                            String[] split = trim.split(" ");
                            try {
                                if (index > 0) {
                                    start = (Integer.parseInt(split[0]) + Integer.parseInt(split[1])) / 12;
                                } else {
                                    start = Integer.parseInt(split[0]) / 12;
                                }
                                tuple2._2().setWorkyear(String.valueOf(start));
                            } catch (Exception e) {
                                tuple2._2().setWorkyear("error");
                            }
                        } else {
                            tuple2._2().setWorkyear("error");
                        }
                    }
                } else
                    tuple2._2().setWorkyear("unknow");
                return tuple2;
            }
        });
        //city  期望工作城市
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            private final String[] citys = out_citys.split(",");

            public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                String expect_city = tuple2._1().getString("expect_city");
                tuple2._2().setExpect_city(new HashSet<String>());
                if (StringUtils.isNotEmpty(expect_city)) {
                    for (String city : citys) {
                        int i = expect_city.indexOf(city);
                        if (i >= 0)
                            tuple2._2().getExpect_city().add(city);
                    }
                    if (tuple2._2().getExpect_city().size() == 0)
                        tuple2._2().getExpect_city().add("unknow");
                } else {
                    tuple2._2().getExpect_city().add("unknow");
                }
                return tuple2;
            }
        });
        // 居住的
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            private final String[] citys = out_citys.split(",");

            public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                String living_city = tuple2._1().getString("living");
                tuple2._2().setLiving_city(new HashSet<String>());
                if (StringUtils.isNotEmpty(living_city)) {
                    for (String city : citys) {
                        int i = living_city.indexOf(city);
                        if (i >= 0)
                            tuple2._2().getLiving_city().add(city);
                    }
                    if (tuple2._2().getLiving_city().size() == 0)
                        tuple2._2().getLiving_city().add("unknow");
                } else {
                    tuple2._2().getLiving_city().add("unknow");
                }
                return tuple2;
            }
        });
        // 家乡
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            private final String[] citys = out_citys.split(",");

            public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                String hometown = tuple2._1().getString("hometown");
                tuple2._2().setHometown_city(new HashSet<String>());
                if (StringUtils.isNotEmpty(hometown)) {
                    for (String city : citys) {
                        int i = hometown.indexOf(city);
                        if (i >= 0)
                            tuple2._2().getHometown_city().add(city);
                    }
                    if (tuple2._2().getHometown_city().size() == 0)
                        tuple2._2().getHometown_city().add("unknow");
                } else {
                    tuple2._2().getHometown_city().add("unknow");
                }
                return tuple2;
            }
        });
        // salary 期望薪资
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                String expect_salary = tuple2._1().getString("expect_salary");
                if (StringUtils.isNotEmpty(expect_salary)) {
                    if (expect_salary.indexOf("面议") >= 0) {
                        tuple2._2().setExpect_salary("面议");
                        return tuple2;
                    }
                    String s = KeyValueUtils.dealSalary(expect_salary);
                    if (StringUtils.isNotEmpty(s)) {
                        if (s.indexOf("-") >= 0) {
                            String[] split = s.split("-");
                            int salary = 0;
                            if(split.length >= 2)
                                salary  = (Integer.parseInt(split[0]) + Integer.parseInt(split[1])) / 2;
                            else if(split.length == 1)
                                salary = Integer.parseInt(split[0]);
                            tuple2._2().setExpect_salary("" + salary);
                        } else
                            tuple2._2().setExpect_salary(s);
                    } else {
                        tuple2._2().setExpect_salary("error");
                    }
                } else {
                    tuple2._2().setExpect_salary("unknow");
                }
                return tuple2;
            }
        });
        // 历史薪资
        dimenResuem = dimenResuem.mapToPair(new PairFunction<Tuple2<Document, ResumeType>, Document, ResumeType>() {
            public Tuple2<Document, ResumeType> call(Tuple2<Document, ResumeType> tuple2) throws Exception {
                String last_enterprise_salary = tuple2._1().getString("last_enterprise_salary");
                if (StringUtils.isNotEmpty(last_enterprise_salary)) {
                    if (last_enterprise_salary.indexOf("面议") >= 0) {
                        tuple2._2().setLast_salary("面议");
                        return tuple2;
                    }
                    String s = KeyValueUtils.dealSalary(last_enterprise_salary);
                    if (StringUtils.isNotEmpty(s)) {
                        if (s.indexOf("-") >= 0) {
                            String[] split = s.split("-");
                            int salary = 0;
                            if(split.length >= 2)
                                salary  = (Integer.parseInt(split[0]) + Integer.parseInt(split[1])) / 2;
                            else if(split.length == 1)
                                salary = Integer.parseInt(split[0]);
                            tuple2._2().setLast_salary("" + salary);
                        } else
                            tuple2._2().setLast_salary(s);
                    } else {
                        tuple2._2().setLast_salary("error");
                    }
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
                                        if(split.length >= 2)
                                            salary2  = (Integer.parseInt(split[0]) + Integer.parseInt(split[1])) / 2;
                                        else if(split.length == 1)
                                            salary2 = Integer.parseInt(split[0]);
                                        tuple2._2().setLast_salary("" + salary2);
                                    } else
                                        tuple2._2().setLast_salary(s);
                                    return tuple2;
                                }
                            }
                        }
                        tuple2._2().setLast_salary("unknow");
                    } else {
                        tuple2._2().setLast_salary("unknow");
                    }
                }
                return tuple2;
            }
        });

        return dimenResuem;
    }

}
