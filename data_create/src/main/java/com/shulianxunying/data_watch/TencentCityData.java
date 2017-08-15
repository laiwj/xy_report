package com.shulianxunying.data_watch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by SuChang on 2017/6/7 11:29.
 */
public class TencentCityData {

    public static String getFileString(String path) {
        InputStream resourceAsStream = TencentCityData.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {

        }
        return null;
    }


    public static void main(String[] args) {
        // 读取英文xml
        String en_string = getFileString("/LocList-en.xml");
        Document parse = Jsoup.parse(en_string);

        Set<String> spe = new HashSet<>();
        spe.add("北京");
        spe.add("上海");
        spe.add("重庆");
        spe.add("天津");
        spe.add("香港");
        spe.add("澳门");
        spe.add("台湾");

        JSONObject cn = JSON.parseObject(getFileString("/city list-zh.txt"));
        JSONArray jsonArray = cn.getJSONObject("Location").getJSONArray("CountryRegion");
        // 国家列表
//        for (Object object : jsonArray) {
//            JSONObject jsonObject = (JSONObject) object;
//            System.out.println(jsonObject.getString("Name"));
//        }
        // 国家城市列表
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            String country = jsonObject.getString("Name");
            String country_code = jsonObject.getString("Code");
            Element select = parse.select("CountryRegion[Code=" + country_code + "]").first();
            String country_en_name = select.attr("Name");
            country = country + ":" + country_en_name;
            Object state = jsonObject.get("State");
            if (state == null) {
                //  没有state的国家
                System.out.println("-\t" + country);
            } else if (state instanceof JSONObject) {
                JSONObject pri = (JSONObject) state;
                Object city1 = pri.get("City");
                if (city1 instanceof JSONArray) {
                    JSONArray citylist = (JSONArray) city1;
                    for (Object city : citylist) {
                        JSONObject c = (JSONObject) city;
                        String city_name = c.getString("Name");
                        String city_code = c.getString("Code");
                        Element select1 = parse.select("CountryRegion[Code=" + country_code + "] City[Code=" + city_code + "]").first();
                        String city_en_name = select1.attr("Name");
                        city_name = city_name + ":" + city_en_name;
                        System.out.println("-\t" + country + "\t" + city_name);
                    }
                }
            } else if (state instanceof JSONArray) {
                for (Object o : (JSONArray) state) {
                    JSONObject pri = (JSONObject) o;
                    String pri_name = pri.getString("Name");
                    String pri_code = pri.getString("Code");
                    Element select1 = parse.select("CountryRegion[Code=" + country_code + "] State[Code=" + pri_code + "]").first();
                    String pri_en_name = select1.attr("Name");
                    if (spe.contains(pri_name)) {
                        pri_name = pri_name + ":" + pri_en_name;
                        Object city = pri.get("City");
                        if (city != null && city instanceof JSONArray) {
                            JSONArray city2 = (JSONArray) city;
                            for (Object c1 : city2) {
                                JSONObject c = (JSONObject) c1;
                                String area_name = c.getString("Name");
                                String code = c.getString("Code");
                                Element select2 = parse.select("CountryRegion[Code=" + country_code + "] State[Code=" + pri_code + "] City[Code=" + code + "]").first();
                                String area_en_name = "";
                                if (select2 != null) {
                                    area_en_name = select2.attr("Name");
                                }
                                area_name = area_name + ":" + area_en_name;
                                System.out.println("-\t" + country + "\t" + pri_name + "\t" + area_name);
                            }
                        } else {
                            System.out.println("-\t" + country + "\t" + pri_name);
                        }
                        continue;
                    }
                    pri_name = pri_name + ":" + pri_en_name;
                    Object city1 = pri.get("City");
                    if (city1 == null) {
                        System.out.println("-\t" + country + "\t" + pri_name);
                    } else if (city1 instanceof JSONArray) {
                        JSONArray citylist = (JSONArray) city1;
                        for (Object city : citylist) {
                            JSONObject c = (JSONObject) city;
                            String city_name = c.getString("Name");
                            String city_code = c.getString("Code");
                            Element select2 = parse.select("CountryRegion[Code=" + country_code + "] State[Code=" + pri_code + "] City[Code=" + city_code + "]").first();
                            String city_en_name = "";
                            if (select2 != null) {
                                city_en_name = select2.attr("Name");
                            } else {
//                                System.out.println(country_code + "\t" + pri_code + "\t" + city_code);
                            }
                            city_name = city_name + ":" + city_en_name;
                            Object region = c.get("Region");
                            if (region != null && region instanceof JSONArray) {
                                for (Object r : (JSONArray) region) {
                                    JSONObject area = (JSONObject) r;
                                    String area_name = area.getString("Name");
                                    String code = area.getString("Code");
                                    Element select3 = parse.select("CountryRegion[Code=" + country_code + "] State[Code=" + pri_code + "] City[Code=" + city_code + "] Region[Code=" + code + "]").first();
                                    String area_en_name = "";
                                    if (select3 != null) {
                                        area_en_name = select3.attr("Name");
                                    }
                                    area_name = area_name + ":" + area_en_name;
                                    System.out.println("-\t" + country + "\t" + pri_name + "\t" + city_name + "\t" + area_name);
                                }
                            } else {
                                System.out.println("-\t" + country + "\t" + pri_name + "\t" + city_name);
                            }
                        }
                    } else if (city1 instanceof JSONObject) {
                        String city_name = ((JSONObject) city1).getString("Name");
                        String city_code = ((JSONObject) city1).getString("Code");
                        Element select2 = parse.select("CountryRegion[Code=" + country_code + "] State[Code=" + pri_code + "] City[Code=" + city_code + "]").first();
                        String city_en_name = select2.attr("Name");
                        city_name = city_name + ":" + city_en_name;
                        System.out.println("-\t" + country + "\t" + pri_name + "\t" + city_name);
                    }
                }
            } else {
                System.out.println("数据有误");
            }
        }

    }
}




