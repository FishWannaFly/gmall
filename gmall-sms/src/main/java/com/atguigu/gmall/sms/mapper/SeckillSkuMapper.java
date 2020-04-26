package com.atguigu.gmall.sms.mapper;

import com.atguigu.gmall.sms.api.entity.SeckillSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动商品关联
 * 
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:15:52
 */
@Mapper
public interface SeckillSkuMapper extends BaseMapper<SeckillSkuEntity> {
	
}
