package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.pms.entity.SkuSaleAttrVo;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * sku销售属性&值
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:06:45
 */
@Api(tags = "sku销售属性&值 管理")
@RestController
@RequestMapping("pms/skuattrvalue")
public class SkuAttrValueController {

    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @GetMapping("getSkuAttrValueBySkuId/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> getSkuAttrValueBySkuId(@PathVariable Long skuId){
        List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueService.list(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId));
        return ResponseVo.ok(skuAttrValueEntities);
    }

    @GetMapping("getSkuSaleAttrVoList/{spuId}")
    public ResponseVo<List<SkuSaleAttrVo>> getSkuSaleAttrVoList(@PathVariable Long spuId){
        List<SkuSaleAttrVo> list = skuAttrValueService.getSkuSaleAttrVoList(spuId);
        return ResponseVo.ok(list);
    }

    @GetMapping("getAttrListByIds")
    public ResponseVo<List<SkuAttrValueEntity>> getAttrListByIds(@RequestParam List<Long> ids, @RequestParam Long skuId){
        List<SkuAttrValueEntity> list = skuAttrValueService.list(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).in("attr_id", ids));
        return ResponseVo.ok(list);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = skuAttrValueService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuAttrValueEntity> querySkuAttrValueById(@PathVariable("id") Long id){
		SkuAttrValueEntity skuAttrValue = skuAttrValueService.getById(id);

        return ResponseVo.ok(skuAttrValue);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuAttrValueEntity skuAttrValue){
		skuAttrValueService.save(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuAttrValueEntity skuAttrValue){
		skuAttrValueService.updateById(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		skuAttrValueService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
