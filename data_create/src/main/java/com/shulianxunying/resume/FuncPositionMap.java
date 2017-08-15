package com.shulianxunying.resume;

import com.alibaba.fastjson.JSON;
import com.shulianxunying.data_watch.CityMap;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SuChang on 2017/5/8 14:25.
 */
public class FuncPositionMap {
    public static List<PositionFunc> data = null;


    public static List<PositionFunc> getList() {
        if (data != null)
            return data;
        data = new ArrayList<>();
        String path = "/职能岗位对应.txt";
        InputStream resourceAsStream = CityMap.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                PositionFunc positionFunc = new PositionFunc(split[0], split[1], split[2]);
                for (String key : split[3].split("\\+")) {
                    key = key.trim().toUpperCase();
                    if (StringUtils.isNotEmpty(key))
                        positionFunc.getKeywords().add(key);
                }
                if (split[3].contains("$")) {
                    String[] exclude = split[3].split("\\$");
                    exclude[0] = "";
                    for (String x : exclude) {
                        x = x.trim().toUpperCase();
                        if (StringUtils.isNotEmpty(x))
                            positionFunc.getExclude().add(x);
                    }
                }
                data.add(positionFunc);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(data);
        return data;
    }
}