package com.atguigu.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.config.GoodsRepository;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttrValue;
import com.atguigu.gmall.search.feign.SpuFeignClient;
import com.atguigu.gmall.search.feign.WmsFeignClient;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.sleuth.util.SpanNameUtil;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.sql.SQLOutput;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {
    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    ElasticsearchRestTemplate restTemplate;
    @Autowired
    SpuFeignClient spuFeignClient;
    @Autowired
    WmsFeignClient wmsFeignClient;
    @Test
    void contextLoads() {
//        System.out.println(restTemplate);

    }
    @Test
    void test1(){
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        Integer page = 1;
        List<SpuEntity> spuEntities;
        do{
            PageParamVo pageParamVo = new PageParamVo();
            pageParamVo.setPageNum(page);
            pageParamVo.setPageSize(100);
            ResponseVo<List<SpuEntity>> responseVo = spuFeignClient.listByPage(pageParamVo);
            spuEntities = responseVo.getData();
            if(CollectionUtils.isEmpty(spuEntities)){
                return;
            }
            spuEntities.forEach(spuEntity -> {
                //查询sku信息
                ResponseVo<List<SkuEntity>> skuEntityVo = spuFeignClient.getListBySpuid(spuEntity.getId());
                List<SkuEntity> skuEntities = skuEntityVo.getData();
                if(!CollectionUtils.isEmpty(skuEntities)){
                    List<Goods> list = skuEntities.stream().map(skuEntity -> {
                        Goods goods = new Goods();
                        goods.setBrandId(spuEntity.getBrandId());
                        goods.setCategoryId(spuEntity.getCategoryId());
                        goods.setCreateTime(spuEntity.getCreateTime());
                        goods.setPic(skuEntity.getDefaultImage());
                        goods.setPrice(skuEntity.getPrice().doubleValue());
                        goods.setSkuId(skuEntity.getId());
                        goods.setTitle(skuEntity.getTitle());
                        //查询品牌名
                        ResponseVo<BrandEntity> brandEntityResponseVo = spuFeignClient.queryBrandById(spuEntity.getBrandId());
                        BrandEntity brandEntity = brandEntityResponseVo.getData();
                        if(brandEntity != null){
                            goods.setBrandName(brandEntity.getName());
                            goods.setLogo(brandEntity.getLogo());
                        }
                        //查询目录名
                        ResponseVo<CategoryEntity> categoryEntityResponseVo = spuFeignClient.queryCategoryById(spuEntity.getCategoryId());
                        CategoryEntity categoryEntity = categoryEntityResponseVo.getData();
                        if (categoryEntity != null) {
                            goods.setCategoryName(categoryEntity.getName());
                        }
                        //查询库存和销量
                        ResponseVo<List<WareSkuEntity>> listResponseVo = wmsFeignClient.getListBySkuId(skuEntity.getId().intValue());
                        List<WareSkuEntity> wareSkuEntities = listResponseVo.getData();
                        if(!CollectionUtils.isEmpty(wareSkuEntities)){
                            Long sales = wareSkuEntities.stream().map(wareSkuEntity -> wareSkuEntity.getSales()).reduce((a, b) -> a + b).get();
                            goods.setSales(sales);
                            goods.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity->wareSkuEntity.getStock()>0));
                        }else{
                            //如果没有设置默认无库存，0销售
                            goods.setSales(0L);
                            goods.setStore(false);
                        }
                        ResponseVo<List<AttrEntity>> responseVo1 = spuFeignClient.getAttrEntityListByCid(spuEntity.getCategoryId(), null, 1);
                        List<AttrEntity> attrEntities = responseVo1.getData();
                        if(!CollectionUtils.isEmpty(attrEntities)){
                            List<Long> ids = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());
                            ResponseVo<List<SkuAttrValueEntity>> responseVo2 = spuFeignClient.getSkuAttrListByIds(ids, skuEntity.getId());
                            ResponseVo<List<SpuAttrValueEntity>> responseVo3 = spuFeignClient.getSpuAttrListByIds(ids, spuEntity.getId());
                            List<SkuAttrValueEntity> skuAttrValueEntities = responseVo2.getData();
                            List<SpuAttrValueEntity> spuAttrValueEntities = responseVo3.getData();
                            List<SearchAttrValue> searchAttrValues = skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                                SearchAttrValue searchAttrValue = new SearchAttrValue();
                                BeanUtils.copyProperties(skuAttrValueEntity, searchAttrValue);
                                return searchAttrValue;
                            }).collect(Collectors.toList());
                            List<SearchAttrValue> searchAttrValues1 = spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                                SearchAttrValue searchAttrValue = new SearchAttrValue();
                                BeanUtils.copyProperties(spuAttrValueEntity, searchAttrValue);
                                return searchAttrValue;
                            }).collect(Collectors.toList());
                            goods.setAttrs(searchAttrValues);
                            goods.getAttrs().addAll(searchAttrValues1);
                        }

                        return goods;
                    }).collect(Collectors.toList());
                    goodsRepository.saveAll(list);
                }
            });
            page++;
        }while (spuEntities.size() == 100);

    }

}
