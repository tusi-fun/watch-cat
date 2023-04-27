package com.cat.watchcat.sensitive.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数加解密配置
 * @author hudongshan
 * @version 20210425
 */
@Data
@ConfigurationProperties(prefix = "watchcat.sensitive")
public class SensitiveCatProperties {

    private boolean enabled = false;

}