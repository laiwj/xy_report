package com.shulianxunying.utils;

import com.shulianxunying.data_watch.CityMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuChang on 2017/5/22 16:28.
 */
public class AreaUtils {


    public static List<CityEntity> getCitys() {
        String path = "/省市区整理后";
        InputStream resourceAsStream = CityMap.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        List<CityEntity> out = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                String type = split[0];
                String province = split[1];
                String city = split[2];
                String area = split[3];
                loop:
                if (type.equals("区")) {
                    for (CityEntity cityEntity : out) {
                        if (cityEntity.getCity().equals(city)) {
                            cityEntity.getArea().add(area);
                            break loop;
                        }
                    }
                    out.add(new CityEntity("中国", province, city, area));
                }
            }
        } catch (IOException e) {

        }
        return out;
    }

    public static void main(String[] args) {
        List<CityEntity> citys = getCitys();
        System.out.println();
    }
}

