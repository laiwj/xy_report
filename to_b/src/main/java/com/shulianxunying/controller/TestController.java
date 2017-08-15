package com.shulianxunying.controller;

import com.shulianxunying.annotation.RequestLimit;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jiangwei on 2016/8/10 0010.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestLimit(count=2,time=60000)
    @RequestMapping(value = "/ttt")
    public ModelAndView userStaticLogin(HttpServletRequest request, HttpServletResponse resp,ModelMap modelMap) throws Exception {
        modelMap.put("data","张三");
        return new ModelAndView("error");
    }

}
