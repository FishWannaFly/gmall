package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface SpuFeign {
    @PostMapping("pms/spu/page")
    public ResponseVo<List<SpuEntity>> listByPage(@RequestBody PageParamVo paramVo);
    @GetMapping("pms/sku/spu/{spuId}")
    public ResponseVo<List<SkuEntity>> getListBySpuid(@PathVariable Long spuId);
    @GetMapping("pms/brand/{id}")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);
    @GetMapping("pms/category/{id}")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);
    @GetMapping("pms/attr/category/{cid}")
    public ResponseVo<List<AttrEntity>> getAttrEntityListByCid(@PathVariable Long cid,
                                                               @RequestParam(required = false) Integer type,
                                                               @RequestParam(required = false) Integer searchType);
    @GetMapping("pms/skuattrvalue/getAttrListByIds")
    public ResponseVo<List<SkuAttrValueEntity>> getSkuAttrListByIds(@RequestParam List<Long> ids, @RequestParam Long skuId);
    @GetMapping("pms/spuattrvalue/getAttrListByIds")
    public ResponseVo<List<SpuAttrValueEntity>> getSpuAttrListByIds(@RequestParam List<Long> ids,@RequestParam Long spuId);

    @GetMapping("pms/spu/{id}")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);
    @GetMapping("pms/category/parent/{parentId}")
    public ResponseVo<List<CategoryEntity>> getParent(@PathVariable Long parentId);
    @GetMapping("pms/category/children/{parentId}")
    public ResponseVo<List<CategoryVo>> getChildren(@PathVariable Long parentId);
    @GetMapping("pms/sku/{id}")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);
    @GetMapping("pms/category/getThreeCate/{cId}")
    public ResponseVo<List<ThreeCategoryVo>> getThreeCate(@PathVariable Long cId);
    @GetMapping("pms/skuimages/getImagesBySkuId/{skuId}")
    public ResponseVo<SkuImagesEntity> getImagesBySkuId(@PathVariable Long skuId);
    @GetMapping("pms/spudesc/{spuId}")
    public ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);
    @GetMapping("pms/skuattrvalue/getSkuSaleAttrVoList/{spuId}")
    public ResponseVo<List<SkuSaleAttrVo>> getSkuSaleAttrVoList(@PathVariable Long spuId);
    @GetMapping("pms/attrgroup/getGroupAttr")
    public ResponseVo<List<GroupAttrVo>> getGroupAttr(@RequestParam("cId")Long cId,
                                                      @RequestParam("skuId")Long skuId,
                                                      @RequestParam("spuId")Long spuId);
    @GetMapping("pms/skuattrvalue/getSkuAttrValueBySkuId/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> getSkuAttrValueBySkuId(@PathVariable Long skuId);
}
