package com.shulianxunying.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 19866 on 2017/6/19.
 */
public class DegreeUtils {
    private static final String Bachelor = "academy|Academy|BB|DBA|MIT|B.COMM|LDE|Associate|associate|BFA|college|Bachelor|bachelor|本科|學士|学士|大学|BS|B S|Magister|magister|LLM|LL.M|Bsc|bs|B.EngLLB|AS|B.S.J|AB|B.A|abd|BTech|BE|AA|AA|MRes|DPhil|MPhil|graduated";
    private static final String Master = "Post-graduate|PostGrad|MS|MCA|M Soc|碩士|硕士|研究生|Master|master|MBA|M.S|M.A|MA|ma?trise|MPH|M.Ed.|Msc|MSc|MSC|M.F.A|MFA|MIA";
    private static final String Diploma = "Diploma|diploma|大专|专科|Education Continuing";
    private static final String Doctor = "Dr.rer.nat|PHD|PH.D|博士|MD|Doctor|doc|Ph.D|PharmD|ScD|Postdoc|PostDoc|Post-doc";
    private static final String Senior = "高中|高职|职高|Senior|Secondary|High School|GED";
    private static final String junior = "初中|中专|中职|中技|技校|junior|小学";


    public static String formatDegree(String degree) {

        String defaultDegree = "unknown";
        if (StringUtils.isEmpty(degree))
            return "degree_"+defaultDegree;
        HashMap<String, String> degreeReMap = new HashMap<>();
        degreeReMap.put("Bachelor", Bachelor);
        degreeReMap.put("Master", Master);
        degreeReMap.put("Diploma", Diploma);
        degreeReMap.put("Doctor", Doctor);
        degreeReMap.put("Senior", Senior);
        degreeReMap.put("Junior", junior);
        for (Map.Entry<String, String> entry : degreeReMap.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getValue());
            Matcher matcher = pattern.matcher(degree);
            if (matcher.find()) {
                defaultDegree = entry.getKey();
                break;
            }
        }
        return "degree_"+defaultDegree;
    }

    public static void main(String[] args) {
        System.out.println(formatDegree("大学"));
    }
}


