package com.shulianxunying.data_watch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SuChang on 2017/6/13 9:22.
 */
public class CityMapFix {


    public static void main(String[] args) {
        InputStream resourceAsStream = CityMapFix.class.getResourceAsStream("/国家城市映射表");
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                String area = split[split.length - 1];
                int length = area.length();
                if (length > 3) {
                    if (area.endsWith("市:")) {
                        area = area.substring(0, length - 2)+":";
                    } else if (area.endsWith("区:") || area.endsWith("县:")) {
                        if(!area.contains("自治") && !area.contains("矿区")){
                            area = area.substring(0, length - 2)+":";
                        }
                    }
                    for (int i = 0; i < split.length - 1; i++) {
                        System.out.print(split[i]+"\t");
                    }
                    System.out.println(area);
                } else
                    System.out.println(line);

            }
        } catch (IOException e) {

        }
    }
}
