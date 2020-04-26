package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.service.SpuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class GmallPmsApplicationTests {
    @Autowired
    SpuService spuService;
    @Test
    void contextLoads() {
        SpuEntity spuEntity = new SpuEntity();
        spuEntity.setName("1111");
        spuEntity.setCreateTime(new Date());
        spuEntity.setUpdateTime(new Date());
        spuService.save(spuEntity);
    }
    @Test
    void contextLoads1() {

        System.out.println(spuService.getById(19L));
    }

}
