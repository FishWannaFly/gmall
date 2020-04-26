package com.atguigu.gmall.pms.entity;

import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupAttrVo extends AttrGroupEntity {
    private List<SkuSaleAttrVo> allAttrVos = new ArrayList<>();
}
