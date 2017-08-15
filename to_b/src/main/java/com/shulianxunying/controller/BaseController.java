package com.shulianxunying.controller;

import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.cache.Menu;
import com.shulianxunying.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by SuChang on 2017/4/28 18:03.
 */
@RestController
@RequestMapping("/")
public class BaseController {

    @RequestMapping(value = "/{viewName}")
    public ModelAndView userStaticLogin(@PathVariable("viewName") String viewName, HttpServletRequest request, ModelMap modelMap) throws Exception {
        ModelAndView modelAndView = new ModelAndView(viewName);
        // if(viewName.startsWith("menu")){
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            HashSet<Integer> power_list = user.getPower_list();
            List<Menu> list = new ArrayList<>();
            List<Menu> accountMenu = new ArrayList<>();
            if (power_list.contains(CommonParams.POWER_REPORT_1))
                list.add(new Menu("人才分布", "/talentdistribution", "icon-tile-four"));
            if (power_list.contains(CommonParams.POWER_REPORT_2))
                list.add(new Menu("人才流动", "/talentflow", "icon-tile-four"));
            if (power_list.contains(CommonParams.POWER_REPORT_3))
                list.add(new Menu("供需指数", "/supplydemand", "icon-tile-four"));
            if (viewName.startsWith("account")) {
                if (power_list.contains(CommonParams.POWER_REPORT_DOWNLOAD))
                    accountMenu.add(new Menu("下载管理", "/node", "icon-tile-four"));
                if (power_list.contains(CommonParams.POWER_REPORT_COLLECT))
                    accountMenu.add(new Menu("收藏管理", "/node", "icon-tile-four"));
                modelMap.put("default_industry", StringUtils.isEmpty(user.getDefault_industry())? "互联网全行业":user.getDefault_industry());
                modelMap.put("default_city",user.getDefault_city());
                modelMap.put("default_sub_industry",user.getDefault_sub_industry());
            }
            modelMap.put("menu", list);
            modelMap.put("accountMenu", accountMenu);
            modelMap.put("userinfo", user);
        } else {
            modelMap.put("menu", "没有登录");
        }
        modelAndView.addAllObjects(modelMap);
        // }
        return modelAndView;
    }
}
