package com.cat.watchcat.sms.config;

import lombok.Data;

/**
 * 短信验证码模版对象
 * @author: hudongshan
 * @date: 2020/12/7
 **/
@Data
public class SmsTemplate {

    private String code;
    private String structure;
    private String desc;

}