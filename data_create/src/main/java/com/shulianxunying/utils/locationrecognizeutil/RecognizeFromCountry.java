package com.shulianxunying.utils.locationrecognizeutil;

import com.shulianxunying.utils.locationrecognizeutil.utils.ReadAllCities;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 19866 on 2017/6/26.
 * 主要用于识别比较完整的地址
 */
public class RecognizeFromCountry {
    private static Tuple3<
            Tuple4<ArrayList<String>, ArrayList<String>, ArrayList<String>, ArrayList<String>>,
            Tuple3<HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>, HashMap<String, ArrayList<String>>>,
            Tuple4<HashMap<String, String>, HashMap<String, String>, HashMap<String, String>, HashMap<String, String>>> locationTuple = ReadAllCities.getReadResult();

    private static String defaultLocation = "unknown";

    public static Tuple2<String, String> recognize(String re, String address) {
        String location = defaultLocation;
        Pattern countryPattern = Pattern.compile(re.toLowerCase());
        Matcher matcher = countryPattern.matcher(address);
        if (matcher.find()) {
            address = address.replace(matcher.group(), "");
            location = re;
        }
        return new Tuple2<>(location, address);
    }

    public static Tuple4<String, String, String, String> recognizeLocation(String address) {
        address = address.toLowerCase();
        if (address.equals("") || address.length() < 2) {
            return new Tuple4<>(defaultLocation, defaultLocation, defaultLocation, defaultLocation);
        }
        ArrayList<String> countryList = locationTuple._1()._1();
        ArrayList<String> provinceList = locationTuple._1()._2();
        ArrayList<String> cityList = locationTuple._1()._3();
        ArrayList<String> areaList = locationTuple._1()._4();
        HashMap<String, ArrayList<String>> country2Province = locationTuple._2()._1();
        HashMap<String, ArrayList<String>> province2City = locationTuple._2()._2();
        HashMap<String, ArrayList<String>> city2Area = locationTuple._2()._3();
        HashMap<String, String> english2Chinese = locationTuple._3()._4();
        HashMap<String, String> province2Country = locationTuple._3()._1();
        HashMap<String, String> city2Province = locationTuple._3()._2();
        HashMap<String, String> area2City = locationTuple._3()._3();
        String country = defaultLocation;
        String province = defaultLocation;
        String city = defaultLocation;
        String area = defaultLocation;

        for (String countryRe : countryList) {
            Tuple2<String, String> recognizeCountryTuple = recognize(countryRe, address);
            if (recognizeCountryTuple._1().equals(defaultLocation)) {
                continue;
            } else {
                country = recognizeCountryTuple._1();
                address = recognizeCountryTuple._2();
                break;
            }
        }

        if (country.equals(defaultLocation)) {
            for (String provinceRe : provinceList) {
                Tuple2<String, String> recognizeProvinceTuple = recognize(provinceRe, address);
                if (recognizeProvinceTuple._1().equals(defaultLocation)) {
                    continue;
                } else {
                    province = recognizeProvinceTuple._1();
                    country = province2Country.getOrDefault(province, defaultLocation);
                    address = recognizeProvinceTuple._2();
                    break;
                }
            }
        } else {
            for (String provinceRe : country2Province.getOrDefault(country, new ArrayList<>())) {
                Tuple2<String, String> recognizeProvinceTuple = recognize(provinceRe, address);
                if (recognizeProvinceTuple._1().equals(defaultLocation)) {
                    continue;
                } else {
                    province = recognizeProvinceTuple._1();
                    address = recognizeProvinceTuple._2();
                    country = province2Country.getOrDefault(province, defaultLocation);
                    break;
                }
            }
        }

        if (province.equals(defaultLocation)) {
            for (String cityRe : cityList) {
                Tuple2<String, String> recognizeCityTuple = recognize(cityRe, address);
                if (recognizeCityTuple._1().equals(defaultLocation)) {
                    continue;
                } else {
                    city = recognizeCityTuple._1();
                    province = city2Province.getOrDefault(city, defaultLocation);
                    country = province2Country.getOrDefault(province, defaultLocation);
                    address = recognizeCityTuple._2();
                    break;
                }
            }
        } else {
            for (String cityRe : province2City.getOrDefault(province, new ArrayList<>())) {
                Tuple2<String, String> recognizeCityTuple = recognize(cityRe, address);
                if (recognizeCityTuple._1().equals(defaultLocation)) {
                    continue;
                } else {
                    city = recognizeCityTuple._1();
                    province = city2Province.getOrDefault(city, defaultLocation);
                    country = province2Country.getOrDefault(province, defaultLocation);
                    address = recognizeCityTuple._2();
                    break;
                }
            }
        }
        if (!city.equals(defaultLocation)) {
            for (String areaRe : city2Area.getOrDefault(city, new ArrayList<>())) {
                Tuple2<String, String> recognizeAreaTuple = recognize(areaRe, address);
                if (!area.equals(defaultLocation)) {
                    area = recognizeAreaTuple._1();
                    break;
                }
            }
        } else {
            if (country.equals(defaultLocation)) {
                for (String areaRe : areaList) {
                    Tuple2<String, String> recognizeAreaTuple = recognize(areaRe, address);
                    if (recognizeAreaTuple._1().equals(defaultLocation)) {
                        continue;
                    } else {
                        area = recognizeAreaTuple._1();
                        city = area2City.getOrDefault(area, defaultLocation);
                        province = city2Province.getOrDefault(city, defaultLocation);
                        country = province2Country.getOrDefault(province, defaultLocation);

                        break;
                    }
                }
            }
        }
        return new Tuple4<>(
                english2Chinese.getOrDefault(area, area),
                english2Chinese.getOrDefault(city, city),
                english2Chinese.getOrDefault(province, province),
                english2Chinese.getOrDefault(country, country)
        );
    }


    public static void main(String[] args) {
        System.out.println(recognizeLocation("长春市高新区越达路667号").toString());
//        System.out.println(recognizeLocation("University of Twente,Centre for Telematics and Information Technology P.O. Box 217  7500 AE Enschede,The Netherlands").toString());
    }

}
