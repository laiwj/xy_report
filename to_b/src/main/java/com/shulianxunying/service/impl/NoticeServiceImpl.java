package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MNoticeDao;
import com.shulianxunying.entity.NoticeInfo;
import com.shulianxunying.service.INoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by SuChang on 2017/4/27 16:13.
 */
@Service("notice")
public class NoticeServiceImpl implements INoticeService {

    @Resource
    MNoticeDao noticeDao;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Model notice_list(String user_id, Boolean status, Integer page, Integer pageSize) {
        List<NoticeInfo> user_notices = noticeDao.find_user_notices(user_id, status, page, pageSize);
        long count = 0;
//        if (page == 1)
        count = noticeDao.count_user_notices(user_id, status);
        Model model = new Model();
        JSONObject data = new JSONObject();
        data.put("data", user_notices);
        data.put("count", count);
        model.setData(data);
        return model;
    }

    @Override
    public Model click(String notice_id) {
        boolean flag = noticeDao.update_some_key_by_id(MNoticeDao.getCollectionName(), notice_id, "isClick", true, "click_time", sdf.format(new Date()));
        return new Model(flag);
    }
}
