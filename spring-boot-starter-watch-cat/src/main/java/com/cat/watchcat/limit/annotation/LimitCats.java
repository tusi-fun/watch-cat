package com.cat.watchcat.limit.annotation;

import java.lang.annotation.*;

/**
 * 业务频率注解
 * @author hudongshan
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LimitCats {

    LimitCat[] value();
}
