package com.atguigu.gmall.item.entity;

import com.atguigu.gmall.pms.entity.GroupAttrVo;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrVo;
import com.atguigu.gmall.pms.entity.ThreeCategoryVo;
import com.atguigu.gmall.sms.api.vo.SaleVo;
import lombok.Data;

import java.util.List;

@Data
public class ItemEntity extends SkuEntity {
    //√
    private Long brandId;
    private String brandName;
    private String spuName;
    private List<ThreeCategoryVo> threeCategoryVos;
    private Boolean store;
    //销售属性 √
    private List<SkuSaleAttrVo> skuSaleAttrVos;
    //spu详情图 √
    private List<String> desc;
    //sku图片   √
    private List<String> skuImages;
    //分组参数 √
    private List<GroupAttrVo> groupAttrVos;
    //优惠信息 √
    private List<SaleVo> saleVos;
}
