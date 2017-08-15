package com.shulianxunying.utils.locationrecognizeutil.utils;

import org.bson.Document;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 19866 on 2017/6/12.
 */
public class IsContainsArea {
    public static String isContainsArea(String patternStr,String location){
        String hitWords = "NotHitAnyWord";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(location);
        if (matcher.find()){
            hitWords = matcher.group();
        }
        return hitWords;
    }
    public static Tuple2<String, String> getCompanyName(ArrayList<Document> workExp) {
        String firstCompany = "";
        String secondCompany = "";
        Document firstDoc = workExp.get(0);
        Document secondDoc = workExp.get(1);
        String firstTime = (String) firstDoc.getOrDefault("end_date", "");
        String secondTime = (String) firstDoc.getOrDefault("end_date", "");
        if (firstTime.compareTo(secondTime) >= 0) {
            firstCompany = (String) firstDoc.getOrDefault("enterprise_name", "");
            secondCompany = (String) secondDoc.getOrDefault("enterprise_name", "");
        } else {
            firstCompany = (String) secondDoc.getOrDefault("enterprise_name", "");
            secondCompany = (String) firstDoc.getOrDefault("enterprise_name", "");
        }
        return new Tuple2<>(firstCompany, secondCompany);
    }
}
