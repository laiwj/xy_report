package com.shulianxunying.controller.user;

import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.service.IUserService;
import com.shulianxunying.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
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
    @Resource(name = "mailSender")
    JavaMailSenderImpl mailSender;

    Logger logger = Logger.getLogger(UserController.class);

    @SystemLogAnnotation(description = "login")
    @RequestMapping("/login")
    public Model login(@RequestParam(required = true) String account, @RequestParam(required = true) String password, HttpServletRequest request) {
        Model model = userService.login(account, password, CommonUtil.getIP(request));
        request.getSession().setAttribute("user", model.getData());
        return model;
    }

    @SystemLogAnnotation(description = "regist")
    @RequestMapping("/regist")
    public Model regist(@RequestParam(required = true) String account, @RequestParam(required = true) String password, @RequestParam(required = true) String username, HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        if(StringUtils.isEmpty(account) || StringUtils.isEmpty(password) || StringUtils.isEmpty(username))
            return new Model(-2);
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

    @RequestMapping("/list/b")
    public Model b_user_list(String user_id, @RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize, HttpServletRequest request) throws ServletException, IOException {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        if (StringUtils.isEmpty(user_id)) {
            user_id = user.get_id();
        } else {
            Model info = userService.info(user_id);
            user = (PMUser) info.getData();
        }
        return userService.b_user_list(user, user_id, page, 10);
    }


    @RequestMapping("/info")
    public Model info(String user_id, HttpServletRequest request) {
        if (StringUtils.isEmpty(user_id)) {
            PMUser user = (PMUser) request.getSession().getAttribute("user");
            user_id = user.get_id();
        }
        Model model = userService.info(user_id);
        return model;
    }

    @RequestMapping("/power/add")
    public Model addPower(String user_id,
                          @RequestParam(required = true)String source,
                          @RequestParam(required = true) String power,
                          @RequestParam(required = true) String power_del,
                          HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        List<Integer> powerList = new ArrayList<>();
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

        return userService.addPower(user, user_id,source, powerList,power_delList);
    }

    @RequestMapping("/power/remove")
    public Model removePower(String user_id, String power, HttpServletRequest request) {
        PMUser user = (PMUser) request.getSession().getAttribute("user");
        List<Integer> powerList = new ArrayList<>();
        for (String s : power.split(",")) {
            try {
                powerList.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
            }
        }
        return userService.removePower(user, user_id, powerList);
    }

    @RequestMapping("/send/email")
    public Model sendEmail(@RequestParam(required = true) String email, @RequestParam(required = true) String username, @RequestParam(required = true) String info) {
        MimeMessage mimeMsg = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMsg, false, "utf-8");
            mimeMsg.setContent(info, "text/html");
            helper.setTo(email);
            helper.setSubject("注册邀请");
            helper.setFrom("service@suchang.site");
            mailSender.send(mimeMsg);
            return new Model();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return new Model(-1);
    }

}
