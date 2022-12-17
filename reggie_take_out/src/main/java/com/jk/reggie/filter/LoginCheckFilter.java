package com.jk.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.jk.reggie.common.BaseContext;
import com.jk.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取拦截路径
        String uri = request.getRequestURI();
        log.info("拦截到请求" + uri);

        //放行路径数组
        String[] uris = new String[]{
                "/employee/login",
                "/user/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        //判断是否属于放行路径
        boolean check = check(uris, uri);
        //是则放行
        if (check){
            log.info(uri + "请求放行");
            filterChain.doFilter(request, response);
            return;
        }
        //不是则检查是否已登录，登录则放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录");

            //treadLocal中存入用户id
            BaseContext.setCurrent((Long)request.getSession().getAttribute("employee"));

            filterChain.doFilter(request, response);
            return;
        }

        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录");

            //treadLocal中存入用户id
            BaseContext.setCurrent((Long)request.getSession().getAttribute("user"));

            filterChain.doFilter(request, response);
            return;
        }

        //未登录返回错误信息
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("用户未登录");
    }

    public boolean check(String[] uris, String requestUri){
        for (String uri : uris) {
            boolean match = PATH_MATCHER.match(uri, requestUri);
            if (match){
                return true;
            }
        }
        return false;
    }

}
