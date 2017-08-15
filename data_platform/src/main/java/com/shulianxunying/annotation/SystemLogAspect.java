package com.shulianxunying.annotation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.controller.Model;
import com.shulianxunying.dao.impldao.MLogDao;
import com.shulianxunying.entity.LogInfo;
import com.shulianxunying.util.CommonUtil;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 切点类
 * Created by jiangwei on 2016/8/29 0029.
 */
@Aspect
@Component
public class SystemLogAspect {
    private static final Logger logger = Logger.getLogger(SystemLogAspect.class.getName());
    //注入Service用于把日志保存数据库
    @Resource
    private MLogDao logDao;

    //Controller层切点
    @Pointcut("@annotation(com.shulianxunying.annotation.SystemLogAnnotation)")
    public void controllerAspect() {
    }

    @Around("controllerAspect()")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        // 获得入参
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();
        Map<String, String> params = new HashMap<>();
        Enumeration enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            /*String str = paraName + ":" + request.getParameter(paraName)+"__";
            params += str;*/
            params.put(paraName, request.getParameter(paraName));
        }
        LogInfo logInfo = new LogInfo();
        long inputTime = System.currentTimeMillis();
        Object returnValue = null;
        // 执行方法 获得返回
        try {
            returnValue = point.proceed();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : e.getStackTrace()) {
                sb.append(element.toString() + "\r\n");
            }
            logInfo.setE(sb.toString());
            logger.error(e.getMessage(), e);
        }
        long outputTime = System.currentTimeMillis();

        logInfo.set_id(new ObjectId(new Date()).toString());
        logInfo.setExecMethod(point.getSignature().toLongString());
        logInfo.setParams(params);
        String requestIp = CommonUtil.getIP(request);
        logInfo.setIp(requestIp);
        logInfo.setInputTime(new Date(inputTime));
        double consume = (outputTime - inputTime) / 1000.0;
        logInfo.setReturnTime(new Date(outputTime));
        logInfo.setConsumeTime(consume);
        logInfo.setUrl(requestURI);

        logger.debug("目标方法名：" + point.getSignature().getDeclaringTypeName() + " " + point.getSignature().getName());
        logger.debug("来源ip：" + requestIp);
        try {
            String returnV = "";
            if (returnValue != null) {
                returnV = JSONObject.toJSONString(returnValue);
                logInfo.setResult(returnV);
                logger.debug("返回值：" + returnV);
            } else {
                Model model = new Model(-5);
                returnValue = model;
            }
            logDao.addLog(logInfo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        outputTime = System.currentTimeMillis();
        logger.info(requestURI + " 请求总共费时(s)：" + (outputTime - inputTime) / 1000.0 + " 操作费时(s)：" + consume + " inputParameter:" + JSON.toJSONString(params));
        return returnValue;
    }
}
