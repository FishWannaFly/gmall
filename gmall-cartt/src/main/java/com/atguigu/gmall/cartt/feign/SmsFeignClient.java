package com.atguigu.gmall.cartt.feign;

import com.atguigu.gmall.sms.api.feign.SkuBoundsFeign;
import com.atguigu.gmall.wms.feign.WmsFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface SmsFeignClient extends SkuBoundsFeign {
}
