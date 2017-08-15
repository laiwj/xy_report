package com.shulianxunying.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.annotation.AuthAnnotation;
import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.IDataApiService;
import com.shulianxunying.util.ApiParamsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by SuChang on 2017/4/27 15:07.
 */
@RestController
@RequestMapping("/api")
public class ApiController {
    @Resource
    private IDataApiService dataApiService;

    /**
     * @param city     现在是单选，但是我是按照多选来做的 所以 前三个参数 允许用 英文逗号连接
     * @param industry 选择行业
     * @param cf       city or func 代表是 地域分布 还是 职能分布
     * @param type     时间类型  1:周 2：月 3：季 4：年 目前只用 2 3 4
     * @param top      top几
     * @param request
     * @return
     */
    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_1)
    @SystemLogAnnotation(description = "distribution")
    @RequestMapping("/talent/distribution")
    public Model distribution(@RequestParam(required = true) String city,
                              @RequestParam(required = true) String industry,
                              @RequestParam(required = true) String cf,
                              @RequestParam(required = true) Integer type,
                              @RequestParam(required = true) Integer top,
                              HttpServletRequest request) {
        if (type < 0 || type > 4)
            return new Model(-3);
        if (top != 5 && top != 10)
            return new Model(-3);
        if (!StringUtils.equals(cf, "city") && !StringUtils.equals(cf, "func"))
            return new Model(-3);
        User user = (User) request.getSession().getAttribute("user");
        JSONObject params = new JSONObject();
        params.put("city",city);
        params.put("industry",industry);
        params.put("cf",cf);
        params.put("type",type);
        params.put("top",top);
        // 对入参进行 去重和排序 一定程度上保证参数有效性
        Set<String> citySet = ApiParamsUtils.splitParam(city, "全国");
//        industry = industry.replace("互联网金融", "金融");
        Set<String> industrySet = ApiParamsUtils.splitParam(industry, "互联网全行业");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Model model = dataApiService.talent_distribution(user,
                ApiParamsUtils.joinParam(citySet),
                ApiParamsUtils.joinParam(industrySet),
                cf,
                type, sdf.format(new Date()), top,params);
        return model;
    }

    /**
     * 人才 流入/流出
     *
     * @param city      人才地域流动 需要填写 (此参数为空 则为 top几的热门城市，填写了城市则为 对应城市的人才流动)
     * @param industry  选择行业
     * @param type      时间类型  1:周 2：月 3：季 4：年 目前只用 2 3 4
     * @param direction in or out 代表流入或流出
     * @param cf        city or func 代表是 地域流动 还是 职能流动
     * @param top       top几
     * @param request
     * @return
     */
    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_2)
    @SystemLogAnnotation(description = "flow")
    @RequestMapping("/talent/flow")
    public Model flow(@RequestParam(required = true) String city,
                      @RequestParam(required = true) String industry,
                      @RequestParam(required = true) Integer type,
                      @RequestParam(required = true) String direction,
                      @RequestParam(required = true) String cf, // city or func
                      @RequestParam(required = true) Integer top,
                      HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        JSONObject params = new JSONObject();
        params.put("city",city);
        params.put("industry",industry);
        params.put("type",type);
        params.put("direction",direction);
        params.put("cf",cf);
        params.put("top",top);
        if (!StringUtils.equals(direction, "in") && !StringUtils.equals(direction, "out"))
            return new Model(-3);
        if (!StringUtils.equals(cf, "city") && !StringUtils.equals(cf, "func"))
            return new Model(-3);
        if (type < 0 || type > 4)
            return new Model(-3);
        if (top != 5 && top != 10)
            return new Model(-3);
        Set<String> citySet = ApiParamsUtils.splitParam(city, "全国");
//        industry = industry.replace("互联网金融", "金融");
        Set<String> industrySet = ApiParamsUtils.splitParam(industry);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Model model = dataApiService.talent_flow(user,
                ApiParamsUtils.joinParam(citySet),
                ApiParamsUtils.joinParam(industrySet),
                type, sdf.format(new Date()),
                direction,
                cf,
                top,
                params
        );
        return model;
    }

    /**
     * 人才供需报告
     *
     * @param city     此参数为预留参数，目前填空
     * @param industry 选择行业
     * @param type     时间类型  1:周 2：月 3：季 4：年 目前只用 2 3 4
     * @param fp       func or position 代表 职能供需 或 岗位供需
     * @param na       need or all 代表 供给报告  或 供需指数
     * @param top      top几
     * @param request
     * @return
     */
    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_3)
    @SystemLogAnnotation(description = "exponential")
    @RequestMapping("/talent/exponential")
    public Model exponential(@RequestParam(required = true) String city,
                             @RequestParam(required = true) String industry,
                             @RequestParam(required = true) Integer type,
                             @RequestParam(required = true) String fp, //func_or_position
                             @RequestParam(required = true) String na, //need_or_all
                             @RequestParam(required = true) Integer top,
                             HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        JSONObject params = new JSONObject();
        params.put("city",city);
        params.put("industry",industry);
        params.put("type",type);
        params.put("fp",fp);
        params.put("na",na);
        params.put("top",top);

        if (!StringUtils.equals(fp, "func") && !StringUtils.equals(fp, "position"))
            return new Model(-3);
        if (!StringUtils.equals(na, "need") && !StringUtils.equals(na, "all"))
            return new Model(-3);
        if (type < 0 || type > 4)
            return new Model(-3);
        if (top != 5 && top != 10)
            return new Model(-3);
        Set<String> citySet = ApiParamsUtils.splitParam(city, "全国");
        Set<String> industrySet = ApiParamsUtils.splitParam(industry);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Model model = dataApiService.talent_exponential(user,
                ApiParamsUtils.joinParam(citySet),
                ApiParamsUtils.joinParam(industrySet),
                type, sdf.format(new Date()),
                fp,
                na,
                top,
                params
        );
        return model;
    }
//    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_3)
    @SystemLogAnnotation(description = "position_key")
    @RequestMapping("/hot/position/keyword")
    public Model hot_position_pay_keyword(HttpServletRequest request,
                                          @RequestParam(required = true) String position,
                                          @RequestParam(required = true) Integer type,
                                          @RequestParam(required = true) Integer top){
        User user = (User) request.getSession().getAttribute("user");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject params = new JSONObject();
        params.put("position",position);
        params.put("top",top);
        params.put("type",type);
        return dataApiService.hot_position_pay_keyword(user,position,type,top,sdf.format(new Date()),params);
    }

    /**
     *
     * @param request
     * @param industry
     * @param index
     * @param type
     * @param top
//     * @param type
     * @return
     */

    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_4)
    @SystemLogAnnotation(description = "talent_salary_analysis")
    @RequestMapping("/talent/salary/analysis")
    public Model talent_salary_analysis(HttpServletRequest request,
                                          @RequestParam(required = true) String industry,
                                          @RequestParam(required = true) String index,
                                          @RequestParam(defaultValue = "10") Integer top,
                                          @RequestParam(required = true) Integer type,
                                          @RequestParam(required = true)String label
//                                          @RequestParam(required = true) String type
                                          ){
        User user = (User) request.getSession().getAttribute("user");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject params = new JSONObject();
        params.put("industry",industry);
        params.put("index",index);
        params.put("top",top);
        params.put("type",type);
        params.put("label",label);
//        params.put("api_uri","/api/")
        Model model = dataApiService.talent_salary_analysis(user, industry, index, top, sdf.format(new Date()), type, label, params);
        JSONArray data = new JSONArray();
        Integer[] p = new Integer[4];

        data=(JSONArray)((JSONObject) ((JSONObject) model.getData()).get("data")).get("data");
        if(data == null){
            return model;
        }
//        try {
//            for(Object item :data){
//                int p75 = 0;
//                int p100 = 0;
//                JSONObject p100object = null;
//                for(String key:((JSONObject)item).keySet()){
//                    if(((JSONObject)item).get(key) instanceof JSONArray) {
//                        for(int i=0;i<((JSONObject) item).getJSONArray(key).size();i++) {
//                            JSONObject temp = ((JSONObject) ((JSONArray) ((JSONObject) item).get(key)).get(i));
//                            if ((temp.getString("name")).equals("p75")) {
//                                p75 = temp.getInteger("max_salary");
//                            } else if ((temp.getString("name")).equals("p100")) {
//                                p100 = temp.getInteger("max_salary");
//                                p100object = temp;
//                            }
//                        }
//                    }
//                }
//                if(p100>=101*p75 ){
//                    String st = ""+p100;
//                    p100object.put("max_salary",st.substring(0,st.length()-2));
//                }else if(p100>=15*p75 ){
//                    String st = ""+p100;
//                    p100object.put("max_salary",st.substring(0,st.length()-1));
//                }
//            }
//            for(int i=0;i<data.size();i++){
//                ((JSONObject)data.get(0)).get("max_salary");
//            }
//        }catch (Exception e){
//            return model;
//        }

        return model;

    }

    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_6)
    @SystemLogAnnotation(description = "position_salary_analysis")
    @RequestMapping("/position/salary/analysis")
    public Model position_salary_analysis(HttpServletRequest request,
                                          @RequestParam(required = true) String name,
                                          @RequestParam(required = true) String industry,
                                          @RequestParam(required = true) String city,
                                          @RequestParam(required = true) String experience,
                                          @RequestParam(required = true) Integer type,
                                          @RequestParam(required = true) Integer top
    ){
        User user = (User) request.getSession().getAttribute("user");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject params = new JSONObject();
        params.put("name",name);
        params.put("industry",industry);
        params.put("city",city);
        params.put("experience",experience);
        params.put("type",type);
        params.put("top",top);
        return dataApiService.position_salary_analysis(user, name, industry,city,experience,type,top,sdf.format(new Date()),params);
    }

    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_5)
    @SystemLogAnnotation(description = "func_salary_analysis")
    @RequestMapping("/func/salary/analysis")
    public Model func_salary_analysis(HttpServletRequest request,
                                      @RequestParam(required = true) String name,
                                      @RequestParam(required = true) String industry,
                                      @RequestParam(required = true) String city,
                                      @RequestParam(required = true) String experience,
                                      @RequestParam(required = true) Integer type,
                                      @RequestParam(required = true) Integer top){
        User user = (User) request.getSession().getAttribute("user");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        JSONObject params = new JSONObject();
        params.put("name",name);
        params.put("industry",industry);
        params.put("city",city);
        params.put("experience",experience);
        params.put("type",type);
        params.put("top",top);


        return dataApiService.func_salary_analysis(user,name, industry,city,experience,type, top,sdf.format(new Date()),params);
    }

    /**
     *
     * @param request
     * @param name    岗位/职能名
     * @param pf      position or func (表示岗位或职能)
     * @param label   标签（多个标签用英文逗号隔开）
     * @param type    报告周期类型  （1 周 2 月 3 季 4 年 目前只有2,3,4）
     * @return
     */
    @SystemLogAnnotation(description = "position_salary_analysis")
    @RequestMapping("/feature/portraits")
    public Model feature_portraits(HttpServletRequest request,
                                      @RequestParam(required = true)String name,
                                      @RequestParam(required = true) String pf,
                                      @RequestParam(required = true) String label,
                                      @RequestParam(required = true) Integer type){
        User user = (User) request.getSession().getAttribute("user");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject params = new JSONObject();
        params.put("name",name);
        params.put("pf",pf);
        params.put("label",label);
        params.put("type",type);
        return dataApiService.feature_portraits(user, name, pf, label,sdf.format(new Date()),type,params);
    }
}
