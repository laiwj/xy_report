package com.shulianxunying.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.*;
import com.shulianxunying.entity.*;
import com.shulianxunying.service.IReportService;
import com.shulianxunying.util.CommonUtil;
import com.shulianxunying.util.ZipUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.zip.ZipInputStream;


/**
 * Created by SuChang on 2017/4/27 16:31.
 */
@Service("report")
public class ReportServiceImpl implements IReportService {

    @Resource
    MDownloadReportDao downloadReportDao;
    @Resource
    MColletReportDao colletReportDao;
    @Resource
    MReportInfoDao reportInfoDao;
    @Resource
    MTempDataDao tempDataDao;
    @Resource
    MReportConfigDao reportConfigDao;
    @Resource
    MSearchDao mSearchDao;


    @Override
    public Model download_report_list(String user_id, Integer page, Integer pageSize) {
        List<DownloadReport> user_download_report = downloadReportDao.find_user_download_report(user_id, page, pageSize);
        long count = 0;
//        if (page == 1)
        count = downloadReportDao.count_user_download_report(user_id);
        Model model = new Model();
        JSONObject data = new JSONObject();
        data.put("data", user_download_report);
        data.put("count", count);
        data.put("page",page);
        model.setData(data);
        return model;
    }

    @Override
    public Model download_report(User user, String[] report_name, String[] data_id,Integer[] type, HttpServletResponse response, String path) throws Exception {
        DownloadReport downloadReport = new DownloadReport();
        JSONObject error = null;
        boolean flag = true;
        File file = new File(path,"DataReport");
        file.mkdir();
        //如果目录里的文件数多余一个 就压缩 否则就进入目录 下载 pdf
        for(int i=0;i<data_id.length;i++){
            TempData apiData = tempDataDao.find_one(data_id[i]);
            JSONObject param = create_param(apiData.getParams());
            param.put("api_url", apiData.getApi_url());
            ReportInfo reportInfo = reportInfoDao.findParams_by_key_values(MReportInfoDao.getCollectionName(), ReportInfo.class, param);
            if (apiData == null) {
                return new Model(-1, "参数错误,下载失败");
            }
            downloadReport.set_id(CommonUtil.getUuid());
            // 生成短id 用于前端展示
            downloadReport.setShort_id(CommonUtil.shortUrl(downloadReport.get_id())[0]);
            downloadReport.setDownload_time(new Date());
            downloadReport.setApi_url(apiData.getApi_url());
            if (reportInfo != null)
                downloadReport.setInfo(reportInfo.getInfo());
            else
                downloadReport.setInfo("");
            downloadReport.setData(apiData.getData());
            downloadReport.setParams(apiData.getParams());
            downloadReport.setReport_name(report_name[i]);
            downloadReport.setUser_id(user.get_id());
            downloadReport.setPm_user_id(user.getPm_user_id());

            create_file(report_name[i], type[i], path+"/DataReport", downloadReport.getData(), reportInfo,path);

//            download_report(report_name[i], type[i], response, path+"/DataReport", downloadReport.getData(), reportInfo);
            if (!downloadReportDao.add_user_download_report(downloadReport)){
                error.put(i+"","添加到下载列表失败");
                flag =false;
            }
//            return new Model(-1, "添加到下载列表失败");
        }
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");

        if(file.list().length>1){
            //TODO
            //压缩
            response.addHeader("Content-Disposition", "attachment; filename=\""
                    + "DataReport.zip" + "\"");

//            ZipUtil.compress(file, new ZipOutputStream(new FileOutputStream(new File(path + "DataReport.zip"))), path + "/DataReport");
//            response.getOutputStream(new FileOutputStream(new File(path + "DataReport.zip")));
            ZipUtil.compress(path+"DataReport",path+"DataReport.zip");
            OutputStream sos = response.getOutputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(path+"DataReport.zip"), "utf-8");
            InputStreamReader inputStreamReader = new InputStreamReader(new ZipInputStream( new FileInputStream(path+"DataReport.zip")), "utf-8");
            IOUtils.copy(inputStreamReader, sos, "utf-8");
        }else if(file.list().length == 1){
            //TODO
            //直接下载file里的pdf文件
        }
        if (flag == false){
            return new Model().setData(error);
        }
        return new Model();
    }

    @Override
    public Model collect_report_list(String user_id, Integer page, Integer pageSize) {
        List<CollectReport> user_collect_report = colletReportDao.find_user_collect_report(user_id, page, pageSize);
        long count = 0;
//        if (page == 1)
        count = colletReportDao.count_user_collect_report(user_id);
        Model model = new Model();
        JSONObject data = new JSONObject();
        data.put("data", user_collect_report);
        data.put("count", count);
        data.put("page",page);
        model.setData(data);
        return model;
    }

    @Override
    public Model del_collect_report_one(String user_id, String report_id) {
        boolean flag = colletReportDao.del_user_collect_report(user_id, report_id);
        return new Model(flag);
    }

    @Override
    public Model del_collect_report_more(String user_id, String[] report_id) {
        boolean flag = false;
        for(String id : report_id){
            if(colletReportDao.del_user_collect_report(user_id, id)){
                flag = true;
            }else {
                flag = false;
                return new Model(-1, "删除失败");
            }
        }
        return new Model();
    }

    @Override
    public Model collect_report_info(String user_id, String report_id) {
        CollectReport collectReport = colletReportDao.find_by_id(MColletReportDao.collectionName, report_id, CollectReport.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            collectReport.setTime(sdf.format(collectReport.getCollect_time()));
        }catch (Exception e){
            System.out.print(e);
        }

        if (collectReport != null && user_id.equals(collectReport.getUser_id()))
            return new Model().setData(collectReport);
        return new Model(-1, "获取报告失败");
    }

    @Override
    public CollectReport find_report_info(String user_id, String data_id, String report_name) {
        return colletReportDao.find_report_info(user_id, data_id, report_name);

    }

    @Override
    public Model add_collect_report(User user, String report_name, String data_id, String info_id) {

        CollectReport collectReport = new CollectReport();
        TempData apiData = tempDataDao.find_one(data_id);
        SearchCache searchCache = null;
        String id ="";
        if(apiData == null){
           searchCache = (SearchCache) mSearchDao.find_by_id(MSearchDao.getCollectionName(), data_id,SearchCache.class);
            id = searchCache.get_id();
        }else{
            id = apiData.get_id();
        }

        if (CommonUtil.isNotEmpty(find_report_info(user.get_id(),id, report_name))) {
            return new Model(-1, "该报告已经收藏");
        }
        ReportInfo reportInfo = reportInfoDao.find_by_id(MReportInfoDao.getCollectionName(), info_id, ReportInfo.class);
        if (apiData == null && searchCache == null) {
            return new Model(-1, "参数错误，收藏失败");
        }
        collectReport.set_id(CommonUtil.getUuid());
        // 生成短id 用于前端展示
        collectReport.setShort_id(CommonUtil.shortUrl(collectReport.get_id())[0]);
        collectReport.setCollect_time(new Date());
//        collectReport.setApi_url(apiData.getApi_url());
        if (reportInfo != null) {
            collectReport.setInfo(reportInfo.getInfo());
        } else
            collectReport.setInfo("");
//        collectReport.setData(apiData.getData());
//        collectReport.setParams(apiData.getParams());
        collectReport.setReport_name(report_name);
        collectReport.setUser_id(user.get_id());
        collectReport.setPm_user_id(user.getPm_user_id());
//        collectReport.setData_id(apiData.get_id());
        if(apiData == null){
            collectReport.setApi_url(searchCache.getApi_url());
            collectReport.setData(searchCache.getData());
            collectReport.setParams((JSONObject) searchCache.getParam());
            collectReport.setData_id(id);
        }else{
            collectReport.setData(apiData.getData());
            collectReport.setParams(apiData.getParams());
            collectReport.setData_id(apiData.get_id());
            collectReport.setApi_url(apiData.getApi_url());
        }

        if (colletReportDao.add_user_collect_report(collectReport))
            return new Model();
        return new Model(-1, "收藏失败");
    }

    @Override
    public Model config_info(Integer report_type, String config_type) {
        ReportConfig one = reportConfigDao.find_one_config(report_type, config_type);
        if (one != null)
            return new Model().setData(one);
        return new Model(-1, config_type + " 查询失败");
    }

    @Override
    public Model config_info(Integer report_type) {
        List<ReportConfig> all_config = reportConfigDao.find_all_config(report_type);
        if (all_config != null)
            return new Model().setData(all_config);
        return new Model(-1, "查询配置失败");
    }

    @Override
    public String find_url_by_id(String data_id) {
        SearchCache cache = (SearchCache) mSearchDao.find_by_id(MSearchDao.collectionName, data_id,SearchCache.class);
        return cache.getApi_url();
    }

    @Override
    public SearchCache find_by_id( String id) {
        return (SearchCache)mSearchDao.find_by_id(MSearchDao.getCollectionName(),id,SearchCache.class);

    }


    public void download_report(String report_name, int type, HttpServletResponse response, String path, Object data, ReportInfo info) {

        Document document = new Document();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");

        if (type == 1) {
            // xls
            response.addHeader("Content-Disposition", "attachment; filename=\""
                    + report_name+".xls" + "\"");
        } else {
            if (type == 2) {
                response.addHeader("Content-Disposition", "attachment; filename=\""
                        + report_name+".pdf" + "\"");
                try {
                    create_pdf(response, report_name, path, info);
                    IOUtils.copy(new InputStreamReader(new FileInputStream(new File(report_name+"df"))),response.getOutputStream(), "utf-8");
                    File file = new File(report_name+".pdf");
                    if(file.isFile()){
                        file.delete();
                    }

//                PdfWriter pdfWriter = PdfWriter.getInstance(document,response.getOutputStream());
//                document.open();
//                document.add(new Paragraph("Some content here"));
//                document.addTitle("this is a title");
//                document.addAuthor("X_Y");
//                document.addSubject("this is subject");
//                document.addKeywords("keyword");
//                document.addCreationDate();
//                document.addCreator("xy.com");
//                document.close();
//            OutputStream sos = response.getOutputStream();

                    // 获取原文件名
//            InputStream resourceAsStream = ReportController.class.getResourceAsStream("test.pdf");
//            if (resourceAsStream == null) {
//                sos.write("没有这个文件".getBytes());
//                return;
//            }
//            String fileName = new String("test.pdf".getBytes(), "ISO8859-1");
//            // 设置下载文件名
//            response.addHeader("Content-Disposition", "attachment; filename=\""
//                    + fileName + "\"");
                    //向客户端输出文件
//            InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, "utf-8");
//                    IOUtils.copy(inputStreamReader, sos, "utf-8");
                } catch (Exception e) {
//            return new Model(-1,"链接异常");
                    System.out.println(e);
                }
            } else {
                response.addHeader("Content-Disposition", "attachment; filename=\""
                        + report_name+".pdf" + "\"");
                try {
                    response.getWriter().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void create_pdf(HttpServletResponse response,String report_name, String path, ReportInfo info) throws Exception {

        Document document = new Document(PageSize.A4.rotate());
        List<File> files = new ArrayList<>();
//        new FileOutputStream();
        PdfWriter.getInstance(document,new FileOutputStream(new File(path+"/"+report_name+".pdf")));
        response.getOutputStream();
        //设置显示中文
            BaseFont bfChineseF = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font fontChinese16 = new Font(bfChineseF, 16, Font.NORMAL);
            Font fontChinese18 = new Font(bfChineseF, 18, Font.BOLD, BaseColor.RED);
            document.open();

            //标题
            PdfPTable table = new PdfPTable(2);
            PdfPCell celltile = new PdfPCell(new Paragraph("人才价值引擎-岗位薪酬分析", fontChinese16));
            celltile.setVerticalAlignment(Element.ALIGN_BOTTOM);
            celltile.setHorizontalAlignment(Element.ALIGN_CENTER);
            celltile.setBorder(0);

            Image logo = Image.getInstance(path + "/images/reportlogo.png");
            int[] width = {80, 20};
            table.setWidths(width);
            table.getDefaultCell().setBorder(0);
            table.addCell(celltile);
            table.addCell(logo);
            document.add(table);

            Paragraph blankRow1 = new Paragraph(18f, " ", fontChinese16);
            document.add(blankRow1);
            //报告
            PdfPTable tableItem = new PdfPTable(1);
            PdfPCell item1 = new PdfPCell(new Paragraph("分析岗位", fontChinese18));

            item1.setBorder(0);
            item1.setBorderWidthRight(1);
            item1.setHorizontalAlignment(Element.ALIGN_CENTER);
            item1.setVerticalAlignment(Element.ALIGN_CENTER);
            tableItem.addCell(item1);
            if (CommonUtil.isNotEmpty(info)){
                for(String item : (String[])info.getParams().get("name")){
                    tableItem.addCell(item);
                }
            }

            PdfPCell item2 = new PdfPCell(new Paragraph("分析数据筛选", fontChinese18));
            item2.setBorder(0);
            item2.setBorderWidthRight(1);
            item2.setHorizontalAlignment(Element.ALIGN_CENTER);
            item2.setVerticalAlignment(Element.ALIGN_CENTER);
            if (CommonUtil.isNotEmpty(info)){
                for(String item : (String[])info.getParams().get("industry")){
                    tableItem.addCell(item);
                }
                for(String item : (String[])info.getParams().get("city")){
                    tableItem.addCell(item);
                }
            }

            tableItem.addCell(item2);
            PdfPCell item3 = new PdfPCell(new Paragraph("分析周期", fontChinese18));
            item3.setBorder(0);
            item3.setBorderWidthRight(1);
            item3.setHorizontalAlignment(Element.ALIGN_CENTER);
            item3.setVerticalAlignment(Element.ALIGN_CENTER);
            tableItem.addCell(item3);

            PdfPTable tabletext = new PdfPTable(2);

            int[] widthText = {20, 80};
            tabletext.setWidths(widthText);
            tabletext.getDefaultCell().setBorder(0);
            tableItem.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabletext.addCell(tableItem);
            Image reportImage = Image.getInstance(path + "/images/reportlogo.png");
            tabletext.addCell(reportImage);
            document.add(tabletext);

            Paragraph blankRow2 = new Paragraph(18f, " ", fontChinese16);
            document.add(blankRow2);
            //报告说明
            if(CommonUtil.isNotEmpty(info)){
                Paragraph report = new Paragraph(info.getInfo(), fontChinese16);
                document.add(report);
            }

        document.close();

    }
    public void create_pdf(String report_name, String path, ReportInfo info,String imgpath) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        List<File> files = new ArrayList<>();
        PdfWriter.getInstance(document,new FileOutputStream(new File(path,report_name+".pdf")));
        //设置显示中文
        BaseFont bfChineseF = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontChinese16 = new Font(bfChineseF, 16, Font.NORMAL);
        Font fontChinese18 = new Font(bfChineseF, 18, Font.BOLD, BaseColor.RED);
        document.open();
        //标题
        PdfPTable table = new PdfPTable(2);
        PdfPCell celltile = new PdfPCell(new Paragraph("人才价值引擎-岗位薪酬分析", fontChinese16));
        celltile.setVerticalAlignment(Element.ALIGN_BOTTOM);
        celltile.setHorizontalAlignment(Element.ALIGN_CENTER);
        celltile.setBorder(0);

        Image logo = Image.getInstance(imgpath + "/images/reportlogo.png");
        int[] width = {80, 20};
        table.setWidths(width);
        table.getDefaultCell().setBorder(0);
        table.addCell(celltile);
        table.addCell(logo);
        document.add(table);

        Paragraph blankRow1 = new Paragraph(18f, " ", fontChinese16);
        document.add(blankRow1);
        //报告
        PdfPTable tableItem = new PdfPTable(1);
        PdfPCell item1 = new PdfPCell(new Paragraph("分析岗位", fontChinese18));

        item1.setBorder(0);
        item1.setBorderWidthRight(1);
        item1.setHorizontalAlignment(Element.ALIGN_CENTER);
        item1.setVerticalAlignment(Element.ALIGN_CENTER);
        tableItem.addCell(item1);
        if (CommonUtil.isNotEmpty(info)){
            try {// 有些报告没有岗位信息
                for(String item : (String[])info.getParams().get("name")){
                    tableItem.addCell(item);
                }
            }catch (Exception e){

            }
        }

        PdfPCell item2 = new PdfPCell(new Paragraph("分析数据筛选", fontChinese18));
        item2.setBorder(0);
        item2.setBorderWidthRight(1);
        item2.setHorizontalAlignment(Element.ALIGN_CENTER);
        item2.setVerticalAlignment(Element.ALIGN_CENTER);
        if (CommonUtil.isNotEmpty(info)){
            for(Object item : (JSONArray)info.getParams().get("industry")){
                tableItem.addCell(item.toString());
            }
            for(Object item : (JSONArray)info.getParams().get("city")){
                tableItem.addCell(item.toString());
            }
        }

        tableItem.addCell(item2);
        PdfPCell item3 = new PdfPCell(new Paragraph("分析周期", fontChinese18));
        item3.setBorder(0);
        item3.setBorderWidthRight(1);
        item3.setHorizontalAlignment(Element.ALIGN_CENTER);
        item3.setVerticalAlignment(Element.ALIGN_CENTER);
        tableItem.addCell(item3);

        PdfPTable tabletext = new PdfPTable(2);

        int[] widthText = {20, 80};
        tabletext.setWidths(widthText);
        tabletext.getDefaultCell().setBorder(0);
        tableItem.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabletext.addCell(tableItem);
        Image reportImage = Image.getInstance(imgpath + "/images/reportlogo.png");
        tabletext.addCell(reportImage);
        document.add(tabletext);

        Paragraph blankRow2 = new Paragraph(18f, " ", fontChinese16);
        document.add(blankRow2);
        //报告说明
        if(CommonUtil.isNotEmpty(info)){
            Paragraph report = new Paragraph(info.getInfo(), fontChinese16);
            document.add(report);
        }

        document.close();

    }
    public void create_file(String report_name, int type, String path, Object data, ReportInfo info,String imgpath) throws Exception {
        if(type == 1){
            //TODo
            //生成excel
        }else if(type == 2){
            //TODO 生成pdf
            create_pdf(report_name,path,info,imgpath);
        }else if(type == 3 ){
            //TODO 生成zip压缩包
        }
    }

    public JSONObject create_param(JSONObject params) {
        JSONObject param = new JSONObject();
        for (String key : params.keySet()) {
            if (!"".equals(key) && !"".equals(params.getString(key))) {
                if (params.get(key) instanceof JSONArray) {
                    JSONArray array = (JSONArray) params.get(key);
                    param.put("params." + key, Arrays.asList(array.toArray()));
                } else {
                    param.put("params." + key, params.get(key));
                }
            }
        }
        return param;
    }
}
