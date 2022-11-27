package com.zy7y.reggie_take_out.interceptor;

import com.alibaba.fastjson.JSON;
import com.zy7y.reggie_take_out.common.BaseContext;
import com.zy7y.reggie_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截器 {}", request.getRequestURI());
        Long empId = (Long) request.getSession().getAttribute("employee");
        if (Objects.nonNull(empId)) {
            // 存储到当前线程工具类中
            log.info("{} 已登录后台", empId);
            BaseContext.setCurrentId(empId);
            return true;
        }

        Long userId = (Long) request.getSession().getAttribute("user");
        if (Objects.nonNull(userId)){
            log.info("已登录移动端");
            BaseContext.setCurrentId(userId);
            return true;
        }

        // response result
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("用户未登录");
        return false;
    }
}