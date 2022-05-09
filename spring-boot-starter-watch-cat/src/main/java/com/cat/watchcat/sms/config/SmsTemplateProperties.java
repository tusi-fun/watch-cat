package com.cat.watchcat.sms.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信推送模版
 * @author hudongshan
 * @version 20211014
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms.template")
public class SmsTemplateProperties {

    private Map<String,SmsTemplate> aliyun;

}