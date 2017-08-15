package com.shulianxunying.service.impl;

import com.mongodb.MongoException;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MPMUserDao;
import com.shulianxunying.dao.impldao.MUserDao;
import com.shulianxunying.entity.PMUser;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.IUserService;
import com.shulianxunying.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by suchang on 2017/5/2 0010.
 */
@Service("userService")
public class UserServiceImpl implements IUserService {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Resource
    MUserDao userDao;
    @Resource
    MPMUserDao pmUserDao;

    @Override
    public Model login(String email_or_telphone, String password, String last_ip) {
        User user = userDao.findUser(email_or_telphone);
        if (user == null)
            return new Model(-4, "密码或账号错误");
        if (!StringUtils.equals(password, user.getPassword()))
            return new Model(-5, "密码或账号错误");
        if (!StringUtils.equals(user.getLast_ip(), last_ip)) {
            user.setLast_ip(last_ip);
            userDao.saveIp(user.get_id(), last_ip);
        }
        user.setPassword("");
        Model model = new Model();
        model.setData(user);
        return model;
    }

    @Override
    public Model regist(String email_or_phone, String password, String username, String inviter) {
        if (null != userDao.findUser(email_or_phone))
            return new Model(-1, "邮箱或电话已存在");
        PMUser pm_uer = pmUserDao.findOne_by_key_values(MPMUserDao.getCollectionName(), PMUser.class, "short_id", inviter);
        if (pm_uer == null)
            return new Model(-1, "邀请人不存在");
        User user = new User();
        user.set_id(CommonUtil.getUuid());
        if (email_or_phone.contains("@"))
            user.setEmail(email_or_phone);
        try {
            long i = Long.parseLong(email_or_phone);
            user.setTelphone(email_or_phone);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(user.getEmail()) && StringUtils.isEmpty(user.getTelphone()))
            return new Model(-1, "邮箱或电话错误");
        user.setCreateTime(new Date());
        user.setType(1);
        user.setUsername(username);
        user.setPassword(password);
        user.setPm_user_id(pm_uer.get_id());
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.add(Calendar.MONTH, 6);
        user.setExpire_time(ca.getTime());
        user.setPower_list(CommonParams.INIT_B_POWER);
        boolean b = userDao.registAccount(user);
        if (b) {
            user.setPassword("");
            return new Model().setData(user);
        } else
            return new Model(-1, "创建失败");
    }


    @Override
    public Model info(String user_id) {
        User user = userDao.find_by_id(MUserDao.getCollectionName(), user_id, User.class);
        user.setPassword("");
        user.setParent_id("");
        user.setPower_list(null);
        return new Model().setData(user);
    }

    @Override
    public Model modify_city(String user_id, String ciyt) {
        try {
            userDao.update_some_key_by_id(MUserDao.collectionName, user_id, "default_city", ciyt);
            return new Model(0, "更新成功");
        } catch (MongoException e) {

        }
        return new Model(-1, "更新失败");
    }

    @Override
    public Model modify_industry(String user_id, String industry) {
        try {
            userDao.update_some_key_by_id(MUserDao.collectionName, user_id, "default_industry", industry);
            return new Model(0, "更新成功");
        } catch (MongoException e) {

        }
        return new Model(-1, "更新失败");
    }

    @Override
    public Model modify_sub_industry(String user_id, String sub_industry) {
        try {
            userDao.update_some_key_by_id(MUserDao.collectionName, user_id, "default_sub_industry", sub_industry);
            return new Model(0, "更新成功");
        } catch (MongoException e) {

        }
        return new Model(-1, "更新失败");
    }


}
