package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:06:45
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {
	
}
