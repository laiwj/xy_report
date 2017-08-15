package com.shulianxunying.utils;

/**
 * Created by 19866 on 2017/6/20.
 */
public class WorkYearUtils {

    public static String formatWorkYear(String workyear) {
        String defaultWorkYear = "workyear_unknown";
        int ageNum;
        try {
            ageNum = Integer.parseInt(workyear);
        } catch (NumberFormatException e) {
            return defaultWorkYear;
        }
        if (ageNum <= 3) {
            return "workyear_0-3";
        } else if (ageNum <= 5) {
            return "workyear_3-5";
        } else if (ageNum <= 8) {
            return "workyear_5-8";
        } else if (ageNum <= 12) {
            return "workyear_8-12";
        } else if (ageNum > 12)
            return "workyear_12+";
        else
            return defaultWorkYear;
    }

    public static void main(String[] args) {
        System.out.println(formatWorkYear("8"));
    }
}
