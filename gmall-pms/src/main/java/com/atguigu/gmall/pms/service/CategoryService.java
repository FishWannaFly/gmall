package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.CategoryVo;
import com.atguigu.gmall.pms.entity.ThreeCategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;

import java.util.List;

/**
 * 商品三级分类
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:06:45
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<CategoryEntity> getParent(Long parentId);

    List<CategoryVo> getChildren(Long parentId);

    List<ThreeCategoryVo> getThreeCate(Long cId);
}

