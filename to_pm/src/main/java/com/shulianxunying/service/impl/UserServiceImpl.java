package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by jiangwei on 2016/8/10 0010.
 */
@Service("userService")
public class UserServiceImpl implements IUserService {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Resource
    MPMUserDao pmuserDao;
    @Resource
    MUserDao userDao;

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
    public Model b_user_list(PMUser op_user, String user_id, int page, int pageSize) {
        Integer op_userType = op_user.getType();
        long count = 0;
        List<User> users = null;
        if (op_userType == 3) {
            count = userDao.getCount(user_id);
            users = userDao.user_list(user_id, page, pageSize);

        } else if (op_userType == 2) {
            users = new ArrayList<>();
            List<PMUser> pm_users = pmuserDao.user_list(user_id, 0, pageSize);
            for (PMUser pmUser : pm_users) {
                List<User> users1 = userDao.user_list(pmUser.get_id(), 0, pageSize);
                users.addAll(users1);
            }
            JSONObject data = new JSONObject();
            int size = users.size();
            int max = page * pageSize;
            int min = (page - 1) * pageSize;
            if (size <= page * pageSize)
                max = size;

            if ((page - 1) * pageSize > size)
                min = size - pageSize;
            if (min < 0)
                min = 0;
            data.put("data", users.subList(min, max));
            data.put("count", size);
            return new Model().setData(data);
        } else if (op_userType == 1) {
            users = userDao.user_list(null, page, pageSize);
            count = userDao.getCount(null);
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
                if(!pmuserDao.addPower(user_id, power))
                    return new Model(-1,"添加权限失败");
                if(!pmuserDao.removePower(user_id, power_del))
                    return new Model(-1,"删除权限失败");
                return new Model();
            } else if (source.equals("b")) {
                if(!userDao.addPower(user_id, power))
                    return new Model(-1,"添加权限失败");
                if(!userDao.removePower(user_id, power_del))
                    return new Model(-1,"删除权限失败");
                return new Model();
            }
            return new Model(-1, "修改失败");
        } else {
            return new Model(CommonParams.RESULTCODE_AUTH_FAIL);
        }
    }

    @Override
    public Model removePower(PMUser op_user, String user_id, List<Integer> power) {
        PMUser pm_user = null;
        User b_user = null;
        boolean canUse = false;
        if (op_user.getType() == 1)
            canUse = true;
        else if (op_user.getType() == 2) {
            pm_user = pmuserDao.find_by_id(MPMUserDao.collectionName, user_id, PMUser.class);
            if (pm_user.getParent_id().equals(op_user.get_id())) {
                canUse = true;
            }
        } else if (op_user.getType() == 3) {
            b_user = userDao.find_by_id(MUserDao.collectionName, user_id, User.class);
            if (b_user.getPm_user_id().equals(op_user.get_id())) {
                canUse = true;
            }
        }
        if (canUse) {
            if (op_user.getType() == 2 || op_user.getType() == 1) {
                if (pmuserDao.removePower(user_id, power)) {
                    return new Model();
                }
            } else if (op_user.getType() == 3)
                if (userDao.removePower(user_id, power))
                    return new Model();
            return new Model(-1, "修改失败");
        } else {
            return new Model(CommonParams.RESULTCODE_AUTH_FAIL);
        }
    }


}
