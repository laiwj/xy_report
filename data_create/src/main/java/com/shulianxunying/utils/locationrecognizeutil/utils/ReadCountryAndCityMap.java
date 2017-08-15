package com.shulianxunying.utils.locationrecognizeutil.utils;

import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import scala.Tuple5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 19866 on 2017/6/7.
 */
public class ReadCountryAndCityMap {
    public static void main(String[] args) {
        String out_cities = "北京,上海,广州,深圳,成都,杭州,武汉,天津,南京,重庆,西安,长沙,青岛,沈阳,大连,厦门,苏州,宁波,无锡";
        Tuple4<
                Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>>,
                Tuple2<LinkedHashMap<String, HashSet<String>>, LinkedHashMap<String, HashSet<String>>>,
                HashMap<String, String>,
                HashMap<String, HashSet<String>>>tuple = readCountryAndCityMap();
        Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>>tuple3 = tuple._1();
        for (Map.Entry<String,String>entry:tuple3._2().entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    public static Tuple4<
            Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>>,
            Tuple2<LinkedHashMap<String, HashSet<String>>, LinkedHashMap<String, HashSet<String>>>,
            HashMap<String, String>,
            HashMap<String, HashSet<String>>> readCountryAndCityMap() {
        return readCountryAndCityMap("");
    }

    //返回 国 省 市
    public static Tuple4<
            Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>>,
            Tuple2<LinkedHashMap<String, HashSet<String>>, LinkedHashMap<String, HashSet<String>>>,
            HashMap<String, String>,
            HashMap<String, HashSet<String>>> readCountryAndCityMap(String needRecognizeCities) {
        String[] cities = StringUtils.isNotEmpty(needRecognizeCities) ? needRecognizeCities.split(",") : "".split(",");

        String path = "/国家城市映射表";
        InputStream resourceAsStream = ReadCountryAndCityMap.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        HashSet<Tuple3<String, String, String>> invertedSet = new HashSet<>();
        HashSet<Tuple3<String, String, String>> positiveSet = new HashSet<>();
        HashSet<Tuple2<String, String>> english2Chinese = new HashSet<>();
        HashMap<String, String> english2ChineseMap = new HashMap<>();
        HashMap<String, HashSet<String>> cityAndDistinguishMap = new HashMap<>();
        String defaultLocationString = "unknown";
        try {
            while ((line = br.readLine()) != null) {

                if (cities.length != 0) {
                    Boolean isContinueFlag = false;
                    for (String c : cities) {
                        if (line.contains(c)) {
                            isContinueFlag = true;
                            break;
                        }
                    }
                    if (!isContinueFlag)
                        continue;
                }
                Tuple5<String, Tuple3<String, String, String>, Tuple3<String, String, String>, HashMap<String, String>, HashMap<String, String>> lineTuple = DealWithLocationLine.dealWithLocationLine(line);
                if (!lineTuple._1().equals("wrong")) {
                    if (lineTuple._1().equals("positive")) {
                        positiveSet.add(lineTuple._2());
                        positiveSet.add(lineTuple._3());
                    } else {
                        invertedSet.add(lineTuple._2());
                        invertedSet.add(lineTuple._3());
                    }
                    if (!lineTuple._4().isEmpty()) {
                        for (Map.Entry<String, String> cityEntry : lineTuple._4().entrySet()) {
                            HashSet distinguishSet = cityAndDistinguishMap.getOrDefault(cityEntry.getKey(), new HashSet<>());
                            distinguishSet.add(cityEntry.getValue());
                            cityAndDistinguishMap.put(cityEntry.getKey(), distinguishSet);
                        }
                    }
                    if (!lineTuple._5().isEmpty()) {
                        for (Map.Entry<String, String> english2ChineseEntry : lineTuple._5().entrySet()) {
                            english2Chinese.add(new Tuple2<>(english2ChineseEntry.getKey(), english2ChineseEntry.getValue()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>> positiveSequenceTuple = DealWithLocationLine.getPositiveSequenceTuple(positiveSet);

        Tuple2<LinkedHashMap<String, HashSet<String>>, LinkedHashMap<String, HashSet<String>>> invertedSequenceTuple = DealWithLocationLine.getInvertedSequence(invertedSet);

        for (Tuple2<String, String> tuple2 : english2Chinese) {
            english2ChineseMap.put(tuple2._1, tuple2._2);
        }
        return new Tuple4<>(positiveSequenceTuple, invertedSequenceTuple, english2ChineseMap, cityAndDistinguishMap);
    }


}
