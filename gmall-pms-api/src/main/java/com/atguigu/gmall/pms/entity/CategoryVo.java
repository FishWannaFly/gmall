package com.atguigu.gmall.pms.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class CategoryVo extends CategoryEntity implements Serializable {
    private List<CategoryEntity> subs;
}
