package com.atguigu.gmall.item.service;

import com.atguigu.gmall.item.entity.ItemEntity;
import com.atguigu.gmall.item.feign.PmsFeignClient;
import com.atguigu.gmall.item.feign.SmsFeignClient;
import com.atguigu.gmall.item.feign.WmsFeignClient;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.sms.api.vo.SaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemService {
    @Autowired
    PmsFeignClient pmsFeignClient;
    @Autowired
    WmsFeignClient wmsFeignClient;
    @Autowired
    SmsFeignClient smsFeignClient;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    public ItemEntity itemInfo(Long skuId) {
        ItemEntity itemEntity = new ItemEntity();
        //设置sku基本信息
        CompletableFuture<SkuEntity> skuEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuEntity skuEntity = pmsFeignClient.querySkuById(skuId).getData();
            if (skuEntity == null) {
                return null;
            }
            BeanUtils.copyProperties(skuEntity, itemEntity);
            return skuEntity;
        }, threadPoolExecutor);

        //设置品牌信息
        CompletableFuture<Void> brandCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            BrandEntity brandEntity = pmsFeignClient.queryBrandById(skuEntity.getBrandId()).getData();
            if (brandEntity != null) {
                itemEntity.setBrandId(brandEntity.getId());
                itemEntity.setBrandName(brandEntity.getName());
            }
        }, threadPoolExecutor);

        //设置分类信息
        CompletableFuture<Void> cateCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            List<ThreeCategoryVo> threeCategoryVos = pmsFeignClient.getThreeCate(skuEntity.getCatagoryId()).getData();
            itemEntity.setThreeCategoryVos(threeCategoryVos);
        }, threadPoolExecutor);

        //设置sku图片
        CompletableFuture<Void> skuImagesCompletableFuture = CompletableFuture.runAsync(() -> {
            SkuImagesEntity skuImagesEntity = pmsFeignClient.getImagesBySkuId(skuId).getData();
            if (skuImagesEntity != null) {
                String url = skuImagesEntity.getUrl();
                String[] images = url.split(",");
                itemEntity.setSkuImages(Arrays.asList(images));
            }
        }, threadPoolExecutor);
        //设置spu详情图
        CompletableFuture<Void> spuImagesCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity->{
            SpuDescEntity spuDescEntity = pmsFeignClient.querySpuDescById(skuEntity.getSpuId()).getData();
            if(spuDescEntity != null){
                String url = spuDescEntity.getDecript();
                String[] images = url.split(",");
                itemEntity.setDesc(Arrays.asList(images));
            }
        },threadPoolExecutor);

        //设置spuName
        CompletableFuture<Void> spuNameCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity->{
            SpuEntity spuEntity = pmsFeignClient.querySpuById(skuEntity.getSpuId()).getData();
            if(spuEntity != null){
                itemEntity.setSpuName(spuEntity.getName());
                itemEntity.setSpuId(spuEntity.getId());
            }
        },threadPoolExecutor);

        //设置库存
        CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(()->{
            List<WareSkuEntity> wareSkuEntities = wmsFeignClient.getListBySkuId(skuId.intValue()).getData();
            itemEntity.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock()-wareSkuEntity.getStockLocked()>0));
        },threadPoolExecutor);
        //设置优惠信息
        CompletableFuture<Void> benifitCompletableFuture = CompletableFuture.runAsync(()->{
            List<SaleVo> saleVos = smsFeignClient.getSaleBenifit(skuId).getData();
            itemEntity.setSaleVos(saleVos);
        },threadPoolExecutor);
        //设置销售属性
        CompletableFuture<Void> saleCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity->{
            List<SkuSaleAttrVo> skuSaleAttrVos = pmsFeignClient.getSkuSaleAttrVoList(skuEntity.getSpuId()).getData();
            itemEntity.setSkuSaleAttrVos(skuSaleAttrVos);
        },threadPoolExecutor);
        //设置详情属性
        CompletableFuture<Void> skuAttrCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity->{
            List<GroupAttrVo> groupAttrVos = pmsFeignClient.getGroupAttr(skuEntity.getCatagoryId(), skuId, skuEntity.getSpuId()).getData();
            itemEntity.setGroupAttrVos(groupAttrVos);
        },threadPoolExecutor);
        CompletableFuture.allOf(skuEntityCompletableFuture,skuAttrCompletableFuture,saleCompletableFuture
        ,benifitCompletableFuture,storeCompletableFuture,spuNameCompletableFuture,
                spuImagesCompletableFuture,skuImagesCompletableFuture,cateCompletableFuture,
                brandCompletableFuture).join();
        return itemEntity;
    }


}
