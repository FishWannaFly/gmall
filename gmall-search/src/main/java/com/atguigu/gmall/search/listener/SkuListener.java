package com.atguigu.gmall.search.listener;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.config.GoodsRepository;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttrValue;
import com.atguigu.gmall.search.feign.SpuFeignClient;
import com.atguigu.gmall.search.feign.WmsFeignClient;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SkuListener {
    @Autowired
    SpuFeignClient spuFeignClient;
    @Autowired
    WmsFeignClient wmsFeignClient;
    @Autowired
    GoodsRepository goodsRepository;
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "spu-queue",durable = "true"),
                    exchange = @Exchange(value = "spu-exchange",type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions = "true"),
                    key = {"spu.save","spu.update"}))
    public void listen(Long spuId, Channel channel, Message message) throws IOException {
        //根据spuid查询spu
        ResponseVo<SpuEntity> spuEntityResponseVo = spuFeignClient.querySpuById(spuId);
        SpuEntity spuEntity = spuEntityResponseVo.getData();
        if(spuEntity == null){
            return;
        }
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
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }
}
