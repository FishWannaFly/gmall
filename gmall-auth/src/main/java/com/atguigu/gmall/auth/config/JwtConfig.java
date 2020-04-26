package com.atguigu.gmall.auth.config;

import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
@Data
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtConfig {
    private String priPath;
    private String pubPath;
    private Integer expire;
    private String secret;
    private String cookieName;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    @PostConstruct
    public void init(){
        try {
            if(privateKey == null || publicKey == null){
                RsaUtils.generateKey(pubPath,priPath,secret);
                PrivateKey privateKey = RsaUtils.getPrivateKey(priPath);
                PublicKey publicKey = RsaUtils.getPublicKey(pubPath);
                this.privateKey = privateKey;
                this.publicKey = publicKey;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
