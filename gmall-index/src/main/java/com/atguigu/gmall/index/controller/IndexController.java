package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    IndexService indexService;
    @GetMapping("/cates")
    public ResponseVo<List<CategoryEntity>> categoryListLv1(){
        List<CategoryEntity> list = indexService.categoryListLv1();
        return ResponseVo.ok(list);
    }
    @GetMapping("/cates/{pid}")
    public ResponseVo<List<CategoryVo>> categoryListLv23(@PathVariable Long pid){
        List<CategoryVo> list = indexService.categoryListLv23(pid);
        return ResponseVo.ok(list);
    }
    @GetMapping("/test")
    public ResponseVo<Object> test(){
        indexService.test();
        return ResponseVo.ok();
    }
}
