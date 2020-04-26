package com.atguigu.gmall.cartt.entity;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.sms.api.vo.SaleVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartEntity {
    private Long skuId;
    private String title;
    private List<SkuAttrValueEntity> saleAttrValue;
    private BigDecimal price;
    private Integer count;
    private Boolean store;
    private Boolean check;
    private String defalutImage;
    private List<SaleVo> saleInfo;
    private BigDecimal currentPrice;

}
