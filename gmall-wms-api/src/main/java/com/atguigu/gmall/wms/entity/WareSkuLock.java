package com.atguigu.gmall.wms.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class WareSkuLock implements Serializable {
    private Long skuId;
    private Integer count;
    private Boolean store;
    private Long wareSkuId;//记录库存id，如果失败则回滚该仓库。
    private String orderToken;
}
