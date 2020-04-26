package com.atguigu.gmall.search.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResponseParamVo {
    private List<Goods> goodsList;
    private ResponseAttr categoryAttr;
    private ResponseAttr brandAttr;
    private List<ResponseAttr> attrList;
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
}
