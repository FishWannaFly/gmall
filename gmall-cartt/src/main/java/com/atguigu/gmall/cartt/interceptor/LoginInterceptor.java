package com.atguigu.gmall.cartt.interceptor;

import com.atguigu.gmall.cartt.config.JwtConfig;
import com.atguigu.gmall.cartt.entity.UserInfo;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    JwtConfig jwtConfig;
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwtValue = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        String userKeyValue = CookieUtils.getCookieValue(request, jwtConfig.getUserKey());
        UserInfo userInfo = new UserInfo();
        if(StringUtils.isEmpty(userKeyValue)){
            userKeyValue = UUID.randomUUID().toString();
            CookieUtils.setCookie(request,response,jwtConfig.getUserKey(),userKeyValue,jwtConfig.getUserKeyExpire());
        }
        userInfo.setUserKey(userKeyValue);
        if(StringUtils.isEmpty(jwtValue)){
            THREAD_LOCAL.set(userInfo);
            return true;
        }
        Map<String, Object> map = JwtUtils.getInfoFromToken(jwtValue, jwtConfig.getPublicKey());
        userInfo.setUserId(Long.parseLong(map.get("userId").toString()));

        THREAD_LOCAL.set(userInfo);
        return true;
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
