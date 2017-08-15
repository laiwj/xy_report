package com.shulianxunying.utils.locationrecognizeutil.utils;

import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by 19866 on 2017/6/26.
 */
public class ReadAllCities {
    private static String defaultLocationString = "unknown";
    private static Comparator comparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return -(o1.length() - o2.length());
        }
    };

    private static Tuple3<
            Tuple4<HashSet<String>, HashSet<String>, HashSet<String>, HashSet<String>>,
            Tuple3<HashMap<String, HashSet<String>>, HashMap<String, HashSet<String>>, HashMap<String, HashSet<String>>>,
            Tuple4<HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>>> getMaps(String path) {
        InputStream inputStream = ReadUniqueCityName.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        HashSet<String> country = new HashSet<>();
        HashSet<String> province = new HashSet<>();
        HashSet<String> city = new HashSet<>();
        HashSet<String> area = new HashSet<>();
        HashMap<String, HashSet<String>> country2Province = new HashMap<>();
        HashMap<String, HashSet<String>> province2City = new HashMap<>();
        HashMap<String, HashSet<String>> city2Area = new HashMap<>();
        HashSet<Tuple2<String, String>> english2ChineseMap = new HashSet<>();
        HashSet<Tuple2<String, String>> area2CityMap = new HashSet<>();
        HashSet<Tuple2<String, String>> city2ProvinceMap = new HashSet<>();
        HashSet<Tuple2<String, String>> province2CountryMap = new HashSet<>();
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String[] splits = line.split("\t");
                String[] countrySplit = splits[1].split(":");
                String countryChinese = countrySplit[0];
                String countryEnglish = countrySplit[1];
                String provinceChinese = defaultLocationString;
                String provinceEnglish = defaultLocationString;
                String cityChinese = defaultLocationString;
                String cityEnglish = defaultLocationString;
                String areaChinese = defaultLocationString;
                String areaEnglish = defaultLocationString;
                if (splits.length >= 3) {
                    String[] provinceSplit = splits[2].split(":");
                    provinceChinese = provinceSplit[0];
                    provinceEnglish = provinceSplit[1];

                    if (splits.length >= 4) {
                        String[] citySplit = splits[3].split(":");
                        cityChinese = citySplit[0];
                        city.add(cityChinese);
                        if (citySplit.length > 1) {
                            cityEnglish = citySplit[1];
                            city.add(cityEnglish);
                        }
                        if (splits.length == 5) {
                            String[] areaSplit = splits[4].split(":");
                            areaChinese = areaSplit[0];
                            area.add(areaChinese);
                            if (areaSplit.length > 1) {
                                areaEnglish = areaSplit[1];
                                area.add(areaEnglish);
                            }
                        }
                    }
                }
                country.add(countryChinese);
                country.add(countryEnglish);
                english2ChineseMap.add(new Tuple2<>(countryEnglish, countryChinese));
                if (!provinceChinese.equals(defaultLocationString)) {
                    province.add(provinceChinese);
                    HashSet<String> provinceChineseSet = country2Province.getOrDefault(countryChinese, new HashSet<>());
                    provinceChineseSet.add(provinceChinese);
                    country2Province.put(countryChinese, provinceChineseSet);
                    province2CountryMap.add(new Tuple2<>(provinceChinese, countryChinese));
                }
                if (!provinceEnglish.equals(defaultLocationString)) {
                    HashSet<String> provinceEnglishSet = country2Province.getOrDefault(countryEnglish, new HashSet<>());
                    provinceEnglishSet.add(provinceEnglish);
                    country2Province.put(countryEnglish, provinceEnglishSet);
                    english2ChineseMap.add(new Tuple2<>(provinceEnglish, provinceChinese));
                    province.add(provinceEnglish);
                    province2CountryMap.add(new Tuple2<>(provinceEnglish, countryEnglish));

                }
                if (!cityChinese.equals(defaultLocationString)) {
                    city.add(cityChinese);
                    city2ProvinceMap.add(new Tuple2<>(cityChinese, provinceChinese));
                    HashSet<String> cityChineseSet = country2Province.getOrDefault(provinceChinese, new HashSet<>());
                    cityChineseSet.add(cityChinese);
                    province2City.put(provinceChinese, cityChineseSet);
                }
                if (!cityEnglish.equals(defaultLocationString)) {
                    city.add(cityEnglish);
                    city2ProvinceMap.add(new Tuple2<>(cityEnglish, provinceEnglish));
                    HashSet<String> cityEnglishSet = country2Province.getOrDefault(provinceEnglish, new HashSet<>());
                    cityEnglishSet.add(provinceEnglish);
                    province2City.put(provinceEnglish, cityEnglishSet);
                    english2ChineseMap.add(new Tuple2<>(cityEnglish, cityChinese));

                }
                if (!areaChinese.equals(defaultLocationString)) {
                    area.add(areaChinese);
                    area2CityMap.add(new Tuple2<>(areaChinese, cityChinese));

                    HashSet<String> areaChineseSet = country2Province.getOrDefault(cityChinese, new HashSet<>());
                    areaChineseSet.add(cityChinese);
                    city2Area.put(cityChinese, areaChineseSet);
                }

                if (!areaEnglish.equals(defaultLocationString)) {
                    HashSet<String> areaEnglishSet = country2Province.getOrDefault(cityEnglish, new HashSet<>());
                    areaEnglishSet.add(areaEnglish);
                    city2Area.put(cityEnglish, areaEnglishSet);
                    english2ChineseMap.add(new Tuple2<>(areaEnglish, areaChinese));
                    area.add(areaEnglish);
                    area2CityMap.add(new Tuple2<>(areaEnglish, cityEnglish));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Tuple3<>(
                new Tuple4<>(country, province, city, area),
                new Tuple3<>(country2Province, province2City, city2Area),
                new Tuple4<>(province2CountryMap, city2ProvinceMap, area2CityMap, english2ChineseMap)
        );
    }

    public static HashMap<String, ArrayList<String>> mapSet2MapList(HashMap<String, HashSet<String>> mapSet) {
        HashMap<String, ArrayList<String>> mapList = new HashMap<>();
        for (Map.Entry<String, HashSet<String>> entry : mapSet.entrySet()) {
            ArrayList<String> list = new ArrayList<>();
            for (String str : entry.getValue()) {
                list.add(str);
            }
            Collections.sort(list, comparator);
            mapList.put(entry.getKey(), list);
        }
        return mapList;
    }

    public static Tuple3<
            Tuple4<ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>>,
            Tuple3<HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>>,
            Tuple4<HashMap<String, String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>>> getReadResult() {

        String path1 = "/国家城市映射表";
        String path = "/国家城市唯一特征";
        Tuple3<Tuple4<HashSet<String>, HashSet<String>, HashSet<String>, HashSet<String>>,
                Tuple3<HashMap<String, HashSet<String>>, HashMap<String, HashSet<String>>, HashMap<String, HashSet<String>>>,
                Tuple4<HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>>> a = getMaps(path);
        Tuple3<Tuple4<HashSet<String>, HashSet<String>, HashSet<String>, HashSet<String>>,
                Tuple3<HashMap<String, HashSet<String>>, HashMap<String, HashSet<String>>, HashMap<String, HashSet<String>>>,
                Tuple4<HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>, HashSet<Tuple2<String, String>>>> b = getMaps(path1);


        //country province city area set
        HashSet<String> country = a._1()._1();
        country.addAll(b._1()._1());
        HashSet<String> province = a._1()._2();
        province.addAll(b._1()._2());
        HashSet<String> city = a._1()._3();
        city.addAll(b._1()._3());
        HashSet<String> area = a._1()._4();
        area.addAll(b._1()._4());

        //country2province
        HashMap<String, HashSet<String>> country2Province = a._2()._1();
        for (Map.Entry<String, HashSet<String>> entry : b._2()._1().entrySet()) {
            HashSet<String> provinceSet = a._2()._1().getOrDefault(entry.getKey(), new HashSet<>());
            provinceSet.addAll(entry.getValue());
            country2Province.put(entry.getKey(), provinceSet);
        }
        //province2city
        HashMap<String, HashSet<String>> province2City = a._2()._2();
        for (Map.Entry<String, HashSet<String>> entry : b._2()._2().entrySet()) {
            HashSet<String> citySet = a._2()._2().getOrDefault(entry.getKey(), new HashSet<>());
            citySet.addAll(entry.getValue());
            province2City.put(entry.getKey(), citySet);
        }
        //city2Area
        HashMap<String, HashSet<String>> city2Area = a._2()._3();
        for (Map.Entry<String, HashSet<String>> entry : b._2()._3().entrySet()) {
            HashSet<String> areaSet = a._2()._3().getOrDefault(entry.getKey(), new HashSet<>());
            areaSet.addAll(entry.getValue());
            city2Area.put(entry.getKey(), areaSet);
        }
        //english2chinese
        HashSet<Tuple2<String, String>> english2ChineseMap = a._3()._4();
        english2ChineseMap.addAll(b._3()._4());
        //province2countryMap
        HashSet<Tuple2<String, String>> province2CountryMap = a._3()._1();
        province2CountryMap.addAll(b._3()._1());
        //city2ProvinceMap
        HashSet<Tuple2<String, String>> city2ProvinceMap = a._3()._2();
        city2ProvinceMap.addAll(b._3()._2());
        //area2CityMap
        HashSet<Tuple2<String, String>> area2CityMap = a._3()._3();
        area2CityMap.addAll(b._3()._3());

        ArrayList<String> countryList = new ArrayList<>();
        for (String countryStr : country) {
            countryList.add(countryStr);
        }
        ArrayList<String> provinceList = new ArrayList<>();
        for (String provinceStr : province) {
            provinceList.add(provinceStr);
        }
        ArrayList<String> cityList = new ArrayList<>();
        for (String cityStr : city) {
            cityList.add(cityStr);
        }
        ArrayList<String> areaList = new ArrayList<>();
        for (String areaStr : area) {
            areaList.add(areaStr);
        }

        Collections.sort(countryList, comparator);
        Collections.sort(provinceList, comparator);
        Collections.sort(cityList, comparator);
        Collections.sort(areaList, comparator);

        HashMap<String, String> english2Chinese = new HashMap<>();
        for (Tuple2<String, String> tuple2 : english2ChineseMap) {
            english2Chinese.put(tuple2._1(), tuple2._2());
        }
        HashMap<String, String> province2Country = new HashMap<>();
        for (Tuple2<String, String> tuple2 : province2CountryMap) {
            province2Country.put(tuple2._1(), tuple2._2());
        }
        HashMap<String, String> city2Province = new HashMap<>();
        for (Tuple2<String, String> tuple2 : city2ProvinceMap) {
            city2Province.put(tuple2._1(), tuple2._2());
        }
        HashMap<String, String> area2City = new HashMap<>();
        for (Tuple2<String, String> tuple2 : area2CityMap) {
            area2City.put(tuple2._1(), tuple2._2());
        }
        HashMap<String, ArrayList<String>> country2ProvinceList = mapSet2MapList(country2Province);
        HashMap<String, ArrayList<String>> province2CityList = mapSet2MapList(province2City);
        HashMap<String, ArrayList<String>> city2AreaList = mapSet2MapList(city2Area);
        return new Tuple3<>(
                new Tuple4<>(countryList, provinceList, cityList, areaList),
                new Tuple3<>(country2ProvinceList, province2CityList, city2AreaList),
                new Tuple4<>(province2Country, city2Province, area2City, english2Chinese));
    }

    public static void main(String[] args) {
        Tuple3<
                Tuple4<ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>>,
                Tuple3<HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>>,
                Tuple4<HashMap<String, String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>>> tuple = getReadResult();

        for (Map.Entry<String, String> entry : tuple._3()._1().entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

    }
}
