package com.cat.sign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 接口签名验证参数配置（适用于非对称算法rsa、sm2等）
 * @author hudongshan
 */
@Data
@ConfigurationProperties(prefix = "watchcat.sign")
public class SignSymmetricProperties {

    private boolean enabled = false;

    private Map<String,SymmetricSignProvider> symmetric;

    @Data
    public static class SymmetricSignProvider {
        /**
         * 签名算法
         */
        private String algorithm;

        /**
         * 前后宽容时间(s)
         */
        private Long tolerant;

        /**
         * 公钥
         */
        private String publicKey;

        /**
         * 私钥
         */
        private String privateKey;
    }

}