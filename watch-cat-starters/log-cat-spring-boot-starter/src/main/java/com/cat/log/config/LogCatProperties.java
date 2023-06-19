package com.cat.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 业务调用流控配置
 * @author xy783
 * @version 20210425
 */
@Data
@ConfigurationProperties(prefix = "watchcat.log")
public class LogCatProperties {

    private boolean enabled = false;
    private String ipKey = "";

}