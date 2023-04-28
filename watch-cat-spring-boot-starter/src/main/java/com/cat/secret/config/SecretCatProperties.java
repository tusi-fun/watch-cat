package com.cat.secret.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数加解密配置
 * @author hudongshan
 * @version 20210425
 */
@Data
@ConfigurationProperties(prefix = "watchcat.secret")
public class SecretCatProperties {

    private boolean enabled = false;

    private String privateKey;
    private String publicKey;

}