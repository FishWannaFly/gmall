package com.atguigu.gmall.wms.controller;

import java.util.List;

import com.atguigu.gmall.wms.entity.WareSkuLock;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品库存
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 19:17:15
 */
@Api(tags = "商品库存 管理")
@RestController
@RequestMapping("wms/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lockStock")
    public ResponseVo<List<WareSkuLock>> lockStock(@RequestBody List<WareSkuLock> list){
        wareSkuService.lockStock(list);
        return ResponseVo.ok(list);
    }

    @GetMapping("/sku/{skuId}")
    public ResponseVo<List<WareSkuEntity>> getListBySkuId(@PathVariable Integer skuId){
        List<WareSkuEntity> wareSkuEntities = wareSkuService.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId));
        return ResponseVo.ok(wareSkuEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> list(PageParamVo paramVo){
        PageResultVo pageResultVo = wareSkuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<WareSkuEntity> queryWareSkuById(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return ResponseVo.ok(wareSku);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		wareSkuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
