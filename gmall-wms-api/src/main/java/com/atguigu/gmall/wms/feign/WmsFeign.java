package com.atguigu.gmall.wms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.entity.WareSkuLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface WmsFeign {
    @GetMapping("wms/waresku/sku/{skuId}")
    public ResponseVo<List<WareSkuEntity>> getListBySkuId(@PathVariable Integer skuId);
    @PostMapping("wms/waresku/lockStock")
    public ResponseVo<List<WareSkuLock>> lockStock(@RequestBody List<WareSkuLock> list);
}
