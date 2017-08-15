package com.shulianxunying.utils.locationrecognizeutil.utils;

import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.Tuple5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by 19866 on 2017/6/14.
 */
public class ReadUniqueCityName {
    public static Tuple5<
            ArrayList<String>,
            HashMap<String, String>,
            HashMap<String, String>,
            HashMap<String, String>,
            HashMap<String, String>> readUniqueCityName() {
        String defaultCity = "";
        return readUniqueCityName(defaultCity);
    }

    public static Tuple5<
            ArrayList<String>,
            HashMap<String, String>,
            HashMap<String, String>,
            HashMap<String, String>,
            HashMap<String, String>> readUniqueCityName(String needRecognizeCities) {
        String[] cities = StringUtils.isNotEmpty(needRecognizeCities) ? needRecognizeCities.split(",") : "".split(",");
        HashSet<Tuple2<String, String>> english2Chinese = new HashSet<>();

        //从低级向高级推演Map
        HashSet<Tuple2<String, String>> distinguishCitySet = new HashSet<>();
        HashMap<String, String> distinguishCityMap = new HashMap<>();
        HashSet<Tuple2<String, String>> cityProvinceSet = new HashSet<>();
        HashMap<String, String> cityProvinceMap = new HashMap<>();
        HashSet<Tuple2<String, String>> provinceCountrySet = new HashSet<>();
        HashMap<String, String> provinceCountryMap = new HashMap<>();

        //英文转中文Map
        HashMap<String, String> english2ChineseMap = new HashMap<>();
        //最低一级地域名称
        HashSet<String> firstMatchSet = new HashSet<>();

        String path = "/国家城市唯一特征";
        InputStream inputStream = ReadUniqueCityName.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
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
                String[] splits = line.split("\t");
                int splitsLength = splits.length;
                String[] lastAreas = splits[splitsLength - 1].split(":");

                for (String area : lastAreas) {
                    if (area.length() > 1)
                        firstMatchSet.add(area);
                }
                if (splitsLength == 2) {
                    String[] countries = splits[1].split(":");
                    english2Chinese.add(new Tuple2<>(countries[1], countries[0]));
                    continue;
                }
                for (int index = splitsLength - 1; index > 1; index--) {
                    String[] current = splits[index].split(":");
                    String[] previous = splits[index - 1].split(":");
                    if (current.length == 2) {
                        english2Chinese.add(new Tuple2<>(current[1], current[0]));
                    }
                    if (previous.length == 2) {
                        english2Chinese.add(new Tuple2<>(previous[1], previous[1]));
                    }
                    for (int currentIndex = 0; currentIndex < current.length; currentIndex++) {
                        switch (index) {
                            case 4: {
                                distinguishCitySet.add(new Tuple2<>(current[currentIndex], previous[currentIndex]));
                                break;
                            }
                            case 3: {
                                cityProvinceSet.add(new Tuple2<>(current[currentIndex], previous[currentIndex]));
                                break;
                            }
                            case 2: {
                                provinceCountrySet.add(new Tuple2<>(current[currentIndex], previous[currentIndex]));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Tuple2<String, String> tuple2 : english2Chinese) {
            english2ChineseMap.put(tuple2._1, tuple2._2);
        }
        for (Tuple2<String, String> tuple2 : distinguishCitySet) {
            distinguishCityMap.put(tuple2._1, tuple2._2);
        }
        for (Tuple2<String, String> tuple2 : cityProvinceSet) {
            cityProvinceMap.put(tuple2._1, tuple2._2);
        }
        for (Tuple2<String, String> tuple2 : provinceCountrySet) {
            provinceCountryMap.put(tuple2._1, tuple2._2);
        }
        ArrayList<String> firstMatchList = new ArrayList<>();
        for (String city : firstMatchSet) {
            firstMatchList.add(city);
        }
        Collections.sort(firstMatchList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.length() == o2.length())
                    return 0;
                return o1.length() > o2.length() ? -1 : 1;
            }
        });
        return new Tuple5<>(firstMatchList, distinguishCityMap, cityProvinceMap, provinceCountryMap, english2ChineseMap);
    }

    public static void main(String[] args) {
        String out_cities = "北京,上海,广州,深圳,成都,杭州,武汉,天津,南京,重庆,西安,长沙,青岛,沈阳,大连,厦门,苏州,宁波,无锡";
        Tuple5<ArrayList<String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>> tuple = readUniqueCityName();
        for (String a : tuple._1()) {
            System.out.println(a);
        }
    }
}
