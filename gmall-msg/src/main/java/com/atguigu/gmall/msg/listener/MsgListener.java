package com.atguigu.gmall.msg.listener;

import com.atguigu.gmall.common.utils.HttpUtils;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@Component
@ConfigurationProperties(prefix="sms")
public class MsgListener {
    private String host;
    private String path;
    private String method;
    private String appcode;
    @Autowired
    StringRedisTemplate redisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "gmall.msg.queue", durable = "true"),
            exchange = @Exchange(
                    value = "gmall.msg.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"send.#"}))
    public void listen(String msg) {

        Map<String, String> headers = new HashMap<String, String>();
        // 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        querys.put("mobile", msg);
        //截取uuid前6位作为验证码
        String code = UUID.randomUUID().toString().substring(0,6);
        querys.put("param", "code:"+ code);
        //手机验证码放入redis缓存中30分钟过期
        redisTemplate.opsForValue().set(msg + ":code",code,30, TimeUnit.MINUTES);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<>();
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
