package com.atguigu.gmall.sms.controller;

import java.util.List;

import com.atguigu.gmall.sms.api.vo.SaleVo;
import com.atguigu.gmall.sms.vo.BenifitVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.sms.api.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品spu积分设置
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:15:52
 */
@Api(tags = "商品spu积分设置 管理")
@RestController
@RequestMapping("sms/spubounds")
public class SkuBoundsController {

    @Autowired
    private SkuBoundsService spuBoundsService;

    @GetMapping("/getBoundsBySkuId/{skuId}")
    public ResponseVo<SkuBoundsEntity> getBoundsBySkuId(@PathVariable Long skuId){
        SkuBoundsEntity skuBoundsEntity = spuBoundsService.getOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        return ResponseVo.ok(skuBoundsEntity);
    }

    @GetMapping("/saleBenifit/{skuId}")
    public ResponseVo<List<SaleVo>> getSaleBenifit(@PathVariable Long skuId){
        List<SaleVo> list = spuBoundsService.getSaleBenifit(skuId);
        return ResponseVo.ok(list);
    }

    @PostMapping("/saveBenifitVo")
    public ResponseVo<Object> saveBenifitVo(@RequestBody BenifitVo benifitVo){
        spuBoundsService.saveBenifitVo(benifitVo);
        return ResponseVo.ok();
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = spuBoundsService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuBoundsEntity> querySpuBoundsById(@PathVariable("id") Long id){
		SkuBoundsEntity spuBounds = spuBoundsService.getById(id);

        return ResponseVo.ok(spuBounds);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuBoundsEntity spuBounds){
		spuBoundsService.save(spuBounds);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuBoundsEntity spuBounds){
		spuBoundsService.updateById(spuBounds);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		spuBoundsService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
