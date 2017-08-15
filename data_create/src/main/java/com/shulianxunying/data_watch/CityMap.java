package com.shulianxunying.data_watch;

import com.alibaba.fastjson.JSON;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SuChang on 2017/5/8 11:03.
 */
public class CityMap {

    public static void main(String[] args) {
        List<AreaEntity> province = new ArrayList<>();
        List<AreaEntity> city = new ArrayList<>();
        List<AreaEntity> area = new ArrayList<>();
        String path = "/统计局行政区域划分.txt";
        InputStream resourceAsStream = CityMap.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            String province_number = null;
            String city_number = null;
            String province_name = null;
            String city_name = null;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("     ");
                String number = split[0];
                String name = split[1];

                // 省
                if (number.endsWith("0000")) {
                    if (name.endsWith("省") || name.endsWith("市"))
                        name = name.substring(0, name.length() - 1);
                    System.out.println("省\t" + name + "\t" + name + "\t" + name);
                    province_name = name;
                    province_number = number.substring(0, 2);
                    province.add(new AreaEntity(name, "", "", 1));
                    continue;
                }
                // 市
                if (number.endsWith("00") && number.startsWith(province_number)) {
                    if (name.equals("市辖区") || name.equals("县")) {
                        city_number = number.substring(2, 4);
                        city_name = province_name;
                        continue;
                    } else if (name.equals("省直辖县级行政区划")) {
                        city_number = number.substring(2, 4);
                        city_name = province_name;
                        continue;
                    } else if (name.equals("自治区直辖县级行政区划")) {
                        city_number = number.substring(2, 4);
                        city_name = province_name;
                        continue;
                    }
                    if (name.endsWith("市"))
                        name = name.substring(0, name.length() - 1);
                    else if (name.endsWith("自治州")) {
                        name = name.substring(0, name.length() - 3);
                    }
                    System.out.println("市\t" + province_name + "\t" + name + "\t" + name);
                    city_number = number.substring(2, 4);
                    city_name = name;
                    city.add(new AreaEntity(province_name, name, "", 2));
                    continue;
                }
                if (number.startsWith(province_number + city_number)) {
                    if (name.equals("市辖区")) {
                        continue;
                    }
                    if (name.endsWith("市"))
                        name = name.substring(0, name.length() - 1);
                    System.out.println("区\t" + province_name + "\t" + city_name + "\t" + name);
                    area.add(new AreaEntity(province_name,city_name,name,3));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println();


    }
}
