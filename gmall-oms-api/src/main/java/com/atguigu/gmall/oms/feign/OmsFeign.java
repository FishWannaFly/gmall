package com.atguigu.gmall.oms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface OmsFeign {
    @PostMapping("oms/order")
    public ResponseVo<OrderEntity> save(@RequestBody OrderSubmitVo orderSubmitVo);
}
