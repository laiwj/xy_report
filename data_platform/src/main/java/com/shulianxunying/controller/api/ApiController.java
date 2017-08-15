package com.shulianxunying.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.ApiData;
import com.shulianxunying.entity.TempData;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
     *
     * @param city     城市 -- 预处理 (全国、省份-->城市) -- 匹配词
     * @param industry
     * @return
     */
    @SystemLogAnnotation(description = "distribution_city")
    @RequestMapping("/talent/distribution/city")
    public Model distribution_city(@RequestParam(required = true) String city,
                                   @RequestParam(required = true) String industry,
                                   String function,
                                   @RequestParam(required = true) Integer type, // 月度 季度 年度
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
        Model model = dataApiService.talent_distribution_city(requestURI, params, time, type);
        return model;
    }

    /**
     * 人才职能分布
     *
     * @param city
     * @param industry
     * @param type
     * @param time
     * @param top
     * @param request
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
        Model model = dataApiService.talent_distribution_func(requestURI, params, time, type);
        return model;
    }

    /**
     * 流入 人才 的热门城市
     *
     * @return
     */
    @SystemLogAnnotation(description = "flow_in_city")
    @RequestMapping("/talent/flow/in/city/top")
    public Model flow_in(@RequestParam(required = true) Integer type,
                         @RequestParam(required = true) String time,
                         @RequestParam(required = true) Integer top,
                         HttpServletRequest request) {
        JSONObject params = new JSONObject();
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_city_flow_in_top(requestURI, params, time, type);
    }

    /**
     * 流入 人才 的热门城市
     *
     * @return
     */
    @SystemLogAnnotation(description = "flow_out_city")
    @RequestMapping("/talent/flow/out/city/top")
    public Model flow_out(@RequestParam(required = true) Integer type,
                          @RequestParam(required = true) String time,
                          @RequestParam(required = true) Integer top,
                          HttpServletRequest request) {
        JSONObject params = new JSONObject();
        params.put("top", top);
        params.put("type", type);
        String requestURI = request.getRequestURI();
        return dataApiService.talent_city_flow_out_top(requestURI, params, time, type);
    }


    /**
     * 流入 人才 的热门职能
     *
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
            ApiData data = (ApiData) model.getData();
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
     * 流出 人才 的热门职能 返回热门职能 和 职能的流出
     *
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
     * 流入 某城市的人才分析
     *
     * @param city     city：哪些城市流向 这个城市top5  func：哪些职能流向这个城市top5
     * @param industry
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
     * 人才 地域 流出
     *
     * @param city
     * @param industry city：这个城市 流出人才去哪些城市top5  func：这个城市 流出人才去哪些职位
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
     * 职能流动   流入某职能 (由于交互方式改变 暂时废弃此方法，由top方法统一调度)
     *
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
     * 人才 职能 流出 (由于交互方式改变 暂时废弃此方法，由top方法统一调度)
     *
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
     * 人才 供需指数
     *
     * @param city
     * @param industry
     * @param step
     * @param type
     * @param time
     * @param top
     * @param request
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
}
