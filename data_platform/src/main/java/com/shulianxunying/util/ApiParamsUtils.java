package com.shulianxunying.util;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by SuChang on 2017/5/4 17:28.
 */
public class ApiParamsUtils {

    /**
     * 把逗号分隔的字符串 放到Set中，如果包含特殊词，则放弃此次执行
     *
     * @param set
     * @param str
     * @param specialValue
     */
    public static void splitParam(Set<String> set, @NotNull String str, String... specialValue) {
        for(String s : specialValue)
            if(str.contains(s))
                return;
        if (StringUtils.isNotEmpty(str)) {
            String[] split = str.split(",");
            for (String s : split)
                set.add(s);
            set.remove("");
        }
    }

    public static Set<String> splitParam(@NotNull String str, String... specialValue) {
        Set<String> out = new TreeSet<String>();
        if(StringUtils.isNotEmpty(str))
            splitParam(out, str,specialValue);
        return out;
    }

    public static String joinParam(@NotNull Set<String> valueSet) {
        StringBuilder sb = new StringBuilder();
        if (valueSet.size() > 0) {
            for (String s : valueSet) {
                sb.append(s);
                sb.append(",");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
        return "";
    }
}
