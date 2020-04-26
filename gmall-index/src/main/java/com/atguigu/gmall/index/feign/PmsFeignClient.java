package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.feign.SpuFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("pms-service")
public interface PmsFeignClient extends SpuFeign {
}
