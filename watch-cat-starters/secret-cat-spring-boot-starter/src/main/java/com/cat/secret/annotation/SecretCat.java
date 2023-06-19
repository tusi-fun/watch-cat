package com.cat.secret.annotation;

import java.lang.annotation.*;

/**
 * 参数加解密注解 (目前仅支持 AES + RSA 方式)
 * @author xy783
 * @version 20211122
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SecretCat {

    /**
     * 启用响应加密（默认禁用）
     * @return
     */
    boolean encryptPong() default false;

    /**
     * 响应加密结果填充字段
     * @return
     */
    String encryptPongField() default "data";

    /**
     * 请求加密结果填充字段
     * @return
     */
    String encryptPingKeyField() default "encryptKey";

    /**
     * 请求加密结果填充字段
     * @return
     */
    String encryptPingDataField() default "encryptData";

    /**
     * 启用原文验证（默认启用）
     * @return
     */
    boolean verifyPlaintext() default true;

    /**
     * 原文填充目标
     * @return
     */
    String plaintextParameter();

}