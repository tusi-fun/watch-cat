package com.cat.watchcat.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 短信验证码服务配置
 * @author hudongshan
 */
@Data
@Component
@ConfigurationProperties(prefix = "watchcat.sms")
public class SmsProperties {

//    sms.audit.phone=17761275221
//    sms.audit.out-id=app-store-check
//    sms.audit.sms-code=3751
//
//    sms.aliyun.access-key-id=xxx
//    sms.aliyun.secret=xxx
//    sms.aliyun.sign-name=阿里云短信测试专用
//
//    sms.cache-key-name=sms:%s:%s
//    sms.cache-key-timeout=15m
//
//    sms.template.aliyun.SMS_CODE.code=SMS_76015427
//    sms.template.aliyun.SMS_CODE.structure={"code":"%s","product":"%s"}
//    sms.template.aliyun.SMS_CODE.desc=短信验证码
//    sms.template.aliyun.ORDER_PAID.code=SMS_214805749
//    sms.template.aliyun.ORDER_PAID.structure={"name":"%s","bid":"%s"}
//    sms.template.aliyun.ORDER_PAID.desc=订单支付成功

    private String cacheKeyName;
    private Duration cacheKeyTimeout;

    private AuditBean audit;
    private AliyunBean aliyun;

    @Data
    public static class AuditBean {
        private String phone;
        private String outId;
        private String smsCode;
    }

    @Data
    public static class AliyunBean {
        private String accessKeyId;
        private String secret;
        private String signName;
    }

}