package com.cat.sign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 接口签名验证参数配置（适用于散列算法）
 * @author hudongshan
 */
@Data
@ConfigurationProperties(prefix = "watchcat.sign.sha")
public class SignShaProperties {

    private boolean enabled = false;

    /**
     * 签名算法
     */
    private String algorithm;

    /**
     * 前后宽容时间
     */
    private Duration tolerant;

}