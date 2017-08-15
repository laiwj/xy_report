package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MPMUserDao;
import com.shulianxunying.dao.impldao.MUserDao;
import com.shulianxunying.decorator.LoginInterceptor;
import com.shulianxunying.entity.ApiUrlBuilder;
import com.shulianxunying.entity.EmailContent;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.IUserService;
import com.shulianxunying.util.CommonUtil;
import com.shulianxunying.util.HttpUtils.HttpHelper2;
import com.shulianxunying.util.HttpUtils.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 用户操作相关实现
 */


@Service("userService")
public class UserServiceImpl implements IUserService {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Resource
    MPMUserDao pmuserDao;
    @Resource
    MUserDao userDao;
    @Resource(name = "httpHelper")
    HttpHelper2 httpHelper;
    @Resource(name = "apiUrlBuilder")
    ApiUrlBuilder apiUrlBuilder;

    @Override
    public Model login(String email_or_telphone, String password, String last_ip) {
        PMUser user = pmuserDao.findUser(email_or_telphone);
        if (user == null)
            return new Model(-4, "账号不存在");
        if (!StringUtils.equals(password, user.getPassword()))
            return new Model(-4, "密码或账号错误");
        if (user.getStatus() < 0)
            return new Model(-4, "账号异常");
        if (!StringUtils.equals(user.getLast_ip(), last_ip)) {
            user.setLast_ip(last_ip);
            pmuserDao.saveIp(user.get_id(), user.getLast_ip());
        }
        user.setPassword("");
        Model model = new Model();
        model.setData(user);
        return model;
    }

    @Override
    public Model regist() {
        PMUser user = new PMUser();
        user.set_id("00000000000000000000000000000000");
        String[] strings = CommonUtil.shortUrl(user.get_id());
        user.setShort_id(strings[0]);
        user.setUsername("admin");
        user.setPassword("slxy123456");
        user.setTelphone("admin");
        user.setType(1);
        user.setParent_id("");
        user.setCreateTime(new Date());
        HashSet<Integer> powers = new HashSet<>();
        powers.add(CommonParams.POWER_ADD_ACCOUNT);
        powers.add(CommonParams.POWER_ADD_INFO);
        powers.add(CommonParams.POWER_DATA_MODFIY);
        powers.add(CommonParams.POWER_REPORT_CONFIG_MANAGE);
        powers.add(CommonParams.POWER_NOTIC);
        powers.add(CommonParams.POWER_REPORT_1);
        powers.add(CommonParams.POWER_REPORT_2);
        powers.add(CommonParams.POWER_REPORT_3);
        powers.add(CommonParams.POWER_REPORT_4);
        powers.add(CommonParams.POWER_REPORT_5);
        powers.add(CommonParams.POWER_REPORT_6);
        user.setPower_list(powers);
        boolean b = pmuserDao.registAccount(user);
        if (b) {
            user.setPassword("");
            return new Model().setData(user);
        } else
            return new Model(-1, "创建失败");
    }

    @Override
    public Model password(PMUser user, String user_id, String old_password, String password, String type) {
        boolean canUse = false;
        PMUser pmuser = null;
        User buser = null;
        if (user.getType() == 1)
            canUse = true;
        if (!canUse) {
            if ("pm".equals(type)) {
                pmuser = pmuserDao.find_by_id(MPMUserDao.collectionName, user_id, PMUser.class);
                if (pmuser != null && pmuser.getParent_id().equals(user.get_id()))
                    canUse = true;
            } else if ("b".equals(type)) {
                buser = userDao.find_by_id(MUserDao.collectionName, user_id, User.class);
                if (user != null && buser.getPm_user_id().equals(user.get_id()))
                    canUse = true;
            }
        }
        if (canUse) {
            if ("pm".equals(type)) {
                pmuserDao.password(pmuser, old_password, password);
            } else if ("b".equals(type)) {
                userDao.password(buser, old_password, password);
            }
        } else {
            return new Model(-6);
        }

        return null;
    }

    @Override
    public Model regist(PMUser op_user, String email_or_phone, String password, String username) {
        Integer op_userType = op_user.getType();
        if (op_userType >= 3)
            return new Model(CommonParams.RESULTCODE_AUTH_FAIL);
        if (null != pmuserDao.findUser(email_or_phone))
            return new Model(-1, "邮箱或电话已存在");
        PMUser user = new PMUser();
        user.set_id(CommonUtil.getUuid());
        String[] strings = CommonUtil.shortUrl(user.get_id());
        user.setShort_id(strings[0]);
        if (email_or_phone.contains("@"))
            user.setEmail(email_or_phone);
        try {
            long i = Long.parseLong(email_or_phone);
            user.setTelphone(email_or_phone);
        } catch (NumberFormatException e) {
        }
        user.setParent_id(op_user.get_id());
        user.setCreateTime(new Date());
        user.setType(op_userType + 1);
        user.setPower_list(CommonParams.INIT_PM_POWER);
        user.setPassword(password);
        user.setUsername(username);
        boolean b = pmuserDao.registAccount(user);
        if (b) {
            user.setPassword("");
            return new Model().setData(user);
        } else
            return new Model(-1, "创建失败");

    }


    @Override
    public Model info(String user_id) {
        PMUser user = pmuserDao.find_by_id(MPMUserDao.getCollectionName(), user_id, PMUser.class);
        user.setPassword("");
//        user.setParent_id("");
        user.setPower_list(null);
        return new Model().setData(user);
    }

    @Override
    public Model user_list(PMUser op_user, String user_id, int page, int pageSize) {
        PMUser user = pmuserDao.find_by_id(MPMUserDao.getCollectionName(), user_id, PMUser.class);
        if (op_user.getType() > user.getType())
            return new Model(CommonParams.RESULTCODE_AUTH_FAIL);
        long count = 0;
        if (op_user.getType() == 3) {
            count = userDao.getCount(user_id);
            List<User> users = userDao.user_list(user_id, page, pageSize);
            JSONObject data = new JSONObject();
            data.put("data", users);
            data.put("count", count);
            return new Model().setData(data);
        } else if (op_user.getType() < 3 && op_user.getType() > 0) {
            count = pmuserDao.userCount(user_id);
            List<PMUser> users = pmuserDao.user_list(user_id, page, pageSize);
            List<JSONObject> userOut = new ArrayList<>();
            for (PMUser pmUser : users) {
                JSONObject object = JSONObject.parseObject(JSON.toJSONString(pmUser));
                Integer type = pmUser.getType();
                long l = 0;
                if (type < 3 && type > 0) {
                    l = pmuserDao.userCount(pmUser.get_id());
                } else if (type == 3) {
                    l = userDao.getCount(pmUser.get_id());
                }
                object.put("count", l);
                userOut.add(object);
            }
            JSONObject data = new JSONObject();
            data.put("data", userOut);
            data.put("count", count);
            return new Model().setData(data);
        }
        return new Model(-6);
    }

    @Override
    public Model b_user_list(PMUser  op_user, String user_id,String pm_user_id, int page, int pageSize) {
        Integer op_userType = op_user.getType();
        List<User> users = null;
        long count = 0;
        if(CommonUtil.isNotEmpty(pm_user_id)){
            users = userDao.user_list_by_parent_id(pm_user_id, page, pageSize);
            count =users.size();
            JSONObject data = new JSONObject();
            data.put("data", users);
            data.put("count", count);
            return new Model().setData(data);
        }

        if (op_userType == 3) {
            count = userDao.getCount(user_id);
            users = userDao.user_list(user_id, page, pageSize);
        } else if (op_userType == 2) {
            List<PMUser> pm_users = pmuserDao.user_list(user_id, 0, pageSize);
            HashSet<String> set = new HashSet<>();
            for (PMUser pmUser : pm_users) {
                set.add(pmUser.get_id());
            }
            users = userDao.user_list(set, page, pageSize);
            count = userDao.getCount(set);
        } else if (op_userType == 1) {
            users = userDao.user_list("", page, pageSize);
            count = userDao.getCount("");
        }
        JSONObject data = new JSONObject();
        data.put("data", users);
        data.put("count", count);
        return new Model().setData(data);
    }

    @Override
    public Model addPower(PMUser op_user, String user_id, String source, List<Integer> power, List<Integer> power_del) {
        PMUser pm_user = null;
        User b_user = null;
        if (source.equals("pm"))
            pm_user = pmuserDao.find_by_id(MPMUserDao.collectionName, user_id, PMUser.class);
        else if (source.equals("b"))
            b_user = userDao.find_by_id(MUserDao.collectionName, user_id, User.class);
        else
            return new Model(-1, "操作错误");
        boolean canUse = false;
        if (op_user.getType() == 1) {
            canUse = true;
        } else if (op_user.getType() == 2) {
            canUse = true;
        } else if (op_user.getType() == 3) {
            if (b_user.getPm_user_id().equals(op_user.get_id())) {
                canUse = true;
            }
        }
        if (canUse) {
            if (source.equals("pm")) {
                HttpSession httpSession = LoginInterceptor.getMap().get(user_id);
                PMUser user = null;
                if (httpSession != null) {
                    user = (PMUser) httpSession.getAttribute("user");
                }
                if (!pmuserDao.addPower(user_id, power))
                    return new Model(-1, "添加权限失败");
                if (user != null)
                    user.getPower_list().addAll(power);
                if (!pmuserDao.removePower(user_id, power_del))
                    return new Model(-1, "删除权限失败");
                if (user != null)
                    user.getPower_list().removeAll(power_del);
                return new Model();
            } else if (source.equals("b")) {
                if (!userDao.addPower(user_id, power))
                    return new Model(-1, "添加权限失败");
                // todo 调用B端 实时修改权限的接口
                if (!userDao.removePower(user_id, power_del))
                    return new Model(-1, "删除权限失败");
                // todo 调用B端 实时修改权限的接口

                return new Model();
            }
            return new Model(-1, "修改失败");
        } else {
            return new Model(CommonParams.RESULTCODE_AUTH_FAIL);
        }
    }

    /**
     * 发送邮件
     *
     * @param op_user
     * @param email
     * @param username
     * @param info
     * @return
     */
    @Override
    public Model sendEmail(PMUser op_user, String email, String username, String info) throws IOException {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(info))
            return new Model(-1, "参数错误");
        // todo 构造info的内容
        // ...
        HttpClient httpClient = null;
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(1000 * 60 * 2);
        builder.setConnectTimeout(1000 * 60 * 1);
        builder.setSocketTimeout(1000 * 60 * 1);
        RequestConfig requestConfig = builder.build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        Page page = null;
        try {
//            HashMap<String, String> params = new HashMap<>();
//            params.put("email", "549917764@qq.com");
//            params.put("subject", "测试的邮件发送主题");
//            params.put("content", "填写的测试内容，希望不要被屏蔽掉");
            String subject = new String("测试主题".getBytes(), "gb2312");
            subject = MimeUtility.encodeText(subject, MimeUtility.mimeCharset("gb2312"), null);
            String url = EmailContent.mail_url + "?email=" + email + "&subject=" + subject + "&content=" + info;
            page = httpHelper.doGet(httpClient, url);
        } catch (Exception e) {
            return new Model(-1, "发送邮件失败");
        }
        if (page != null && "".equals(page.getRawText()))
            return new Model();
        return new Model(-1, "发送邮件失败");
    }

    public Model notice_user_real_time(String user_id,String power, String power_del,String api_url){
        HttpClient httpClient = null;
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(1000 * 60 * 5);
        builder.setConnectTimeout(10000 * 60 * 1);
        builder.setSocketTimeout(10000 * 60 * 1);
        RequestConfig requestConfig = builder.build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        String url = apiUrlBuilder.buildFinalUrl(api_url);
        Page page = null;
        Logger logger = Logger.getLogger(DataApiServiceImpl.class.getName());
        try {
            page = httpHelper.doGet(httpClient, url);
        } catch (Exception e) {
            logger.error("api web error\r\n" + e.getMessage());
            return new Model(-1, "获取数据失败");
        }
        return new Model();
    }
}





