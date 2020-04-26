package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.CategoryVo;

import java.util.List;

public interface IndexService {
    List<CategoryEntity> categoryListLv1();

    List<CategoryVo> categoryListLv23(Long pid);

    void test();
}
