package com.atguigu.gmall.sms.api.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.api.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.api.vo.BenifitVo;
import com.atguigu.gmall.sms.api.vo.SaleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface SkuBoundsFeign {
    @PostMapping("/sms/spubounds/saveBenifitVo")
    public ResponseVo<Object> saveBenifitVo(@RequestBody BenifitVo benifitVo);
    @GetMapping("sms/spubounds/saleBenifit/{skuId}")
    public ResponseVo<List<SaleVo>> getSaleBenifit(@PathVariable Long skuId);
    @GetMapping("sms/spubounds/getBoundsBySkuId/{skuId}")
    public ResponseVo<SkuBoundsEntity> getBoundsBySkuId(@PathVariable Long skuId);
}
