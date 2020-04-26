package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.api.vo.SaleVo;
import com.atguigu.gmall.sms.api.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.api.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.mapper.SkuFullReductionMapper;
import com.atguigu.gmall.sms.mapper.SkuLadderMapper;
import com.atguigu.gmall.sms.vo.BenifitVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.api.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service("spuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Autowired
    SkuFullReductionMapper skuFullReductionMapper;
    @Autowired
    SkuLadderMapper skuLadderMapper;
    @Override
    @Transactional
    public void saveBenifitVo(BenifitVo benifitVo) {
        //保存极分
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(benifitVo,skuBoundsEntity);
        baseMapper.insert(skuBoundsEntity);
        //保存满减
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(benifitVo,skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(benifitVo.getFullAddOther());
        skuFullReductionMapper.insert(skuFullReductionEntity);
        //保存打折
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(benifitVo,skuLadderEntity);
        skuLadderEntity.setAddOther(benifitVo.getLadderAddOther());
        skuLadderMapper.insert(skuLadderEntity);

    }

    @Override
    public List<SaleVo> getSaleBenifit(Long skuId) {
        SkuBoundsEntity skuBoundsEntity = baseMapper.selectOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        SkuFullReductionEntity skuFullReductionEntity = skuFullReductionMapper.selectOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        SkuLadderEntity skuLadderEntity = skuLadderMapper.selectOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        List<SaleVo> saleVos = new ArrayList<>();
        if(skuBoundsEntity != null){
            BigDecimal growBounds = skuBoundsEntity.getGrowBounds();
            BigDecimal buyBounds = skuBoundsEntity.getBuyBounds();
            SaleVo saleVo = new SaleVo();
            saleVo.setType("积分");
            saleVo.setDesc("赠送"+growBounds+"成长积分，赠送"+buyBounds+"购物积分");
            saleVos.add(saleVo);
        }
        if(skuFullReductionEntity != null){
            BigDecimal fullPrice = skuFullReductionEntity.getFullPrice();
            BigDecimal reducePrice = skuFullReductionEntity.getReducePrice();
            SaleVo saleVo = new SaleVo();
            saleVo.setType("满减");
            saleVo.setDesc("满"+fullPrice+"元减"+reducePrice+"元");
            saleVos.add(saleVo);
        }
        if(skuLadderEntity != null){
            BigDecimal discount = skuLadderEntity.getDiscount();
            Integer fullCount = skuLadderEntity.getFullCount();
            SaleVo saleVo = new SaleVo();
            saleVo.setType("打折");
            saleVo.setDesc("满"+fullCount+"件打"+discount.divide(new BigDecimal(10))+"折");
            saleVos.add(saleVo);
        }

        return saleVos;
    }

}