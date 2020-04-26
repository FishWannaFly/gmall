package com.atguigu.gmall.order.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.entity.OrderInfo;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.atguigu.gmall.order.config.AlipayTemplate;
import com.atguigu.gmall.order.entity.PayAsyncVo;
import com.atguigu.gmall.order.entity.PayVo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    AlipayTemplate alipayTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @PostMapping("confirm")
    public ResponseVo<OrderInfo> orderConfirm(){
        OrderInfo orderInfo = orderService.orderConfirm();
        return ResponseVo.ok(orderInfo);
    }
    @PostMapping("submit")
    public ResponseVo<Object> orderSubmit(@RequestBody OrderSubmitVo orderSubmitVo){
        try {
            OrderEntity orderEntity = orderService.orderSubmit(orderSubmitVo);
            PayVo payVo = new PayVo();
            payVo.setOut_trade_no(orderEntity.getOrderSn());
            payVo.setTotal_amount(orderEntity.getTotalAmount().toString());
            payVo.setSubject("aaaa");
            payVo.setBody("bbbb");
            String result = alipayTemplate.pay(payVo);
            return ResponseVo.ok(result);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return ResponseVo.fail();
    }
    @RequestMapping("/success")
    public ResponseVo<Object> success(PayAsyncVo vo){
        //TODO 订单状态修改，库存修改
        rabbitTemplate.convertAndSend("wms-exchange","order.success",vo.getOut_trade_no());
//        System.out.println("付款成功");
        return ResponseVo.ok();
    }
}
