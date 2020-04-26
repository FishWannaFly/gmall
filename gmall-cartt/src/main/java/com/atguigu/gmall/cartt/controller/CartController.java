package com.atguigu.gmall.cartt.controller;

import com.atguigu.gmall.cartt.entity.CartEntity;
import com.atguigu.gmall.cartt.entity.UserInfo;
import com.atguigu.gmall.cartt.interceptor.LoginInterceptor;
import com.atguigu.gmall.cartt.service.CartService;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;
//    @GetMapping("/test")
//    public ResponseVo<Object> test(){
//        UserInfo userInfo = LoginInterceptor.getThreadLocalValue();
//        System.out.println(userInfo);
//        return ResponseVo.ok();
//    }
    @GetMapping("addCart/{skuId}/{count}")
    public ResponseVo<CartEntity> addCart(@PathVariable Long skuId,@PathVariable Integer count){
        cartService.addCart(skuId,count);
        return ResponseVo.ok();
    }
    @GetMapping("getCart")
    public ResponseVo<List<CartEntity>> getCart(){
        List<CartEntity> cartEntities = cartService.getCart();
        return ResponseVo.ok(cartEntities);
    }
    @GetMapping("getCartByUserId/{userId}")
    public ResponseVo<List<CartEntity>> getCartByUserId(@PathVariable Long userId){
        List<CartEntity> cartEntities = cartService.getCartByUserId(userId);
        return ResponseVo.ok(cartEntities);
    }
    @GetMapping("/updateCount/{skuId}/{count}")
    public ResponseVo<CartEntity> updateCount(@PathVariable Long skuId,@PathVariable Integer count){
        cartService.updateCount(skuId,count);
        return ResponseVo.ok();
    }
    @DeleteMapping("/{skuId}")
    public ResponseVo<Object> deleteBySkuId(@PathVariable Long skuId){
        cartService.deleteBySkuId(skuId);
        return ResponseVo.ok();
    }

}
