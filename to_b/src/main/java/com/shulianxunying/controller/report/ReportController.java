package com.shulianxunying.controller.report;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.shulianxunying.annotation.AuthAnnotation;
import com.shulianxunying.annotation.SystemLogAnnotation;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.controller.Model;
import com.shulianxunying.entity.SearchCache;
import com.shulianxunying.entity.User;
import com.shulianxunying.service.IReportService;
import com.shulianxunying.service.impl.DataApiServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * Created by SuChang on 2017/4/27 16:29.
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Resource
    private IReportService reportService;

    /**
     * 获取用户 下载简历 历史的列表
     *
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping("/download/list")
    public Model download_report_list(@RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.download_report_list(user.get_id(), page, 10);
        return model;
    }

    /**
     * 下载报告
     * 目前 仅做到 添加报告到下载列表，尚未提供文件下载
     *
     * @param report_name
     * @param request
     * @return
     */
    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_DOWNLOAD)
    @RequestMapping("/download")
    public void download_report(
                                HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(required = true) String[] report_name,
                                @RequestParam(required = true) String[] data_id,
                                @RequestParam(required = true) Integer[] type
//                                @RequestParam(required = true)ImageIO image,
                                ) {

        String path = request.getSession().getServletContext().getRealPath("/");
        User user = (User) request.getSession().getAttribute("user");
        JSONObject jsonObject = null;
        try {
            Model model = reportService.download_report(user, report_name, data_id,type,response,path);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        reportService.download_report(report_name,type,response,path);
    }

    /**
     * 收藏列表
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping("/collect/list")
    public Model collect_report_list(@RequestParam(required = true, defaultValue = "1") Integer page, Integer pageSize, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.collect_report_list(user.get_id(), page, 10);
        return model;
    }

    /**
     * 添加 报告收藏
     *
     * @param report_name 报告名称 把筛选条件 按顺序 value1-value2-value3 连接在一起 例如 城市-行业-职能-时间-TOP5
     * @param request
     * @return
     */
    @AuthAnnotation(auth_code = CommonParams.POWER_REPORT_COLLECT)
    @SystemLogAnnotation(description = "collect_report")
    @RequestMapping("/collect")
    public Model notic_add(@RequestParam(required = true) String report_name,
                           @RequestParam(required = true) String data_id,
                           @RequestParam(required = true) String info_id,
                           HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.add_collect_report(user, report_name, data_id, info_id);
        return model;
    }

    /**
     * 从列表中移除
     *
     * @param report_id
     * @param request
     * @return
     */
//    @RequestMapping("/collect/del")
//    public Model notic_del(@RequestParam(required = true) String report_id, HttpServletRequest request) {
//        User user = (User) request.getSession().getAttribute("user");
//        Model model = reportService.del_collect_report_one(user.get_id(), report_id);
//        return model;
//    }
    /**
     * 从列表中移除
     *
     * @param report_id  要删除的报告ID
     * @param request
     * @return
     */
    @RequestMapping("/collect/del")
    public Model collect_del(@RequestParam(required = true) String[] report_id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Model model = reportService.del_collect_report_more(user.get_id(), report_id);
        return model;
    }

    /**
     * 查看报告信息
     *
     * @param report_id
     * @param request
     * @return
     */
    @RequestMapping("/collect/info")
    public Model notic_info(@RequestParam(required = true) String report_id, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");


        Model model = reportService.collect_report_info(user.get_id(), report_id);
        return model;
    }

    @RequestMapping("/config/one")
    public Model config_one(@RequestParam(required = true) Integer report_type,
                            @RequestParam(required = true) String config_type,
                            HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        return reportService.config_info(report_type, config_type);
    }

    @RequestMapping("/config/all")
    public Model config_all(@RequestParam(required = true) Integer report_type,
                            HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        return reportService.config_info(report_type);
    }
    @RequestMapping("/get_share_data")
    public Model get_share_data(String id){
        SearchCache searchCache = reportService.find_by_id(id);
        return new DataApiServiceImpl().getDataAndInfoByPost(searchCache.getUser(),searchCache.getApi_url(),
                (HashMap<String,String>)searchCache.getParam());
    }

    @RequestMapping("/share")
    public void share(@RequestParam(required = true) String[] data_ids,
                      HttpServletRequest request,
                      HttpServletResponse response) throws Exception{
        OutputStream os = null;
        os = response.getOutputStream();
        response.setContentType("image/png");
        response.setCharacterEncoding("UTF-8");
        int width = 100;
        int height = 100;
        String format =  "png";
        for(String id :data_ids ){
//            String url = reportService.find_url_by_id(id);
            String url = "http://10.101.1.131:8087/report/get_share_data?id="+id;
//            String url = "http://www.baidu.com";
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET,"utf-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url,BarcodeFormat.QR_CODE,width,height,hints);
            File outputFile = new File(request.getSession().getServletContext().getRealPath("/")+"new.png");
            MatrixToImageWriter.writeToPath(bitMatrix,format,outputFile.toPath());
            BufferedImage image = ImageIO.read(new File(request.getSession().getServletContext().getRealPath("/")+"new.png"));
            ImageIO.write(image,"png",response.getOutputStream());
        }
    }
}


