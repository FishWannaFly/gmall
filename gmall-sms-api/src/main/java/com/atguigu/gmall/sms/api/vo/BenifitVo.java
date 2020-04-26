package com.atguigu.gmall.sms.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BenifitVo {
    //积分
    private Long skuId;
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private Integer work;
    //满减
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
    //打折
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;
}
