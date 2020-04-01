package com.atguigu.gmall.oms.mapper;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 17:35:45
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
	
}
