package com.atguigu.gmall.cartt.config;

import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Configuration
@Data
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtConfig {
    private String pubPath;
    private String cookieName;
    private String userKey;
    private Integer userKeyExpire;

    private PublicKey publicKey;

    @PostConstruct
    public void init(){
        try {
            PublicKey publicKey = RsaUtils.getPublicKey(pubPath);
            this.publicKey = publicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
