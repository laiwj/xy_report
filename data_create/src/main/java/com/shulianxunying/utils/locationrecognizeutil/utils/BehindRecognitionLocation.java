package com.shulianxunying.utils.locationrecognizeutil.utils;

import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

import java.util.*;

/**
 * Created by 19866 on 2017/6/9.
 */
public class BehindRecognitionLocation {
    private static Tuple4<
            Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>>,
            Tuple2<LinkedHashMap<String, HashSet<String>>, LinkedHashMap<String, HashSet<String>>>,
            HashMap<String, String>,
            HashMap<String, HashSet<String>>> cityAndCountryTuple = ReadCountryAndCityMap.readCountryAndCityMap();
    private static final Tuple3<LinkedHashMap<String, String>, LinkedHashMap<String, String>, HashSet<String>> positiveSeqTuple = cityAndCountryTuple._1();
    private static final Tuple2<LinkedHashMap<String, HashSet<String>>, LinkedHashMap<String, HashSet<String>>> invertedSeqTuple = cityAndCountryTuple._2();
    private static final HashMap<String, String> english2Chinese = cityAndCountryTuple._3();
    private static final HashMap<String, HashSet<String>> cityAndDistinguishMap = cityAndCountryTuple._4();


    private static HashSet<Tuple4<String, String, String, String>> dealByPositiveSeq(String location) {

        HashSet<Tuple4<String, String, String, String>> cityHitSet = new HashSet<>();
        if (StringUtils.isEmpty(location)) {
            return cityHitSet;
        } else {
            LinkedHashMap<String, String> cityAndProvinceMap = positiveSeqTuple._1();
            LinkedHashMap<String, String> provinceAndCountryMap = positiveSeqTuple._2();
            HashSet<String> countrySet = positiveSeqTuple._3();
            HashSet<Tuple2<String, String>> provinceHitSet = new HashSet<>();
            HashSet<String> countryHitSet = new HashSet<>();

            for (Map.Entry<String, String> cityAndProvinceEntry : cityAndProvinceMap.entrySet()) {
                String key = cityAndProvinceEntry.getKey();
                if (key.equals(""))
                    continue;
                String hitWords = IsContainsArea.isContainsArea(key.toLowerCase(), location);
                if (!hitWords.equals("NotHitAnyWord")) {
                    location = location.replaceAll(hitWords, "");
                    String city = cityAndProvinceEntry.getKey();
                    String province = cityAndProvinceEntry.getValue();
                    String country = provinceAndCountryMap.get(province);
                    String distinguish = "unknown";
                    for (String distinguishStr : cityAndDistinguishMap.getOrDefault(city, new HashSet<>())) {
                        String hitDistinguishWord = IsContainsArea.isContainsArea(distinguishStr.toLowerCase(), location);
                        if (!hitDistinguishWord.equals("NotHitAnyWord")) {
                            location = location.replaceAll(hitDistinguishWord, "");
                            distinguish = distinguishStr;
                            break;
                        }
                    }
                    country = english2Chinese.getOrDefault(country, country);
                    province = english2Chinese.getOrDefault(province, province);
                    city = english2Chinese.getOrDefault(city, city);
                    distinguish = english2Chinese.getOrDefault(distinguish, distinguish);

                    cityHitSet.add(new Tuple4<>(distinguish, city, province, country));
                }
            }
            for (Map.Entry<String, String> provinceAndCountryEntry : provinceAndCountryMap.entrySet()) {
                String hitWords = IsContainsArea.isContainsArea(provinceAndCountryEntry.getKey().toLowerCase(), location);
                if (!hitWords.equals("NotHitAnyWord")) {
                    location = location.replaceAll(hitWords, "");
                    String province = provinceAndCountryEntry.getKey();
                    //天坑！！！
                    //韩国:Korea	光州:Gwangju
                    //韩国:Korea	京畿道:Gyeonggi-do	广州市:Gwangju
                    if (province.equals("Gwangju"))
                        province = "光州";
                    String country = provinceAndCountryMap.get(province);

                    province = english2Chinese.getOrDefault(province, province);
                    country = english2Chinese.getOrDefault(country, country);

                    provinceHitSet.add(new Tuple2<>(province, country));
                }
            }

            for (String country : countrySet) {
                String hitWords = IsContainsArea.isContainsArea(country.toLowerCase(), location);
                if (!hitWords.equals("NotHitAnyWord")) {
                    location = location.replaceAll(hitWords, "");
                    country = english2Chinese.getOrDefault(country, country);
                    countryHitSet.add(country);
                }
            }
            if (!provinceHitSet.isEmpty()) {
                for (Tuple2<String, String> tuple : provinceHitSet) {
                    String province = tuple._1;
                    Boolean isContainProvince = false;
                    for (Tuple4<String, String, String, String> cityString : cityHitSet) {
                        if (cityString.toString().toLowerCase().contains(province)) {
                            isContainProvince = true;
                            break;
                        }
                    }
                    if (!isContainProvince) {
                        cityHitSet.add(new Tuple4<>("unknown", "unknown", province, tuple._2));
                    }
                }
            }

            if (!countryHitSet.isEmpty()) {
                for (String country : countryHitSet) {
                    Boolean isContainCountry = false;
                    for (Tuple4<String, String, String, String> cityString : cityHitSet) {
                        if (cityString.toString().toLowerCase().contains(country)) {
                            isContainCountry = true;
                            break;
                        }
                    }
                    if (!isContainCountry) {
                        cityHitSet.add(new Tuple4<>("unknown", "unknown", "unknown", country));
                    }
                }
            }
            if (cityHitSet.isEmpty() && provinceHitSet.isEmpty() && countryHitSet.isEmpty()) {
                cityHitSet.add(new Tuple4<>("unknown", "unknown", "unknown", "unknown"));
            }
        }
//        cityHitSet.forEach(p -> {
//            String city = p._2();
//            if (otherNameOfCity.containsKey(city)) {
//                cityHitSet.remove(p);
//                cityHitSet.add(new Tuple4<>(p._1(), otherNameOfCity.get(city), p._3(), p._4()));
//            }
//        });
        return cityHitSet;
    }

    private static HashSet<Tuple4<String, String, String, String>> dealByInvertedSeq(String location) {
        HashSet<Tuple4<String, String, String, String>> cityHitSet = new HashSet<>();
        if (StringUtils.isEmpty(location)) {
            return cityHitSet;
        } else {
            LinkedHashMap<String, HashSet<String>> countryAndProvinceMap = invertedSeqTuple._1();
            LinkedHashMap<String, HashSet<String>> provinceAndCityMap = invertedSeqTuple._2();
            String defaultDistinguish = "unknown";
            for (Map.Entry<String, HashSet<String>> countryAndProvinceEntry : countryAndProvinceMap.entrySet()) {
                String countryString = "unknown";
                String country = countryAndProvinceEntry.getKey();
                if (location.contains(country.toLowerCase())) {
                    countryString = country;
                    HashSet<String> provinceSet = countryAndProvinceEntry.getValue();
                    String provinceString = "unknown";
                    if (!provinceSet.isEmpty()) {
                        for (String province : provinceSet) {
                            if (location.contains(province.toLowerCase())) {
                                provinceString = province;
                                HashSet<String> citySet = provinceAndCityMap.get(province);
                                String cityString = "unknown";
                                if (!citySet.isEmpty()) {
                                    for (String c : citySet) {
                                        if (location.contains(c.toLowerCase())) {
                                            cityString = c;
                                            cityHitSet.add(new Tuple4<>(
                                                    defaultDistinguish,
                                                    english2Chinese.getOrDefault(cityString, cityString),
                                                    english2Chinese.getOrDefault(provinceString, provinceString),
                                                    english2Chinese.getOrDefault(countryString, countryString)));
                                        } else {
                                            cityHitSet.add(new Tuple4<>(
                                                    defaultDistinguish,
                                                    english2Chinese.getOrDefault(cityString, cityString),
                                                    english2Chinese.getOrDefault(provinceString, provinceString),
                                                    english2Chinese.getOrDefault(countryString, countryString)));
                                        }
                                    }
                                } else {
                                    cityHitSet.add(new Tuple4<>(
                                            defaultDistinguish,
                                            english2Chinese.getOrDefault(cityString, cityString),
                                            english2Chinese.getOrDefault(provinceString, provinceString),
                                            english2Chinese.getOrDefault(countryString, countryString)));
                                }
                            } else {
                                cityHitSet.add(new Tuple4<>(defaultDistinguish, "unknown", provinceString, english2Chinese.getOrDefault(countryString, countryString)));
                            }
                        }
                    } else {
                        cityHitSet.add(new Tuple4<>(defaultDistinguish, "unknown", provinceString, english2Chinese.getOrDefault(countryString, countryString)));
                    }
                    if (provinceString.equals("unknown")) {
                        for (String province : provinceSet) {
                            provinceString = province;
                            HashSet<String> citySet = provinceAndCityMap.get(province);
                            String cityString = "unknown";
                            if (!citySet.isEmpty()) {
                                for (String c : citySet) {
                                    if (location.contains(c.toLowerCase())) {
                                        cityString = c;
                                        cityHitSet.add(new Tuple4<>(
                                                defaultDistinguish,
                                                english2Chinese.getOrDefault(cityString, cityString),
                                                english2Chinese.getOrDefault(provinceString, provinceString),
                                                english2Chinese.getOrDefault(countryString, countryString)));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (cityHitSet.isEmpty())
                        cityHitSet.add(new Tuple4<>(defaultDistinguish, "unknown", "unknown", countryString));
                }
            }
        }

        return cityHitSet;
    }

    public static HashSet<Tuple4<String, String, String, String>> behindRecognitionLocation(String location) {
        HashSet<Tuple4<String, String, String, String>> positive = dealByPositiveSeq(location.toLowerCase());
        HashSet<Tuple4<String, String, String, String>> inverted = dealByInvertedSeq(location.toLowerCase());
        HashSet<Tuple4<String, String, String, String>> returnSet;
        if (positive.size() == 1 && inverted.size() != 1) {
            Tuple4<String, String, String, String> tuple4 = (Tuple4<String, String, String, String>) positive.toArray()[0];
            if (tuple4._3().equals("unknown")) {
                returnSet = inverted;
            } else {
                inverted.add(tuple4);
                returnSet = inverted;
            }
        } else if (positive.size() != 1 && inverted.size() == 1) {
            Tuple4<String, String, String, String> tuple4 = (Tuple4<String, String, String, String>) inverted.toArray()[0];
            if (tuple4._3().equals("unknown")) {
                returnSet = positive;
            } else {
                positive.add(tuple4);
                returnSet = positive;
            }
        } else {
            positive.addAll(inverted);
            returnSet = positive;
        }
        return returnSet;
    }

    public static void main(String[] args) {
        String location = "恩施";
        Iterator<Tuple4<String, String, String, String>> set = behindRecognitionLocation(location.toLowerCase()).iterator();
        while (set.hasNext()) {
            System.out.println(set.next().toString());
        }
    }
}
