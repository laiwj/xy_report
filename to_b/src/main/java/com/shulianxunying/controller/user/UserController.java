package com.shulianxunying.controller.user;

import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.controller.Model;
import com.shulianxunying.decorator.LoginInterceptor;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.IUserService;
import com.shulianxunying.util.ApiParamsUtils;
import com.shulianxunying.util.CommonUtil;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        Model model = userService.login(account, password, CommonUtil.getIP(request),request.getSession().getId());
        request.getSession().setAttribute("user", model.getData());
        return model;
    }

    /**
     * 注册 主账号
     *
     * @param account  邮箱或者电话
     * @param inviter  PM端邀请者id
     * @param password
     * @param username
     * @param request
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
        return model;
    }


    @SystemLogAnnotation(description = "logout")
    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();
        request.getRequestDispatcher("/login").forward(request, response);
    }

    /**
     * 查看自己个人信息
     *
     * @param request
     * @return
     */
    @SystemLogAnnotation(description = "user_info")
    @RequestMapping("/info")
    public Model info(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null)
            return new Model().setData(user);
        Model model = userService.info(user.get_id());
        return model;
    }

    /**
     * 修改默认城市
     *
     * @param city
     * @param request
     * @return
     */
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

    /**
     * 修改默认行业
     *
     * @param industry
     * @param request
     * @return
     */
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

    /**
     * 修改默认子行业
     *
     * @param sub_industry
     * @param request
     * @return
     */
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

    /**
     * 修改密码
     * @param request
     * @param userId
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @RequestMapping("/modify/password")
    public Model updatePassword(HttpServletRequest request, @RequestParam(required = true) String userId,
                                @RequestParam(required = true) String oldPwd,
                                @RequestParam(required = true) String newPwd) {
        Model model = userService.update_password(userId, oldPwd, newPwd);
        return model;

    }

    @RequestMapping("/send/email")
    public Model sendEmail(HttpServletRequest request,@RequestParam(required = true) String email,
                                                      @RequestParam(required = true) String userName,
                                                      @RequestParam(required = true) String info,
                                                      @RequestParam(required = true)String subject) throws IOException{
        User user = (User)request.getSession().getAttribute("user");
        return userService.sendEmail(user,email,userName,info,subject);
    }

    @RequestMapping("/defualt_industry")
    public Model get_defualt_industry(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        return userService.get_defualt_industry(user.get_id());
    }

    @RequestMapping("/userInfo")
    public Model get_uesr_info(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        Model model = new Model();
        if(CommonUtil.isEmpty(user)){
            return new Model(-1,"未登录");
        }else{
           return userService.info(user.get_id());

        }
    }
    @RequestMapping("/upadte/power")
    public Model upadate_power(String user_id,String power, String power_del){
//        request.get
        User user = (User) LoginInterceptor.getMap().get(user_id).getAttribute("user");
        if(user!=null){
            user.getPower_list().addAll(ApiParamsUtils.listItemToInteger(power.split(",")));
            user.getPower_list().removeAll(ApiParamsUtils.listItemToInteger(power_del.split(",")));
        }
        return new Model();
    }
}
