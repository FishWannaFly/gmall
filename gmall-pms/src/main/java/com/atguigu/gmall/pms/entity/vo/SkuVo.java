package com.atguigu.gmall.pms.entity.vo;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuVo extends SkuEntity {
    List<String> images;
    List<SkuAttrValueEntity> saleAttrs;
    //积分
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;
    //满减
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
    //打折
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;
}
