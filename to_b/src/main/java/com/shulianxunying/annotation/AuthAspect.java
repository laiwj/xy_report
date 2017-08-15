package com.shulianxunying.annotation;

import com.shulianxunying.entity.User;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;



/**
 * 权限验证注解
 */
@Aspect
@Component
public class AuthAspect {

    private Logger logger = Logger.getLogger(AuthAspect.class);

    //Service层切点
    @Pointcut("@annotation(com.shulianxunying.annotation.AuthAnnotation)")
    public void authAspect() {
    }

    /**
     * 判断是否包含对应权限，如过假则抛出 权限异常
     * @param joinPoint
     * @param limit
     * @throws AuthException
     */
    @Before("authAspect() && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint, AuthAnnotation limit) throws AuthException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            User user = (User) request.getSession().getAttribute("user");
            if (user != null) {
                boolean flag = user.getPower_list().contains(limit.auth_code());
                if (flag)
                    return;
            }
        }
        throw new AuthException();
    }
}
