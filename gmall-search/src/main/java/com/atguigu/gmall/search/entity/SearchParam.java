package com.atguigu.gmall.search.entity;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    private String keyword;
    private List<Long> brandId;
    private List<String> props;//props=5:骁龙-麒麟,6:小米-华为
    private Long categoryId;
    private Boolean store;
    private Double priceFrom;
    private Double priceTo;
    private Integer pageNum = 1;
    private final Integer pageSize = 40;
    private String order;//1:desc




}
