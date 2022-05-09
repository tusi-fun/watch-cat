package com.cat.watchcat.secret.annotation;

import java.lang.annotation.*;

/**
 * 参数加解密注解
 * @author hudongshan
 * @version 20211122
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SecretCat {

    /**
     * 响应数据是否需要加密
     * @return
     */
    boolean pongEncrypt() default false;

    /**
     * 响应参数解密结果填充字段
     * @return
     */
    String pongEncryptField() default "data";

    /**
     * 是否启用防重验证
     * @return
     */
    boolean enableReplay() default true;

    /**
     * 是否启用参数验证
     * @return
     */
    boolean enableValid() default true;

}