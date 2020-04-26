package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.entity.vo.SkuVo;
import com.atguigu.gmall.pms.entity.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.entity.vo.SpuVo;
import com.atguigu.gmall.pms.feign.SkuBoundsFeignClient;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.sms.api.vo.BenifitVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo getListByCid(Long cid, PageParamVo paramVo) {
        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();
        if(cid != 0){
            wrapper.eq("catagory_id",cid);
        }
        String key = paramVo.getKey();
        if(!StringUtils.isEmpty(key)){
            wrapper.and(t -> {
                t.eq("id",key).or().like("name",key);
            });
        }
        IPage page = paramVo.getPage();
        baseMapper.selectPage(page,wrapper);
        return new PageResultVo(page);
    }
    @Autowired
    SpuAttrValueService spuAttrValueService;
    @Autowired
    SpuDescService spuDescService;
    @Autowired
    SkuService skuService;
    @Autowired
    SkuAttrValueService skuAttrValueService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuBoundsFeignClient skuBoundsFeign;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    @GlobalTransactional
    public void bigSave(SpuVo spuVo) {
        SpuServiceImpl spuService = (SpuServiceImpl) AopContext.currentProxy();
        spuService.saveSpuBaseInfo(spuVo);
        spuService.saveSpuAttr(spuVo);
        List<String> urls = spuService.saveSpuImages(spuVo);
        spuService.saveSku(spuVo, urls);
        rabbitTemplate.convertAndSend("spu-exchange","spu.save",spuVo.getId());
    }
        @Transactional
    public void saveSku(SpuVo spuVo, List<String> urls) {
        //保存sku
        List<SkuVo> skus = spuVo.getSkus();
        skus.forEach(sku ->{
            sku.setSpuId(spuVo.getId());
            sku.setCatagoryId(spuVo.getCategoryId());
            sku.setBrandId(spuVo.getBrandId());
            if(!CollectionUtils.isEmpty(urls)){
                sku.setDefaultImage(sku.getDefaultImage() == null ? urls.get(0) : sku.getDefaultImage());
            }
            skuService.save(sku);
            //保存sku属性值
            List<SkuAttrValueEntity> saleAttrs = sku.getSaleAttrs();
            saleAttrs.forEach(saleAttr ->{
                saleAttr.setSkuId(sku.getId());
            });
            skuAttrValueService.saveBatch(saleAttrs);
            //保存sku图片
            List<String> images = sku.getImages();
            if(!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(url -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setUrl(url);
                    skuImagesEntity.setSkuId(sku.getId());
                    skuImagesEntity.setSort(0);
                    if (url.equals(sku.getDefaultImage())) {
                        skuImagesEntity.setDefaultStatus(1);
                    }
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);
            }
            //保存优惠信息
            BenifitVo benifitVo = new BenifitVo();
            BeanUtils.copyProperties(sku,benifitVo);
            //work属性类型不一致会保存不成功，手动设置上。
            benifitVo.setSkuId(sku.getId());
            List<Integer> works = sku.getWork();
            Integer work = works.get(0) * 8 + works.get(1) * 4 + works.get(2) * 2 + works.get(3);
            benifitVo.setWork(work);

            skuBoundsFeign.saveBenifitVo(benifitVo);

        });
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> saveSpuImages(SpuVo spuVo) {
        //保存spu的详细图片信息
        List<String> urls = spuVo.getSpuImages();
        if(!CollectionUtils.isEmpty(urls)){
            SpuDescEntity spuDescEntity = new SpuDescEntity();
            spuDescEntity.setSpuId(spuVo.getId());
            spuDescEntity.setDecript(org.apache.commons.lang.StringUtils.join(urls,","));
            spuDescService.save(spuDescEntity);
        }
        return urls;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSpuAttr(SpuVo spuVo) {
        //保存spu的属性信息
        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
        List<SpuAttrValueEntity> attrValueEntities = baseAttrs.stream().map(baseAttr -> {
            SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
            BeanUtils.copyProperties(baseAttr, spuAttrValueEntity);
            spuAttrValueEntity.setSpuId(spuVo.getId());
            spuAttrValueEntity.setSort(0);
            return spuAttrValueEntity;
        }).collect(Collectors.toList());
        spuAttrValueService.saveBatch(attrValueEntities);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSpuBaseInfo(SpuVo spuVo) {
        //保存spu得到spuId来保存其他信息
        baseMapper.insert(spuVo);
    }

}