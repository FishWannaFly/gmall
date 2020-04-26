package com.atguigu.gmall.oms.vo;

import com.atguigu.gmall.oms.entity.OrderItem;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderSubmitVo {
    private List<OrderItem> orderItems;
    private String orderToken;
    private UserAddressEntity userAddressEntity;
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    private Integer sourceType = 0;
    private BigDecimal totalPrice;
    private Integer useIntegration;

}
