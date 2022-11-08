package com.cat.watchcat.sign.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 接口签名验证参数配置（适用于非对称算法rsa、sm2等）
 * @author hudongshan
 */
@Data
@Component
@ConfigurationProperties(prefix = "sign")
public class SignSymmetricProperties {

    private Map<String,SymmetricSignProvider> symmetric;

}