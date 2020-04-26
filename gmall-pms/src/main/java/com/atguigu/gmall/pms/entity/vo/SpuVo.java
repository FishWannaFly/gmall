package com.atguigu.gmall.pms.entity.vo;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuVo extends SpuEntity {
    //图片详情
     List<String> spuImages;

     List<SpuAttrValueVo> baseAttrs;

     List<SkuVo> skus;

}
