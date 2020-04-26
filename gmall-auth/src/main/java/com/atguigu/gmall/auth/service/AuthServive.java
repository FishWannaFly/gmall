package com.atguigu.gmall.auth.service;

import com.atguigu.gmall.auth.config.JwtConfig;
import com.atguigu.gmall.auth.feign.UmsFeignClient;
import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServive {
    @Autowired
    UmsFeignClient umsFeignClient;
    @Autowired
    JwtConfig jwtConfig;

    public String setJwtCookie(String username, String password) {
        UserEntity userEntity = umsFeignClient.queryUserEntity(username, password).getData();
        if(userEntity == null){
            throw new GmallException("账号或密码错误");
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("userId",userEntity.getId());
            map.put("username",userEntity.getUsername());
            String jwtToken = JwtUtils.generateToken(map, jwtConfig.getPrivateKey(), jwtConfig.getExpire() * 60);
            return jwtToken;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GmallException("生成jwt异常");
        }
    }
}
