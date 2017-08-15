package com.shulianxunying.utils.locationrecognizeutil.utils;

import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.Tuple4;
import scala.Tuple5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 19866 on 2017/6/15.
 */
public class PriorRecognitionLocation {
    private static final String defaultLocation = "unknown";
    private static Tuple5<ArrayList<String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>> uniqueCity = ReadUniqueCityName.readUniqueCityName();
    private static ArrayList<String> matchLocationNameList = uniqueCity._1();
    private static HashMap<String, String> distinguishCityMap = uniqueCity._2();
    private static HashMap<String, String> cityAndProvinceMap = uniqueCity._3();
    private static HashMap<String, String> provinceAndCountryMap = uniqueCity._4();
    private static HashMap<String, String> english2ChineseMap = uniqueCity._5();



    public static Tuple2<HashSet<Tuple4<String, String, String, String>>, String> priorRecognize(String location) {
        HashSet<Tuple4<String, String, String, String>> resultSet = new HashSet<>();
        if (StringUtils.isEmpty(location)) {
            return new Tuple2<>(resultSet, location);
        }
        location = location.toLowerCase();
        for (String lastArea : matchLocationNameList) {
            Pattern pattern = Pattern.compile(lastArea.toLowerCase());
            Matcher matcher = pattern.matcher(location);
            if (matcher.find()) {
                location = location.replace(matcher.group(), "");
                lastArea = english2ChineseMap.getOrDefault(lastArea, lastArea);
                if (distinguishCityMap.containsKey(lastArea)) {
                    String city = distinguishCityMap.getOrDefault(lastArea, defaultLocation);
                    String province = cityAndProvinceMap.getOrDefault(city, defaultLocation);
                    String country = provinceAndCountryMap.getOrDefault(province, defaultLocation);
                    resultSet.add(new Tuple4<>(lastArea, city, province, country));
                } else if (cityAndProvinceMap.containsKey(lastArea)) {
                    String province = cityAndProvinceMap.getOrDefault(lastArea, defaultLocation);
                    String country = provinceAndCountryMap.getOrDefault(province, defaultLocation);
                    resultSet.add(new Tuple4<>(defaultLocation, lastArea, province, country));
                } else if (provinceAndCountryMap.containsKey(lastArea)) {
                    String country = provinceAndCountryMap.getOrDefault(lastArea, defaultLocation);
                    resultSet.add(new Tuple4<>(defaultLocation, defaultLocation, lastArea, country));
                } else {
                    resultSet.add(new Tuple4<>(defaultLocation, defaultLocation, defaultLocation, lastArea));
                }
            }
        }
        return new Tuple2<>(resultSet, location);
    }
}
