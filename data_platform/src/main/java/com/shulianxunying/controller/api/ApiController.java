package com.shulianxunying.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.ApiData;
import com.shulianxunying.entity.impl.ApiTempData;
import com.shulianxunying.service.IDataApiService;
import com.shulianxunying.util.ApiParamsUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by SuChang on 2017/4/27 14:06.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Resource
    private IDataApiService dataApiService;

    /**
     * 人才城市分布
     * @param city      城市(若包含"全国"、"city",则默认为全世界)
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param function  职能(若包含"全部",则包含全部职能)
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "distribution_city")
    @RequestMapping("/talent/distribution/city")
    public Model distribution_city(@RequestParam(required = true) String city,
                                   @RequestParam(required = true) String industry,
                                   String function,
                                   @RequestParam(required = true) Integer type,
                                   @RequestParam(required = true) String time, //获取 xx日期之前的报告
                                   @RequestParam(required = true) Integer top,
                                   HttpServletRequest request) {
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国", "city");
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");
        Set<String> functionList = ApiParamsUtils.splitParam(function, "全部");
        JSONObject params = new JSONObject();
        params.put("city", cityList);
        params.put("industry", industryList);
        params.put("function", functionList);
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_distribution_city(requestURI, params, time, type);
    }

    /**
     * 人才职能分布
     * @param city      城市(若包含"全国"、"city",则默认为全世界)
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "distribution")
    @RequestMapping("/talent/distribution/func")
    public Model distribution_func(@RequestParam(required = true) String city,
                                   @RequestParam(required = true) String industry,
                                   @RequestParam(required = true) Integer type,
                                   @RequestParam(required = true) String time,
                                   @RequestParam(required = true) Integer top,
                                   HttpServletRequest request) {
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国", "city");
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");
        JSONObject params = new JSONObject();
        params.put("city", cityList);
        params.put("industry", industryList);
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_distribution_func(requestURI, params, time, type);
    }

    /**
     * 人才流入的热门城市TOP
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "flow_in_city")
    @RequestMapping("/talent/flow/in/city/top")
    public Model flow_in(@RequestParam(required = true) String industry,
                         @RequestParam(required = true) Integer type,
                         @RequestParam(required = true) String time,
                         @RequestParam(required = true) Integer top,
                         HttpServletRequest request) {
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");
        JSONObject params = new JSONObject();
        params.put("top", top);
        params.put("type", type);
        params.put("industry", industryList);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_city_flow_in_top(requestURI, params, time, type);
    }

    /**
     * 人才流出的热门城市TOP
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "flow_out_city")
    @RequestMapping("/talent/flow/out/city/top")
    public Model flow_out(@RequestParam(required = true) String industry,
                          @RequestParam(required = true) Integer type,
                          @RequestParam(required = true) String time,
                          @RequestParam(required = true) Integer top,
                          HttpServletRequest request) {
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");
        JSONObject params = new JSONObject();
        params.put("top", top);
        params.put("type", type);
        params.put("industry", industryList);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_city_flow_out_top(requestURI, params, time, type);
    }


    /**
     * 人才的热门流入职能TOP
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "flow_func_in")
    @RequestMapping("/talent/flow/in/func/top")
    public Model flow_func_in(@RequestParam(required = true) String industry,
                              @RequestParam(required = true) Integer type,
                              @RequestParam(required = true) String time,
                              @RequestParam(required = true) Integer top,
                              HttpServletRequest request) {
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");
        JSONObject params = new JSONObject();
        params.put("top", top);
        params.put("type", type);
        params.put("industry", industryList);
        String requestURI = request.getRequestURI();
        Model model = dataApiService.talent_func_flow_in_top(requestURI, params, time, type);
        List<JSONObject> out = new ArrayList<>();
        if (model.getCode() == 0) {
            ApiTempData data = (ApiTempData) model.getData();
            for (Object document : (ArrayList)data.getData()) {
                JSONObject d = new JSONObject();
                String name = ((Document)document).getString("name");
                params.put("func", name);
                Model model1 = dataApiService.talent_flow_in_func("/api/talent/flow/in/func", params, time, type);
                ApiData data1 = (ApiData) model1.getData();
                d.put(name, data1.getData());
                out.add(d);
            }
            params.remove("func");
            data.setData(out);
            return model;
        } else
            return model;
    }

    /**
     * 人才的热门流出职能TOP
     * @param industry      行业(若包含"互联网全行业",则默认全行业)
     * @param type          报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time          报告时间,格式 xxxx-xx-xx
     * @param top           Top指数
     * @param request       客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "flow_func_out")
    @RequestMapping("/talent/flow/out/func/top")
    public Model flow_func_out(@RequestParam(required = true) String industry,
                               @RequestParam(required = true) Integer type,
                               @RequestParam(required = true) String time,
                               @RequestParam(required = true) Integer top,
                               HttpServletRequest request) {
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");
        JSONObject params = new JSONObject();
        params.put("top", top);
        params.put("type", type);
        params.put("industry", industryList);
        String requestURI = request.getRequestURI();
        Model model = dataApiService.talent_func_flow_out_top(requestURI, params, time, type);
        List<JSONObject> out = new ArrayList<>();
        if (model.getCode() == 0) {
            ApiData data = (ApiData) model.getData();
            for (Object document : (ArrayList)data.getData()) {
                JSONObject d = new JSONObject();
                String name = ((Document)document).getString("name");
                params.put("pre_func", name);
                Model model1 = dataApiService.talent_flow_out_func("/api/talent/flow/out/func", params, time, type);
                ApiData data1 = (ApiData) model1.getData();
                d.put(name, data1.getData());
                out.add(d);
            }
            params.remove("pre_func");
            data.setData(out);
            return model;
        } else
            return model;
    }

    /**
     * 人才流入某城市的前城市TOP
     * @param city      城市(若包含"全国"、"city",则默认为全世界)
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "flow_in_city")
    @RequestMapping("/talent/flow/in/city")
    public Model flow_in_city(@RequestParam(required = true) String city,
                              @RequestParam(required = true) String industry,
                              @RequestParam(required = true) Integer type,
                              @RequestParam(required = true) String time,
                              @RequestParam(required = true) Integer top,
                              HttpServletRequest request) {
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国", "city");
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");

        JSONObject params = new JSONObject();
        params.put("city", cityList);
        params.put("industry", industryList);
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_flow_in_city(requestURI, params, time, type);
    }

    /**
     * 人才从某城市流出到其他城市的TOP
     * @param city      城市(若包含"全国"、"city",则默认为全世界)
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "flow_out_city")
    @RequestMapping("/talent/flow/out/city")
    public Model flow_out_city(@RequestParam(required = true) String city,
                               @RequestParam(required = true) String industry,
                               @RequestParam(required = true) Integer type,
                               @RequestParam(required = true) String time,
                               @RequestParam(required = true) Integer top,
                               HttpServletRequest request) {
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国", "city");
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");

        JSONObject params = new JSONObject();
        params.put("city", cityList);
        params.put("industry", industryList);
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_flow_out_city(requestURI, params, time, type);
    }


    /**
     * TODO:职能流动   流入某职能 (由于交互方式改变 暂时废弃此方法，由top方法统一调度)
     * @param city
     * @param industry
     * @param type
     * @param time
     * @param top
     * @param request
     * @return
     */
    @SystemLogAnnotation(description = "flow_in_func")
    @RequestMapping("/talent/flow/in/func")
    public Model flow_in_func(@RequestParam(required = true) String city,
                              @RequestParam(required = true) String industry,
                              @RequestParam(required = true) Integer type,
                              @RequestParam(required = true) String time,
                              @RequestParam(required = true) Integer top,
                              HttpServletRequest request) {
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国", "city");
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");

        JSONObject params = new JSONObject();
        params.put("city", cityList);
        params.put("industry", industryList);
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_flow_in_func(requestURI, params, time, type);
    }


    /**
     * TODO:人才 职能 流出 (由于交互方式改变 暂时废弃此方法，由top方法统一调度)
     * @param city
     * @param industry
     * @param type
     * @param time
     * @param top
     * @param request
     * @return
     */
    @SystemLogAnnotation(description = "flow_out_func")
    @RequestMapping("/talent/flow/out/func")
    public Model flow_out_func(@RequestParam(required = true) String city,
                               @RequestParam(required = true) String industry,
                               @RequestParam(required = true) Integer type,
                               @RequestParam(required = true) String time,
                               @RequestParam(required = true) Integer top,
                               HttpServletRequest request) {
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国", "city");
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");

        JSONObject params = new JSONObject();
        params.put("city", cityList);
        params.put("industry", industryList);
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_flow_out_func(requestURI, params, time, type);
    }

    /**
     * 人才职能 供需指数或需求量
     * @param city      此参数为预留参数，目前填空
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param step      need or all 代表:需求量 或 供需指数
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "exponention_func")
    @RequestMapping("/talent/exponention/func/{need_or_all}")
    public Model exponention_func(@RequestParam(required = true) String city,
                                  @RequestParam(required = true) String industry,
                                  @PathVariable("need_or_all") String step,
                                  @RequestParam(required = true) Integer type,
                                  @RequestParam(required = true) String time,
                                  @RequestParam(required = true) Integer top,
                                  HttpServletRequest request) {
        if (!StringUtils.equals("need", step) && !StringUtils.equals("all", step)) {
            return new Model(-2);
        }
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国", "city");
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");

        JSONObject params = new JSONObject();
        params.put("city", cityList);
        params.put("industry", industryList);
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        Model model = new Model(-1, "查询失败");
        if (StringUtils.equals("need", step)) {
            model = dataApiService.talent_supply_func(requestURI, params, time, type);
        } else if (StringUtils.equals("all", step)) {
            model = dataApiService.talent_demand_func(requestURI, params, time, type);
        }
        return model;
    }

    /**
     * 人才岗位 供需指数或供给报告(需求量)
     * @param city      此参数为预留参数，目前填空
     * @param industry  行业(若包含"互联网全行业",则默认全行业)
     * @param step      need or all 代表:供给报告 或 供需指数
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param top       Top指数
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "exponention_position")
    @RequestMapping("/talent/exponention/position/{need_or_all}")
    public Model exponention_position(@RequestParam(required = true) String city,
                                      @RequestParam(required = true) String industry,
                                      @PathVariable("need_or_all") String step,
                                      @RequestParam(required = true) Integer type,
                                      @RequestParam(required = true) String time,
                                      @RequestParam(required = true) Integer top,
                                      HttpServletRequest request) {
        if (!StringUtils.equals("need", step) && !StringUtils.equals("all", step)) {
            return new Model(-2);
        }
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国", "city");
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");

        JSONObject params = new JSONObject();
        params.put("city", cityList);
        params.put("industry", industryList);
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        Model model = new Model(-1, "查询失败");
        if (StringUtils.equals("need", step)) {
            model = dataApiService.talent_supply_position(requestURI, params, time, type);
        } else if (StringUtils.equals("all", step)) {
            model = dataApiService.talent_demand_position(requestURI, params, time, type);
        }
        return model;
    }

    /**
     * 热门岗位薪酬分析
     * @param industry  行业
     * @param index     岗位供需指数
     * @param type      报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time      报告时间,格式 xxxx-xx-xx
     * @param label     标签，多个用逗号隔开
     * @param top       Top指数，默认为5
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "salary_analysis")
    @RequestMapping("/talent/salary/analysis")
    public Model salary_analysis(@RequestParam(required = true) String industry,
                                 @RequestParam(required = true) String index,
                                 @RequestParam(required = true) Integer type,
                                 @RequestParam(required = true) String time,
                                 @RequestParam(required = true) String label,
                                 @RequestParam(defaultValue = "5") Integer top,
                                 HttpServletRequest request) {
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");
        JSONObject params = new JSONObject();
        params.put("industry", industryList);
        params.put("index", index);
        params.put("t", type);
        params.put("top", top);
        params.put("label", label);
        String api_url = request.getRequestURI();
        return dataApiService.salary_analysis(params, time, api_url);
    }


    /**
     * 职位/职能薪酬分析
     * @param name       职位或职能
     * @param industry   行业
     * @param city       城市
     * @param experience 经验
     * @param type       报告周期类型,1:周 2:月 3:季 4:年,目前只用 2 3 4
     * @param time       报告时间,格式 xxxx-xx-xx
     * @param step       职位或职能(position or func)
     * @param request    客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "position_salary_analysis")
    @RequestMapping("/{position_or_func}/salary/analysis")
    public Model position_salary_analysis(@RequestParam(required = true) String name,
                                          @RequestParam(required = true) String industry,
                                          @RequestParam(required = true) String city,
                                          @RequestParam(required = true) String experience,
                                          @RequestParam(required = true) Integer type,
                                          @RequestParam(required = true) String time,
                                          @PathVariable("position_or_func") String step,
                                          HttpServletRequest request) {
        Set<String> nameList = ApiParamsUtils.splitParam(name);
        Set<String> industryList = ApiParamsUtils.splitParam(industry, "互联网全行业");
        Set<String> cityList = ApiParamsUtils.splitParam(city, "全国");
        Set<String> experienceList = ApiParamsUtils.splitParam(experience, "-1");
        String api_url = request.getRequestURI();
        JSONObject params = new JSONObject();
        params.put("name", nameList);
        params.put("industry", industryList);
        params.put("city", cityList);
        params.put("experience", experienceList);
        params.put("t", type);
        // 职能或职位为空时，直接返回
        if (nameList.size() == 0)
            return new Model();

        String key;
        if (step.equals("position"))
            key = "position";
        else if (step.equals("func"))
            key = "func";
        else
            return new Model(-2, "访问地址错误。");

        String types;
        if (industryList.size() == 0 && cityList.size() == 0 && experienceList.size() == 0)
            types = "position_only";
        else if (industryList.size() > 0 && cityList.size() == 0 && experienceList.size() == 0)
            types = "position_and_industry";
        else if (experienceList.size() > 0 && industryList.size() == 0 && cityList.size() == 0)
            types = "position_and_work_year";
        else if (cityList.size() > 0 && industryList.size() == 0 && experienceList.size() == 0)
            types = "position_and_city";
        else if (industryList.size() > 0 && cityList.size() > 0 && experienceList.size() == 0)
            types = "position_and_industry_and_city";
        else if (experienceList.size() > 0 && cityList.size() > 0 && industryList.size() == 0)
            types = "position_and_work_year_and_city";
        else if (industryList.size() > 0 && experienceList.size() > 0 && cityList.size() == 0)
            types = "position_and_industry_and_work_year";
        else
            types = "position_and_industry_and__work_year_and_city";

        return dataApiService.salary_analysis(params, time, api_url, key, types);
    }

    /**
     * 数据干预
     * @param id        缓存唯一ID
     * @param data      修改的json数组数据
     * @param api_url   生成报告的api地址
     * @param api_time  生成报告的时间
     * @param params    参数json字符串
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "data_modify")
    @RequestMapping("/data/modify")
    public Model data_modify(@RequestParam(required = true) String id,
                             @RequestParam(required = true) String data,
                             @RequestParam(required = true) String api_url,
                             @RequestParam(required = true) String api_time,
                             @RequestParam(required = true) String params,
                             HttpServletRequest request) {
        JSONArray data_arr = null;
        JSONObject params_jsonb = null;
        try {
            data_arr = JSONArray.parseArray(data);
            params_jsonb = JSONObject.parseObject(params);
        } catch (JSONException e) {
            return new Model(-3);
        }
        return dataApiService.report_modify(id, api_url, api_time, data_arr, params_jsonb);
    }

}
