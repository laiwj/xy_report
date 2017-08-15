package com.shulianxunying.data_watch;

import com.alibaba.fastjson.JSON;
import org.apache.spark.sql.catalyst.expressions.In;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by SuChang on 2017/6/14 10:12.
 */
public class CityMapFeature {

    /**
     * 输出 国家城市映射表中 ，中文和英文都是唯一的 地区
     */
    public static void getOneHitArea() {
        HashMap<String, Integer> allKey = new HashMap<>();
        ArrayList<String> allLine = new ArrayList<>();
        InputStream resourceAsStream = CityMapFix.class.getResourceAsStream("/国家城市映射表");
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                allLine.add(line);
                for (int i = 1; i < split.length; i++) {
                    String[] split1 = split[i].split(":");
                    for (String key : split1) {
                        if (allKey.containsKey(key))
                            allKey.put(key, (allKey.get(key) + 1));
                        else
                            allKey.put(key, 1);
                    }
                }

            }
            for (String l : allLine) {
                line = l;
                String[] split = line.split("\t");
                for (int i = 1; i < split.length; i++) {
                    String[] split1 = split[i].split(":");
                    boolean flag = true;
                    for (String key : split1) {
                        if (allKey.get(key) != 1) {
                            flag = false;
                        }
                    }
                    if (flag)
                        System.out.println(line);
                }

            }
        } catch (IOException e) {

        }
    }

    public static List<String> allLine(String path) {
        List<String> out = new ArrayList<>();
        InputStream resourceAsStream = CityMapFix.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                out.add(line);
            }
        } catch (Exception e) {

        }
        return out;
    }

    public static HashMap<String, Integer> 词频统计(List<String> strings) {
        HashMap<String, Integer> out = new HashMap<>();
        for (String line : strings) {
            String[] split = line.split("\t");
            for (int i = 1; i < split.length; i++) {
                String[] split1 = split[i].split(":");
                for (String key : split1) {
                    if (out.containsKey(key))
                        out.put(key, (out.get(key) + 1));
                    else
                        out.put(key, 1);
                }
            }
        }
        return out;
    }

    public static boolean 判断个数是否相同(HashMap<String, Integer> map, HashMap<String, Integer> map1, String... keys) {
        boolean flag = true;
        for (String key : keys) {
            if (map.get(key) != map1.get(key)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        List<String> strings = allLine("/国家城市映射表");
        String json = JSON.toJSONString(strings);
        List<String> strings1 = allLine("/国家城市唯一特征");
//        HashMap<String, Integer> map = 词频统计(strings);
//        HashMap<String, Integer> map1 = 词频统计(strings1);
        HashSet<String> out = new HashSet<>();
        for (String line : strings1) {
            String[] split = line.split("\t");
            int length = split.length;
            String last = split[length - 1];
            String[] split1 = last.split(":");
            if (split1.length == 1) {
                if (json.contains(split1[0]))
                    System.out.println(line);
            } else {
                if (json.contains(split1[1]) || json.contains(split1[0])) {
                    System.out.println(line);
                }
            }
        }
    }
}

