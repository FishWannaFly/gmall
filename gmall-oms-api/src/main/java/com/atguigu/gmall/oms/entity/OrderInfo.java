package com.atguigu.gmall.oms.entity;

import com.atguigu.gmall.ums.entity.UserAddressEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderInfo {
    private List<UserAddressEntity> userAddressEntities;

    private List<OrderItem> orderItems;

    private Integer integration;//购物积分

    private String orderToken;

}
