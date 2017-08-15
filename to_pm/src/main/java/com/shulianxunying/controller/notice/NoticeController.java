package com.shulianxunying.controller.notice;

import com.shulianxunying.annotation.AuthAnnotation;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.controller.Model;
import com.shulianxunying.service.INoticeService;
import com.shulianxunying.util.ApiParamsUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by SuChang on 2017/4/28 15:25.
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private INoticeService noticeService;

    @RequestMapping("/list")
    public Model notic(@RequestParam(required = true) String user_id, @RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize, HttpServletRequest request) {
        Model model = noticeService.notice_list(user_id, null, page, 10);
        return model;
    }

    @AuthAnnotation(auth_code = CommonParams.POWER_NOTIC)
    @RequestMapping("/send/all")
    public Model send_notice_to_all(@RequestParam(required = true) String msg,String mess_type) {
        return noticeService.send_notice_to_all(msg,mess_type);
    }

    @AuthAnnotation(auth_code = CommonParams.POWER_NOTIC)
    @RequestMapping("/send")
    public Model send_notice_to_some(@RequestParam(required = true) String msg, @RequestParam(required = true) String user_id,String mess_type) {
        Set<String> user_ids = ApiParamsUtils.splitParam(user_id);
        return noticeService.send_notice_to_users(msg, user_ids,mess_type);
    }
}
