package com.atguigu.gmall.wms.service;

import com.atguigu.gmall.wms.entity.WareSkuLock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 19:17:15
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void lockStock(List<WareSkuLock> list);
}

