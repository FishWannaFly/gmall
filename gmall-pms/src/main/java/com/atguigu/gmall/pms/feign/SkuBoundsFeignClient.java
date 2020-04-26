package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.api.feign.SkuBoundsFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface SkuBoundsFeignClient extends SkuBoundsFeign {
}
