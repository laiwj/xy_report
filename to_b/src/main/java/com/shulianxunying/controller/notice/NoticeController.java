package com.shulianxunying.controller.notice;

import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.INoticeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by SuChang on 2017/4/27 16:14.
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private INoticeService noticeService;

    @RequestMapping("/list")
    public Model notic(@RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = noticeService.notice_list(user.get_id(), null, page, 10);
        return model;
    }

    @RequestMapping("/click")
    public Model notic(@RequestParam(required = true) String notic_id) {
        Model model = noticeService.click(notic_id);
        return model;
    }
}
