package com.atguigu.gmall.cartt.feign;

import com.atguigu.gmall.cartt.entity.CartEntity;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface CartFeign {
    @GetMapping("/cart/getCartByUserId/{userId}")
    public ResponseVo<List<CartEntity>> getCartByUserId(@PathVariable Long userId);
}
