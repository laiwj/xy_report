package com.shulianxunying.entity;

import com.shulianxunying.util.DateUtils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SuChang on 2017/5/3 14:34.
 */
public class ApiUrlBuilder {
    String ip_address;
    String port;

    public ApiUrlBuilder() {
    }

    public ApiUrlBuilder(String ip_address, String port) {
        this.ip_address = ip_address;
        this.port = port;
    }

    public String create_talent_distribution_url(String city, String industry, String cf, String type, String time, Integer top) {
        HashMap<String, String> values = new HashMap<>();
        values.put("city", city);
        values.put("industry", industry);
        values.put("type", type);
        values.put("time", time);
        values.put("top", "" + top);
        return createUrl("/api/talent/distribution/" + cf, values);
    }

    public String create_talent_flow_url(String city, String industry, String type, String time, String direction, String city_or_func, Integer top) {
        HashMap<String, String> values = new HashMap<>();
        values.put("city", city);
        values.put("industry", industry);
        values.put("type", type);
        values.put("time", time);
        values.put("top", "" + top);
        return createUrl("/api/talent/flow/" + direction + "/" + city_or_func, values);
    }

    public String create_talent_flow_top_url(String industry, String type, String time, String direction, String city_or_func, Integer top) {
        HashMap<String, String> values = new HashMap<>();
        values.put("type", type);
        values.put("time", time);
        values.put("top", "" + top);
        values.put("industry", industry);
        return createUrl("/api/talent/flow/" + direction + "/" + city_or_func + "/top", values);
    }

    public String create_talent_exponential_url(String city, String industry, String type, String time, String func_or_position, String need_or_all, Integer top) {
        HashMap<String, String> values = new HashMap<>();
        values.put("city", city);
        values.put("industry", industry);
        values.put("type", type);
        values.put("time", time);
        values.put("top", "" + top);
        return createUrl("/api/talent/exponention/" + func_or_position + "/" + need_or_all, values);
    }

    public String buildFinalUrl(String postfix) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(getIp_address());
        sb.append(":");
        sb.append(getPort());
        sb.append(postfix);
        return sb.toString();
    }

    public String createUrl(String prefix, HashMap<String, String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("?");
        int i = 0;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (i++ != 0)
                sb.append("&");
            sb.append(entry.getKey());
            sb.append("=");
//            sb.append(URLEncoder.encode(entry.getValue()));
            sb.append(entry.getValue());
        }
        return sb.toString();
    }


    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
