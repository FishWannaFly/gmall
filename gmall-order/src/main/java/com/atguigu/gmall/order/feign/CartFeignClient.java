package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cartt.feign.CartFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cart-service")
public interface CartFeignClient extends CartFeign {
}
