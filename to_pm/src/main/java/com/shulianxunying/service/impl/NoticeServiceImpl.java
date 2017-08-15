package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MNoticeDao;
import com.shulianxunying.dao.impldao.MPMUserDao;
import com.shulianxunying.entity.NoticeInfo;
import com.shulianxunying.service.INoticeService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SuChang on 2017/4/27 16:13.
 */
@Service("notice")
public class NoticeServiceImpl implements INoticeService {

    @Resource
    MNoticeDao noticeDao;
    @Resource
    MPMUserDao userDao;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Model notice_list(String user_id, Boolean status, Integer page, Integer pageSize) {
        List<NoticeInfo> user_notices = noticeDao.find_user_notices(user_id, status, page, pageSize);
        long count = 0;
        if (page == 1)
            count = noticeDao.count_user_notices(user_id, status);
        Model model = new Model();
        JSONObject data = new JSONObject();
        data.put("data", user_notices);
        data.put("count", count);
        model.setData(data);
        return model;
    }

    @Override
    public Model send_notice_to_users(String msg, Set<String> user_ids) {
        int step = 50;
        ArrayList<Document> data = new ArrayList<>();
        int count = 0;
        for (String id : user_ids) {
            Date date = new Date();
            NoticeInfo noticeInfo = new NoticeInfo();
            noticeInfo.setUser_id(id);
            noticeInfo.set_id(new ObjectId(date, count).toHexString());
            noticeInfo.setMsg(msg);
            noticeInfo.setTime(sdf.format(date));
            data.add(Document.parse(JSON.toJSONString(noticeInfo)));
            if (count != 0 && count % step == 0) {
                noticeDao.getCollection(MNoticeDao.collectionName).insertMany(data);
                data.clear();
            }
        }
        if (data.size() > 0)
            noticeDao.getCollection(MNoticeDao.collectionName).insertMany(data);
        return new Model();
    }

    @Override
    public Model send_notice_to_all(String msg) {
        MongoCursor<Document> iterator = userDao.getCollection(MPMUserDao.collectionName).find().projection(new Document("_id", 1)).iterator();
        int step = 50;
        ArrayList<Document> data = new ArrayList<>();
        int count = 0;
        while (iterator.hasNext()) {
            Document next = iterator.next();
            String id = next.getString("_id");
            Date date = new Date();
            NoticeInfo noticeInfo = new NoticeInfo();
            noticeInfo.setUser_id(id);
            noticeInfo.set_id(new ObjectId(date, count).toHexString());
            noticeInfo.setMsg(msg);
            noticeInfo.setTime(sdf.format(date));
            data.add(Document.parse(JSON.toJSONString(noticeInfo)));
            if (count != 0 && count % step == 0) {
                noticeDao.getCollection(MNoticeDao.collectionName).insertMany(data);
                data.clear();
            }
        }
        return new Model();
    }


}
