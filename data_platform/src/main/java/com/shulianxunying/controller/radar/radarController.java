package com.shulianxunying.controller.radar;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.controller.Model;
import com.shulianxunying.service.IDataRadarService;
import org.bson.Document;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Created by zhang on 2017/6/12.
 * 人才雷达-成都区域
 */
@RestController
@RequestMapping("/radar")
public class radarController {

    @Resource
    private IDataRadarService dataRadarService;

    /**
     * 检查更新(返回表名和条数，客户端自行判断是否更新)
     * @param year      报告时间-年
     * @param month     报告时间-月
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "check_update")
    @RequestMapping("/check/update")
    public Model check_update(@RequestParam(required = true) String year,
                              @RequestParam(required = true) String month,
                              @RequestParam(defaultValue = "2") Integer t,
                              HttpServletRequest request) {
        String start_time = year + "-" + month + "-01";
        return dataRadarService.check_update(start_time, t);
    }


    /**
     * 人才流动
     * @param city      流入或流出城市
     * @param year      报告-年
     * @param month     报告-月
     * @param top       Top指数
     * @param t         报告周期类型,1:周 2:月 3:季 4:年
     * @param step      流入(in)/流出(out)
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "talent_flow")
    @RequestMapping("/talent/flow/{in_or_out}")
    public Model talent_flow(@RequestParam(required = true) String city,
                             @RequestParam(required = true) String year,
                             @RequestParam(required = true) String month,
                             @RequestParam(defaultValue = "0") Integer top,
                             @RequestParam(defaultValue = "2") Integer t,
                             @PathVariable("in_or_out") String step,
                             HttpServletRequest request) {
        String start_time = year + "-" + month + "-01";
        String api_url = request.getRequestURI();
        JSONObject params = new JSONObject();
        params.put("city", city);
        params.put("start_time", start_time);
        params.put("top", top);
        params.put("t", t);
        String key;
        Document query = new Document();
        if (step.equals("in")) {
            key = "pre_city";
            query.append("living_city", city);
            query.append("pre_city", new Document("$ne", city));
        } else if (step.equals("out")) {
            key = "living_city";
            query.append("pre_city", city);
            query.append("living_city", new Document("$ne", city));
        } else
            return new Model(-2);

        return dataRadarService.talent_flow(query, key, api_url, params);
    }


    /**
     * 人才需求分布
     * @param city      城市
     * @param year      报告-年
     * @param month     报告-月
     * @param top       Top指数
     * @param t         报告周期类型,1:周 2:月 3:季 4:年
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "talent_demand_distribute")
    @RequestMapping("/talent/demand/distribute")
    public Model talent_demand_distribute(@RequestParam(required = true) String city,
                                          @RequestParam(required = true) String year,
                                          @RequestParam(required = true) String month,
                                          @RequestParam(defaultValue = "0") Integer top,
                                          @RequestParam(defaultValue = "2") Integer t,
                                          HttpServletRequest request) {
        String start_time = year + "-" + month + "-01";
        String api_url = request.getRequestURI();
        Document query = new Document();
        query.append("city", city);
        JSONObject params = new JSONObject();
        params.put("city", city);
        params.put("start_time", start_time);
        params.put("top", top);
        params.put("t", t);
        return dataRadarService.talent_demand_distribute(api_url, query, params);
    }

    /**
     * 人才分布
     * @param year      报告-年
     * @param month     报告-年
     * @param top       Top指数
     * @param t         报告周期类型,1:周 2:月 3:季 4:年
     * @param request   客户端请求对象
     * @return
     */
    @SystemLogAnnotation(description = "talent_distribute")
    @RequestMapping("/talent/distribute")
    public Model talent_distribute(@RequestParam(required = true) String year,
                                   @RequestParam(required = true) String month,
                                   @RequestParam(defaultValue = "0") Integer top,
                                   @RequestParam(defaultValue = "2") Integer t,
                                   HttpServletRequest request) {
        String start_time = year + "-" + month + "-01";
        String api_url = request.getRequestURI();
        JSONObject params = new JSONObject();
        params.put("start_time", start_time);
        params.put("top", top);
        params.put("t", t);
        return dataRadarService.talent_distribute(api_url, params);
    }
}
