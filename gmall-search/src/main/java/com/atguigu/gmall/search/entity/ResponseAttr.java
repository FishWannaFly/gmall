package com.atguigu.gmall.search.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseAttr {
    private Long attrId;
    private String attrName;
    private List<String> attrValues = new ArrayList<>();
}
