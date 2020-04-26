package com.atguigu.gmall.pms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableFeignClients
@EnableSwagger2
@EnableDiscoveryClient
@MapperScan("com.atguigu.gmall.pms.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class GmallPmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallPmsApplication.class, args);
    }

}
