package com.shulianxunying.utils.locationrecognizeutil.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 19866 on 2017/6/13.
 */
public class ReadOtherNameOfCity {

    public static HashMap<String,String> readOtherNameOfCity(){
        HashMap<String,String>nameMap = new HashMap<>();
        String path = "/同义城市名称表";
        InputStream resourceAsStream = ReadOtherNameOfCity.class.getResourceAsStream(path);
        BufferedReader br =new BufferedReader(new InputStreamReader(resourceAsStream));
        String line ="";
        try {
            while ((line =br.readLine())!=null){
                String[] split = line.split("\t");
                if (split.length==2){
                    nameMap.put(split[1].trim(),split[0].trim());
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return nameMap;
    }

    public static void main(String[]args){
        HashMap<String,String>test = readOtherNameOfCity();
        for (Map.Entry<String,String>entry:test.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
}
