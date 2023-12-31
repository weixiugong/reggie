package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 1、获取本次请求的URI
 * 2、判断本次请求是否需要处理
 * 3、如果不需要处理，则直接放行
 * 4、判断登录状态，如果已登录，则直接放行
 * 5、如果未登录则返回未登录结果
 */
@WebFilter(urlPatterns = "/*", filterName = "LoginCheckFilter")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    /**
     * 不需要拦截的 URI
     */
    public static final String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/user/sendMes", // 移动端发送短信
            "/user/login" // 移动端登录
    };



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        // 不需要处理
        if(noNeedProcess(requestURI)){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        // 账号密码登录
        Long employeeId = (Long) request.getSession().getAttribute("employee");
        if (employeeId != null) {
            log.info("当前用户已登录，用户id为：{}", employeeId);

            // 在LogincheckFilter的doFilter方法中调用BaseContext来设置当前登录用户的id
            BaseContext.setCurrentId( employeeId);

            filterChain.doFilter(request, response);
            return;
        }

        // 手机号登录
        Long userId = (Long) request.getSession().getAttribute("user");
        if(userId != null){
            log.info("用户已登录，用户id为：{}",userId);

            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOT LOGIN")));
    }

    /**
     * 不需要处理返回true
     * @param requestURI
     * @return
     */
    private boolean noNeedProcess(String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
