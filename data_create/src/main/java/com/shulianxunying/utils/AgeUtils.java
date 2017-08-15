package com.shulianxunying.utils;

/**
 * Created by 19866 on 2017/6/19.
 */
public class AgeUtils {

    public static String formatAge(String age) {
        String defaultAge = "age_unknown";
        int ageNum;
        try {
            ageNum = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            return defaultAge;
        }
        if (ageNum <= 25) {
            return "age_18-25";
        } else if (ageNum <= 30) {
            return "age_25-30";
        } else if (ageNum <= 35) {
            return "age_30-35";
        } else if (ageNum <= 40) {
            return "age_35-40";
        } else if (ageNum > 40)
            return "age_40+";
        else
            return defaultAge;
    }

    public static void main(String[] args) {
        System.out.println(formatAge(""));
    }
}
