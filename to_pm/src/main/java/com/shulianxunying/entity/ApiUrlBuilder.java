package com.shulianxunying.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    public String create_hot_position_keyword_url(String position, Integer type, Integer top, String time) {
        HashMap<String, String> values = new HashMap<>();
        values.put("position", position);
        values.put("type", type+"");
        values.put("top", top+"");
        values.put("time", time);
        return createUrl("/api/hot/position/pay/keyword", values);
    }

    public String create_talent_salary_analysis_url(String industry, String index, Integer top,String time,Integer type,String label){

        HashMap<String,String> values = new HashMap<>();
        values.put("industry",industry);
        values.put("index",index);
        values.put("top",top+"");
        values.put("time",time);
        values.put("type",type+"");
        values.put("label",label);
        return createUrl("/api/talent/salary/analysis",values);

    }

    public String create_position_salary_analysis_url(String name,  String industry,String city,String experience,Integer type, Integer top){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String,String> values = new HashMap<>();
        values.put("name",name);
        values.put("industry",industry);
        values.put("city",city);
        values.put("experience",experience);
        values.put("type",type+"");
        values.put("top",top+"");
        values.put("time",sdf.format(new Date()));
        return createUrl("/api/position/salary/analysis",values);

    }

    public String create_fun_salary_analysis_url(String name,  String industry,String city,String experience,Integer type, Integer top){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HashMap<String,String> values = new HashMap<>();
        values.put("name",name);
        values.put("industry",industry);
        values.put("city",city);
        values.put("experience",experience);
        values.put("type",type+"");
        values.put("top",top+"");
        values.put("time",sdf.format(new Date()));
        return createUrl("/api/func/salary/analysis",values);
    }

    public String create_feature_portraits_url(String name, String pf, String label,String time,Integer type){
        HashMap<String,String> values = new HashMap<>();
        values.put("name",name);
        values.put("pf",pf);
        values.put("label",label);
        values.put("time",time);
        values.put("type",type+"");
        return createUrl("/api/feature/portraits",values);
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

    public String create_report_interpose(String id, String data, String api_url, String api_time, String params){
        HashMap<String,String> values = new HashMap<>();
        values.put("id", id);
        values.put("data", data);
        values.put("api_url", api_url);
        values.put("api_time", api_time);
        values.put("params", params );
        return createUrl("/api/data/modify", values);
    }

    public String create_update_power(String user_id,List<Integer> power, List<Integer> power_del){
        HashMap<String,String> values = new HashMap<>();
        values.put("id", user_id);
        values.put("data", power.toString());
        values.put("api_url", power_del.toString());
        return createUrl("/api/data/modify", values);
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
//        return "10.101.1.31";
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getPort() {
        return port;
//        return "8080";
    }

    public void setPort(String port) {
        this.port = port;
    }
}
