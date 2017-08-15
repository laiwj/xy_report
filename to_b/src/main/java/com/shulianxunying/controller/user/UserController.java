package com.shulianxunying.controller.user;

import com.shulianxunying.annotation.RequestLimit;
import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.cache.Menu;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.IUserService;
import com.shulianxunying.util.CommonUtil;
import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by jiangwei on 2016/8/10 0010.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    Logger logger = Logger.getLogger(UserController.class);


    /**
     * 用户登录
     *
     * @param account  可以传 email或者telphone
     * @param password
     * @param request
     * @return 返回 账号不存在 或者 密码错误
     */
    @SystemLogAnnotation(description = "login")
//    @RequestLimit(count=2,time=60000)
    @RequestMapping("/login")
    public Model login(@RequestParam(required = true) String account,
                       @RequestParam(required = true) String password,
                       HttpServletRequest request) {
        Model model = userService.login(account, password, CommonUtil.getIP(request));
        request.getSession().setAttribute("user", model.getData());
        return model;
    }

    /**
     * 注册 主账号
     *
     * @return
     */
    @SystemLogAnnotation(description = "regist")
    @RequestMapping("/regist")
    public Model regist(@RequestParam(required = true) String account,
                        @RequestParam(required = true) String inviter,
                        @RequestParam(required = true) String password,
                        @RequestParam(required = true) String username,
                        HttpServletRequest request) {
        Model model = userService.regist(account, password, username, inviter);
        request.getSession().setAttribute("user", model.getData());
        return model;
    }


    @SystemLogAnnotation(description = "logout")
    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();
        request.getRequestDispatcher("/login.vm").forward(request, response);
    }


    @SystemLogAnnotation(description = "user_info")
    @RequestMapping("/info")
    public Model info(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null)
            return new Model().setData(user);
        Model model = userService.info(user.get_id());
        return model;
    }

    @SystemLogAnnotation(description = "modify_user_default_city")
    @RequestMapping("/modify/city")
    public Model modify_user_default_city(@RequestParam(required = true) String city, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = userService.modify_city(user.get_id(), city);
        if (model.getCode() == 0) {
            user.setDefault_city(city);
            request.getSession().setAttribute("user", user);
        }
        return model;
    }

    @SystemLogAnnotation(description = "modify_user_default_industry")
    @RequestMapping("/modify/industry")
    public Model modify_user_default_industry(@RequestParam(required = true) String industry, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = userService.modify_industry(user.get_id(), industry);
        if (model.getCode() == 0) {
            user.setDefault_industry(industry);
            request.getSession().setAttribute("user", user);
        }
        return model;
    }

    @SystemLogAnnotation(description = "modify_user_default_sub_industry")
    @RequestMapping("/modify/subindustry")
    public Model modify_user_default_sub_industry(@RequestParam(required = true) String sub_industry, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = userService.modify_sub_industry(user.get_id(), sub_industry);
        if (model.getCode() == 0) {
            user.setDefault_sub_industry(sub_industry);
            request.getSession().setAttribute("user", user);
        }
        return model;
    }

    @RequestMapping("/menu")
    public ModelAndView menuList(HttpServletRequest request, ModelMap modelMap) {
        ModelAndView modelAndView = new ModelAndView();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            HashSet<Integer> power_list = user.getPower_list();
            List<Menu> list = new ArrayList<>();
            if (power_list.contains(CommonParams.POWER_REPORT_1))
                list.add(new Menu("人才分布", "#/node", "icon-tile-four"));
            if (power_list.contains(CommonParams.POWER_REPORT_2))
                list.add(new Menu("人才流动", "#/node", "icon-tile-four"));
            if (power_list.contains(CommonParams.POWER_REPORT_3))
                list.add(new Menu("供需指数", "#/node", "icon-tile-four"));
            if (power_list.contains(CommonParams.POWER_REPORT_DOWNLOAD))
                list.add(new Menu("下载管理", "#/node", "icon-tile-four"));
            if (power_list.contains(CommonParams.POWER_REPORT_COLLECT))
                list.add(new Menu("收藏管理", "#/node", "icon-tile-four"));
            modelMap.put("menu", list);
        } else {
            modelMap.put("menu", "没有登录");
        }
        modelAndView.addAllObjects(modelMap);
        modelAndView.setViewName("menu");
        return modelAndView;
    }


}
