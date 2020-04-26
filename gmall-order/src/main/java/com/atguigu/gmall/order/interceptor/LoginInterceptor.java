package com.atguigu.gmall.order.interceptor;

import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.order.config.JwtConfig;
import com.atguigu.gmall.order.entity.UserInfo;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    JwtConfig jwtConfig;
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwtValue = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        UserInfo userInfo = new UserInfo();
//        if(StringUtils.isEmpty(jwtValue)){
//            THREAD_LOCAL.set(userInfo);
//            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
//            return false;
//        }
        try {
            Map<String, Object> map = JwtUtils.getInfoFromToken(jwtValue, jwtConfig.getPublicKey());
            userInfo.setUserId(Long.parseLong(map.get("userId").toString()));

            THREAD_LOCAL.set(userInfo);
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
//            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
//            return false;
            return true;//删掉
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 调用删除方法，是必须选项。因为使用的是tomcat线程池，请求结束后，线程不会结束。
        // 如果不手动删除线程变量，可能会导致内存泄漏
        THREAD_LOCAL.remove();
    }

    public static UserInfo getThreadLocalValue(){

        return THREAD_LOCAL.get();
    }
}
