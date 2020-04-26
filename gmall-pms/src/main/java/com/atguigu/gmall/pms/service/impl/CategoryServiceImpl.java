package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.CategoryVo;
import com.atguigu.gmall.pms.entity.ThreeCategoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> getParent(Long parentId) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        if(parentId != -1){
            wrapper.eq("parent_id",parentId);
        }
        List<CategoryEntity> categoryEntities = baseMapper.selectList(wrapper);
        return categoryEntities;
    }

    @Override
    public List<CategoryVo> getChildren(Long parentId) {
        return baseMapper.getChildren(parentId);
    }

    @Override
    public List<ThreeCategoryVo> getThreeCate(Long cId) {
        CategoryEntity categoryEntity3 = baseMapper.selectById(cId);
        CategoryEntity categoryEntity2 = baseMapper.selectOne(new QueryWrapper<CategoryEntity>().eq("id", categoryEntity3.getParentId()));
        CategoryEntity categoryEntity1 = baseMapper.selectOne(new QueryWrapper<CategoryEntity>().eq("id", categoryEntity2.getParentId()));
        ArrayList<ThreeCategoryVo> threeCategoryVos = new ArrayList<>();
        ThreeCategoryVo one = new ThreeCategoryVo();
        ThreeCategoryVo two = new ThreeCategoryVo();
        ThreeCategoryVo three = new ThreeCategoryVo();
        BeanUtils.copyProperties(categoryEntity3,three);
        BeanUtils.copyProperties(categoryEntity2,two);
        BeanUtils.copyProperties(categoryEntity1,one);
        threeCategoryVos.add(one);
        threeCategoryVos.add(two);
        threeCategoryVos.add(three);
        return threeCategoryVos;
    }

}