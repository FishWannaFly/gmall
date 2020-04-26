package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.api.vo.SaleVo;
import com.atguigu.gmall.sms.vo.BenifitVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.sms.api.entity.SkuBoundsEntity;

import java.util.List;

/**
 * 商品spu积分设置
 *
 * @author yxl
 * @email 111@qq.com
 * @date 2020-03-31 18:15:52
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void saveBenifitVo(BenifitVo benifitVo);

    List<SaleVo> getSaleBenifit(Long skuId);
}

