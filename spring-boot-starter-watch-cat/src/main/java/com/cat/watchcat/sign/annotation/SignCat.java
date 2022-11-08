package com.cat.watchcat.sign.annotation;

import java.lang.annotation.*;

/**
 * 请求验签，响应加签注解
 * @author hudongshan
 * @version 20221018
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SignCat {
}
