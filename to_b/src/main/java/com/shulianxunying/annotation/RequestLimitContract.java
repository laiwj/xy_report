package com.shulianxunying.annotation;

import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.decorator.RequestLimitException;
import com.shulianxunying.util.CommonUtil;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 请求限制注解实现
 * Created by jiangwei on 2016/8/30 0030.
 */
@Aspect
@Component
public class RequestLimitContract {

    private Logger logger = Logger.getLogger(RequestLimit.class);

    private Map<String, Long> redisTemplate = new HashMap<>();

    //Service层切点
    @Pointcut("@annotation(com.shulianxunying.annotation.RequestLimit)")
    public void limitAspect() {
    }

    @Before("limitAspect() && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint, RequestLimit limit) throws RequestLimitException {

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request == null) {
                throw new RequestLimitException("方法中缺失HttpServletRequest参数");
            }
            String ip = CommonUtil.getIP(request);
            String url = request.getRequestURL().toString();
            final String key = "req_limit_".concat(url).concat(ip);

            long count = (redisTemplate.get(key) == null ? 0l : redisTemplate.get(key)) + 1;
            redisTemplate.put(key, count);
            //long count = redisTemplate.opsForValue().increment(key, 1);//自增1

            if (count > limit.count()) {
                logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
                TimerTask task = new TimerTask() {    //创建一个新的计时器任务。
                    @Override
                    public void run() {
                        redisTemplate.remove(key);
                        System.out.println(redisTemplate);
                    }
                };
                CommonParams.timer.schedule(task, limit.time());//多久后执行
                throw new RequestLimitException();
            }
        } catch (RequestLimitException e) {
            throw e;
        } catch (Exception e) {
            logger.error("发生异常: ", e);
        }
    }
}
