package com.shulianxunying.controller.api;

import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.IDataApiService;
import com.shulianxunying.util.ApiParamsUtils;
import com.shulianxunying.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

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
     * @param industry
     * @param type     时间类型  1:周 2：月 3：季 4：年 目前只用 2 3 4
     * @param request
     * @param top      top几
     * @return
     */
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
        // 对入参进行 去重和排序 一定程度上保证参数有效性
        Set<String> citySet = ApiParamsUtils.splitParam(city, "全国");
        industry = industry.replace("互联网金融","金融");
        Set<String> industrySet = ApiParamsUtils.splitParam(industry, "互联网全行业");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Model model = dataApiService.talent_distribution(user,
                ApiParamsUtils.joinParam(citySet),
                ApiParamsUtils.joinParam(industrySet),
                cf,
                type, sdf.format(new Date()), top);
        return model;
    }

    /**
     * 人才 流入/流出
     *
     * @param city  人才地域流动 需要填写 (此参数为空 则为 top几的热门城市，填写了城市则为 对应城市的人才流动)
     * @param industry  选择行业
     * @param type   时间类型  1:周 2：月 3：季 4：年 目前只用 2 3 4
     * @param direction  in or out 代表流入或流出
     * @param cf     city or func 代表是 地域流动 还是 职能流动
     * @param top    top几
     * @param request
     * @return
     */
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
        if (!StringUtils.equals(direction, "in") && !StringUtils.equals(direction, "out"))
            return new Model(-3);
        if (!StringUtils.equals(cf, "city") && !StringUtils.equals(cf, "func"))
            return new Model(-3);
        if (type < 0 || type > 4)
            return new Model(-3);
        if (top != 5 && top != 10)
            return new Model(-3);
        Set<String> citySet = ApiParamsUtils.splitParam(city, "全国");
        industry = industry.replace("互联网金融","金融");
        Set<String> industrySet = ApiParamsUtils.splitParam(industry);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Model model = dataApiService.talent_flow(user,
                ApiParamsUtils.joinParam(citySet),
                ApiParamsUtils.joinParam(industrySet),
                type, sdf.format(new Date()),
                direction,
                cf,
                top
        );
        return model;
    }


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
                top
        );
        return model;
    }
}
