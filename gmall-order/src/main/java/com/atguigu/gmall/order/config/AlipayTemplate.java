package com.atguigu.gmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.order.entity.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016101800713957";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCkmlumv1Hnd0fAQm84SLM8JJEIDm6cgS+L7BNwfwnE2sHpio/P5wsBwBfCwObDjdR4s46frAaD8tRKkR1hhydhPnaWb9CtkjjZkBbJBdYQN9DqNiquRjtxoDC+g2HradPvLPTxFivhsnVAMFFPspm8jSLuUwgDMKHlm7kb3htkGJyATnFzNiD/MHPx3tjN/NUrWfYk754d8ng8ZyTo5fpN5w2qcXkomTwpZVj0k9gU4TX/29HJwNJ5h3vKXezoDZ6hC/VL9zpIWPFHtSYoMNGfJszART6+12cs/PhKlnQllpVVb9BKhyzPXOJj54cnA8isUEysLyN6QfRpG+9grcGNAgMBAAECggEAKAUXj30orrxDh60fWXHjkhXZ5TmNDyQhqUVmnI1mlKQTTXLQ8F1eQbiwjjtUX+tV41rUrGGo29/oWZDaseGoY25Kat0YwXqxs8PiplFy8FKBytAMpH6S6VdnQoqy/gem7Znim9MmLCO1ejvFKTllQ4CVI5iwdAYY9rhb0zi3NCibuXFzXtJ9gAjTEuQTdAHv+9mKPfrqL/bpgK5RqUJ4pMpdXEk13OERL6E4vI6DiigyD/6ADn2rvuC1XT9YKM8w0rIxb3tIeomAjdU160HRh/93DpFoUY0MLIceX4hlzc1YIhTrJTuevvR6H//x+HSILVA4+DY1EjZKRCiMsIouwQKBgQDZRhMDX0Dm7pI6rjOhEicdGqtcJAlmFXcS1wrxQO6loqNJZnk0Aqn3IGzm4NvYdXiqsrJbFnqugARLbB/Wo0NjPnEkAmBKFsIUwspUZzqRCQQanPjAR63Dvg9EQg4iKFJOrgO5DmiDhmAgukO4KPta3Acd9RBCDevNMOtEGfV8qQKBgQDB8PuytW0bsPRJ+lLpdR7y/+qfqqMPJXrj+Prd5q/OJFLuSNAJq129hVlZ+y54PMw/dy6rF7FMfCf18637Z0dUc0wfcGTolMhkax5dK1tkk+88FRa41b0p0YXFqePHuEJ6RKFAGuHmIomDB1ZWZHaiKPcchXT5pPbfXSlcgHroRQKBgQC4GrSJqvDnvUeTCYEd/q+97Zc+IBsGsTGQeT8AZjDRkRojW/1foNI8YPFmQSqIJCH3IiwXGtkhe8An2Jqx2O2E7qgYBxtoLBiQVu3yzvXFZ4pespvk8de/gRCWmhI3x0rW+ISXDippKDfUA4DWT4OM3dv5+mmoTvt7hvQTZJuq4QKBgCKRMjuDDMRvwMrivB5ySqbjKE13G4MamhHCEey1LkaewM1J2xKuIZIjD27zTzANHhZ9xqqmGWrZgkHbQfpaSSNrPXkpDkNKKYGEei//B1Yg6/YYiCk/p2yptJ8rjbbOR8MFMnx7jiH3q+zXxPK3C1IR3SGPQ+8vEPV05GdhWwbFAoGAIce1FSnf7M/+DyT72kzUfB4KFs/YvocAQukPXmJhIbt3YxKd3hvoR3nX67ZuBtpfu0iN033PVorpw3zMSnswOakFnWY27e1mFclkUQRhQATExm6nquGahBSfBV3EtZG/NHCaifLUYJKxEsmjQtF6gjm2c85V6MCoUuUlyBAcRZs=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjjNJrzVzmOLv3+9vbC740bXRjgU1t/Nz3ImJVaQPezqjRXw4h2Iw2ykWn3It0s5JBDeasTNNxKGzZlGlYD4wB6shIqUtR5SvA3EIikTUb93UfjjZffpkvJCqY5hzwAw8cZgMOjusdlE31oQ0GoOOPyJaZd4da/29cK2AAmiN/fqwHr8biQaJCu7tAff8ZHWBR8N0FmFqHAgPWylXt6bYs/DtwOmZIrELYiZYh4mx+5RRv03+py204sGCOX/XmXjFIvv4486F243ikisEshY9+HsuMkw1PsnMTgGlUAs6BfXVQbR/57zWgpE9nnOO/BXRQX7ItAec191V7dqDnO6fxQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://gmall1010.free.idcfengye.com/order/success";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
