package com.cat.watchcat.sign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 接口签名验证参数配置（适用于散列算法）
 * @author hudongshan
 */
@Data
@Component
@ConfigurationProperties(prefix = "watchcat.sign.sha")
public class SignShaProperties {

    /**
     * 签名算法
     */
    private String algorithm;

    /**
     * 前后宽容时间(s)
     */
    private Long tolerant = 0L;

}