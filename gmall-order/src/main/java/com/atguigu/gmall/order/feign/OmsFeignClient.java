package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.oms.feign.OmsFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("oms-service")
public interface OmsFeignClient extends OmsFeign {
}
