package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.sms.api.feign.SkuBoundsFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface SmsFeignClient extends SkuBoundsFeign {
}
