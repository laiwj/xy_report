package com.shulianxunying.decorator;

import com.alibaba.fastjson.JSON;
import com.shulianxunying.controller.Model;
import com.shulianxunying.util.JsonUtil;
import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class GlobalException extends ExceptionHandlerExceptionResolver{
    /*创建日志对象*/
    private static final Logger log = Logger.getLogger(GlobalException.class);

    /**
     * 异常处理
     * @param request
     * @param response
     * @param handlerMethod
     * @param exception
     * @return
     */
    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        Model model = new Model();
        try{
            Map<String,String> params = new HashMap<>();
            Enumeration enu = request.getParameterNames();
            while (enu.hasMoreElements()){
                String paraName = (String) enu.nextElement();
                params.put(paraName,request.getParameter(paraName));
            }
            //System.out.println(params);

            StackTraceElement[] exceptionElement = exception.getStackTrace();
            if (exceptionElement.length>0){
                log.info(exception.toString());
                log.error(String.format("ServletPath:[%s] - Message:[%s] - Parameters:[%s] - Method[%s]", request.getServletPath(), exception.getMessage(), JsonUtil.entityToJson(params), exceptionElement[0].toString()));
            }else {
                log.error(String.format("ServletPath:[%s] - Message:[%s] - Parameters:[%s]", request.getServletPath(), exception.getMessage(), JsonUtil.entityToJson(params)));
            }
        }catch (Exception ex){
            if(ex instanceof RequestLimitException){

            }else
                ex.printStackTrace();
        }

        String reqType = request.getHeader("X-Requested-With");
        if(null != reqType && reqType.equals("XMLHttpRequest")){//ajax请求
            //System.out.println(request.getServletPath() + ":" + "ajax请求");

            model.setCode(-1);
            model.setData("The server runs out.");
            //model.setData(exception.toString());
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                //System.out.println(JSON.toJSONString(model));
//                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json");
                outputStream.println(JSON.toJSONString(model));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ModelAndView();

        }else {//普通请求，跳转到
            //System.out.println(request.getServletPath()+":"+"普通请求");
            return new ModelAndView("error");
        }
    }

    /**
     * 往HttpServletResponse中手动添加Json数据
     * @param response
     * @param data
     */
    private void jsonReturn(HttpServletResponse response, Object data){
        /**
         * 设置Response的编码方式为UTF-8
         */
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        try {
            String json = JsonUtil.entityToJson(data);
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
