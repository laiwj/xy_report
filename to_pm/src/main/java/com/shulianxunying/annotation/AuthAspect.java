package com.shulianxunying.annotation;

import com.shulianxunying.entity.PMUser;
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

    @Before("authAspect() && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint, AuthAnnotation limit) throws AuthException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            PMUser user = (PMUser) request.getSession().getAttribute("user");
            if (user != null) {
                boolean flag = user.getPower_list().contains(limit.auth_code());
                if (flag)
                    return;
            }
        }
        throw new AuthException("没有权限");
    }
}
