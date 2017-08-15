package com.shulianxunying.utils.locationrecognizeutil.utils;

import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple5;

import java.util.*;

/**
 * Created by 19866 on 2017/6/14.
 */
public class DealWithLocationLine {

    public static void main(String[] args) {
        System.out.println(dealWithLocationLine("-\t阿根廷:Argentina\t巴拉那:Parana").toString());
    }
    private static Comparator comparator = new Comparator<Tuple2<String, String>>() {
        @Override
        public int compare(Tuple2<String, String> o1, Tuple2<String, String> o2) {
            if (o1._1().length() > o2._1().length())
                return -1;
            else if (o1._1().length() < o2._1().length())
                return 1;
            else
                return 0;
        }
    };
    private static Comparator comparatorTuple3 = new Comparator<Tuple3<String, String, String>>() {
        @Override
        public int compare(Tuple3<String, String, String> o1, Tuple3<String, String, String> o2) {
            if (o1._1().length() > o2._1().length())
                return -1;
            else if (o1._1().length() < o2._1().length())
                return 1;
            else
                return 0;
        }
    };

    public static Tuple5<
            String,
            Tuple3<String, String, String>,
            Tuple3<String, String, String>,
            HashMap<String, String>,
            HashMap<String, String>> dealWithLocationLine(String line) {
        String defaultLocationString = "unknown";
        String[] split = line.split("\t");
        Tuple3<String, String, String> positiveSeqChineseLocationTuple = new Tuple3<>(defaultLocationString, defaultLocationString, defaultLocationString);
        Tuple3<String, String, String> invertedSeqChineseLocationTuple = new Tuple3<>(defaultLocationString, defaultLocationString, defaultLocationString);
        Tuple3<String, String, String> positiveSeqEnglishLocationTuple = new Tuple3<>(defaultLocationString, defaultLocationString, defaultLocationString);
        Tuple3<String, String, String> invertedSeqEnglishLocationTuple = new Tuple3<>(defaultLocationString, defaultLocationString, defaultLocationString);
        HashMap<String, String> english2Chinese = new HashMap<>();
        HashMap<String, String> cityAndDistinguish = new HashMap<>();

        if (split.length == 5) {
            String[] countryArray = split[1].split(":");
            String[] provinceArray = split[2].split(":");
            String[] cityArray = split[3].split(":");
            String[] distinguishArray = split[4].split(":");
            if (split[0].contains("-")) {
                //过滤掉单字城市名
                if (provinceArray[0].length() > 1 && cityArray[0].length() > 1) {
                    positiveSeqChineseLocationTuple = new Tuple3<>(countryArray[0], provinceArray[0], cityArray[0]);
                }
                if (cityArray.length >= 2) {
                    positiveSeqEnglishLocationTuple = new Tuple3<>(countryArray[1], provinceArray[1], cityArray[1]);
                    english2Chinese.put(cityArray[1], cityArray[0]);
                }
                english2Chinese.put(provinceArray[1], provinceArray[0]);
                english2Chinese.put(countryArray[1], countryArray[0]);
                if (distinguishArray.length == 2) {
                    if (StringUtils.isNotEmpty(distinguishArray[1])) {
                        english2Chinese.put(distinguishArray[1], distinguishArray[0]);
                    }
                }
                for (int index = 0; index < distinguishArray.length; index++) {
                    cityAndDistinguish.put(cityArray[index], distinguishArray[index]);
                }
                return new Tuple5<>("positive", positiveSeqChineseLocationTuple, positiveSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
            } else {
                if (provinceArray[0].length() > 1 && cityArray[0].length() > 1) {
                    invertedSeqChineseLocationTuple = new Tuple3<>(countryArray[0], provinceArray[0], cityArray[0]);
                }
                invertedSeqEnglishLocationTuple = new Tuple3<>(countryArray[1], provinceArray[1], cityArray[1]);
                english2Chinese.put(provinceArray[1], provinceArray[0]);
                english2Chinese.put(countryArray[1], countryArray[0]);
                if (cityArray.length >= 2)
                    english2Chinese.put(cityArray[1], cityArray[0]);
                for (int index = 0; index < distinguishArray.length; index++) {
                    cityAndDistinguish.put(cityArray[index], distinguishArray[index]);
                }
                return new Tuple5<>("inverted", invertedSeqChineseLocationTuple, invertedSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
            }
        } else if (split.length == 4) {
            String[] countryArray = split[1].split(":");
            String[] provinceArray = split[2].split(":");
            String[] cityArray = split[3].split(":");

            if (split[0].contains("-")) {
                if (provinceArray[0].length() > 1 && cityArray[0].length() > 1) {
                    positiveSeqChineseLocationTuple = new Tuple3<>(countryArray[0], provinceArray[0], cityArray[0]);
                }
                positiveSeqEnglishLocationTuple = new Tuple3<>(countryArray[1], provinceArray[1], cityArray[1]);
                english2Chinese.put(provinceArray[1], provinceArray[0]);
                english2Chinese.put(countryArray[1], countryArray[0]);
                english2Chinese.put(cityArray[1], cityArray[0]);
                return new Tuple5<>("positive", positiveSeqChineseLocationTuple, positiveSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
            } else {
                if (provinceArray[0].length() > 1 && cityArray[0].length() > 1) {
                    invertedSeqChineseLocationTuple = new Tuple3<>(countryArray[0], provinceArray[0], cityArray[0]);
                }
                invertedSeqEnglishLocationTuple = new Tuple3<>(countryArray[1], provinceArray[1], cityArray[1]);
                english2Chinese.put(countryArray[1], countryArray[0]);
                english2Chinese.put(provinceArray[1], provinceArray[0]);
                english2Chinese.put(cityArray[1], cityArray[0]);
                return new Tuple5<>("inverted", invertedSeqChineseLocationTuple, invertedSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
            }
        } else if (split.length == 3) {
            String[] countryArray = split[1].split(":");
            String[] provinceArray = split[2].split(":");
            if (split[0].contains("-")) {
                if (provinceArray[0].length() > 1)
                    positiveSeqChineseLocationTuple = new Tuple3<>(countryArray[0], provinceArray[0], defaultLocationString);
                positiveSeqEnglishLocationTuple = new Tuple3<>(countryArray[1], provinceArray[1], defaultLocationString);
                english2Chinese.put(countryArray[1], countryArray[0]);
                english2Chinese.put(provinceArray[1], provinceArray[0]);
                return new Tuple5<>("positive", positiveSeqChineseLocationTuple, positiveSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
            } else {
                if (provinceArray[0].length() > 1)
                    invertedSeqChineseLocationTuple = new Tuple3<>(countryArray[0], provinceArray[0], defaultLocationString);
                invertedSeqEnglishLocationTuple = new Tuple3<>(countryArray[1], provinceArray[1], defaultLocationString);
                english2Chinese.put(countryArray[1], countryArray[0]);
                english2Chinese.put(provinceArray[1], provinceArray[0]);
                return new Tuple5<>("inverted", invertedSeqChineseLocationTuple, invertedSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
            }
        } else if (split.length == 2) {
            String[] countryArray = split[1].split(":");
            if (split[0].contains("-")) {
                positiveSeqChineseLocationTuple = new Tuple3<>(countryArray[0], defaultLocationString, defaultLocationString);
                positiveSeqEnglishLocationTuple = new Tuple3<>(countryArray[1], defaultLocationString, defaultLocationString);
                english2Chinese.put(countryArray[1], countryArray[0]);
                return new Tuple5<>("positive", positiveSeqChineseLocationTuple, positiveSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
            } else {
                invertedSeqChineseLocationTuple = new Tuple3<>(countryArray[0], defaultLocationString, defaultLocationString);
                invertedSeqEnglishLocationTuple = new Tuple3<>(countryArray[1], defaultLocationString, defaultLocationString);
                english2Chinese.put(countryArray[1], countryArray[0]);
                return new Tuple5<>("inverted", invertedSeqChineseLocationTuple, invertedSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
            }
        }else {
            return new Tuple5<>("wrong", invertedSeqChineseLocationTuple, invertedSeqEnglishLocationTuple, cityAndDistinguish, english2Chinese);
        }
    }

    public static Tuple3<
            LinkedHashMap<String, String>,
            LinkedHashMap<String, String>,
            HashSet<String>> getPositiveSequenceTuple(HashSet<Tuple3<String, String, String>> chinaSet) {

        LinkedHashMap<String, String> cityAndProvinceMap = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> ProvinceAndCountryMap = new LinkedHashMap<String, String>();
        HashSet<String> countrySet = new HashSet<>();
        ArrayList<Tuple2<String, String>> cityAndProvinceList = new ArrayList<>();
        ArrayList<Tuple2<String, String>> provinceAndCountryList = new ArrayList<>();
        for (Tuple3<String, String, String> tuple : chinaSet) {
            String country = tuple._1();
            String province = tuple._2();
            String city = tuple._3();
            Tuple2<String, String> cityProvince = new Tuple2<>(city, province);
            Tuple2<String, String> provinceCountry = new Tuple2<>(province, country);
            if (!city.equals("unknown") && !cityAndProvinceList.contains(cityProvince))
                cityAndProvinceList.add(cityProvince);
            if (!provinceAndCountryList.contains(provinceCountry))
                provinceAndCountryList.add(provinceCountry);
            countrySet.add(country);
        }

        Collections.sort(provinceAndCountryList, comparator);
        Collections.sort(cityAndProvinceList, comparator);
        for (Tuple2<String, String> pc : cityAndProvinceList) {
            cityAndProvinceMap.put(pc._1, pc._2);
        }
        for (Tuple2<String, String> pc : provinceAndCountryList) {
            ProvinceAndCountryMap.put(pc._1, pc._2);
        }
        return new Tuple3<>(cityAndProvinceMap, ProvinceAndCountryMap, countrySet);
    }

    public static Tuple2<
            LinkedHashMap<String, HashSet<String>>,
            LinkedHashMap<String, HashSet<String>>> getInvertedSequence(HashSet<Tuple3<String, String, String>> otherSet) {
        ArrayList<Tuple3<String, String, String>> otList = new ArrayList<>();
        for (Tuple3<String, String, String> ot : otherSet) {
            otList.add(ot);
        }
        Collections.sort(otList, comparatorTuple3);
        LinkedHashMap<String, HashSet<String>> otherCountryProvinceMap = new LinkedHashMap<String, HashSet<String>>();
        LinkedHashMap<String, HashSet<String>> otherProvinceCityMap = new LinkedHashMap<String, HashSet<String>>();
        for (Tuple3<String, String, String> tuple : otList) {
            String country = tuple._1();
            String province = tuple._2();
            String city = tuple._3();
            if (otherCountryProvinceMap.containsKey(country)) {
                otherCountryProvinceMap.get(country).add(province);
            } else {
                HashSet provinceSet = new HashSet();
                provinceSet.add(province);
                otherCountryProvinceMap.put(country, provinceSet);
            }
            if (otherProvinceCityMap.containsKey(province)) {
                otherProvinceCityMap.get(province).add(city);
            } else {
                HashSet citySet = new HashSet();
                citySet.add(city);
                otherProvinceCityMap.put(province, citySet);
            }
        }

        return new Tuple2<>(otherCountryProvinceMap, otherProvinceCityMap);
    }
}



