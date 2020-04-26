package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.feign.WmsFeign;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-service")
public interface WmsFeignClient extends WmsFeign {
}
