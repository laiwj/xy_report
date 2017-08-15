package com.shulianxunying;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shulianxunying.utils.WorkYearUtils.formatWorkYear;

/**
 * Created by 19866 on 2017/6/26.
 */
public class Test {
    public static void callTest(String tuple2) throws Exception {
        String work_year = tuple2;
        if (StringUtils.isNotEmpty(work_year)) {
            if (work_year.startsWith("0")) {
                System.out.println(formatWorkYear(String.valueOf("0")).split("_")[1]);
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
                        System.out.println(formatWorkYear(String.valueOf(start)).split("_")[1]);
                    } catch (Exception e) {
                        System.out.println("unknown");
                    }
                } else {
                    System.out.println("unknown");
                }
            }
        } else
            System.out.println("unknown");
    }


    public static void main(String[] args) throws Exception {


        System.out.println("4".compareTo("3333333333333333"));
//        callTest("0-996");
//        SparkConf conf = new SparkConf()
//                .setAppName("Test")
//                .setMaster("local[*]");
//        JavaSparkContext jsc = new JavaSparkContext(conf);
//        List<Tuple2<String, Tuple2<String, Integer>>> pairs = Lists.newArrayList(
//                new Tuple2("a", new Tuple2<>("this", 1)),
//                new Tuple2("a", new Tuple2<>("that", 5)),
//                new Tuple2("b", new Tuple2<>("that", 10)),
//                new Tuple2("b", new Tuple2<>("this", 116)),
//                new Tuple2("c", new Tuple2<>("my", 2)),
//                new Tuple2("c", new Tuple2<>("your", 5)));
//        JavaPairRDD<String, Tuple2<String, Integer>> p = jsc.parallelizePairs(pairs);
//        JavaPairRDD<String, Iterator<Tuple2<String, Integer>>> reduceByKy;
//        String a = "1";

//        Integer b = Integer.parseInt(a);
    }

}
