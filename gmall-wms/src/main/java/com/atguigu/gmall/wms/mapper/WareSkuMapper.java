package com.atguigu.gmall.wms.mapper;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 19:17:15
 */
@Mapper
public interface WareSkuMapper extends BaseMapper<WareSkuEntity> {

    List<WareSkuEntity> ableWareSku(@Param("skuId") Long skuId, @Param("count") Integer count);

    int lockWare(@Param("id") Long id, @Param("count") Integer count);

    void unlockWare(@Param("id") Long wareSkuId, @Param("count") Integer count);

    void minusStore(@Param("id") Long id,@Param("count") Integer count);
}
