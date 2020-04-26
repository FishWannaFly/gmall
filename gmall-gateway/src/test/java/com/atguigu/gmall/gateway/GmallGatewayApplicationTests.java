package com.atguigu.gmall.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
class GmallGatewayApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(new Random().nextInt(15));
    }

}
