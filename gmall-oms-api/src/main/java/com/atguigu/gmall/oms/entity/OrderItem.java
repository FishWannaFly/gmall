package com.atguigu.gmall.oms.entity;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.sms.api.vo.SaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItem {
    private Long skuId;
    private String title;
    private String defaultImages;
    private BigDecimal price;
    private BigDecimal weight;
    private Long count;
    private Boolean store;
    private List<SkuAttrValueEntity> skuAttrValueEntities;
    private List<SaleVo> saleVos;
}
