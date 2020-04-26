package com.atguigu.gmall.oms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.oms.entity.OrderItem;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.feign.PmsFeignClient;
import com.atguigu.gmall.oms.feign.SmsFeignClient;
import com.atguigu.gmall.oms.service.OrderItemService;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.sms.api.entity.SkuBoundsEntity;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.oms.mapper.OrderMapper;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {
    @Autowired
    PmsFeignClient pmsFeignClient;
    @Autowired
    SmsFeignClient smsFeignClient;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<OrderEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<OrderEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public OrderEntity saveOrderSubmitVo(OrderSubmitVo orderSubmitVo) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(orderSubmitVo.getUserId());
        orderEntity.setUsername(orderSubmitVo.getUsername());
        orderEntity.setOrderSn(orderSubmitVo.getOrderToken());
        orderEntity.setCreateTime(new Date());
        orderEntity.setTotalAmount(orderSubmitVo.getTotalPrice());
//        orderEntity.setPayAmount(orderSubmitVo.getTotalAmount().subtract(new BigDecimal(orderSubmitVo.getUseIntegration() / 100)));
        orderEntity.setAutoConfirmDay(14);
        orderEntity.setSourceType(orderSubmitVo.getSourceType());
        UserAddressEntity userAddressEntity = orderSubmitVo.getUserAddressEntity();
        orderEntity.setReceiverName(userAddressEntity.getName());
        orderEntity.setReceiverPhone(userAddressEntity.getPhone());
        orderEntity.setReceiverPostCode(userAddressEntity.getPostCode());
        orderEntity.setReceiverProvince(userAddressEntity.getProvince());
        orderEntity.setReceiverCity(userAddressEntity.getCity());
        orderEntity.setReceiverRegion(userAddressEntity.getRegion());
        orderEntity.setReceiverAddress(userAddressEntity.getAddress());
        orderEntity.setConfirmStatus(0);
        orderEntity.setDeleteStatus(0);
        orderEntity.setStatus(0);
        orderEntity.setUseIntegration(orderSubmitVo.getUseIntegration());
        baseMapper.insert(orderEntity);

        List<OrderItem> orderItems = orderSubmitVo.getOrderItems();
        List<OrderItemEntity> orderItemEntities = orderItems.stream().map(orderItem -> {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderId(orderEntity.getId());
            orderItemEntity.setOrderSn(orderEntity.getOrderSn());
            Long skuId = orderItem.getSkuId();
            SkuEntity skuEntity = pmsFeignClient.querySkuById(skuId).getData();
            orderItemEntity.setSkuId(skuId);
            orderItemEntity.setSkuName(skuEntity.getName());
            orderItemEntity.setSkuPic(skuEntity.getDefaultImage());
            orderItemEntity.setSkuQuantity(orderItem.getCount().intValue());
            orderItemEntity.setSkuPrice(skuEntity.getPrice());
            orderItemEntity.setCategoryId(skuEntity.getCatagoryId());
            BrandEntity brandEntity = pmsFeignClient.queryBrandById(skuEntity.getBrandId()).getData();
            orderItemEntity.setSpuBrand(brandEntity.getName());
            SpuEntity spuEntity = pmsFeignClient.querySpuById(skuEntity.getSpuId()).getData();
            orderItemEntity.setSpuId(spuEntity.getId());
            orderItemEntity.setSpuName(spuEntity.getName());
            SpuDescEntity spuDescEntity = pmsFeignClient.querySpuDescById(skuEntity.getSpuId()).getData();
            orderItemEntity.setSpuPic(spuDescEntity.getDecript());
            List<SkuAttrValueEntity> skuAttrValueEntities = pmsFeignClient.getSkuAttrValueBySkuId(skuId).getData();
            orderItemEntity.setSkuAttrsVals(JSON.toJSONString(skuAttrValueEntities));
            SkuBoundsEntity skuBoundsEntity = smsFeignClient.getBoundsBySkuId(skuId).getData();
            orderItemEntity.setGiftIntegration(skuBoundsEntity.getBuyBounds().multiply(new BigDecimal(orderItem.getCount())).intValue());
            orderItemEntity.setGiftGrowth(skuBoundsEntity.getGrowBounds().multiply(new BigDecimal(orderItem.getCount())).intValue());
            return orderItemEntity;
        }).collect(Collectors.toList());
        orderItemService.saveBatch(orderItemEntities);
        return orderEntity;
    }

}