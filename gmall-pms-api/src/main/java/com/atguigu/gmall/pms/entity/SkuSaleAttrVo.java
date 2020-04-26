package com.atguigu.gmall.pms.entity;

import lombok.Data;

@Data
public class SkuSaleAttrVo {
    private Long attrId;
    private String attrName;
    private String attrValue;//多个值用 ，分割
}
