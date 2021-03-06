package com.shulianxunying.decorator;

import com.alibaba.fastjson.JSONObject;
import com.shulianxunying.entity.PMUser;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by jiangwei on 2016/7/15 0015.
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static Map<String, HttpSession> map = new HashMap<String, HttpSession>();
    private static Set< String> set= new HashSet<>();

    public static Map<String, HttpSession> getMap() {
        return map;
    }
    public static Set<String> getSet() {
        return set;
    }


    /**
     * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，SpringMVC中的Interceptor拦截器是链式的，可以同时存在
     * 多个Interceptor，然后SpringMVC会根据声明的前后顺序一个接一个的执行，而且所有的Interceptor中的preHandle方法都会在
     * Controller方法调用之前调用。SpringMVC的这种Interceptor链式结构也是可以进行中断的，这种中断方式是令preHandle的返
     * 回值为false，当preHandle的返回值为false的时候整个请求就结束了。
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String requestURI = httpServletRequest.getRequestURI();
        PMUser login = (PMUser) httpServletRequest.getSession().getAttribute("user");
        if (!requestURI.startsWith("/user/login") && null == login) {
            if (httpServletRequest.getMethod().equals("GET")) {
                httpServletResponse.reset();
                httpServletResponse.setStatus(302);
                httpServletResponse.setHeader("location", "/");
                httpServletResponse.sendRedirect(String.format("%s%s", httpServletRequest.getContextPath(), "/"));
            }
            else {
                httpServletResponse.reset();
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
                JSONObject object = new JSONObject();
                object.put("code", -10);
                object.put("msg", "登录失效");
                object.put("flag", false);
                httpServletResponse.getWriter().write(object.toJSONString());
                httpServletResponse.setStatus(200);
            }
            return false;
        }
        else if(!requestURI.startsWith("/user/login") && null != login){
            if(getSet().size() != 0 && getSet().contains(httpServletRequest.getSession().getId())){
                HttpSession session = httpServletRequest.getSession();
                httpServletResponse.reset();
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
                JSONObject object = new JSONObject();
                object.put("code", -11);
                object.put("msg", "你的账号在其它地方登录,该账号不能同时登录");
                object.put("flag", false);
                httpServletResponse.getWriter().write(object.toJSONString());
                httpServletResponse.setStatus(200);
                return false;
            }
//            return false;
        }
        return true;
    }

    /**
     * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之
     * 后，也就是在Controller的方法调用之后执行，但是它会在DispatcherServlet进行视图的渲染之前执行，也就是说在这个方法中你可以对ModelAndView进行操
     * 作。这个方法的链式结构跟正常访问的方向是相反的，也就是说先声明的Interceptor拦截器该方法反而会后调用，这跟Struts2里面的拦截器的执行过程有点像，
     * 只是Struts2里面的intercept方法中要手动的调用ActionInvocation的invoke方法，Struts2中调用ActionInvocation的invoke方法就是调用下一个Interceptor
     * 或者是调用action，然后要在Interceptor之前调用的内容都写在调用invoke之前，要在Interceptor之后调用的内容都写在调用invoke方法之后。
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    /**
     *
     * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行，
     * 这个方法的主要作用是用于清理资源的，当然这个方法也只能在当前这个Interceptor的preHandle方法的返回值为true时才会执行。
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
