package com.shulianxunying.controller.user;

import com.shulianxunying.annotation.AuthAnnotation;
import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.service.IUserService;
import com.shulianxunying.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

    @SystemLogAnnotation(description = "login")
    @RequestMapping("/login")
    public Model login(@RequestParam(required = true) String account, @RequestParam(required = true) String password, HttpServletRequest request) {
        Model model = userService.login(account, password, CommonUtil.getIP(request));
        request.getSession().setAttribute("user", model.getData());
        return model;
    }
    @AuthAnnotation(auth_code = CommonParams.POWER_ADD_ACCOUNT)
    @SystemLogAnnotation(description = "regist")
    @RequestMapping("/regist")
    public Model regist(@RequestParam(required = true) String account, @RequestParam(required = true) String password, @RequestParam(required = true) String username, HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password) || StringUtils.isEmpty(username))
            return new Model(-8,"公司名称,手机/email,密码 不能为空");
        Model model = userService.regist(user, account, password, username);
        return model;
    }

    /**
     * @param user_id      修改此id的密码
     * @param password     需要修改的密码
     * @param old_password 原始密码
     * @param type         pm or b
     * @param request
     * @return
     */
    @SystemLogAnnotation(description = "modify_password")
    @RequestMapping("/password")
    public Model password(String user_id, @RequestParam(required = true) String old_password, @RequestParam(required = true) String password, String type, HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        if (StringUtils.isEmpty(user_id)) {
            user_id = user.get_id();
        }
        Model model = userService.password(user, user_id, old_password, password, type);
        return model;
    }

    /**
     * 注册一个初始超管账户
     *
     * @param request
     * @return
     */
    @SystemLogAnnotation(description = "regist_admin")
    @RequestMapping("/regist/admin")
    public Model registAdmin(HttpServletRequest request) {
//        PMUser user = (PMUser) request.getSession().getAttribute("user");
        Model model = userService.regist();
        return model;
    }

    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();
        request.getRequestDispatcher("/html/login.vm").forward(request, response);
    }

    @RequestMapping("/toIndex")
    public ModelAndView toIndex() {
        return new ModelAndView("index");
    }

    /**
     * 用户列表
     *
     * @param user_id
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/list")
    public Model user_list(String user_id, @RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize, HttpServletRequest request) throws ServletException, IOException {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        if (StringUtils.isEmpty(user_id)) {
            user_id = user.get_id();
        } else {
            Model info = userService.info(user_id);
            user = (PMUser) info.getData();
        }
        return userService.user_list(user, user_id, page, 10);
    }
    /**
     * B端用户列表
     *
     * @param user_id
     * @param page
     * @param pageSize
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/list/b")
    public Model b_user_list(String user_id, @RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize,
                             HttpServletRequest request,String pm_user_id) throws ServletException, IOException {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        if (StringUtils.isEmpty(user_id)) {
            user_id = user.get_id();
        } else {
            Model info = userService.info(user_id);
            user = (PMUser) info.getData();
        }
        return userService.b_user_list(user, user_id,pm_user_id, page, 10);
    }

    /**
     * 用户个人信息
     *
     * @param user_id
     * @param request
     * @return
     */
    @RequestMapping("/info")
    public Model info(String user_id, HttpServletRequest request) {
        if (StringUtils.isEmpty(user_id)) {
            PMUser user = (PMUser) request.getSession().getAttribute("user");
            user_id = user.get_id();
        }
        Model model = userService.info(user_id);
        return model;
    }

    /**
     * 修改权限
     *
     * @param user_id
     * @param source    用户来源 添加 b or pm，标识该user_id的来源
     * @param power     添加的权限
     * @param power_del 修改的权限
     * @param request
     * @return
     */
    @AuthAnnotation(auth_code = CommonParams.POWER_AUTH)
    @SystemLogAnnotation(description = "power_add")
    @RequestMapping("/power/add")
    public Model addPower(String user_id,
                          @RequestParam(required = true) String source,
                          @RequestParam(required = true) String power,
                          @RequestParam(required = true) String power_del,
                          HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        List<Integer> powerList = new ArrayList<>();
        // todo 严格检查 权限参数，如有问题，封停账号
        for (String s : power.split(",")) {
            try {
                powerList.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
            }
        }
        List<Integer> power_delList = new ArrayList<>();
        for (String s : power_del.split(",")) {
            try {
                power_delList.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
            }
        }


        return userService.addPower(user, user_id, source, powerList, power_delList);
    }

    /**
     * 发送邮件
     *
     * @param email
     * @param username
     * @param info
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("/send/email")
    public Model sendEmail(@RequestParam(required = true) String email,
                           @RequestParam(required = true) String username,
                           @RequestParam(required = true) String info,
                           HttpServletRequest request) throws IOException {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        return userService.sendEmail(user, email, username, info);
    }

    /**
     * @param pmuser_id 管理员Id
     * @param user_id 用户Id
     * @param permission 要添加的权限值
     */

//    @AuthAnnotation(auth_code = )// 判断管理员是有权限 修改用户的权限
    @RequestMapping("/add/permission")
    public Model add_permission(HttpServletRequest request,
                                   @RequestParam(required = true) String pmuser_id,
                                   @RequestParam(required = true) String user_id,
                                   @RequestParam(required = true) String permission){
        Model model = null;
        return model;

    }

}
