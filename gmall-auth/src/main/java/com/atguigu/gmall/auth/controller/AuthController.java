package com.atguigu.gmall.auth.controller;

import com.atguigu.gmall.auth.config.JwtConfig;
import com.atguigu.gmall.auth.service.AuthServive;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthServive authServive;
    @Autowired
    JwtConfig jwtConfig;
    @PostMapping
    public ResponseVo<Object> setJwtCookie(@RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                           HttpServletRequest request,
                                           HttpServletResponse response){
        String jwtCookie = authServive.setJwtCookie(username, password);
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),jwtCookie,
                jwtConfig.getExpire()*60);
        return ResponseVo.ok();
    }
}
