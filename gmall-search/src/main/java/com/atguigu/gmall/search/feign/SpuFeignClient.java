package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.feign.SpuFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("pms-service")
public interface SpuFeignClient extends SpuFeign {
}
