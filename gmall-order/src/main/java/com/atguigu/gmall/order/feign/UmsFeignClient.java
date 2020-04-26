package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.ums.feign.UmsFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("ums-service")
public interface UmsFeignClient extends UmsFeign {
}
