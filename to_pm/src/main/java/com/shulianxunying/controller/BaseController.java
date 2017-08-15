package com.shulianxunying.controller;

import com.shulianxunying.cache.Menu;
import com.shulianxunying.entity.PMUser;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        if (user != null) {
            List<Menu> list = new ArrayList<>();
            Integer type = user.getType();
            if (type == 1) {
                list.add(new Menu("数据管理", "#/report", "icon-report"));
                list.add(new Menu("用户管理", "#/userInfo", "icon-user"));
                list.add(new Menu("账号管理", "#/account", "icon-account"));
            } else if (type == 2) {
                list.add(new Menu("数据管理", "#/report", "icon-report"));
                list.add(new Menu("用户管理", "#/userInfo", "icon-user"));
                list.add(new Menu("账号管理", "#/account", "icon-account"));
            } else if (type == 3) {
                list.add(new Menu("数据管理", "#/report", "icon-report"));
                list.add(new Menu("用户管理", "#/userInfo", "icon-user"));
            } else {
                modelAndView.setViewName("error");
            }
            modelMap.put("menu", list);
            modelMap.put("userinfo", user);
        } else {
            modelMap.put("menu", "没有登录");
        }

        modelAndView.addAllObjects(modelMap);

        modelAndView.setViewName(viewName);
        return modelAndView;
    }
}
