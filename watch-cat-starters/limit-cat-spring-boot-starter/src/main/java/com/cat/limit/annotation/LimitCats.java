package com.cat.limit.annotation;

import java.lang.annotation.*;

/**
 * 业务频率注解
 * @author xy783
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LimitCats {

    LimitCat[] value();
}
